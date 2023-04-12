(ns mt2.users
  (:require
   #_[clojure.java.jdbc :as jdbc]
   [hato.client :as hc]))

;; (defn find-user [{ds :spec} login]
;;   (first (jdbc/find-by-keys ds :users {:login login})))

(defn find-user [_ login]
  (let [url (str "https://l22.melt.kyutech.ac.jp/api/user/" login)
        body (:body (hc/get url {:as :json}))]
    body))

(find-user nil "hkimura")

;; (defn find-admins [{ds :spec}]
;;   (jdbc/query ds "select login from users where admin=true"))
;; u {:uhour "*",
;;    :password "bcrypt+sha512$ce9b5fd9ebf015a8ae981414eae7a860$12$9c9caf98ed8124923abbd7765e6a0c6419789bb10bb81105", 
;;    :name "木村　広",
;;    :is_admin true,
;;    :login "hkimura",
;;    :updated_at #inst "2022-10-03T07:11:10.238583000-00:00",
;;    :id 197, :created_at #inst "2022-10-03T07:11:10.238583000-00:00", 
;;    :sid "999A0001"}

(comment
  (let [url "https://l22.melt.kyutech.ac.jp/api/user/hkimura"
        body (:body (hc/get url {:as :json}))]
    body)
  :rcf)
