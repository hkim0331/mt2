(ns mt2.client
  (:require
    [clojure.string  :as str]
    [cljs.core.async :as async  :refer (<! >! put! chan)]
    [taoensso.encore :as encore :refer-macros (have have?)]
    [taoensso.timbre :as timbre :refer-macros (debugf infof warnf errorf)]
    [taoensso.sente  :as sente  :refer (cb-success?)]))

(js/console.log "FIXME")
(js/alert "under construction")

(def output-el (.getElementById js/document "output"))
(defn ->output! [fmt & args]
  (let [msg (apply encore/format fmt args)]
    (timbre/debugf msg)
    (aset output-el "value" (str "• " (.-value output-el) "\n" msg))
    (aset output-el "scrollTop" (.-scrollHeight output-el))))

(->output! "ClojureScript appears to have loaded correctly.")

(def message-el (.getElementById js/document "message"))
(aset message-el "value" "are you OK?")

;;;; Sente channel socket client

(def ?csrf-token
  (when-let [el (.getElementById js/document "sente-csrf-token")]
    (.getAttribute el "data-csrf-token")))

(if ?csrf-token
  (->output! "CSRF token detected in HTML, great!")
  (->output! "CSRF token NOT detected in HTML, default Sente config will reject requests"))

;;; not yet

;;;; UI events

(when-let [target-el (.getElementById js/document "send")]
  (.addEventListener target-el "click"
    (fn [ev]
      (let [msg (str (.-value message-el))]
        (js/alert (str "clicked send button, " msg))
        (aset message-el "value" "")))))

(when-let [target-el (.getElementById js/document "clear")]
  (.addEventListener target-el "click"
    (fn [ev]
      (timbre/debug "debug: clicked clear button.")
      (timbre/debugf "clicked clear button")
      (aset output-el "value" ""))))
