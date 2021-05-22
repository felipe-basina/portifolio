(ns authorizer.core
  (:require [authorizer.adapter :as adapter]
            [authorizer.storage.storage-client :as storage-client]))

(defn -main
  "Entrypoint to run the application.
  Provides application configs"
  [& _]
  (adapter/process-input {:storage (storage-client/storage-client)}))
