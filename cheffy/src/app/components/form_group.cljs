(ns app.components.form-group
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [clojure.string :as str]
            [app.components.page-nav :refer [page-nav]]
            ["@smooth-ui/core-sc" :refer [FormGroup Label Input TextArea ControlFeedback]]))

(defn form-group
      [{:keys [id label type values textarea on-key-down]}]
      (let [errors @(rf/subscribe [:errors])
            input-error (get errors id)
            is-empty? (str/blank? (id @values))
            valid (if input-error false nil)
            validate (fn []
                         (if is-empty?
                           (rf/dispatch [:has-value? id])
                           (rf/dispatch [:clear-error id])))]
           [:> FormGroup
            [:> Label {:html-for id} label]
            (if textarea
              [:> TextArea {:control   true
                            :valid     valid
                            :on-blur   validate
                            :rows      6
                            :id        id
                            :type      type
                            :value     (id @values)
                            :on-change #(swap! values assoc id (.. % -target -value))}]
              [:> Input {:control     true
                         :valid       valid
                         :on-blur     validate
                         :id          id
                         :type        type
                         :value       (id @values)
                         :on-change   #(swap! values assoc id (.. % -target -value))
                         :on-key-down on-key-down}])
            (when input-error
                  [:> ControlFeedback {:valid false}
                   input-error])]))