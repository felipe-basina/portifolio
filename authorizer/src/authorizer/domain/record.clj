(ns authorizer.domain.record
  (:require [authorizer.domain.account :as acc]
            [authorizer.domain.protocol.authorize :as authorize]
            [authorizer.domain.transaction :as trans]))

(defrecord Account [active-card available-limit]
  authorize/AuthorizeProtocol
  (process [this storage]
    (acc/process-account this storage))

  authorize/AccountStateProtocol
  (get-account-state [_ storage]
    (acc/get-account-state storage))

  (reset-violations! [_ storage]
    (acc/reset-violations! storage)))

(defrecord Transaction [merchant amount time]
  authorize/AuthorizeProtocol
  (process [this storage]
    (trans/process-transaction this storage))

  authorize/AccountStateProtocol
  (get-account-state [_ storage]
    (acc/get-account-state storage))

  (reset-violations! [_ storage]
    (acc/reset-violations! storage)))

(defn account-model
  "Returns an account record"
  [{:keys [active-card available-limit]}]
  (->Account active-card available-limit))

(defn transaction-model
  "Returns a transaction record"
  [{:keys [merchant amount time]}]
  (->Transaction merchant amount time))

(defn model-as-record
  "Returns a record accordingly to the model data type"
  [model]
  (cond
    (contains? model :transaction) (transaction-model (:transaction model))
    :else (account-model (:account model))))