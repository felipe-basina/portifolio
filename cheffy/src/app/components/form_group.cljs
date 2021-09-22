(ns app.components.form-group
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [app.components.page-nav :refer [page-nav]]
            ["@smooth-ui/core-sc" :refer [FormGroup Label Input]]))

(defn form-group
      [{:keys [id label type values]}]
      [:> FormGroup
       [:> Label {:html-for id} label]
       [:> Input {:control   true
                  :id        id
                  :type      type
                  :value     (id @values)
                  :on-change #(swap! values assoc id (.. % -target -value))}]])