(ns app.core
  (:require [reagent.core :as r]
            [app.theme :refer [cheffy-theme]]
            ["@smooth-ui/core-sc" :refer [Normalize Button ThemeProvider]]))

(defn app
      []
      [:<>
       [:> Normalize]                                       ; Alternative to (r/adapt-react-class Normalize)
       [:> ThemeProvider {:theme cheffy-theme}
        [:> Button {:variant "danger"} "Hello"]
        [:div "Cheffy"]]])

(defn ^:dev/after-load start
      []
      (r/render [app]
                (.getElementById js/document "app")))

(defn ^:export init
      []
      (start))
