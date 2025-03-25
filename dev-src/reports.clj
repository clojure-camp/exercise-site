(ns reports
  (:require
   [exercise-ui.server.db :as db]))

(defn uses-and-teaches-freqs []
  (->> (db/exercises)
       (mapcat (fn [e]
                 (into (:exercise/uses e)
                       (:exercise/teaches e))))
       frequencies))

(defn topics []
  (->> (uses-and-teaches-freqs)
       (sort-by (comp str key))
       reverse
       (sort-by val)
       reverse
       (map (fn [[term count]]
              (println (format "%3d %s" count (pr-str term)))))
       doall))
