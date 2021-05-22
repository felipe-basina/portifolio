(ns authorizer.account-state-validation
  (:require [clojure.string :as str]
            [clojure.test :refer :all]))

(defn check-account-state
  "Helper function to validate account state"
  [recent-state total-amount & violations]
  (is (not-empty recent-state))
  (is (map? recent-state))
  (is (contains? recent-state :active))
  (is (contains? recent-state :available-limit))
  (is (contains? recent-state :violations))

  (when (not (nil? total-amount))
    (is (= total-amount (:available-limit recent-state))))

  (loop [violations violations]
    (when (not-empty violations)
      (is (some #(= (first violations) %) (:violations recent-state)))
      (recur (rest violations)))))

(defn check-account-state-as-str
  "Helper function to validate account state as string"
  [account-state amount & violations]
  (is (not-empty account-state))
  (is (string? account-state))
  (is (str/includes? account-state "account"))
  (is (str/includes? account-state "active-card"))
  (is (str/includes? account-state "available-limit"))
  (is (str/includes? account-state amount))
  (is (str/includes? account-state "violations"))

  (loop [violations violations]
    (when (not-empty violations)
      (is (str/includes? account-state (first violations)))
      (recur (rest violations)))))