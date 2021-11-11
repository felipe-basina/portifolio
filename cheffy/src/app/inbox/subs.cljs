(ns app.inbox.subs
  (:require [re-frame.core :refer [reg-sub]]))

(defn reverse-comparison [a b]
      (compare b a))

(reg-sub
  :user-inboxes
  (fn [db _]
      (let [uid (get-in db [:auth :uid])]
           (sort-by :update-profile reverse-comparison (get-in db [:users uid :inboxes])))))

(reg-sub
  :inbox-messages
  (fn [db _]
      (let [inbox-id (get-in db [:nav :active-inbox])
            messages (get-in db [:inboxes inbox-id :messages])]
           (sort-by :created-at reverse-comparison messages))))

(reg-sub
  :conversation-with
  (fn [db _]
      (let [uid (get-in db [:auth :uid])
            inbox-id (get-in db [:nav :active-inbox])
            participants (get-in db [:inboxes inbox-id :participants])]
           (first (disj participants uid)))))