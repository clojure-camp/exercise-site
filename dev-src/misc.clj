(ns misc
  (:require
   [malli.core :as m]
   [malli.error :as me]
   [exercise-ui.server.db :as db]
   [exercise-ui.exercises.schema :as schema]))

#_(me/humanize
   (m/explain
    schema/ExerciseInput
    (db/parse-exercise (slurp "../clojure-camp-exercises/exercises/morse-code.edn"))))

(defn validate-many
  [schema data]
  (->> data
       (keep (fn [d]
               (when-let [errors (me/humanize (m/explain schema d))]
                 [(or (:exercise/id d)
                      (::db/file-id (meta d))) errors])))))

#_(clojure.pprint/pprint (validate-many schema/ExerciseInput (db/exercises-raw)))
#_(clojure.pprint/pprint (validate-many schema/Exercise (db/exercises)))
