(ns authorizer.controller-test
  (:require [authorizer.account-state-validation :as asv]
            [authorizer.controller :refer :all]
            [authorizer.domain.account :as account]
            [authorizer.domain.record :as record]
            [authorizer.domain.transaction :as transaction]
            [authorizer.util.violations :as violations]
            [clojure.test :refer :all]))

(deftest test-process-transaction-as-input
  (testing "process transaction as input considering an account with :available-limit 100"
    (with-redefs [account/reset-violations! (fn [_] _)
                  transaction/process-transaction (fn [_ _] _)
                  account/get-account-state (fn [_] {:active          true
                                                     :available-limit 75
                                                     :violations      []})]
      (let [transaction-record (record/transaction-model {:merchant "Burger King"
                                                          :amount   25
                                                          :time     "2019-02-15T10:00:00.000Z"})
            storage {}
            account-state (process-input transaction-record storage)]
        (asv/check-account-state account-state 75)))))

(deftest test-process-transaction-as-input-with-doubled-transaction-violation
  (testing "process transaction as input, with doubled transaction violation"
    (with-redefs [account/reset-violations! (fn [_] _)
                  transaction/process-transaction (fn [_ _] _)
                  account/get-account-state (fn [_] {:active          true
                                                     :available-limit 100
                                                     :violations      [violations/DOUBLED_TRANSACTION]})]
      (let [transaction-record (record/transaction-model {:merchant "Burger King"
                                                          :amount   25
                                                          :time     "2019-02-15T10:00:00.000Z"})
            storage {}
            account-state (process-input transaction-record storage)]
        (asv/check-account-state account-state 100 violations/DOUBLED_TRANSACTION)))))

(deftest test-process-account-as-input
  (testing "process account as input"
    (with-redefs [account/reset-violations! (fn [_] _)
                  account/process-account (fn [_ _] _)
                  account/get-account-state (fn [_] {:active          true
                                                     :available-limit 100
                                                     :violations      []})]
      (let [account-record (record/account-model {:active-card true :available-limit 100})
            storage {}
            account-state (process-input account-record storage)]
        (asv/check-account-state account-state 100)))))

(deftest test-process-account-as-input-with-account-already-initialized-violation
  (testing "process account as input, with account already initialized violation"
    (with-redefs [account/reset-violations! (fn [_] _)
                  account/process-account (fn [_ _] _)
                  account/get-account-state (fn [_] {:active          true
                                                     :available-limit 100
                                                     :violations      [violations/ACCOUNT_ALREADY_INITIALIZED]})]
      (let [account-record (record/account-model {:active-card true :available-limit 100})
            storage {}
            account-state (process-input account-record storage)]
        (asv/check-account-state account-state 100 violations/ACCOUNT_ALREADY_INITIALIZED)))))