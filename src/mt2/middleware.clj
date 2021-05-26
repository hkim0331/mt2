(ns mt2.middleware
  (:require
   [ataraxy.response :as response]
   [buddy.auth :refer [authenticated?]]
   [buddy.auth.accessrules :refer [restrict]]
   [buddy.auth.backends.session :refer [session-backend]]
   [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
   #_[environ.core :refer [env]]
   [integrant.core :as ig]))

(defn unauth-handler
  [req meta]
  (if (authenticated? req)
    [::response/found (:uri req)]
    [::response/found "/login?next=/"]))

; (defn authfn [_ {:keys [username password]}]
;    (and (= username (or (env :mt2-user)     "hkim"))
;         (= password (or (env :mt2-password) "214"))))

(def auth-backend
  (session-backend {:unauthorized-handler unauth-handler}))

(defmethod ig/init-key ::auth [_ _]
  (fn [handler]
    (-> handler
        (restrict {:handler authenticated?})
        (wrap-authorization  auth-backend)
        (wrap-authentication auth-backend))))
