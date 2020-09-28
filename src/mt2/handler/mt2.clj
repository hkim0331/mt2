(ns mt2.handler.mt2
  (:require
    [ataraxy.core     :as ataraxy]
    [ataraxy.response :as response]

    [taoensso.timbre  :refer [debugf infof warnf errorf]]
    [taoensso.sente   :as sente]

    [hiccup.page      :as hiccup]

    [org.httpkit.server :as http-kit]
    [taoensso.sente.server-adapters.http-kit :refer (get-sch-adapter)]

    [integrant.core   :as ig]
    [ring.middleware.anti-forgery :as anti-forgery]))


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
      [:div.container-fluid
       [:h2 "micro Twritter"]
       contents
       [:hr]
       [:div "hkimura 2020-09-27."]
       [:script {:src "/js/main.js"}]]])])


(defmethod ig/init-key :mt2.handler.mt2/index [_ options]
  (fn [{[_] :ataraxy/result}]
    (debugf "index")
    (page
      [:p [:input#message {:placeholder "type your message"}]
          [:button#send {:type "button"} "send"]]
      [:p [:textarea#output {:style "width:100%; height: 400px;"}]]
      [:p [:button#clear {:type "button"} "clear"]])))

(defmethod ig/init-key :mt2.handler.mt2/get-chsk [_ options]
  (fn [req]
    (ring-ajax-get-or-ws-handshake req)))


(defmethod ig/init-key :mt2.handler.mt2/post-chsk [_ options]
  (fn [req]
    (ring-ajax-post req)))


;;;; async push

(defn broadcast!
  [msg]
  (debugf "broadcast %s" msg)
  (doseq [uid (:any @connected-uids)]
    (chsk-send! uid [:mt2/push msg])))

;;;; Sente event handlers
;;; same with client?

(defmulti -event-msg-handler
  "Multimethod to handle Sente `event-msg`s"
  :id) ; Dispatch on event-id


(defn event-msg-handler
  "Wraps `-event-msg-handler` with logging, error catching, etc."
  [{:as ev-msg :keys [id ?data event]}]
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
  (debugf ":mt2/msg: %s" (:?data ev-msg))
  (broadcast! (:?data ev-msg)))

; (defmethod -event-msg-handler :example/toggle-broadcast
;   [{:as ev-msg :keys [?reply-fn]}]
;   (let [loop-enabled? (swap! broadcast-enabled?_ not)]
;     (?reply-fn loop-enabled?)))

;;;
