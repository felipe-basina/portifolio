(ns atol.services.login-test
  (:require [atol.services.login :as ls]
            [buddy.hashers :as hashers]
            [clojure.test :refer :all]))

(def fake-owner [{:owner_pass "bcrypt+sha512$e5b8f9b454c679868ee210e8073a6ae7$12$009edae5051511124d39b51f809fbc5eb1848b1f8ba5801a"}])

(deftest test-valid-login?
  (testing "check whether is valid login"
    (let [login-map {:username "john.doe@domain.com"
                     :password "1234"}]
      (is (nil? (first (ls/valid-login? login-map)))))))

(deftest test-invalid-login-with-bad-username
  (testing "check invalid login with bad username"
    (let [login-map {:username "john.doe"
                     :password "1234"}
          validation-errors (first (ls/valid-login? login-map))]
      (is (not (nil? validation-errors)))
      (is (contains? validation-errors :username))
      (is (= (:username validation-errors) "must be a valid email")))))

(deftest test-invalid-password
  (testing "check invalid password"
    (let [raw-pass "123"]
      (is (not (ls/valid-pass? raw-pass fake-owner))))))

(deftest test-valid-password
  (testing "check valid password"
    (let [raw-pass "1234"]
      (is (ls/valid-pass? raw-pass fake-owner)))))