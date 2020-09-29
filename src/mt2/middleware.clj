(ns mt2.middleware
  (:require
   [integrant.core :as ig]
   [buddy.auth :refer [authenticated?]]
   [buddy.auth.accessrules :refer [restrict]]
   [buddy.auth.backends.httpbasic :refer [http-basic-backend]]
   [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
   [environ.core :refer [env]]))

(defn authfn [_ {:keys [username password]}]
  (and (= username (or (env :mt2-user)     "hkim"))
       (= password (or (env :mt2-password) "214"))))

(def auth-backend (http-basic-backend {:authfn authfn}))

(defmethod ig/init-key ::httpbasic [_ _]
  (fn [handler]
    (-> handler
 (restrict {:handler authenticated?})
 (wrap-authorization  auth-backend)
 (wrap-authentication auth-backend))))
