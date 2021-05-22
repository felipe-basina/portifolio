(ns authorizer.integration-test.controller-integration-test
  (:require [authorizer.account-state-validation :as asv]
            [authorizer.controller :refer :all]
            [authorizer.domain.record :as record]
            [authorizer.storage.storage-client :as storage-client]
            [authorizer.util.violations :as violations]
            [clojure.test :refer :all]))

(defn account-initial-state
  "Helper function to create and validate account initial state"
  [account storage]
  (let [account-state (process-input (record/account-model account) storage)]
    (asv/check-account-state account-state (:available-limit account))))

(deftest test-process-account-as-input
  (testing "integration test : process account as input"
    (let [storage (storage-client/storage-client)]
      (account-initial-state {:active-card true :available-limit 200} storage))))

(deftest test-process-account-as-input-with-account-already-initialized-violation
  (testing "integration test : process account as input, with account already initialized violation"
    (let [storage (storage-client/storage-client)]
      (account-initial-state {:active-card true :available-limit 200} storage)
      (let [recent-state (process-input
                           (record/account-model {:active-card true :available-limit 200})
                           storage)]
        (asv/check-account-state recent-state 200 violations/ACCOUNT_ALREADY_INITIALIZED)))))

(deftest test-process-transaction-as-input
  (testing "integration test : process transaction as input"
    (let [storage (storage-client/storage-client)]
      (account-initial-state {:active-card true :available-limit 200} storage)
      (let [recent-state (process-input
                           (record/transaction-model {:merchant "Giraffas" :amount 22 :time "2019-02-13T10:00:00.000Z"})
                           storage)]
        (asv/check-account-state recent-state 178)))))

(deftest test-process-transaction-as-input-with-account-not-initialized-violation
  (testing "integration test : process transaction as input, with account not initialized violation"
    (let [storage (storage-client/storage-client)
          account-state (process-input (record/transaction-model {:merchant "Giraffas" :amount 22 :time "2019-02-13T10:00:00.000Z"})
                                       storage)]
      (is (not-empty account-state))
      (is (map? account-state))
      (is (not (contains? account-state :active)))
      (is (not (contains? account-state :available-limit)))
      (is (contains? account-state :violations))
      (is (= violations/ACCOUNT_NOT_INITIALIZED (first (:violations account-state)))))))

(deftest test-process-transaction-as-input-with-not-active-card-violation
  (testing "integration test : process transaction as input, with not active card violation"
    (let [storage (storage-client/storage-client)]
      (account-initial-state {:active-card false :available-limit 200} storage)
      (let [recent-state (process-input
                           (record/transaction-model {:merchant "Giraffas" :amount 22 :time "2019-02-13T10:00:00.000Z"})
                           storage)]
        (asv/check-account-state recent-state 200 violations/CARD_NOT_ACTIVE)))))

(deftest test-process-transaction-as-input-with-insufficient-limit-violation
  (testing "integration test : process transaction as input, with insufficient limit violation"
    (let [storage (storage-client/storage-client)]
      (account-initial-state {:active-card true :available-limit 50} storage)
      (let [recent-state (process-input
                           (record/transaction-model {:merchant "Giraffas" :amount 65 :time "2019-02-13T10:00:00.000Z"})
                           storage)]
        (asv/check-account-state recent-state 50 violations/INSUFFICIENT_LIMIT)))))

(deftest test-process-transaction-as-input-with-high-frequency-small-interval-violation
  (testing "integration test : process transaction as input, with high frequency small interval violation"
    (let [storage (storage-client/storage-client)]
      (account-initial-state {:active-card true :available-limit 200} storage)
      (process-input
        (record/transaction-model {:merchant "Subway" :amount 18 :time "2019-02-13T10:00:12.000Z"})
        storage)
      (process-input
        (record/transaction-model {:merchant "Chilli Beans" :amount 55 :time "2019-02-13T10:00:23.000Z"})
        storage)
      (process-input
        (record/transaction-model {:merchant "Star Park" :amount 12 :time "2019-02-13T10:02:00.000Z"})
        storage)
      (let [recent-state
            (process-input
              (record/transaction-model {:merchant "Giraffas" :amount 65 :time "2019-02-13T10:02:12.000Z"})
              storage)]
        (asv/check-account-state recent-state 115 violations/HIGH_FREQUENCY_SMALL_INTERVAL)))))

(deftest test-process-transaction-as-input-when-existing-other-transactions
  (testing "integration test : process transaction as input, when existing others transactions"
    (let [storage (storage-client/storage-client)]
      (account-initial-state {:active-card true :available-limit 200} storage)
      (process-input
        (record/transaction-model {:merchant "Subway" :amount 18 :time "2019-02-13T10:00:00.000Z"})
        storage)
      (process-input
        (record/transaction-model {:merchant "Chilli Beans" :amount 55 :time "2019-02-13T10:00:00.000Z"})
        storage)
      (process-input
        (record/transaction-model {:merchant "Star Park" :amount 12 :time "2019-02-13T10:02:01.000Z"})
        storage)
      (let [recent-state
            (process-input
              (record/transaction-model {:merchant "Giraffas" :amount 65 :time "2019-02-13T10:02:12.000Z"})
              storage)]
        (asv/check-account-state recent-state 50)))))

(deftest test-process-transaction-as-input-with-doubled-transaction-violation
  (testing "integration test : process transaction as input, with doubled transaction violation"
    (let [storage (storage-client/storage-client)]
      (account-initial-state {:active-card true :available-limit 200} storage)
      (process-input
        (record/transaction-model {:merchant "Subway" :amount 18 :time "2019-02-13T10:00:00.000Z"})
        storage)
      (process-input
        (record/transaction-model {:merchant "Chilli Beans" :amount 55 :time "2019-02-13T10:00:00.000Z"})
        storage)
      (let [recent-state (process-input
                           (record/transaction-model {:merchant "Subway" :amount 18 :time "2019-02-13T10:02:00.000Z"})
                           storage)]
        (asv/check-account-state recent-state 127 violations/DOUBLED_TRANSACTION)))))

(deftest test-process-transaction-as-input-with-same-merchant-and-amount-but-different-times
  (testing "integration test : process transaction as input, with same merchant and amount but different times"
    (let [storage (storage-client/storage-client)]
      (account-initial-state {:active-card true :available-limit 200} storage)
      (process-input
        (record/transaction-model {:merchant "Subway" :amount 18 :time "2019-02-13T10:00:00.000Z"})
        storage)
      (process-input
        (record/transaction-model {:merchant "Chilli Beans" :amount 55 :time "2019-02-13T10:00:00.000Z"})
        storage)
      (let [recent-state (process-input
                           (record/transaction-model {:merchant "Subway" :amount 18 :time "2019-02-13T10:02:01.000Z"})
                           storage)]
        (asv/check-account-state recent-state 109)))))

(deftest test-process-transaction-as-input-with-same-merchant-and-time-but-different-amount
  (testing "integration test : process transaction as input, with same merchant and time but different amount"
    (let [storage (storage-client/storage-client)]
      (account-initial-state {:active-card true :available-limit 200} storage)
      (process-input
        (record/transaction-model {:merchant "Subway" :amount 18 :time "2019-02-13T10:00:00.000Z"})
        storage)
      (process-input
        (record/transaction-model {:merchant "Chilli Beans" :amount 55 :time "2019-02-13T10:00:00.000Z"})
        storage)
      (let [recent-state (process-input
                           (record/transaction-model {:merchant "Subway" :amount 19 :time "2019-02-13T10:00:00.000Z"})
                           storage)]
        (asv/check-account-state recent-state 108)))))

(deftest test-process-transaction-as-input-with-multiple-violations
  (testing "integration test : process transaction as input, with multiples violations"
    (let [storage (storage-client/storage-client)]
      (account-initial-state {:active-card true :available-limit 200} storage)
      (process-input
        (record/transaction-model {:merchant "Subway" :amount 18 :time "2019-02-13T10:00:12.000Z"})
        storage)
      (process-input
        (record/transaction-model {:merchant "Chilli Beans" :amount 55 :time "2019-02-13T10:00:23.000Z"})
        storage)
      (process-input
        (record/transaction-model {:merchant "Star Park" :amount 12 :time "2019-02-13T10:02:00.000Z"})
        storage)
      (let [recent-state (process-input
                           (record/transaction-model {:merchant "Giraffas" :amount 120 :time "2019-02-13T10:02:01.000Z"})
                           storage)]
        (asv/check-account-state recent-state 115 violations/INSUFFICIENT_LIMIT violations/HIGH_FREQUENCY_SMALL_INTERVAL)))))