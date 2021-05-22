(ns authorizer.controller
  (:require [authorizer.domain.protocol.authorize :as authorize]))

(defn process-input
  "Process input accordingly to the model business rules implementation.
  This namespace is aimed to execute the authorization flow through abstraction"
  [input storage]
  (authorize/reset-violations! input storage)
  (authorize/process input storage)
  (authorize/get-account-state input storage))