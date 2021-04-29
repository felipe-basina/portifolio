(ns atol.services.contact-test
  (:require [atol.db.core :as db]
            [atol.services.contact :as sc]
            [atol.util :refer :all]
            [clojure.test :refer :all]))

(def contact {:contact_name "Contact Name"
              :email        "contact.name@domain.com"
              :phone_number "11998761233"})

(deftest test-valid-contact
  (testing "check whether is a valid contact"
    (is (nil? (first (sc/valid-contact? contact))))))

(deftest test-invalid-contact-with-bad-email
  (testing "check invalid contact with bad email"
    (let [contact (assoc contact :email "contact.name")
          validation-errors (sc/valid-contact? contact)]
      (validate-domain validation-errors :email "must be a valid email"))))

(deftest test-valid-phone
  (testing "check whether is a valid phone number"
    (is (not-empty (sc/valid-phone? contact)))))

(deftest test-invalid-phone
  (testing "check whether is an invalid phone number"
    (let [contact (assoc contact :phone_number "111999")]
      (is (nil? (sc/valid-phone? contact))))))

(deftest test-get-contacts-by-owner-id
  (testing "check get contacts by owner id"
    (let [contacts (conj [] contact)
          owner-id 1001]
      (with-redefs [db/get-contacts (fn [_] contacts)]
        (let [all-contacts (sc/get-contacts-by-owner-id owner-id)]
          (is (not-empty all-contacts))
          (is (= 1 (count all-contacts)))
          (is (= contact (first all-contacts))))))))

(deftest test-get-contact-by-id
  (testing "check get contact by id"
    (with-redefs [db/get-contact (fn [_] contact)]
      (let [found-contact (sc/get-contact-by-id 1 1001)]
        (is (not-empty found-contact))
        (is (= contact found-contact))))))

(deftest test-create-contact
  (testing "check create contact"
    (with-redefs [db/create-contact! (fn [_] 1)]
      (let [total-created (sc/create-contact! contact 1001)]
        (is (= total-created 1))))))

(deftest test-update-contact
  (testing "check update contact"
    (let [contact (assoc contact :idt 1)]
      (with-redefs [db/update-contact! (fn [_] 1)]
        (let [total-updated (sc/update-contact! contact)]
          (is (= total-updated 1)))))))

(deftest test-delete-contact
  (testing "check delete contact"
    (with-redefs [db/delete-contact (fn [_] nil)]
      (is (nil? (sc/delete-contact! 1 1001))))))