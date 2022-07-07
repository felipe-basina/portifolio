(ns cheffy.account.db
  (:require [next.jdbc.sql :as sql]
            [next.jdbc :as jdbc]))

(defn create-account!
  [db account]
  (sql/insert! db :account account))

(defn delete-account!
  [db account]
  (-> (sql/delete! db :account account)
      ::jdbc/update-count
      (pos?)))
