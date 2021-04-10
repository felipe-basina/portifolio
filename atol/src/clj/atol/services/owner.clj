(ns atol.services.owner
  (:require [atol.db.core :as db]
            [buddy.hashers :as hashers]
            [struct.core :as st]))

(def owner-schema
  [[:owner-name st/required st/string]
   [:email st/required st/email]
   [:owner-pass st/required st/string]
   [:owner-pass-confirm st/required [st/identical-to :owner-pass]]])

(defn get-owner-by-email [email]
  (db/get-owner {:email email}))

(defn save-new-owner [{:keys [owner-name email owner-pass]}]
  (let [encrypted (hashers/derive owner-pass)
        new-owner {:owner_name owner-name
                   :email      email
                   :owner_pass encrypted}]
    (db/create-owner! new-owner)))

(defn valid-owner? [owner]
  (st/validate owner owner-schema))