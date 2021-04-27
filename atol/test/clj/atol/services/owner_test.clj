(ns atol.services.owner-test
  (:require [atol.services.owner :as os]
            [clojure.test :refer :all]))

(deftest test-valid-owner?
  (testing "check whether is valid owner"
    (let [owner {:owner-name         "Aname"
                 :email              "aname@domain.com"
                 :owner-pass         "1234"
                 :owner-pass-confirm "1234"}]
      (is (nil? (first (os/valid-owner? owner)))))))

(defn validate-owner [validation-errors key message]
  (is (not (nil? (first validation-errors))))
  (is (contains? (first validation-errors) key))
  (is (= (key (first validation-errors)) message)))

(deftest test-invalid-owner-with-bad-owner-name
  (testing "check invalid owner with bad owner name"
    (let [owner {:owner-name         12345
                 :email              "aname@domain.com"
                 :owner-pass         "1234"
                 :owner-pass-confirm "1234"}
          validation-errors (os/valid-owner? owner)]
      (validate-owner validation-errors :owner-name "must be a string")))

  (testing "check invalid owner without owner name"
    (let [owner {:email              "aname@domain.com"
                 :owner-pass         "1234"
                 :owner-pass-confirm "1234"}
          validation-errors (os/valid-owner? owner)]
      (validate-owner validation-errors :owner-name "this field is mandatory"))))