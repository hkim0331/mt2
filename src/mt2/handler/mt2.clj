(ns mt2.handler.mt2
  (:require
   [ataraxy.response :as response]
   [hiccup.page      :as hiccup]
   [integrant.core   :as ig]
   [ring.middleware.anti-forgery :as anti-forgery]
   [taoensso.sente   :as sente]
   [taoensso.sente.server-adapters.http-kit :refer (get-sch-adapter)]
   [taoensso.timbre  :as timbre :refer [debugf infof]]))

(def version "0.6.0")

(def msgs (atom []))

(timbre/set-level! :info)
(reset! sente/debug-mode?_ true)

;;; from sente official example

(let [packer :edn ; Default packer, a good choice in most cases

      chsk-server
      (sente/make-channel-socket-server!
       (get-sch-adapter) {:packer packer})

      {:keys [ch-recv send-fn connected-uids
              ajax-post-fn ajax-get-or-ws-handshake-fn]}
      chsk-server]

  (def ring-ajax-post                ajax-post-fn)
  (def ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn)
  (def ch-chsk                       ch-recv) ; ChannelSocket's receive channel
  (def chsk-send!                    send-fn) ; ChannelSocket's send API fn
  (def connected-uids                connected-uids)) ; Watchable, read-only atom

(add-watch connected-uids :connected-uids
           (fn [_ _ old new]
             (when (not= old new)
               (infof "Connected uids change: %s" new))))

(defn page
  [& contents]
  [::response/ok
   (hiccup/html5
    [:head
     [:meta {:charset "utf-8"}]
     [:meta {:name "viewport"
             :content "width=device-width, initial-scale=1, shrink-to-fit=no"}]
     [:title "private micro twitter"]
     (hiccup/include-css
      "https://stackpath.bootstrapcdn.com/bootstrap/4.5.0/css/bootstrap.min.css")]
    [:body
     (let [csrf-token (force anti-forgery/*anti-forgery-token*)]
       [:div#sente-csrf-token {:data-csrf-token csrf-token}])
     [:div.container
      [:h2 "micro Twritter"]
      contents
      [:hr]
      [:div "hkimura, " version "."]
      [:script {:src "/js/main.js"}]]])])

;;; ring event handler

(defmethod ig/init-key :mt2.handler.mt2/index [_ _]
  (fn [{[_] :ataraxy/result}]
    (debugf "index")
    (page
     [:p
      [:div.row
       [:div.col-9
        [:input#message
         {:style "width:100%"
          :placeholder "type your message"}]]
       [:div.col-2
        [:button#send
         {:type "button" :class "btn btn-primary btn-sm"}
         "send"]]]]
     [:p [:textarea#output {:style "width:100%; height:400px;"}]]
     [:p [:button#clear
          {:type "button" :class "btn btn-primary"} "clear"]
         " "
         [:button#reload
          {:type "button" :class "btn btn-primary"} "reload"]])))

(defmethod ig/init-key :mt2.handler.mt2/get-chsk [_ _]
  (fn [req]
    (ring-ajax-get-or-ws-handshake req)))

(defmethod ig/init-key :mt2.handler.mt2/post-chsk [_ _]
  (fn [req]
    (ring-ajax-post req)))

(defmethod ig/init-key :mt2.handler.mt2/reload [_ _]
  (fn [req]
    (let [ret (->> @msgs
                   reverse
                   (interpose "\n")
                   (apply str))]
      (debugf "reload: %s" ret)
      [::response/ok ret])))

;;;; async push

(defn broadcast!
  [msg]
  (let [msg (format "%s\n  %s" (str (java.util.Date.)) msg)]
    (swap! msgs conj msg)
    (debugf "@msgs: %s" @msgs)
    (doseq [uid (:any @connected-uids)]
      (chsk-send! uid [:mt2/broadcast msg]))))

;;;; Sente event handlers
;;; same with client?

(defmulti -event-msg-handler
  "Multimethod to handle Sente `event-msg`s"
  :id) ; Dispatch on event-id


(defn event-msg-handler
  "Wraps `-event-msg-handler` with logging, error catching, etc."
  [{:as ev-msg :keys [id ?data event]}]
  (debugf "event-msg-handler: id:%s :data:%s event:%s"
          id ?data event)
  (-event-msg-handler ev-msg))

(defmethod -event-msg-handler :default
  [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
  (let [session (:session ring-req)
        uid     (:uid     session)]
    (debugf "Unhandled event: %s" event)
    (when ?reply-fn
      (?reply-fn {:umatched-event-as-echoed-from-server event}))))

(defmethod -event-msg-handler :mt2/msg
  [ev-msg]
  (let [{:keys [?data]} ev-msg]
    (debugf ":mt2/msg: %s" ?data)
    (broadcast! ?data)))

;;; sente server loop

(sente/start-server-chsk-router! ch-chsk event-msg-handler)
