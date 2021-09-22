(ns app.auth.events
  (:require [re-frame.core :refer [reg-event-fx reg-event-db]]))

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

;; TODO: add validation to guarantee unique email
(reg-event-fx
  :sign-up
  (fn [{:keys [db]} [_ {:keys [first-name last-name email password]}]]
      {:db       (-> db
                     (assoc-in [:auth :uid] email)
                     (assoc-in [:users email] {:id      email
                                               :profile {:first-name first-name
                                                         :last-name  last-name
                                                         :email      email
                                                         :password   password
                                                         :image      "img/avatar.jpg"}
                                               :saved   #{}
                                               :inboxes {}}))
       :dispatch [:set-active-nav :saved]}))

(reg-event-fx
  :logout
  (fn [{:keys [db]} _]
      {:db       (assoc-in db [:auth :uid] nil)
       :dispatch [:set-active-nav :recipes]}))

;; The update-in will allow to keep all the remain values in the map the same while updating only the specific keys
;; So it merges only the specific values into the existing map
(reg-event-db
  :update-profile
  (fn [db [_ profile]]
      (let [uid (get-in db [:auth :uid])]
           (update-in db [:users uid :profile] merge (select-keys profile [:first-name :last-name])))))

(reg-event-fx
  :delete-account
  (fn [{:keys [db]} _]
      (let [uid (get-in db [:auth :uid])]
           {:db       (-> db
                          (assoc-in [:auth :uid] nil)
                          (update-in [:users] dissoc uid))
            :dispatch [:set-active-nav :recipes]})))