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