(ns authorizer.util.util-test
  (:require [authorizer.account-state-validation :as asv]
            [authorizer.util.util :as util]
            [authorizer.util.violations :as violations]
            [clojure.string :as str]
            [clojure.test :refer :all]))

(deftest test-parse-account-from-json-string-to-map
  (testing "converting account json string into map"
    (let [account-as-json "{\"account\": {\"active-card\": true, \"available-limit\": 100}}"
          account-as-map (util/convert-to-map account-as-json)]
      (is (map? account-as-map))
      (is (contains? account-as-map :account))
      (is (contains? (:account account-as-map) :active-card))
      (is (contains? (:account account-as-map) :available-limit)))))

(deftest test-parse-transaction-from-json-string-to-map
  (testing "converting transaction json string into map"
    (let [transaction-as-json " {\"transaction\": {\"merchant\": \"Burger King\", \"amount\": 20, \"time\": \"2019-02-13T10:00:00.000Z\"}}"
          transaction-as-map (util/convert-to-map transaction-as-json)]
      (is (map? transaction-as-map))
      (is (contains? transaction-as-map :transaction))
      (is (contains? (:transaction transaction-as-map) :merchant))
      (is (contains? (:transaction transaction-as-map) :amount))
      (is (contains? (:transaction transaction-as-map) :time)))))

(deftest test-parse-account-from-map-to-json-string
  (testing "converting account map into json string"
    (let [account-as-map {:account {:active-card true :available-limit 100}}
          account-as-json (util/convert-to-json account-as-map)]
      (is (string? account-as-json))
      (is (str/includes? account-as-json "account"))
      (is (str/includes? account-as-json "active-card"))
      (is (str/includes? account-as-json "available-limit")))))

(deftest test-parse-account-with-violations-from-map-to-json-string
  (testing "converting account with violations map into json string"
    (let [account-as-map {:account {:active-card true :available-limit 100}
                          :violations [violations/ACCOUNT_ALREADY_INITIALIZED violations/CARD_NOT_ACTIVE]}
          account-as-json (util/convert-to-json account-as-map)]
      (asv/check-account-state-as-str account-as-json "100"))))

(deftest test-convert-account-model-into-output
  (testing "converting account model into output"
    (let [account-as-model {:active-card true :available-limit 100 :violations [violations/ACCOUNT_ALREADY_INITIALIZED violations/CARD_NOT_ACTIVE]}
          output (util/convert-to-output-model account-as-model)]
      (is (map? output))
      (is (contains? output :account))
      (is (contains? (:account output) :active-card))
      (is (contains? (:account output) :available-limit))
      (is (contains? output :violations)))))

(deftest test-convert-account-model-with-only-violations-into-output
  (testing "converting account model with only violations into output"
    (let [account-as-model {:violations [violations/ACCOUNT_NOT_INITIALIZED]}
          output (util/convert-to-output-model account-as-model)]
      (is (map? output))
      (is (contains? output :account))
      (is (contains? (:account output) :active-card))
      (is (= (get-in output [:account :active-card]) false))
      (is (contains? (:account output) :available-limit))
      (is (= (get-in output [:account :available-limit]) 0))
      (is (contains? output :violations)))))