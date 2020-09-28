(ns mt2.client
  (:require
   [taoensso.encore :as encore :refer-macros (have have?)]
   [taoensso.sente  :as sente]))

(def MAX_MSG_LEN 70)

(def output-el (.getElementById js/document "output"))

(defn ->output! [fmt & args]
  (let [msg (apply encore/format fmt args)]
    (aset output-el "value" (str msg "\n" (.-value output-el)))
    (aset output-el "scrollTop" 0)))

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
  (-event-msg-handler ev-msg))

(defmethod -event-msg-handler :default
  [{:as ev-msg :keys [event]}]
  (->output! "Unhandled event: %s" event))

(defmethod -event-msg-handler :chsk/state
  [{:as ev-msg :keys [?data]}]
  (let [[old-state-map new-state-map] (have vector? ?data)]
    (if (:first-open? new-state-map)
      (->output! "READY!")
      (->output! "state changed: %s" new-state-map))))

(defmethod -event-msg-handler :chsk/recv
  [{:as ev-msg :keys [?data]}]
  (let [now (-> (js/Date.)
                str
                (subs 0 25))]
    (->output! "%s\n  %s" now (second ?data))))

(defmethod -event-msg-handler :chsk/handshake
  [{:as ev-msg :keys [?data]}]
  (let [[?uid ?csrf-token ?handshake-data] ?data]
    (->output! "Handshake:OK")))

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

;;;; start sente client router

(sente/start-client-chsk-router! ch-chsk event-msg-handler)

