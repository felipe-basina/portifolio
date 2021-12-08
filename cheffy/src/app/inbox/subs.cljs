(ns app.inbox.subs
  (:require [re-frame.core :refer [reg-sub]]))

(defn reverse-comparison [a b]
      (compare b a))

(reg-sub
  :inboxes
  (fn [db _]
      (:inboxes db)))

(reg-sub
  :user-inboxes
  :<- [:user]
  (fn [user _]
      (sort-by :update-profile reverse-comparison (:inboxes user))))

(reg-sub
  :inbox-messages
  :<- [:inboxes]
  :<- [:active-inbox]
  (fn [[inboxes active-inbox] _]
      (let [messages (get-in inboxes [active-inbox :messages])]
           (sort-by :created-at reverse-comparison messages))))

(reg-sub
  :conversation-with
  :<- [:inboxes]
  :<- [:active-inbox]
  :<- [:uid]
  (fn [[inboxes active-inbox uid] _]
      (let [participants (get-in inboxes [active-inbox :participants])]
           (first (disj participants uid)))))