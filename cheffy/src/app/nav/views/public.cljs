(ns app.nav.views.public
  (:require [re-frame.core :as rf]
            [app.router :as router]
            [app.nav.views.nav-item :refer [nav-item]]
            ["@smooth-ui/core-sc" :refer [Box]]))

(defn public
      []
      (let [active-page @(rf/subscribe [:active-page])      ;; The return of rf/subscribe is an ATOM, so it is needed to deferred it to get the values
            nav-items [{:id       :recipes
                        :name     "Recipes"
                        :href     (router/path-for :recipes)
                        :dispatch #(rf/dispatch [:set-active-page :recipes])}
                       {:id       :become-a-chef
                        :name     "Chef"
                        :href     (router/path-for :become-a-chef)
                        :dispatch #(rf/dispatch [:set-active-page :become-a-chef])}
                       {:id       :sign-up
                        :name     "Sign up"
                        :href     (router/path-for :sign-up)
                        :dispatch #(rf/dispatch [:set-active-page :sign-up])}
                       {:id       :log-in
                        :name     "Login"
                        :href     (router/path-for :log-in)
                        :dispatch #(rf/dispatch [:set-active-nav :log-in])}]]
           [:> Box {:display         "flex"
                    :justify-content "flex-end"
                    :py              1}
            (for [{:keys [id name href dispatch]} nav-items]
                 ;; ^{:key (:id item)}                         ;; Added metadata to set the unique id of the component
                 [nav-item {:key         id
                            :id          id
                            :name        name
                            :href        href
                            :dispatch    dispatch
                            :active-page active-page}])]))
