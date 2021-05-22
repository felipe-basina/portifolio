(ns authorizer.service-test
  (:require [authorizer.account-state-validation :as asv]
            [authorizer.service :refer :all]
            [authorizer.storage.protocol.storage :as storage]
            [authorizer.util.violations :as violations]
            [clojure.test :refer :all]))

(deftest test-create-account
  (testing "creates a new account"
    (let [new-account {:active-card true :available-limit 100}
          storage {}]
      (with-redefs [storage/create-new-account! (fn [_ new-account]
                                                  {:account      {:active          (:active-card new-account)
                                                                  :available-limit (:available-limit new-account)
                                                                  :violations      []}
                                                   :transactions nil})]
        (let [response (create-account! new-account storage)]
          (is (not-empty response))
          (asv/check-account-state (:account response) 100)
          (is (contains? response :transactions))
          (is (nil? (:transactions response))))))))

(deftest test-get-existing-account
  (testing "get existing account"
    (let [account {:active true :available-limit 100 :violations []}
          storage {}]
      (with-redefs [storage/get-account (fn [_] account)]
        (let [response (get-account storage)]
          (asv/check-account-state response 100))))))

(deftest test-add-first-violation
  (testing "add first violation (default to create an empty vector)"
    (let [violation violations/CARD_NOT_ACTIVE
          storage {}]
      (with-redefs [storage/add-transaction-violation! (fn [_ violation]
                                                         {:account      {:active          true
                                                                         :available-limit 100
                                                                         :violations      [violation]}
                                                          :transactions nil})]
        (let [response (add-violation! violation storage)]
          (is (not-empty response))
          (asv/check-account-state (:account response) 100 violation)
          (is (contains? response :transactions))
          (is (nil? (:transactions response))))))))

(deftest test-update-available-amount
  (testing "update available amount considering account :available-limit 100"
    (let [new-transaction {:merchant "Pizza Hut" :amount 78 :time "2019-02-13T11:00:00.000Z"}
          storage {}]
      (with-redefs [storage/update-available-amount! (fn [_ new-transaction]
                                                       {:account      {:active          true
                                                                       :available-limit 22
                                                                       :violations      []}
                                                        :transactions '(new-transaction)})]
        (let [response (update-available-amount! new-transaction storage)]
          (is (not-empty response))
          (asv/check-account-state (:account response) 22)
          (is (contains? response :transactions))
          (is (not-empty (:transactions response)))
          (is (list? (:transactions response)))
          (is (= (count (:transactions response)) 1)))))))

(deftest test-add-transaction
  (testing "add new transaction considering account :available-limit 100"
    (let [new-transaction {:merchant "Pizza Hut" :amount 78 :time "2019-02-13T11:00:00.000Z"}
          storage {}]
      (with-redefs [storage/add-new-transaction! (fn [_ new-transaction]
                                                   {:account      {:active          true
                                                                   :available-limit 22
                                                                   :violations      []}
                                                    :transactions '(new-transaction)})]
        (let [response (add-transaction! new-transaction storage)]
          (is (not-empty response))
          (asv/check-account-state (:account response) 22)
          (is (contains? response :transactions))
          (is (not-empty (:transactions response)))
          (is (list? (:transactions response)))
          (is (= (count (:transactions response)) 1)))))))

(deftest test-get-transactions
  (testing "get all available transactions"
    (let [current-transactions '({:merchant "Pizza Hut" :amount 78 :time "2019-02-13T11:00:00.000Z"}
                                 {:merchant "Star Park" :amount 45 :time "2019-02-13T11:01:00.000Z"})
          storage {}]
      (with-redefs [storage/get-transactions (fn [_] current-transactions)]
        (let [transactions (get-transactions storage)]
          (is (not-empty transactions))
          (is (list? transactions))
          (is (= (count transactions) (count current-transactions)))
          (loop [transactions transactions]
            (when (not-empty transactions)
              (is (contains? (first transactions) :merchant))
              (is (contains? (first transactions) :amount))
              (is (contains? (first transactions) :time))
              (recur (rest transactions)))))))))

(deftest test-account-initialized
  (testing "check if initialized account"
    (is (account-initialized? {:active true :available-limit 100}))))

(deftest test-account-not-initialized
  (testing "check if not initialized account"
    (is (not (account-initialized? nil)))))

(deftest test-active-card
  (testing "check if active card"
    (is (active-card? {:active true :available-limit 100}))))

(deftest test-not-active-card
  (testing "check if not active card"
    (is (not (active-card? {:active false :available-limit 100})))))

(deftest test-insufficient-limit
  (testing "check if insufficient limit"
    (is (insufficient-limit?
          {:merchant "Habib's" :amount 101 :time "2019-02-13T11:00:00.000Z"}
          {:active true :available-limit 100}))))

(deftest test-not-insufficient-limit
  (testing "check if not insufficient limit"
    (is (not (insufficient-limit?
               {:merchant "Habib's" :amount 99 :time "2019-02-13T11:00:00.000Z"}
               {:active true :available-limit 100})))))

(deftest test-high-frequency-small-interval
  (testing "check if high frequency small interval"
    (let [transactions '({:merchant "Habib's" :amount 37 :time "2019-02-13T11:01:57.000Z"}
                         {:merchant "Habib's" :amount 25 :time "2019-02-13T11:01:24.000Z"}
                         {:merchant "Habib's" :amount 99 :time "2019-02-13T11:00:00.000Z"})
          transaction {:merchant "Habib's" :amount 37 :time "2019-02-13T11:01:59.000Z"}]
      (is (high-frequency-small-interval? transactions transaction)))))

(deftest test-high-frequency-small-interval-not-reached-minimum-total-of-transactions
  (testing "check if high frequency small interval not reached minimum total of transactions"
    (let [transactions '({:merchant "Habib's" :amount 25 :time "2019-02-13T11:01:24.000Z"}
                         {:merchant "Habib's" :amount 99 :time "2019-02-13T11:00:00.000Z"})
          transaction {:merchant "Habib's" :amount 37 :time "2019-02-13T11:01:59.000Z"}]
      (is (not (high-frequency-small-interval? transactions transaction))))))

(deftest test-not-high-frequency-small-interval
  (testing "check if not high frequency small interval"
    (let [transactions '({:merchant "Habib's" :amount 37 :time "2019-02-13T11:01:57.000Z"}
                         {:merchant "Habib's" :amount 25 :time "2019-02-13T11:01:24.000Z"}
                         {:merchant "Habib's" :amount 99 :time "2019-02-13T11:00:00.000Z"})
          transaction {:merchant "Habib's" :amount 37 :time "2019-02-13T11:03:22.000Z"}]
      (is (not (high-frequency-small-interval? transactions transaction))))))

(deftest test-doubled-transaction
  (testing "check if doubled transaction"
    (let [transactions '({:merchant "Habib's" :amount 25 :time "2019-02-13T11:01:24.000Z"}
                         {:merchant "Habib's" :amount 99 :time "2019-02-13T11:00:00.000Z"})]
      (is (doubled-transaction? {:merchant "Habib's" :amount 25 :time "2019-02-13T11:01:24.000Z"}
                                transactions)))))

(deftest test-not-doubled-transaction-with-different-amount
  (testing "check if not doubled transaction, different amount"
    (let [transactions '({:merchant "Habib's" :amount 25 :time "2019-02-13T11:01:24.000Z"}
                         {:merchant "Habib's" :amount 99 :time "2019-02-13T11:00:00.000Z"})]
      (is (not (doubled-transaction? {:merchant "Habib's" :amount 78 :time "2019-02-13T11:01:24.000Z"}
                                     transactions))))))

(deftest test-not-doubled-transaction-with-different-merchant
  (testing "check if not doubled transaction, different merchant"
    (let [transactions '({:merchant "Patrone" :amount 25 :time "2019-02-13T11:01:24.000Z"}
                         {:merchant "Pizza Hut" :amount 99 :time "2019-02-13T11:00:00.000Z"})]
      (is (not (doubled-transaction? {:merchant "Habib's" :amount 25 :time "2019-02-13T11:01:24.000Z"}
                                     transactions))))))