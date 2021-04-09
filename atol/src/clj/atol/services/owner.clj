(ns atol.services.owner
  (:require [atol.db.core :as db]
            [buddy.hashers :as hashers]))

(defn get-owner-by-email [email]
  (db/get-owner {:email email}))

(defn save-new-owner [name email raw-pass avatar]
  (let [encrypted (hashers/derive raw-pass)
        new-owner {:owner_name name
                   :email      email
                   :owner_pass encrypted}]
    (db/create-owner! new-owner)))

(defn valid-owner? [raw-pass owner]
  (hashers/check raw-pass (:owner_pass owner)))