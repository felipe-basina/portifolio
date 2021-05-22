(ns authorizer.storage.storage-client
  (:require [authorizer.storage.record :as record]))

(defn storage-client []
  (record/->StorageRecord (atom {:account nil :transactions nil})))