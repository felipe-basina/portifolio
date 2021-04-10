(ns atol.services.contact
  (:require [atol.db.core :as db]
            [buddy.hashers :as hashers]
            [struct.core :as st]))

(defn get-contacts-by-owner-id [owner-id]
  (db/get-contacts {:owner_idt owner-id}))