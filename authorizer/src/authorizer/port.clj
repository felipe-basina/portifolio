(ns authorizer.port
  (:require [authorizer.controller :as controller]
            [authorizer.domain.record :as record]
            [authorizer.util.util :as util]))

(defn authorize
  "Authorizes an input which means create a new account or save a new transaction and decrement
  :available-limit value from an existing account.
  This namespace is aimed to execute the following steps to accomplish the state above:
  1. Converts input data to a model the core business rules may understand
  2. Process input accordingly to the input type: account | transaction
  3. Converts output data to satisfy the adapter expectations"
  [input storage]
  (-> input
      (util/convert-to-map)
      (record/model-as-record)
      (controller/process-input storage)
      (util/convert-to-output-model)
      (util/convert-to-json)))