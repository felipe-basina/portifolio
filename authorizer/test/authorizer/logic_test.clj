(ns authorizer.logic-test
  (:require [authorizer.logic :as logic]
            [authorizer.util.util :as util]
            [clojure.test :refer :all]))

(deftest test-filter-transactions-by-merchant
  (testing "filter transactions by merchant"
    (let [habbibs-transaction-1 {:merchant "Habbib's" :amount 20 :time "2019-02-13T09:56:00.000Z"}
          bk-transaction {:merchant "Burger King" :amount 20 :time "2019-02-13T09:56:00.000Z"}
          habbibs-transaction-2 {:merchant "Habbib's" :amount 20 :time "2019-02-13T10:00:00.000Z"}
          transactions (list habbibs-transaction-2 bk-transaction habbibs-transaction-1)
          filtered-transactions (logic/filter-transactions-by transactions
                                                              (fn [transaction] (= "Habbib's" (:merchant transaction))))]
      (is (not-empty filtered-transactions))
      (is (= 2 (count filtered-transactions))))))

(deftest test-has-similar-transaction-in-interval
  (testing "check has similar transaction in interval"
    (let [habbibs-transaction {:merchant "Habbib's" :amount 20 :time "2019-02-13T09:56:00.000Z"}
          new-transaction {:merchant "Habbib's" :amount 20 :time "2019-02-13T09:58:00.000Z"}
          transactions (list habbibs-transaction)]
      (is (logic/has-similar-transaction-in-interval? transactions new-transaction 120000)))))

(deftest test-has-not-similar-transaction-in-interval
  (testing "check has not similar transaction in interval, with different amount"
    (let [habbibs-transaction {:merchant "Habbib's" :amount 20 :time "2019-02-13T09:56:00.000Z"}
          new-transaction {:merchant "Habbib's" :amount 17 :time "2019-02-13T09:58:00.000Z"}
          transactions (list habbibs-transaction)]
      (is (not (logic/has-similar-transaction-in-interval? transactions new-transaction 120000))))))

(deftest test-has-not-similar-transaction-in-interval-with-difference-time-greater-than-interval
  (testing "check has not similar transaction in interval, same transaction with difference time greater than interval"
    (let [habbibs-transaction {:merchant "Habbib's" :amount 20 :time "2019-02-13T09:56:00.000Z"}
          new-transaction {:merchant "Habbib's" :amount 20 :time "2019-02-13T09:59:00.000Z"}
          transactions (list habbibs-transaction)]
      (is (not (logic/has-similar-transaction-in-interval? transactions new-transaction 120000))))))

(deftest test-difference-between-date-times
  (testing "difference between date times, should be a positive value"
    (let [difference (logic/calc-difference-in-milliseconds
                       (util/convert-str-datetime "2019-02-13T10:02:58.000Z")
                       (util/convert-str-datetime "2019-02-13T10:00:00.000Z"))]
      (is (pos? difference))))

  (testing "difference between date times switching order of parameters, should be a positive value"
    (let [difference (logic/calc-difference-in-milliseconds
                       (util/convert-str-datetime "2019-02-13T10:00:00.000Z")
                       (util/convert-str-datetime "2019-02-13T10:02:58.000Z"))]
      (is (pos? difference)))))

(deftest transactions-reached-limit-in-interval
  (testing "check if transactions reached limit in interval"
    (let [transactions '({:merchant "Habib's" :amount 20 :time "2019-02-13T09:59:34.000Z"}
                         {:merchant "Pizza Hut" :amount 20 :time "2019-02-13T09:59:15.000Z"}
                         {:merchant "Subway" :amount 20 :time "2019-02-13T09:59:00.000Z"})
          transaction {:merchant "Giraffas" :amount 20 :time "2019-02-13T10:01:00.000Z"}
          reached-limit? (logic/transactions-reached-limit-in-interval? transactions
                                                                             transaction
                                                                             120000
                                                                             3)]
      (is reached-limit?))))

(deftest transactions-not-reached-limit-in-interval
  (testing "check if transactions not reached limit in interval"
    (let [transactions '({:merchant "Habib's" :amount 20 :time "2019-02-13T09:59:34.000Z"}
                         {:merchant "Pizza Hut" :amount 20 :time "2019-02-13T09:59:15.000Z"}
                         {:merchant "Subway" :amount 20 :time "2019-02-13T09:59:00.000Z"})
          transaction {:merchant "Giraffas" :amount 20 :time "2019-02-13T10:01:12.000Z"}
          reached-limit? (logic/transactions-reached-limit-in-interval? transactions
                                                                             transaction
                                                                             120000
                                                                             3)]
      (is (not reached-limit?)))))