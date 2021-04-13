(ns atol.services.contact
  (:require [atol.db.core :as db]
            [struct.core :as st]))

(def contact-schema
  [[:contact-name st/required st/string]
   [:contact-email st/required st/email]
   [:contact-phone st/required st/string]])

(def phone-pattern #"[0-9]{11,11}")

(defn valid-contact? [contact]
  (st/validate contact contact-schema))

(defn valid-phone? [contact]
  (re-matches phone-pattern (:contact-phone contact)))

(defn get-contacts-by-owner-id [owner-id]
  (db/get-contacts {:owner_idt owner-id}))

(defn create-contact! [contact owner_idt]
  (db/create-contact! {:contact_name (:contact-name contact)
                       :email        (:contact-email contact)
                       :phone_number (:contact-phone contact)
                       :owner_idt    owner_idt}))