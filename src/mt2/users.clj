(ns mt2.users
 (:require
  [integrant.core :as ig]
  [integrant.repl.state :refer [system]]
  [clojure.java.jdbc :as jdbc]))

;; (defn db []
;;  (-> system
;;      (ig/find-derived-1 :duct.database/sql)
;;      val
;;      :spec))

(defn find-user [{db :spec} login]
  (first (jdbc/find-by-keys db :users {:login login})))
