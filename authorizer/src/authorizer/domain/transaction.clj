(ns authorizer.domain.transaction
  (:require [authorizer.service :as service]
            [authorizer.util.violations :as violations]))

(defmulti get-violation
          "Provides an abstraction to return a specific violation
          accordingly to business rules. 'domain-params' is a map
          with following attributes :transaction :account :transactions :violation"
          (fn [domain-params] (:violation domain-params)))

(defmethod get-violation violations/ACCOUNT_NOT_INITIALIZED [domain-params]
  (when (not (service/account-initialized? (:account domain-params)))
    violations/ACCOUNT_NOT_INITIALIZED))

(defmethod get-violation violations/CARD_NOT_ACTIVE [domain-params]
  (when (and (not (nil? (get-in domain-params [:account :active])))
             (not (service/active-card? (:account domain-params))))
    violations/CARD_NOT_ACTIVE))

(defmethod get-violation violations/INSUFFICIENT_LIMIT [domain-params]
  (when (and (not (nil? (get-in domain-params [:account :available-limit])))
             (service/insufficient-limit? (:transaction domain-params) (:account domain-params)))
    violations/INSUFFICIENT_LIMIT))

(defmethod get-violation violations/HIGH_FREQUENCY_SMALL_INTERVAL [domain-params]
  (when (service/high-frequency-small-interval? (:transactions domain-params) (:transaction domain-params))
    violations/HIGH_FREQUENCY_SMALL_INTERVAL))

(defmethod get-violation violations/DOUBLED_TRANSACTION [domain-params]
  (when (service/doubled-transaction? (:transaction domain-params) (:transactions domain-params))
    violations/DOUBLED_TRANSACTION))

(defn get-violations
  "Returns a vector with all 'transaction' violations.
  This function relies on the vector with all known transaction 'violations'"
  [transaction account transactions violations]
  (loop [transaction-violations violations
         current-violations []]
    (if (not-empty transaction-violations)
      (let [transaction-violation (first transaction-violations)
            current-violation (get-violation {:transaction  transaction
                                              :account      account
                                              :transactions transactions
                                              :violation    transaction-violation})]
        (recur (rest transaction-violations)
               (if (not (nil? current-violation))
                 (conj current-violations current-violation)
                 current-violations)))
      current-violations)))

(defn add-violation
  "Adds the specifics violations into the existing account accordingly to business rules"
  [violations storage]
  (loop [violations violations]
    (when (not-empty violations)
      (do
        (service/add-violation! (first violations) storage)
        (recur (rest violations))))))

(defn process-transaction
  "Checks for and add business rules violations or in case none found
  saves a new transaction and updates account's attribute :available-limit"
  [transaction storage]
  (let [current-violations (get-violations transaction
                                           (service/get-account storage)
                                           (service/get-transactions storage)
                                           violations/TRANSACTION_VIOLATIONS)]
    (if (empty? current-violations)
      (do
        (service/add-transaction! transaction storage)
        (service/update-available-amount! transaction storage))
      (add-violation current-violations storage))))