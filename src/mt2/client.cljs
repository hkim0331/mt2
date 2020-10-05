(ns mt2.client
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
   [taoensso.encore :as encore :refer-macros (have)]
   [taoensso.sente  :as sente]
   [taoensso.timbre :as timbre]
   [clojure.string  :as string]
   [cljs-http.client :as http]
   [cljs.core.async :refer [<!]]))

(def MAX_MSG_LEN 70)

(def messages (atom []))

(def output-el (.getElementById js/document "output"))

(defn ->output! [fmt & args]
  (let [msg (apply encore/format fmt args)]
    (aset output-el "value" (str msg "\n" (.-value output-el)))
    (aset output-el "scrollTop" 0)
    (swap! messages conj msg)))

(def message-el (.getElementById js/document "message"))

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
  (timbre/debugf "id: %s, ?data: %s, event: %s" id ?data event)
  (-event-msg-handler ev-msg))

(defmethod -event-msg-handler :default
  [{:as ev-msg :keys [event]}]
  (->output! "Unhandled event: %s" event))

(defmethod -event-msg-handler :chsk/state
  [{:as ev-msg :keys [?data]}]
  (let [[old-state-map new-state-map] (have vector? ?data)]
    (when (:first-open? new-state-map)
      #_(->output! "state changed: %s" new-state-map)
      (timbre/debugf "state changed: %s" new-state-map))))

(defmethod -event-msg-handler :chsk/recv
  [{:as ev-msg :keys [?data]}]
  (when-not (= :chsk/ws-ping (first ?data))
    (->output! (second ?data))))

(defmethod -event-msg-handler :chsk/handshake
  [{:as ev-msg :keys [?data]}]
  (let [[?uid ?csrf-token ?handshake-data] ?data]
    (->output! "micro twitter started.")))

;;;; UI events

(when-let [target-el (.getElementById js/document "send")]
  (.addEventListener target-el "click"
                     (fn [ev]
                       (let [msg (str (.-value message-el))]
                         (when (< 0 (count msg) MAX_MSG_LEN)
                           (chsk-send! [:mt2/msg msg])
                           (aset message-el "value" ""))))))

(when-let [target-el (.getElementById js/document "clear")]
  (.addEventListener target-el "click"
                     (fn [ev]
                       (aset output-el "value" ""))))


;;(when-let [target-el (.getElementById js/document "resume")]
;;  (.addEventListener target-el "click"
;;                     (fn [ev]
;;                       (->output!
;;                        (string/join "\n" (reverse @messages))))))

;; ws 以外で通信しちゃダメかい。
(when-let [target-el (.getElementById js/document "reload")]
  (.addEventListener
   target-el
   "click"
   (fn [ev]
    (go (let [msgs (<! (http/get "/reload"))]
          ;;(js/console.log (:body msgs))
          (->output! (:body msgs)))))))

;;;; start sente client router

(sente/start-client-chsk-router! ch-chsk event-msg-handler)
