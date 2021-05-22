(ns authorizer.service
  (:require [authorizer.logic :as logic]
            [authorizer.storage.protocol.storage :as storage]))

(def MILLISECONDS_INTERVAL_ALLOWED_FOR_TRANSACTIONS 120000)
(def MAX_TRANSACTIONS_ALLOWED_IN_INTERVAL 3)

(defn create-account! [new-account storage]
  (storage/create-new-account! storage new-account))

(defn get-account [storage]
  (storage/get-account storage))

(defn add-violation! [violation storage]
  (storage/add-transaction-violation! storage violation))

(defn update-available-amount! [transaction storage]
  (storage/update-available-amount! storage transaction))

(defn reset-violations! [storage]
  (storage/reset-violations! storage))

(defn add-transaction! [transaction storage]
  (storage/add-new-transaction! storage transaction))

(defn get-transactions [storage]
  (storage/get-transactions storage))

(defn account-initialized? [account]
  (not (nil? account)))

(defn active-card? [account]
  (:active account))

(defn insufficient-limit? [transaction account]
  (> (:amount transaction) (:available-limit account)))

(defn high-frequency-small-interval?
  "Checks for if there are 'MAX_TRANSACTIONS_ALLOWED_IN_INTERVAL' total transactions
  in the same time interval set by 'MILLISECONDS_INTERVAL_ALLOWED_FOR_TRANSACTIONS'"
  [transactions transaction]
  (let [transactions (take MAX_TRANSACTIONS_ALLOWED_IN_INTERVAL transactions)]
    (logic/transactions-reached-limit-in-interval? transactions
                                                   transaction
                                                   MILLISECONDS_INTERVAL_ALLOWED_FOR_TRANSACTIONS
                                                   MAX_TRANSACTIONS_ALLOWED_IN_INTERVAL)))

(defn doubled-transaction?
  "Checks for if there are same transaction, considering :merchant and :amount from 'transactions', in
   the same time interval set by 'MILLISECONDS_INTERVAL_ALLOWED_FOR_TRANSACTIONS'"
  [new-transaction transactions]
  (let [merchant (:merchant new-transaction)
        transactions-same-merchant (logic/filter-transactions-by
                                     transactions (fn [trans] (= merchant (:merchant trans))))]
    (logic/has-similar-transaction-in-interval? transactions-same-merchant
                                                new-transaction
                                                MILLISECONDS_INTERVAL_ALLOWED_FOR_TRANSACTIONS)))

(defn get-total-amount-by-merchant [transactions]
  (let [merchants (set (map #(:merchant %) transactions))]
    (loop [merchants merchants
           total-amount-by-merchant []]
      (if (not-empty merchants)
        (let [merchant (first merchants)
              total-amount (reduce + (map #(:amount %) (filter #(= merchant (:merchant %)) transactions)))]
          (recur (rest merchants)
                 (conj total-amount-by-merchant (assoc {} merchant total-amount))))
        total-amount-by-merchant))))