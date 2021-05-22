(ns authorizer.domain.record-test
  (:require [authorizer.domain.record :as record]
            [clojure.test :refer :all]))

(deftest test-get-account-model-as-record
  (testing "get account model as record"
    (let [account-as-model (record/model-as-record {:account {:active-card true :available-limit 100}})]
      (is (map? account-as-model))
      (is (record? account-as-model))
      (is (contains? account-as-model :active-card))
      (is (contains? account-as-model :available-limit))
      (is (not (contains? account-as-model :account))))))

(deftest test-get-transaction-model-as-record
  (testing "get transaction model as record"
    (let [transaction-as-model (record/model-as-record {:transaction {:merchant "Burger King" :amount 20 :time "2019-02-13T10:00:00.000Z"}})]
      (is (map? transaction-as-model))
      (is (record? transaction-as-model))
      (is (contains? transaction-as-model :merchant))
      (is (contains? transaction-as-model :amount))
      (is (contains? transaction-as-model :time))
      (is (not (contains? transaction-as-model :transaction))))))