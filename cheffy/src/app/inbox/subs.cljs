(ns app.inbox.subs
  (:require [re-frame.core :refer [reg-sub]]))

(defn reverse-comparison [a b]
      (compare b a))

(reg-sub
  :user-inboxes
  (fn [db _]
      (let [uid (get-in db [:auth :uid])]
           (sort-by :update-profile reverse-comparison (get-in db [:users uid :inboxes])))))
