(ns atol.routes.login-test
  (:require [atol.util :as util]
            [atol.routes.login :as rl]
            [atol.services.login :as sl]
            [atol.services.owner :as so]
            [clojure.test :refer :all]
            [ring.mock.request :refer :all]
            [atol.handler :refer :all]
            [atol.middleware.formats :as formats]
            [muuntaja.core :as m]
            [mount.core :as mount]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defn parse-json [body]
  (m/decode formats/instance "application/json" body))

(use-fixtures
  :once
  (fn [f]
    (-> (mount/start #'atol.config/env
                     #'atol.handler/app-routes))
    (f)))

; Disabling CSRF for testing
(def app-mock
  (-> app
      (wrap-defaults (assoc site-defaults :security false))))

(deftest test-login-init-page
  (testing "check login init page"
    (let [response ((app) (request :get "/"))]
      (util/validate-http-response-commons response 200))))

(deftest test-login
  (testing "check valid login"
    (with-redefs [sl/valid-login? (fn [_] [])
                  so/get-owner-by-email (fn [_] [{:idt 1001}])]
      (let [form-body {:username "username"
                       :password "1234"}
            response ((app) (-> (request :post "/login")
                                (body (pr-str form-body))
                                (content-type "application/x-www-form-urlencoded")))]
        (println "response: " response)
        (is (= 1 1))))))

(deftest test-at
  (let [the-app (app)]
    (pr )
    (is (= 1 1))))


(comment
  (let [response ((app) (-> (request :post "/api/math/plus")
                            (body (pr-str {:x 10, :y 6}))
                            (content-type "application/edn")
                            (header "accept" "application/transit+json")))]
    (is (= 200 (:status response)))
    (is (= {:total 16} (m/decode-response-body response)))))