(ns app.nav.views.nav-item
  (:require ["@smooth-ui/core-sc" :refer [Box]]))

(defn nav-item
      [{:keys [id name href]}]
      [:> Box {:key  id                                     ;; The syntax :> does not work with list comprehension, can be also substitute by (r/adapt-react-class Box) without the :div
               :as   "a"                                    ;; Alias to HTML anchor
               :href href
               :ml   2                                      ;; Margin left
               :pb   10}                                    ;; Padding bottom
       name])
