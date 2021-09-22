(ns app.auth.views.log-in
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [app.components.page-nav :refer [page-nav]]
            [app.components.form-group :refer [form-group]]
            ["@smooth-ui/core-sc" :refer [Row Col FormGroup Label Input Button Box]]))

(defn log-in
      []
      (let [initial-values {:email "" :password ""}
            values (r/atom initial-values)]
           (fn []                                           ;; It needs to be inside an anonymous function so the 'let' would run only once and not every time when filling the texts fields
               [:> Row {:justify-content "center"}          ;; Wrapping all in a Row element allows to return all the content from this component
                [:> Col {:xs 12 :sm 6}
                 [page-nav {:center "Log in"}]
                 [form-group {:id     :email
                              :label  "Email"
                              :type   "email"
                              :values values}]
                 [form-group {:id     :password
                              :label  "Password"
                              :type   "password"
                              :values values}]
                 [:> Box {:display         "flex"
                          :justify-content "space-between"}
                  [:> Box {:py 1
                           :pr 2}
                   [:a {:href     "#sign-up"
                        :on-click #(rf/dispatch [:set-active-nav :sign-up])}
                    "New to Cheffy? Create an account!"]]
                  [:> Box
                   [:> Button {:on-click #(js/console.log "log-in")}
                    "Log in"]]]]])))