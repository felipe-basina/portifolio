(ns authorizer.domain.protocol.authorize)

(defprotocol AuthorizeProtocol
  (process [this storage] "Process authorization which means create a new account
                           or add transactions"))

(defprotocol AccountStateProtocol
  (get-account-state [this storage] "Retrieves current account state")
  (reset-violations! [this storage] "Remove all existing violations"))