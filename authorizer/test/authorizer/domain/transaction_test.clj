(ns authorizer.domain.transaction-test
  (:require [authorizer.domain.transaction :refer :all]
            [authorizer.service :as service]
            [authorizer.util.violations :as violations]
            [clojure.test :refer :all]))

(deftest test-get-account-not-initialized-violation
  (testing "get account not initialized violation"
    (with-redefs [service/account-initialized? (fn [_] false)]
      (let [account-not-initialized-violation violations/ACCOUNT_NOT_INITIALIZED
            violation (get-violation {:account      {}
                                      :transaction  {}
                                      :transactions '()
                                      :violation    account-not-initialized-violation})]
        (is (= violation account-not-initialized-violation))))))

(deftest test-get-card-not-active-violation
  (testing "get card not active violation"
    (with-redefs [service/active-card? (fn [_] false)]
      (let [card-not-active-violation violations/CARD_NOT_ACTIVE
            violation (get-violation {:account      {:active false}
                                      :transaction  {}
                                      :transactions '()
                                      :violation    card-not-active-violation})]
        (is (= violation card-not-active-violation))))))

(deftest test-get-insufficient-limit-violation
  (testing "get insufficient limit violation"
    (with-redefs [service/insufficient-limit? (fn [_ _] true)]
      (let [insufficient-limit-violation violations/INSUFFICIENT_LIMIT
            violation (get-violation {:account      {:available-limit 10}
                                      :transaction  {}
                                      :transactions '()
                                      :violation    insufficient-limit-violation})]
        (is (= violation insufficient-limit-violation))))))

(deftest test-not-a-insufficient-limit-violation
  (testing "not a insufficient limit violation"
    (with-redefs [service/insufficient-limit? (fn [_ _] true)]
      (let [insufficient-limit-violation violations/INSUFFICIENT_LIMIT
            violation (get-violation {:account      {}
                                      :transaction  {}
                                      :transactions '()
                                      :violation    insufficient-limit-violation})]
        (is (nil? violation))))))

(deftest test-high-frequency-small-interval-violation
  (testing "get high frequency small interval violation"
    (with-redefs [service/high-frequency-small-interval? (fn [_ _] true)]
      (let [high-frequency-small-interval-violation violations/HIGH_FREQUENCY_SMALL_INTERVAL
            violation (get-violation {:account      {}
                                      :transaction  {}
                                      :transactions '()
                                      :violation    high-frequency-small-interval-violation})]
        (is (= violation high-frequency-small-interval-violation))))))

(deftest test-doubled-transaction-violation
  (testing "get doubled transaction violation"
    (with-redefs [service/doubled-transaction? (fn [_ _] true)]
      (let [doubled-transaction-violation violations/DOUBLED_TRANSACTION
            violation (get-violation {:account      {}
                                      :transaction  {}
                                      :transactions '()
                                      :violation    doubled-transaction-violation})]
        (is (= violation doubled-transaction-violation))))))

(deftest test-add-violation
  (testing "add violation account not initialized"
    (let [transactions nil
          violation violations/ACCOUNT_NOT_INITIALIZED
          storage {}]
      (with-redefs [service/add-violation! (fn [violation _] {:account      {:violations [violation]}
                                                              :transactions transactions})]
        (let [response (add-violation [violation] storage)]
          (is (nil? response)))))))

(deftest test-add-new-transaction
  (testing "add new transaction"
    (let [account {:active true :available-limit 100 :violations []}
          transaction {:merchant "Burger King" :amount 25 :time "2019-02-13T10:00:00.000Z"}
          storage {}]
      (with-redefs [service/get-account (fn [_] account)
                    service/get-transactions (fn [_] nil)
                    service/add-transaction! (fn [_ _] {:account      {:active          true
                                                                       :available-limit 100
                                                                       :violations      []}
                                                        :transactions '(transaction)})
                    service/update-available-amount! (fn [_ _] {:account      {:active          true
                                                                               :available-limit 75
                                                                               :violations      []}
                                                                :transactions '(transaction)})]
        (let [response (process-transaction transaction storage)]
          (is (not-empty response))
          (is (contains? response :account))
          (is (contains? response :transactions))
          (is (contains? (:account response) :violations))
          (is (empty? (:violations (:account response))))
          (is (not (empty? (:transactions response))))
          (is (list? (:transactions response)))
          (is (= 1 (count (:transactions response)))))))))