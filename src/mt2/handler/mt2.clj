(ns mt2.handler.mt2
  (:require
    [ataraxy.core     :as ataraxy]
    [ataraxy.response :as response]
    [clojure.java.io  :as io]
    [taoensso.timbre  :refer [debug infof]]
    [hiccup.page      :as hiccup]
    [integrant.core   :as ig]
    [ring.middleware.anti-forgery :as anti-forgery]))


(defn page
  [& contents]
  [::response/ok
   (hiccup/html5
     [:head
      [:meta {:charset "utf-8"}]
      [:meta {:name "viewport"
              :content "width=device-width, initial-scale=1, shrink-to-fit=no"}]
      [:title "private micro twitter"]
      (hiccup/include-css
        "https://stackpath.bootstrapcdn.com/bootstrap/4.5.0/css/bootstrap.min.css")]
     [:body
      (let [csrf-token (force anti-forgery/*anti-forgery-token*)]
        [:div#sente-csrf-token {:data-csrf-token csrf-token}])
      [:div.container-fluid
       [:h2 "micro Twritter"]
       contents
       [:hr]
       [:div "hkimura 2020-09-27."]
       [:script {:src "/js/main.js"}]]])])


(defmethod ig/init-key :mt2.handler.mt2/index [_ options]
  (fn [{[_] :ataraxy/result}]
    ; (debug "index")
    ; (infof "info %s" (java.util.Date.))
    (page
      [:p [:input#message {:placeholder "type your message"}]
          [:button#send {:type "button"} "send"]]
      [:p [:textarea#output {:style "width:100%; height 400px;"}]]
      [:p [:button#clear {:type "button"} "clear"]])))
