(ns validate
  (:require
   [clojure.pprint :as pprint]
   [malli.core :as m]
   [malli.error :as me]
   [exercise-ui.server.db :as db]
   [exercise-ui.exercises.parse :as parse]
   [exercise-ui.exercises.schema :as schema]))

#_(me/humanize
   (m/explain
    schema/ExerciseInput
    (parse/parse-exercise-file "../clojure-camp-exercises/exercises/morse-code.clj")))

(defn validate-many
  [schema data]
  (->> data
       (keep (fn [d]
               (when-let [errors (me/humanize (m/explain schema d))]
                 [(or (:exercise/id d)
                      (:file-id (meta d))) errors])))))

#_(clojure.pprint/pprint (validate-many schema/ExerciseInput (db/exercises-raw)))
#_(clojure.pprint/pprint (validate-many schema/Exercise (db/exercises)))

;; can run from cli: lein run -m validate/validate-all!

(defn validate-all! []
  (let [exercises (db/exercises-raw)
        _ (println "Validating inputs..." (str "(" (count exercises) ")"))
        errors (validate-many schema/ExerciseInput exercises)]
    (if (seq errors)
      (pprint/pprint errors)
      (println "(No errors)")))

  (let [exercises (db/exercises)
        _ (println "Validating post-processed exercises..." (str "(" (count exercises) ")"))
        errors (validate-many schema/Exercise exercises)]
    (if (seq errors)
      (pprint/pprint errors)
      (println "(No errors)"))))
