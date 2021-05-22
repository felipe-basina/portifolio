(ns atol.services.owner-test
  (:require [atol.db.core :as db]
            [atol.services.owner :as os]
            [atol.util :refer :all]
            [clojure.test :refer :all]))

(def owner {:owner-name         "Aname"
            :email              "aname@domain.com"
            :owner-pass         "1234"
            :owner-pass-confirm "1234"})

(deftest test-valid-owner?
  (testing "check whether is valid owner"
    (is (nil? (first (os/valid-owner? owner))))))

(deftest test-invalid-owner-with-bad-owner-name
  (testing "check invalid owner with bad owner name"
    (let [owner (assoc owner :owner-name 12345)
          validation-errors (os/valid-owner? owner)]
      (validate-domain validation-errors :owner-name "must be a string")))

  (testing "check invalid owner without owner name"
    (let [owner (dissoc owner :owner-name)
          validation-errors (os/valid-owner? owner)]
      (validate-domain validation-errors :owner-name "this field is mandatory"))))

(deftest test-invalid-owner-with-bad-email
  (testing "check invalid owner with bad email"
    (let [owner (assoc owner :email "aname")
          validation-errors (os/valid-owner? owner)]
      (validate-domain validation-errors :email "must be a valid email"))))

(deftest test-invalid-owner-with-passwords-not-match
  (testing "check invalid owner with passwords not match"
    (let [owner (assoc owner :owner-pass-confirm "123")
          validation-errors (os/valid-owner? owner)]
      (validate-domain validation-errors :owner-pass-confirm "does not match"))))

(deftest test-save-new-owner
  (testing "save new owner"
    (with-redefs [db/create-owner! (fn [_] 1)]
      (is (= 1 (os/save-new-owner owner))))))

(deftest test-get-owner-by-email
  (testing "get owner by email"
    (with-redefs [db/get-owner (fn [_] (dissoc owner :owner-pass-confirm))]
      (let [owner-found (os/get-owner-by-email (:email owner))]
        (is (not-empty owner-found))
        (is (contains? owner-found :owner-name))
        (is (contains? owner-found :email))
        (is (contains? owner-found :owner-pass))
        (is (not (contains? owner-found :owner-pass-confirm)))))))