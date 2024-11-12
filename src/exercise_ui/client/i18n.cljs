(ns exercise-ui.client.i18n
  (:require [reagent.core :as r]))

(def languages 
  {:en-US "English" 
   :pt-BR "Português do Brasil"})

(defonce language 
  (r/atom (or (keyword (.getItem js/localStorage "language"))
              (->> js/navigator.languages
                   (map keyword)
                   (filter languages)
                   first)
              :en-US)))

(defn set-language! [lang] 
  (.setItem js/localStorage "language" (name lang))
  (reset! language lang))

(defn value [i18n-map]
  (get i18n-map @language i18n-map))
