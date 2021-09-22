(ns app.auth.events
  (:require [re-frame.core :refer [reg-event-fx]]))

;; Will allow to return multiple events (= effects) as cofx (= maps of events)
(reg-event-fx                                               ;; Using 'reg-event-fx' instead of 'reg-event-db' will allow to dispatch another event from this one (this one = event)
  :log-in
  ;; cofx {:db db :dispatch [:set-active-nav :saved]}
  (fn [{:keys [db]} [_ {:keys [email password]}]]
      (let [user (get-in db [:users email])
            correct-password? (= (get-in user [:profile :password]) password)]
           (cond
             (not user)                                     ;; If there is no user it returns a map, which contains the db. When working with reg-event-db it will return the db
             {:db (assoc-in db [:errors :email] "User not found")}

             (not correct-password?)
             {:db (assoc-in db [:errors :email] "Wrong password")}

             correct-password? {:db       (-> db
                                              (assoc-in [:auth :uid] email)
                                              (update-in [:errors] dissoc :email))
                                :dispatch [:set-active-nav :saved]}))))
