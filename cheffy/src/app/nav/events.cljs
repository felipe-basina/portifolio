(ns app.nav.events
  (:require [re-frame.core :refer [reg-event-db reg-fx]]
            [app.router :as router]))

(reg-event-db
  :set-active-nav                                           ;; (rf/dispatch [:set-active-nav :recipes])
  (fn [db [_ active-nav]]
      (assoc-in db [:nav :active-nav] active-nav)))

(reg-event-db
  :route-changed
  (fn [db [_ {:keys [handler]}]]
      (assoc-in db [:nav :active-page] handler)))

(reg-fx
  :navigate-to                                              ;; (:navigate-to {:path "/saved})
  (fn [{:keys [path]}]
      (router/set-token! path)))