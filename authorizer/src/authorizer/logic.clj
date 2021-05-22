(ns authorizer.logic
  (:require [authorizer.util.util :as util]
            [clj-time.core :as t]))

(defn calc-difference-in-milliseconds
  "Calculates the difference in milliseconds between to clojure's datetime.
  It is expected the first parameter to be greater than the second one, for this reason
  in case of throwing an exception this function should be called once more switching
  the order of them"
  [recent-datetime old-datetime]
  (try
    (t/in-millis (t/interval recent-datetime old-datetime))
    (catch Exception _
      (calc-difference-in-milliseconds old-datetime recent-datetime))))

(defn get-datetime-difference-in-milliseconds
  "Returns the difference in milliseconds between two transactions time attribute value"
  [last-transaction oldest-transaction]
  (calc-difference-in-milliseconds
    (util/convert-str-datetime (:time oldest-transaction)) (util/convert-str-datetime (:time last-transaction))))

(defn filter-transactions-by
  "Applies the filter function 'filter-fn' for each element in 'transactions'"
  [transactions filter-fn]
  (filter filter-fn transactions))

(defn transactions-reached-limit-in-interval?
  "Checks for if reached limit of transactions in interval considering the new transaction's :time
  attribute value as reference"
  [transactions transaction interval total-max]
  (let [base-limit (.minus (util/convert-str-datetime (:time transaction)) interval)
        total-transactions-in-interval (count
                                         (filter-transactions-by transactions
                                                                 #(or (t/after? (util/convert-str-datetime (:time %)) base-limit)
                                                                      (t/equal? (util/convert-str-datetime (:time %)) base-limit))))]
    (= total-transactions-in-interval total-max)))

(defn has-similar-transaction-in-interval?
  "Checks for if there are any transactions in 'transactions' which have same transaction amount
  processed in time less than or equal to 'interval'"
  [transactions new-transaction interval]
  (not-empty (filter-transactions-by transactions
                                     #(and (<= (get-datetime-difference-in-milliseconds new-transaction %) interval)
                                           (= (:amount new-transaction) (:amount %))))))