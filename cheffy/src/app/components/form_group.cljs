(ns app.components.form-group
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [app.components.page-nav :refer [page-nav]]
            ["@smooth-ui/core-sc" :refer [FormGroup Label Input TextArea]]))

(defn form-group
      [{:keys [id label type values textarea on-key-down]}]
      [:> FormGroup
       [:> Label {:html-for id} label]
       (if textarea
         [:> TextArea {:control   true
                       :rows      6
                       :id        id
                       :type      type
                       :value     (id @values)
                       :on-change #(swap! values assoc id (.. % -target -value))}]
         [:> Input {:control     true
                    :id          id
                    :type        type
                    :value       (id @values)
                    :on-change   #(swap! values assoc id (.. % -target -value))
                    :on-key-down on-key-down}])])