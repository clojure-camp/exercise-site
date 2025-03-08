(ns exercise-ui.server.db
  (:require
    [clojure.java.io :as io]
    [clojure.string :as string]
    [exercise-ui.config :refer [config]]
    [exercise-ui.exercises.parse :as parse]))

(defn exercises-raw []
  (->> (file-seq (io/file (:exercise-data-path config) "./exercises/"))
       (filter (fn [f]
                 (and
                   (.isFile f)
                   (string/ends-with? (.getName f) ".edn"))))
       (map (fn [f]
              (-> f
                  slurp
                  parse/parse-exercise
                  (with-meta {:file-id (string/replace (.getName f) #"\.edn$" "")}))))))

(defn exercises []
  (->> (exercises-raw)
       (map parse/normalize-exercise)))

(defn exercise-order []
  (->> (io/file (:exercise-data-path config) "./order.edn")
       slurp
       read-string))
