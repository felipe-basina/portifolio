(ns app.components.form-group
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [clojure.string :as str]
            [app.components.page-nav :refer [page-nav]]
            ["@smooth-ui/core-sc" :refer [FormGroup Label ControlFeedback Input Textarea]]))

;; Removing some attributes to avoid collision
(def skip [:__scTheme :theme :control :valid])

;; A reagent input: converts the values from input into a reagent input
(def r-input
  (r/reactify-component
    (fn [props]
        [:input (apply dissoc props skip)])))               ;; Apply the function `dissoc` to remove values in `skip` from `props`

(def r-textarea
  (r/reactify-component
    (fn [props]
        [:textarea (apply dissoc props skip)])))

(defn form-group
      [{:keys [id label type values element on-key-down] :or {element Input}}] ;; If `element` was not provided so set it as Input
      (let [errors @(rf/subscribe [:errors])
            input-error (get errors id)
            is-empty? (str/blank? (id @values))
            textarea (= element Textarea)
            input (= element Input)
            valid (if input-error false nil)
            validate (fn []
                         (if is-empty?
                           (rf/dispatch [:has-value? id])
                           (rf/dispatch [:clear-error id])))]
           [:> FormGroup
            [:> Label {:html-for id} label]
            [:> element {:as          (cond
                                        input r-input
                                        textarea r-textarea)
                         :control     true
                         :valid       valid
                         :on-blur     validate
                         :rows        (when textarea 6)
                         :id          id
                         :type        type
                         :value       (id @values)
                         :on-change   #(swap! values assoc id (.. % -target -value))
                         :on-key-down on-key-down}]
            (comment                                        ;; Before refactoring
              (if textarea
                [:> TextArea {:control     true
                              :valid       valid
                              :on-blur     validate
                              :rows        6
                              :id          id
                              :type        type
                              :value       (id @values)
                              :on-change   #(swap! values assoc id (.. % -target -value))
                              :on-key-down on-key-down}]
                [:> Input {:control     true
                           :valid       valid
                           :on-blur     validate
                           :id          id
                           :type        type
                           :value       (id @values)
                           :on-change   #(swap! values assoc id (.. % -target -value))
                           :on-key-down on-key-down}]))
            (when input-error
                  [:> ControlFeedback {:valid false}
                   input-error])]))