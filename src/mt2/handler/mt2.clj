(ns mt2.handler.mt2
  (:require
   [ataraxy.response :as response]
   [buddy.hashers :as hashers]
   [clj-time.local :as l]
   [environ.core :refer [env]]
   [hiccup.form :refer [form-to text-field password-field hidden-field
                        submit-button]]
   [hiccup.page :as hiccup]
   [integrant.core :as ig]
   [mt2.users :as users]
   [ring.middleware.anti-forgery :as anti-forgery]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [ring.util.response :refer [redirect]]
   [taoensso.sente :as sente]
   [taoensso.sente.server-adapters.http-kit :refer (get-sch-adapter)]
   [taoensso.timbre  :as timbre :refer [debug info]]))

(def version "1.2.7-SNAPSHOT")
(def version-string (str "hkimura, " version))

(reset! sente/debug-mode?_ false)

(def msgs (atom []))

(defn admin? [req]
  ;; user is a keyword, admin is a string.
  ;; compare them after coersing user into string.
  ;; (find-user db login)の db を渡すのが めんどくさい。
  (let [user  (name (get-in req [:session :identity]))
        admin (env :mt2-admin)]
    (= user admin)))

;;; from sente official example
(let [packer :edn ; Default packer, a good choice in most cases
      chsk-server (sente/make-channel-socket-server!
                   (get-sch-adapter) {:packer packer})
      {:keys [ch-recv send-fn connected-uids
              ajax-post-fn ajax-get-or-ws-handshake-fn]} chsk-server]
  (def ring-ajax-post                ajax-post-fn)
  (def ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn)
  (def ch-chsk                       ch-recv) ; ChannelSocket's receive channel
  (def chsk-send!                    send-fn) ; ChannelSocket's send API fn
  (def connected-uids                connected-uids)) ; Watchable, read-only atom

(add-watch connected-uids :connected-uids
           (fn [_ _ old new]
             (when (not= old new)
               (debug "Connected uids change: " new))))

(defn page
  [& contents]
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
      [:h2 "micro twitter "]
      contents
      [:script {:src "/js/main.js"}]]]))

;;; login/logout
(defmethod ig/init-key :mt2.handler.mt2/login [_ _]
  (fn [{[_] :ataraxy/result :as req}]
    (timbre/debug "req:flash" (:flash req))
    [::response/ok
     (page
      [:h2 "Log in"]
      [:div {:style "color:red;"} (:flash req)]
      (form-to
       [:post "/login"]
       (anti-forgery-field)
       (hidden-field "next" "/")
       (text-field {:placeholder "username"} "username")
       (password-field {:placeholder "password"} "password")
       (submit-button {:class "btn btn-primary btn-sm"} "login"))
      [:hr]
      [:p version-string])]))

(defmethod ig/init-key :mt2.handler.mt2/login-post [_ {:keys [db]}]
  (fn [{[_ {:strs [username password]}] :ataraxy/result}]
    (let [u (users/find-user db username)]
      (if (hashers/check password (:password u))
        (do
          (info "login" username)
          (-> (redirect "/")
              (assoc-in [:session :identity] (keyword username))))
        (do
          (info "login failure" username password)
          (-> (redirect "/login")
              (assoc :flash "login failure")))))))

(defmethod ig/init-key :mt2.handler.mt2/logout [_ _]
  (fn [_]
    (-> (redirect "/login")
        (assoc :session {}))))

;;; ring event handler

(defmethod ig/init-key :mt2.handler.mt2/get-chsk [_ _]
  (fn [req]
    (ring-ajax-get-or-ws-handshake req)))

(defmethod ig/init-key :mt2.handler.mt2/post-chsk [_ _]
  (fn [req]
    (ring-ajax-post req)))

(defmethod ig/init-key :mt2.handler.mt2/index [_ _]
  (fn [{[_] :ataraxy/result :as req}]
    [::response/ok
     (page
      [:input
       {:id "login"
        :type "hidden"
        :name "login"
        :value (name (get-in req [:session :identity]))}]
      [:p
       [:textarea#output {:style "width:100%; height:380px; color:red;"
                          :placeholder version-string
                          :disabled "disabled"}]]
      [:p
       [:div.row
         [:div.col-10
          [:input#message
           {:placeholder "type your message"
            :style "width: 100%;"}]]
         [:div.col-1
          [:button#send
           {:type "button"
            :class "btn btn-primary btn-sm"}
           "send"]]]]
      [:p
       [:button#clear
        {:type "button" :class "btn btn-primary btn-sm"} "clear"]
       " "
       [:button#reload
        {:type "button" :class "btn btn-primary btn-sm"} "reload"]
       " "
       (when (admin? req)
        [:button#reset
         {:type "button" :class "btn btn-danger btn-sm"
          :onclick "location.href='/reset'"}
         "reset"])
       " "
       [:button#logout
        {:type "button" :class "btn btn-warning btn-sm"
         :onclick "location.href='/login'"}
        "logout"]])]))


(defn msgs->str []
  (->> @msgs
       (interpose "\n")
       (apply str)))

(defmethod ig/init-key :mt2.handler.mt2/reload [_ _]
  (fn [_]
    (let [ret (msgs->str)]
      (debug "reload: " ret)
      [::response/ok ret])))


(defn save
  "msgs をファイル log/<localtime>.logに書き出す。"
  [str]
  (let [dest (format "logs/%s.log" (l/local-now))]
    (try
      (spit dest str)
      (catch Exception e
       (page (str "<h1>error</h1><p>" (.getMessage e)))))))

;; reset = save + reset!
(defmethod ig/init-key :mt2.handler.mt2/reset [_ _]
  (fn [req]
    (if (admin? req)
      (do
        (debug "admin called reset")
        (save (msgs->str))
        (reset! msgs ["*** mtの新しい一週間の始まり***\n"])
        [::response/found "/"])
      (do
        (debug "nomal user called reset")
        [::response/unauthorized
          (page "<h1>Forbidden</h1><p><a href='/'>back</a></p>")]))))


;; reset に save の機能を持たせる。
;; endpoint save は廃止してもよい。
;; 「reset 使え」のメッセージでも出すか？
(defmethod ig/init-key :mt2.handler.mt2/save [_ _]
  (fn [req]
    (when (admin? req)
      (save (msgs->str)))
    [::response/found "/"]))

;;;;
;;;; async push
;;;;
(defn broadcast!
  [msg sender]
  (debug "broadcast! sender" sender)
  (let [msg (if (or (= sender "hkimura"))
              (format "%s\n  %s" (str "🍺 " (java.util.Date.)) msg)
              (format "%s\n  %s" (str (java.util.Date.)) msg))]
    (swap! msgs conj msg)
    (doseq [uid (:any @connected-uids)]
      (chsk-send! uid [:mt2/broadcast {:data msg :sender sender}]))))
;;;
;;; Sente event handlers
;;;
(defmulti -event-msg-handler
  "Multimethod to handle Sente `event-msg`s"
  :id) ; Dispatch on event-id

(defn event-msg-handler
  "Wraps `-event-msg-handler` with logging, error catching, etc."
  [{:as ev-msg :keys [id ?data event]}]
  (-event-msg-handler ev-msg))

(defmethod -event-msg-handler :default
  [{:keys [event id ?data ring-req ?reply-fn send-fn]}]
  (debug "Unhandled event, id: " id
          (when ?reply-fn
            (?reply-fn {:umatched-event-as-echoed-from-server event}))))

;; 0.8.3
(defmethod -event-msg-handler :chsk/ws-ping
  [_]
  (debug ":chsk/ws-ping"))

;; 0.9.3 2021-10-07
(defmethod -event-msg-handler :mt2/msg
  [{:keys [?data ring-req]}]
  (broadcast! ?data (name (get-in ring-req [:session :identity]))))

;;
(defmethod ig/init-key :mt2.handler.mt2/error [_ _]
  (fn [req]
    [::response/ok
     (page
      [:h2 "ERROR"]
      [:p "req:" (str req)])]))

;;; sente server loop
(sente/start-server-chsk-router! ch-chsk event-msg-handler)
