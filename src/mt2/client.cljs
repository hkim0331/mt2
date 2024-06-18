(ns mt2.client
  (:require
   [cljs-bach.synthesis :as b]
   [cljs-http.client :as http]
   [cljs.core.async :refer [<!]]
   ;;[clojure.string :refer [replace-first]]
   [taoensso.encore :as encore :refer-macros (have)]
   [taoensso.sente  :as sente]
   [taoensso.timbre :as timbre])
  (:require-macros
   [cljs.core.async.macros :refer [go]]))

(defonce context (b/audio-context))

(defn ping
  "着信音を鳴らす"
  [freq]
  (b/connect->
   (b/square freq)
   (b/percussive 0.01 0.4)
   (b/gain 0.1)))

;; do not work yet. url?
; (defn play-mp3 [url]
;   (let [mp3 (b/connect-> (b/sample url)
;                          (b/gain 0.5)
;                          b/destination)
;     (b/run-with mp3
;                 context
;                 (b/current-time b/audio-context)
;                 1.0)))

(def messages (atom []))

(def MAX_MSG_LEN 140)

(def output-el  (.getElementById js/document "output"))
(def message-el (.getElementById js/document "message"))

(defn login-name
  []
  (-> (.getElementById js/document "login")
      (.getAttribute "value")))

;; changed the order of display messages 0.8.2
(defn ->output!
  [msg & [sender]]
  (timbre/debug "sender" sender "login" (login-name) "msg" msg)
  (aset output-el
        "value"
        (str (.-value output-el) "\n"
             ;;2024-06-19
             (when (= "hkimura" (login-name)) (str sender " ")
             (when (= sender (login-name)) " 🙋‍♀️ ")
             msg))
  (aset output-el "scrollTop" (.-scrollHeight output-el))
  (swap! messages conj msg))

;;;; Sente channel socket client

(def ?csrf-token
  (when-let [el (.getElementById js/document "sente-csrf-token")]
    (.getAttribute el "data-csrf-token")))

(when-not ?csrf-token
  (->output! "CSRF token NOT detected in HTML, default Sente config will reject requests"))

(let [rand-chsk-type :auto
      packer :edn
      {:keys [chsk ch-recv send-fn state]}
      (sente/make-channel-socket-client!
       "/chsk" ; Must match server Ring routing URL
       ?csrf-token
       {:type   rand-chsk-type
        :packer packer})]
  (def chsk       chsk)
  (def ch-chsk    ch-recv) ; ChannelSocket's receive channel
  (def chsk-send! send-fn) ; ChannelSocket's send API fn
  (def chsk-state state))  ; Watchable, read-only atom

;;; Sente event handlers

(defmulti -event-msg-handler
  "Multimethod to handle Sente `event-msg`s"
  :id) ; Dispatch on event-id

(defn event-msg-handler
  "Wraps `-event-msg-handler` with logging, error catching, etc."
  [{:as ev-msg :keys [id ?data event]}]
  (timbre/debug "client.clj: id %s, ?data %s, event %s" id ?data event)
  (-event-msg-handler ev-msg))

(defmethod -event-msg-handler :default
  [{:keys [event]}]
  (->output! (str "Unhandled event: " event)))

(defmethod -event-msg-handler :chsk/state
  [{:keys [?data]}]
  (let [[old-state-map new-state-map] (have vector? ?data)]
    (timbre/debug old-state-map)
    (when (:first-open? new-state-map)
      (timbre/debugf "state changed: %s" new-state-map))))

(defmethod -event-msg-handler :chsk/recv
  [{:keys [?data]}]
  (when-not (= :chsk/ws-ping (first ?data))
    (let [data (second ?data)
          msg (:data data)
          sender (:sender data)]
      (timbre/debug "msg" msg "sender" sender)
      (->output! msg sender)
      (-> (ping 440)
          (b/connect-> b/destination)
          (b/run-with context (b/current-time context) 1.0)))))

(defmethod -event-msg-handler :chsk/handshake
  [{:keys [?data]}]
  (let [[?uid ?csrf-token ?handshake-data] ?data]
    (timbre/debug ?uid " " ?csrf-token ?handshake-data)
    (timbre/debug "handshake")))

;;;
;;; UI events
;;;

;; FIXME .{4,} は any の4以上の繰り返し。
(defn- validate? [s]
  (and
   ;; 長さが MAX_MSG_LEN 未満
   (< 0 (count s) MAX_MSG_LEN)
   ;; 繰り返しが 4 未満
   (< (->> s
           (partition-by identity)
           (map count)
           (apply max)) 4)))

(.addEventListener message-el
                   "keydown"
                   (fn [ev]
                     (when (= (.-keyCode ev) 13)
                       (let [msg (str (.-value message-el))]
                         (when (validate? msg)
                           (chsk-send! [:mt2/msg msg])
                           (aset message-el "value" ""))))))

(when-let [target-el (.getElementById js/document "send")]
  (.addEventListener target-el
                     "click"
                     (fn [_]
                       (let [msg (str (.-value message-el))]
                         (when (< 0 (count msg) MAX_MSG_LEN)
                           (chsk-send! [:mt2/msg msg])
                           (aset message-el "value" ""))))))

(when-let [target-el (.getElementById js/document "clear")]
  (.addEventListener target-el
                     "click"
                     (fn [_]
                       (aset output-el "value" ""))))

(when-let [target-el (.getElementById js/document "reload")]
  (.addEventListener target-el
                     "click"
                     (fn [_]
                       (go (let [msgs (<! (http/get "/reload"))]
                             (->output! (:body msgs)))))))

;;;; start sente client router

(sente/start-client-chsk-router! ch-chsk event-msg-handler)
