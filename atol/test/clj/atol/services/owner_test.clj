(ns atol.services.owner-test
  (:require [atol.services.owner :as os]
            [clojure.test :refer :all]))

(def owner {:owner-name         "Aname"
            :email              "aname@domain.com"
            :owner-pass         "1234"
            :owner-pass-confirm "1234"})

(deftest test-valid-owner?
  (testing "check whether is valid owner"
    (is (nil? (first (os/valid-owner? owner))))))

(defn validate-owner [validation-errors key message]
  (is (not (nil? (first validation-errors))))
  (is (contains? (first validation-errors) key))
  (is (= (key (first validation-errors)) message)))

(deftest test-invalid-owner-with-bad-owner-name
  (testing "check invalid owner with bad owner name"
    (let [owner (assoc owner :owner-name 12345)
          validation-errors (os/valid-owner? owner)]
      (validate-owner validation-errors :owner-name "must be a string")))

  (testing "check invalid owner without owner name"
    (let [owner (dissoc owner :owner-name)
          validation-errors (os/valid-owner? owner)]
      (validate-owner validation-errors :owner-name "this field is mandatory"))))

(deftest test-invalid-owner-with-bad-email
  (testing "check invalid owner with bad email"
    (let [owner (assoc owner :email "aname")
          validation-errors (os/valid-owner? owner)]
      (validate-owner validation-errors :email "must be a valid email"))))

(deftest test-invalid-owner-with-passwords-not-match
  (testing "check invalid owner with passwords not match"
    (let [owner (assoc owner :owner-pass-confirm "123")
          validation-errors (os/valid-owner? owner)]
      (validate-owner validation-errors :owner-pass-confirm "does not match"))))