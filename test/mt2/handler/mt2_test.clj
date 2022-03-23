(ns mt2.handler.mt2-test
  (:require [clojure.test :refer [deftest testing is]]
            [integrant.core :as ig]
            [ring.mock.request :as mock]))
            ;; [mt2.handler.mt2 :as mt2]))

;; (deftest smoke-test
;;   (testing "index page exists"
;;     (let [handler  (ig/init-key :mt2.handler.mt2/index {})
;;           response (handler (mock/request :get "/"))]
;;       (is (= :ataraxy.response/ok (first response)) "response ok"))))
