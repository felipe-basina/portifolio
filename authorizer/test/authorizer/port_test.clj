(ns authorizer.port-test
  (:require [authorizer.account-state-validation :as asv]
            [authorizer.controller :as controller]
            [authorizer.port :as port]
            [clojure.test :refer :all]))

(deftest test-authorize-create-account
  (testing "authorizes creating a new account"
    (with-redefs [controller/process-input (fn [_ _] {:active true :available-limit 100 :violations []})]
      (let [new-account "{\"account\": {\"active-card\": true, \"available-limit\": 100}}"
            storage {}
            account-state (port/authorize new-account storage)]
        (asv/check-account-state-as-str account-state "100")))))

(deftest test-authorize-add-transaction
  (testing "authorizes add transaction, considering an existing account with :available-limit 100"
    (with-redefs [controller/process-input (fn [_ _] {:active true :available-limit 80 :violations []})]
      (let [new-transaction "{\"transaction\": {\"merchant\": \"Burger King\", \"amount\": 20, \"time\": \"2019-02-13T10:00:00.000Z\"}}"
            storage {}
            account-state (port/authorize new-transaction storage)]
        (asv/check-account-state-as-str account-state "80")))))
