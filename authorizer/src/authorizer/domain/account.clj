(ns authorizer.domain.account
  (:require [authorizer.service :as service]
            [authorizer.util.violations :as violations]))

(defn reset-violations!
  "Remove all existing violations"
  [storage]
  (service/reset-violations! storage))

(defn in-full-state?
  "Checks for if current account state has all expected 'keys':
  :active :available-limit and :violations"
  [account-state & keys]
  (every? #(contains? account-state %) keys))

(defn get-account-state
  "Retrieves account current state"
  [storage]
  (service/get-account storage))

(defn process-account
  "Creates a new account or adds ACCOUNT_ALREADY_INITIALIZED as violation
  into an existing account"
  [new-account storage]
  (let [account-state (get-account-state storage)]
    (if (or (nil? account-state)
            (not (in-full-state? account-state :active :available-limit :violations)))
      (service/create-account! new-account storage)
      (service/add-violation! violations/ACCOUNT_ALREADY_INITIALIZED storage))))