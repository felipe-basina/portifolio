(ns authorizer.integration-test.storage-client-integration-test
  (:require [authorizer.storage.protocol.storage :as storage]
            [authorizer.storage.storage-client :as storage-client]
            [authorizer.util.violations :as violations]
            [clojure.test :refer :all]))

(deftest test-save-and-get-new-account
  (testing "integration test : save and get new account"
    (let [storage-client (storage-client/storage-client)
          new-account {:active-card true :available-limit 100}]
      (storage/create-new-account! storage-client new-account)
      (let [saved-account (storage/get-account storage-client)]
        (is (not-empty saved-account))
        (is (contains? saved-account :active))
        (is (:active saved-account))
        (is (contains? saved-account :available-limit))
        (is (= 100 (:available-limit saved-account)))
        (is (contains? saved-account :violations))
        (is (empty? (:violations saved-account)))
        (is (vector? (:violations saved-account)))))))

(deftest test-add-and-get-new-transaction
  (testing "integration test : add and get new transaction"
    (let [storage-client (storage-client/storage-client)
          new-transaction {:merchant "Habib's" :amount 90 :time "2019-02-13T11:00:00.000Z"}]
      (storage/add-new-transaction! storage-client new-transaction)
      (let [saved-transaction (storage/get-transactions storage-client)]
        (is (list? saved-transaction))
        (is (= 1 (count saved-transaction)))
        (let [transaction (first saved-transaction)]
          (is (contains? transaction :merchant))
          (is (= "Habib's" (:merchant transaction)))
          (is (contains? transaction :amount))
          (is (= 90 (:amount transaction)))
          (is (contains? transaction :time))
          (is (= "2019-02-13T11:00:00.000Z" (:time transaction))))))))

(deftest test-update-available-amount
  (testing "integration test : update account available amount"
    (let [storage-client (storage-client/storage-client)
          new-account {:active-card true :available-limit 100}
          new-transaction {:merchant "Habib's" :amount 90 :time "2019-02-13T11:00:00.000Z"}]
      (storage/create-new-account! storage-client new-account)
      (storage/add-new-transaction! storage-client new-transaction)
      (storage/update-available-amount! storage-client new-transaction)
      (let [account-state (storage/get-account storage-client)]
        (is (not-empty account-state))
        (is (= 10 (:available-limit account-state)))))))

(deftest test-add-transaction-violation-account-with-multiple-violations
  (testing "integration test : add transaction violation, account with multiple violations"
    (let [storage-client (storage-client/storage-client)]
      (storage/add-transaction-violation! storage-client violations/CARD_NOT_ACTIVE)
      (storage/add-transaction-violation! storage-client violations/INSUFFICIENT_LIMIT)
      (let [account-state (storage/get-account storage-client)]
        (is (not-empty account-state))
        (is (map? account-state))
        (is (contains? account-state :violations))
        (is (= 2 (count (:violations account-state))))
        (loop [violations (:violations account-state)]
          (when (not-empty violations)
            (is (some #(= (first violations) %)
                      [violations/CARD_NOT_ACTIVE violations/INSUFFICIENT_LIMIT]))))))))

(deftest test-reset-violations
  (testing "integration test : reset existing violations"
    (let [storage-client (storage-client/storage-client)
          account {:active-card true :available-limit 100}]
      (storage/create-new-account! storage-client account)
      (storage/add-transaction-violation! storage-client violations/INSUFFICIENT_LIMIT)
      (storage/add-transaction-violation! storage-client violations/CARD_NOT_ACTIVE)
      (let [account-state (storage/get-account storage-client)]
        (is (not-empty account-state))
        (is (contains? account-state :violations))
        (is (not-empty (:violations account-state)))
        (is (= 2 (count (:violations account-state))))
        (storage/reset-violations! storage-client)
        (let [account-state (storage/get-account storage-client)]
          (is (not-empty account-state))
          (is (contains? account-state :violations))
          (is (empty? (:violations account-state))))))))