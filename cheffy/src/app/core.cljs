(ns app.core
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [app.db]
    ;; -- more to come --
            [app.nav.views.nav :refer [nav]]
            [app.nav.events]
            [app.nav.subs]
            [app.theme :refer [cheffy-theme]]
    ;; This is a react component so it needs to be imported using ""
            ["@smooth-ui/core-sc" :refer [Normalize ThemeProvider Button]]))

(defn app
      []
      [:<>                                                  ;; This is a react 'fragment' which allows to return more than 1 component | It can be replaced by 'div'
       [:> Normalize]                                       ;; Same as (r/adapt-react-class Normalize) | (r/adapt-react-class Button)
       [:> ThemeProvider {:theme cheffy-theme}
        [nav]]])

(defn ^:dev/after-load start
      []
      (rf/dispatch-sync [:initialize-db])                   ;; It runs async, so to guarantee all data will be available before rendering we need to force to be sync
      (r/render [app]
                (.getElementById js/document "app")))

(defn ^:export init
      []
      (start))
