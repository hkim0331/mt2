(ns mt2.client
  (:require
   [cljs.core.async :as async  :refer (<! >! put! chan)]
   [clojure.string  :as str]
   [taoensso.encore :as encore :refer-macros (have have?)]
   [taoensso.sente  :as sente  :refer (cb-success?)]
   [taoensso.timbre :as timbre :refer-macros (debugf infof warnf errorf)])
  (:require-macros
   [cljs.core.async.macros :as asyncm :refer (go go-loop)]))

(timbre/set-level! :debug)

;(js/console.log "FIXME")
;(js/alert "under construction")

(def output-el (.getElementById js/document "output"))

(defn ->output! [fmt & args]
  (let [msg (apply encore/format fmt args)]
    (aset output-el "value" (str msg "\n" (.-value output-el)))
    (aset output-el "scrollTop" 0)))

;(->output! "ClojureScript appears to have loaded correctly.")

(def message-el (.getElementById js/document "message"))
;;(aset message-el "value" "are you OK?")

;;;; Sente channel socket client
;;; same with server?

(def ?csrf-token
  (when-let [el (.getElementById js/document "sente-csrf-token")]
    (.getAttribute el "data-csrf-token")))

;(if ?csrf-token
;  (->output! "CSRF token detected in HTML, great!")
;  (->output! "CSRF token NOT detected in HTML, default Sente config will reject requests"))

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
    (when (seq (second ?data))
      (->output! "%s\n  %s" now (second ?data)))))

(defmethod -event-msg-handler :chsk/handshake
  [{:as ev-msg :keys [?data]}]
  (let [[?uid ?csrf-token ?handshake-data] ?data]
    (->output! "Handshake:OK")))

;;;; Sente event router (our `event-msg-handler` loop)

;(defonce router_ (atom nil))
;(defn  stop-router! [] (when-let [stop-f @router_] (stop-f)))
;(defn start-router! []
;  (stop-router!)
;  (reset! router_
;    (sente/start-client-chsk-router!
;      ch-chsk event-msg-handler)))


;;;; UI events

(when-let [target-el (.getElementById js/document "send")]
  (.addEventListener target-el "click"
                     (fn [ev]
                       (let [msg (str (.-value message-el))]
                         (chsk-send! [:mt2/msg msg])
                         (aset message-el "value" "")))))

(when-let [target-el (.getElementById js/document "clear")]
  (.addEventListener target-el "click"
                     (fn [ev]
                       (aset output-el "value" ""))))

;; start sente client router
(sente/start-client-chsk-router! ch-chsk event-msg-handler)

