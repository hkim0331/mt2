(ns mt2.users
  (:require
   [clojure.java.jdbc :as jdbc]))

(defn find-user [{ds :spec} login]
  (first (jdbc/find-by-keys ds :users {:login login})))

;; (defn find-admins [{ds :spec}]
;;   (jdbc/query ds "select login from users where admin=true"))
