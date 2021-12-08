(ns app.nav.subs
  (:require [re-frame.core :refer [reg-sub subscribe]]))

(reg-sub
  :nav
  (fn [db _]
      (get db :nav)))

;; When the event related to this specific keyword was fired then the function bellow will act getting the value
(reg-sub
  :active-nav
  ;(fn [_] (subscribe [:nav]))                               ;; another to do this is as following below
  :<- [:nav]
  (fn [nav _]
      (get nav :active-nav)))

(reg-sub
  :active-page
  (fn [_] (subscribe [:nav]))
  (fn [nav _]
      (get nav :active-page)))

(reg-sub
  :active-modal
  (fn [_] (subscribe [:nav]))
  (fn [nav _]
      (get nav :active-modal)))

(reg-sub
  :active-inbox
  :<- [:nav]
  (fn [nav _]
      (:active-inbox nav)))

(reg-sub
  :active-recipe
  :<- [:nav]
  (fn [nav _]
      (:active-recipe nav)))