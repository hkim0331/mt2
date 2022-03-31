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
   #_[taoensso.timbre  :as timbre]))

;;
(defn unauth-handler
  [req _]
  ;; ここで (:session req) = {} がおかしい。
  ;;(timbre/debug "unauth-hnandler (:session req)" (:session req))
  (if (authenticated? req)
    [::response/found "/error"]
    [::response/found "/login"]))

(def auth-backend
  (session-backend {:unauthorized-handler unauth-handler}))

;; (defn probe [handler]
;;   (fn [req]
;;     (handler req)))

(defmethod ig/init-key :mt2.middleware/auth [_ _]
  (fn [handler]
    (-> handler
        (restrict {:handler authenticated?})
        (wrap-authorization  auth-backend)
        (wrap-authentication auth-backend))))
