(ns atol.tags.contact
  (:require [selmer.parser :as p]))

(p/add-tag! :phone-prefix-55
            (fn [_ _]
              (str "55")))