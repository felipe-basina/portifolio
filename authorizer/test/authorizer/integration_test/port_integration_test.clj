(ns authorizer.integration-test.port-integration-test
  (:require [authorizer.account-state-validation :as asv]
            [authorizer.port :refer :all]
            [authorizer.storage.storage-client :as storage-client]
            [authorizer.util.violations :as violations]
            [clojure.test :refer :all]))

(deftest test-authorize-account
  (testing "integration test : authorizes create an account"
    (let [storage (storage-client/storage-client)
          account-state (authorize
                          "{\"account\": {\"active-card\": true, \"available-limit\": 100}}"
                          storage)]
      (asv/check-account-state-as-str account-state "100"))))

(deftest test-authorize-transaction
  (testing "integration test : authorizes add a transaction"
    (let [storage (storage-client/storage-client)]
      (authorize
        "{\"account\": {\"active-card\": true, \"available-limit\": 100}}"
        storage)
      (let [account-state (authorize
                            "{\"transaction\": {\"merchant\": \"Burger King\", \"amount\": 20, \"time\": \"2019-02-13T10:00:00.000Z\"}}"
                            storage)]
        (asv/check-account-state-as-str account-state "80")))))

(deftest test-authorize-transaction-with-account-not-initialized-violation
  (testing "integration test : authorizes add a transaction, with account not initialized violation"
    (let [storage (storage-client/storage-client)
          account-state (authorize
                          "{\"transaction\": {\"merchant\": \"Burger King\", \"amount\": 20, \"time\": \"2019-02-13T10:00:00.000Z\"}}"
                          storage)]
      (asv/check-account-state-as-str account-state "0" violations/ACCOUNT_NOT_INITIALIZED))))

(deftest test-authorize-transaction-with-active-card-violation
  (testing "integration test : authorizes add a transaction, with active card violation"
    (let [storage (storage-client/storage-client)]
      (authorize
        "{\"account\": {\"active-card\": false, \"available-limit\": 100}}"
        storage)
      (let [account-state (authorize
                            "{\"transaction\": {\"merchant\": \"Burger King\", \"amount\": 20, \"time\": \"2019-02-13T10:00:00.000Z\"}}"
                            storage)]
        (asv/check-account-state-as-str account-state "100" violations/CARD_NOT_ACTIVE)))))

(deftest test-authorize-transaction-with-multiple-violations
  (testing "integration test : authorizes add a transaction, with multiples violations"
    (let [storage (storage-client/storage-client)]
      (authorize "{\"account\": {\"active-card\": false, \"available-limit\": 0}}" storage)
      (let [account-state (authorize
                            "{\"transaction\": {\"merchant\": \"Burger King\", \"amount\": 20, \"time\": \"2019-02-13T10:00:00.000Z\"}}"
                            storage)]
        (asv/check-account-state-as-str account-state "0" violations/CARD_NOT_ACTIVE violations/INSUFFICIENT_LIMIT)))))