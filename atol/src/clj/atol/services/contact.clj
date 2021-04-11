(ns atol.services.contact
  (:require [atol.db.core :as db]
            [struct.core :as st]))

(def contact-schema
  [[:contact-name st/required st/string]
   [:contact-email st/required st/email]
   [:contact-phone st/required st/string]])

(def phone-pattern #"[0-9]{11,11}")

(defn valid-phone? [contact]
  (re-matches phone-pattern (:contact-phone contact)))

(defn get-contacts-by-owner-id [owner-id]
  (db/get-contacts {:owner_idt owner-id}))