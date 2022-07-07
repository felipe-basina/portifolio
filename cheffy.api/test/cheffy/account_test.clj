(ns cheffy.account-test
  (:require [clojure.test :refer :all]
            [cheffy.test-system :as ts]))

(defn account-fixture
  [f]
  (let [email "account-tests@cheffy.app"
        password "Testing#00"]
    (ts/create-auth0-user {:connection "Username-Password-Authentication"
                           :email      email
                           :password   password})
    (reset! ts/token (ts/get-test-token email))
    (f)
    (reset! ts/token nil)))

(use-fixtures :once account-fixture)

(deftest account-tests

  (testing "Create user account"
    (let [{:keys [status body]} (ts/test-endpoint :post "/v1/accounts" {:auth true})]
      (is (= status 201))
      (is (nil? body))))

  (testing "Update user role account"
    (let [{:keys [status]} (ts/test-endpoint :put "/v1/accounts" {:auth true})]
      (is (= status 204))))

  (testing "Delete use r account"
    (let [{:keys [status]} (ts/test-endpoint :delete "/v1/accounts" {:auth true})]
      (is (= status 204)))))