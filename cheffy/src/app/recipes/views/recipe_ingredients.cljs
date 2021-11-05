(ns app.recipes.views.recipe-ingredients
  (:require [re-frame.core :as rf]
            [reagent.core :as r]
            [clojure.str :as str]
            [app.components.modal :refer [modal]]
            [app.components.form-group :refer [form-group]]
            ["@smooth-ui/core-sc" :refer [Box Typography Row Col Button]]
            ["styled-icons/fa-solid/Plus" :refer [Plus]]))

(defn recipe-ingredients
      []
      (let [initial-values {:id nil :amount 0 :measure "" :name ""}
            values (r/atom initial-values)
            open-modal (fn [{:keys [modal-name ingredients]}]
                           (rf/dispatch [:open-modal modal-name])
                           (reset! values ingredient))
            save (fn [{:keys [id amount measure name]}]
                     (rf/dispatch [:upsert-ingredient {:id      (or id (:keyword (str "ingredient-" (random-uuid))))
                                                       :name    (str/trim name)
                                                       :amount  (js/parseInt amount)
                                                       :measure (str/trim measure)}])
                     (reset! values initial-values))
            ingredients @(rf/subscribe [:ingredients])
            author? @(rf/subscribe [:author])]
           (fn []
               [:> Box {:background-color "white"
                        :border-radius    10
                        :p                2
                        :pt               0}
                [:> Box {:display         "flex"
                         :justify-content "flex-start"}
                 [:> Box
                  [:> Typography {:variant "h5"
                                  :py      15
                                  :m       0}
                   "Ingredients"]]
                 [:> Box {:my 15
                          :pl 10}
                  [:> Button {:variant  "light"
                              :size     "small"
                              :on-click #(open-modal {:modal-name :ingredient-editor
                                                      :ingredient initial-values})}
                   [:> Plus {:size 12}]]]]
                [:> Box
                 (for [{:keys [id amount measure name]} ingredients]
                      ^{:key id}
                      [:> Box {:py 10}
                       [:> Row
                        [:> Col {:xs 3}
                         amount " " measure]
                        [:> Col
                         name]]])
                 (when author?
                       [modal {:modal-name :ingredient-editor
                               :header     "Ingredient"
                               :body       [:<>
                                            [:> Row
                                             [:> Col
                                              [form-group {:id     :amount
                                                           :label  "Amount"
                                                           :type   "number"
                                                           :values values}]]
                                             [:> Col
                                              [form-group {:id     :measure
                                                           :label  "Measure"
                                                           :type   "text"
                                                           :values values}]]]
                                            [form-group {:id     :name
                                                         :label  "Namet"
                                                         :type   "text"
                                                         :values values}]]
                               :footer     [:<>
                                            (when-let [ingredient-id (:id @values)]
                                                      [:a {:href     "#"
                                                           :on-click #(when (js/confirm "Are you sure?")
                                                                            (rf/dispatch [:delete-ingredient ingredient-id]))}
                                                       "Delete"])
                                            [:> Button {:variant  "light"
                                                        :mx       10
                                                        :on-click #(rf/dispatch [:close-modal])}
                                             "Cancel"]
                                            [:> Button {:on-click #(save @values)}
                                             "Save"]]}])]])))
