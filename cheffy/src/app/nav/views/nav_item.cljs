(ns app.nav.views.nav-item
  (:require ["@smooth-ui/core-sc" :refer [Box]]))

(defn nav-item
      [{:keys [id name href dispatch active-page]}]
      [:> Box {:key           id                            ;; The syntax :> does not work with list comprehension, can be also substitute by (r/adapt-react-class Box) without the :div
               :as            "a"                           ;; Alias to HTML anchor
               :href          href
               :on-click      dispatch
               :ml            3                             ;; Margin left
               :pb            10                            ;; Padding bottom
               :border-bottom (when (= active-page id) "2px solid #102A43")}
       name])
