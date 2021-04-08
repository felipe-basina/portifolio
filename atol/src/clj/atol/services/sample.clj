(ns atol.services.sample
  (:require [atol.db.core :as db]))

(defn get-samples []
  (db/get-samples))

(defn create-sample! [{:keys [description]}]
  (db/create-sample! {:description description
                      :created_on (java.util.Date.)}))
