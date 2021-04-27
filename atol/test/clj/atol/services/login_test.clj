(ns atol.services.login-test
  (:require [atol.services.login :as ls]
            [clojure.test :refer :all]))

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
