(ns app.core
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [app.db]
            [app.router :as router]
    ;; -- auths --
            [app.auth.views.log-in :refer [log-in]]
            [app.auth.views.profile :refer [profile]]
            [app.auth.views.sign-up :refer [sign-up]]
            [app.auth.events]
            [app.auth.subs]
    ;; -- become-a-chef --
            [app.become-a-chef.views.become-a-chef :refer [become-a-chef]]
    ;; -- inboxes --
            [app.inbox.views.inboxes :refer [inboxes]]
    ;; -- nav --
            [app.nav.views.nav :refer [nav]]
            [app.nav.events]
            [app.nav.subs]
    ;; -- recipe --
            [app.recipes.views.recipes-page :refer [recipes-page]]
            [app.recipes.views.recipe-page :refer [recipe-page]]
            [app.recipes.subs]
            [app.theme :refer [cheffy-theme]]
    ;; This is a react component so it needs to be imported using ""
            ["@smooth-ui/core-sc" :refer [Normalize ThemeProvider Grid Row Col]]))

(defn pages
      [page-name]
      (case page-name
            :log-in [log-in]
            :profile [profile]
            :sign-up [sign-up]
            :become-a-chef [become-a-chef]
            :inboxes [inboxes]
            :recipes [recipes-page]
            :recipe [recipe-page]
            [recipes-page]))

(defn app
      []
      (let [active-page @(rf/subscribe [:active-page])]
           [:<>                                             ;; This is a react 'fragment' which allows to return more than 1 component | It can be replaced by 'div'
            [:> Normalize]                                  ;; Same as (r/adapt-react-class Normalize) | (r/adapt-react-class Button)
            [:> ThemeProvider {:theme cheffy-theme}
             [:> Grid {:fluid false}                        ;; fluid = false will not take the full screen
              [:> Row
               [:> Col
                [nav]
                [pages active-page]]]]]]))

(defn ^:dev/after-load start
      []
      (r/render [app]
                (.getElementById js/document "app")))

(defn ^:export init
      []
      (router/start!)
      (rf/dispatch-sync [:initialize-db])                   ;; It runs async, so to guarantee all data will be available before rendering we need to force to be sync
      (start))
