(ns authorizer.storage.protocol.storage)

(defprotocol AccountStorageProtocol
  "Provides operations to manage account state"
  (create-new-account! [this account] "Saves a new account with :active :available-limit :violations
                                       attributes as keywords in a new map")
  (get-account [this])
  (update-available-amount! [this transaction] "Updates the account :available-limit attribute accordingly
                                               to the transaction's attribute :amount")
  (add-transaction-violation! [this violation] "Saves a new violation into account's violations attribute")
  (reset-violations! [this] "Remove all existing violations"))


(defprotocol TransactionStorageProtocol
  "Provides operations to manage transaction state"
  (add-new-transaction! [this transaction] "Saves a new transaction with :merchant :amount :time attributes
                                            as keywords in a new map into a collection")
  (get-transactions [this]))