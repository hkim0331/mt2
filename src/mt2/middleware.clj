(ns mt2.middleware
  (:require
   [ataraxy.response :as response]
   [buddy.auth :refer [authenticated?]]
   [buddy.auth.accessrules :refer [restrict]]
   [buddy.auth.backends.session :refer [session-backend]]
   [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
   #_[environ.core :refer [env]]
   [integrant.core :as ig]
   #_[ring.middleware.session :refer [wrap-session]]
   [taoensso.timbre  :as timbre]))

;;
(defn unauth-handler
  [req meta]
  ;; ここで (:session req) = {} がおかしい。
  (timbre/debug "unauth-hnandler (:session req)" (:session req))
  (if (authenticated? req)
    [::response/found "/error"]
    [::response/found "/login"]))

(def auth-backend
  (session-backend {:unauthorized-handler unauth-handler}))

(defn probe [handler]
  (fn [req]
    (timbre/info "probe req:" req)
    (handler req)))

(defmethod ig/init-key ::auth [_ _]
  (fn [handler]
    (-> handler
        (restrict {:handler authenticated?})
        probe
        (wrap-authorization  auth-backend)
        (wrap-authentication auth-backend))))
