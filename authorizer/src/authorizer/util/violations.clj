(ns authorizer.util.violations)

(def ACCOUNT_ALREADY_INITIALIZED "account-already-initialized")
(def ACCOUNT_NOT_INITIALIZED "account-not-initialized")
(def CARD_NOT_ACTIVE "card-not-active")
(def INSUFFICIENT_LIMIT "insufficient-limit")
(def HIGH_FREQUENCY_SMALL_INTERVAL "high-frequency-small-interval")
(def DOUBLED_TRANSACTION "doubled-transaction")

(def TRANSACTION_VIOLATIONS [ACCOUNT_NOT_INITIALIZED
                             CARD_NOT_ACTIVE
                             INSUFFICIENT_LIMIT
                             HIGH_FREQUENCY_SMALL_INTERVAL
                             DOUBLED_TRANSACTION])