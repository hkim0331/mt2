(ns mt2.middleware
  (:require
   [ataraxy.response :as response]
   [buddy.auth :refer [authenticated?]]
   [buddy.auth.accessrules :refer [restrict]]
   [buddy.auth.backends.session :refer [session-backend]]
   [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
   #_[environ.core :refer [env]]
   [integrant.core :as ig]
   [ring.middleware.session :refer [wrap-session]]
   [taoensso.timbre  :as timbre]))

(defn unauth-handler
  [req meta]
  (timbre/debug "unauth-hnandler:req:" (:session req))
  (if (authenticated? req)
    [::response/found (:uri req)]
    [::response/found "/login"]))

(def auth-backend
  (session-backend {:unauthorized-handler unauth-handler}))

(defn print-req [handler]
  (fn [req]
    (timbre/info "req:" req)
    (handler req)))

(defmethod ig/init-key ::auth [_ _]
  (fn [handler]
    (-> handler
        (restrict {:handler authenticated?})
        (wrap-authorization  auth-backend)
        (wrap-authentication auth-backend))))
