(ns app.nav.events
  (:require [re-frame.core :refer [reg-event-db reg-event-fx reg-fx path]]
            [app.router :as router]
            [app.helpers :as h]
            [day8.re-frame.tracing :refer-macros [fn-traced]]))

(def nav-interceptors [(path :nav)])                        ;; Specifies which node from the db should be returned

(reg-event-db
  :set-active-nav                                           ;; (rf/dispatch [:set-active-nav :recipes])
  nav-interceptors
  (fn-traced [nav [_ active-nav]]
             (assoc nav :active-nav active-nav)))

(reg-event-db
  :set-active-page
  nav-interceptors
  (fn-traced [nav [_ active-page]]
             (assoc nav :active-page active-page)))

(reg-event-fx
  :route-changed
  nav-interceptors
  (fn-traced [{:keys [db]} [_ {:keys [handler route-params]}]]
             (let [nav db
                   nav (assoc nav :active-page handler)]
                  (case handler
                        :recipes
                        {:db       nav
                         :dispatch [:get-recipes]}

                        :recipe
                        {:db       (assoc nav :active-recipe (keyword (:recipe-id route-params)))
                         :dispatch [:get-recipes]}

                        :inbox
                        {:db (assoc nav :active-inbox (keyword (:inbox-id route-params)))}

                        {:db (dissoc nav :active-recipe :active-inbox)})))) ;; Default case

(reg-fx
  :navigate-to                                              ;; (:navigate-to {:path "/saved})
  (fn-traced [{:keys [path]}]
             (router/set-token! path)))

(comment
  reg-event-db
  :close-modal
  nav-interceptors
  (fn-traced [nav _]
             (assoc nav :active-modal nil)))

(reg-event-db
  :close-modal
  (fn-traced [db _]
             (h/close-modal db)))

(reg-event-db
  :open-modal
  nav-interceptors
  (fn-traced [nav [_ modal-name]]
             (assoc nav :active-modal modal-name)))