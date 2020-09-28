(ns mt2.handler.example
  (:require
   [ataraxy.core :as ataraxy]
   [ataraxy.response :as response]
   [clojure.java.io :as io]
   [integrant.core :as ig]))

(defmethod ig/init-key :mt2.handler/example [_ options]
  (fn [{[_] :ataraxy/result}]
    [::response/ok (io/resource "mt2/handler/example/example.html")]))
