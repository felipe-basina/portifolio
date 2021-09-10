(ns app.core
  (:require [reagent.core :as r]
            [app.theme :refer [cheffy-theme]]
    ;; This is a react component so it needs to be imported using ""
            ["@smooth-ui/core-sc" :refer [Normalize ThemeProvider Button]]))

(defn app
      []
      [:<>                                                  ;; This is a react 'fragment' which allows to return more than 1 component | It can be replaced by 'div'
       [:> Normalize]                                       ;; Same as (r/adapt-react-class Normalize) | (r/adapt-react-class Button)
       [:> ThemeProvider {:theme cheffy-theme}
        [:> Button "Hello"]]])

(defn ^:dev/after-load start
      []
      (r/render [app]
                (.getElementById js/document "app")))

(defn ^:export init
      []
      (start))
