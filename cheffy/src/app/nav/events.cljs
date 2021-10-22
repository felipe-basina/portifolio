(ns app.nav.events
  (:require [re-frame.core :refer [reg-event-db reg-fx]]
            [app.router :as router]))

(reg-event-db
  :set-active-nav                                           ;; (rf/dispatch [:set-active-nav :recipes])
  (fn [db [_ active-nav]]
      (assoc-in db [:nav :active-nav] active-nav)))

(reg-event-db
  :set-active-page
  (fn [db [_ active-page]]
      (assoc-in db [:nav :active-page] active-page)))

(reg-event-db
  :route-changed
  (fn [db [_ {:keys [handler route-params]}]]
      (-> db
          (assoc-in [:nav :active-page] handler)
          (assoc-in [:nav :active-recipe] (keyword (:recipe-id route-params))))))

(reg-fx
  :navigate-to                                              ;; (:navigate-to {:path "/saved})
  (fn [{:keys [path]}]
      (router/set-token! path)))

(reg-event-db
  :close-modal
  (fn [db _]
      (assoc-in db [:nav :active-modal] nil)))

(reg-event-db
  :open-modal
  (fn [db [_ modal-name]]
      (assoc-in db [:nav :active-modal] modal-name)))