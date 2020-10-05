(ns mt2.handler.msgs
  (:require
   [ataraxy.response :as response]
   ;; 追加 2020-10-03
   [clojure.java.jdbc :as jdbc]
   [duct.database.sql]
   [integrant.core   :as ig]))

(defprotocol Msgs
  (add-msg [db msg])
  (list-msgs [db]))

(extend-protocol Msgs
  duct.database.sql.Boundary
  (add-msg [{db :spec} msg]
    (let [results (jdbc/insert! db :msgs {:content msg})]
      (-> results ffirst val)))
  (list-msgs [{db :spec}]
    (let [results (jdbc/query db ["select content, timestamp from msgs order by id desc"])]
      results)))

;; FIXME: anti forgery
(defmethod ig/init-key ::add [_ {:keys [db]}]
  (fn [req]
    [::response/ok req]))

(defmethod ig/init-key ::list [_ {:keys [db]}]
  (fn [req]
    (let [ret (list-msgs db)]
      [::response/ok
       (concat
        (map (fn [r] (str (:timestamp r) "\n  " (:content r) "\n"))
             ret))])))

