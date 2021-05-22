(ns authorizer.domain.account-test
  (:require [authorizer.account-state-validation :as asv]
            [authorizer.domain.account :refer :all]
            [authorizer.service :as service]
            [authorizer.util.violations :as violations]
            [clojure.test :refer :all]))

(deftest test-create-new-account
  (testing "create new account"
    (let [new-account {:active-card true :available-limit 100}
          storage {}]
      (with-redefs [service/get-account (fn [_] nil)
                    service/create-account! (fn [new-account _]
                                              {:account      {:active          (:active-card new-account)
                                                              :available-limit (:available-limit new-account)
                                                              :violations      []}
                                               :transactions nil})]
        (let [response (process-account new-account storage)]
          (is (not-empty response))
          (asv/check-account-state (:account response) 100)
          (is (contains? response :transactions))
          (is (nil? (:transactions response))))))))

(deftest test-should-not-recreate-account
  (testing "should not recreate account"
    (let [new-account {:active-card true :available-limit 100}
          violation violations/ACCOUNT_ALREADY_INITIALIZED
          storage {}]
      (with-redefs [service/get-account (fn [_] {:active          (:active-card new-account)
                                                 :available-limit (:available-limit new-account)
                                                 :violations      []})
                    service/add-violation! (fn [violation _]
                                             {:account      {:active          (:active-card new-account)
                                                             :available-limit (:available-limit new-account)
                                                             :violations      [violation]}
                                              :transactions nil})]
        (let [response (process-account new-account storage)]
          (is (not-empty response))
          (asv/check-account-state (:account response) 100 violation))))))

(deftest test-get-account-state
  (testing "get account current state"
    (let [storage {}]
      (with-redefs [service/get-account (fn [_] {:active          true
                                                 :available-limit 100
                                                 :violations      []})]
        (let [response (get-account-state storage)]
          (asv/check-account-state response 100))))))

(deftest test-in-full-state
  (testing "check if account has all expected keys"
    (let [account-state {:active true :available-limit 100 :violations []}]
      (is (in-full-state? account-state :active :available-limit :violations)))))

(deftest test-not-in-full-state
  (testing "check if account has not all expected keys"
    (let [account-state {:violations []}]
      (is (not (in-full-state? account-state :active :available-limit :violations))))))