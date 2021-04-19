(ns atol.services.contact
  (:require [atol.db.core :as db]
            [struct.core :as st]))

(def contact-schema
  [[:contact_name st/required st/string]
   [:email st/required st/email]
   [:phone_number st/required st/string]])

(def phone-pattern #"[0-9]{11,11}")

(defn valid-contact? [contact]
  (st/validate contact contact-schema))

(defn valid-phone? [contact]
  (re-matches phone-pattern (:phone_number contact)))

(defn get-contacts-by-owner-id [owner-id]
  (db/get-contacts {:owner_idt owner-id}))

(defn get-contact-by-id [id owner_id]
  (db/get-contact {:idt id :owner_idt owner_id}))

(defn create-contact! [contact owner_idt]
  (db/create-contact! {:contact_name (:contact_name contact)
                       :email        (:email contact)
                       :phone_number (:phone_number contact)
                       :owner_idt    owner_idt}))

(defn update-contact! [contact]
  (db/update-contact! {:contact_name (:contact_name contact)
                       :email        (:email contact)
                       :phone_number (:phone_number contact)
                       :idt          (:idt contact)}))

(defn delete-contact! [id owner_id]
  (db/delete-contact {:idt id :owner_idt owner_id}))

(defn deleted? [contact]
  (> (:next.jdbc/update-count contact) 0))

(defn get-contacts-by-filter [filter owner_idt]
  (let [filter-token (str "%" filter "%")]
    (db/filter-contacts {:contact_name filter-token
                         :email        filter-token
                         :phone_number filter-token
                         :owner_idt    owner_idt})))