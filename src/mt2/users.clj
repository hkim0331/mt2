(ns mt2.users
  (:require
   #_[clojure.java.jdbc :as jdbc]
   [hato.client :as hc]))

;; (defn find-user [{ds :spec} login]
;;   (first (jdbc/find-by-keys ds :users {:login login})))

(defn find-user [login]
  (let [url (str "https://l22.melt.kyutech.ac.jp/api/user/" login)
        body (:body (hc/get url {:as :json}))]
    body))
