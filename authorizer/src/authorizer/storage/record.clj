(ns authorizer.storage.record
  (:require [authorizer.storage.protocol.storage :as storage]))

(defrecord StorageRecord [storage]
  storage/AccountStorageProtocol
  (create-new-account! [_ account]
    (swap! storage assoc :account {:active          (:active-card account)
                                   :available-limit (:available-limit account)
                                   :violations      []}))

  (get-account [_]
    (:account @storage))

  (update-available-amount! [_ transaction]
    (swap! storage update-in [:account :available-limit] - (:amount transaction)))

  (add-transaction-violation! [_ violation]
    (when (nil? (:account @storage))
      (swap! storage update-in [:account :violations] vec))
    (swap! storage update-in [:account :violations] conj violation))

  (reset-violations! [_]
    (when (not (nil? (:account @storage)))
      (swap! storage update-in [:account :violations] subvec 0 0)))

  storage/TransactionStorageProtocol
  (add-new-transaction! [_ transaction]
    (swap! storage update-in [:transactions] conj {:merchant (:merchant transaction)
                                                   :amount   (:amount transaction)
                                                   :time     (:time transaction)}))

  (get-transactions [_]
    (:transactions @storage)))