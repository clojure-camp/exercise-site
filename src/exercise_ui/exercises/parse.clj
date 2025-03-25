(ns exercise-ui.exercises.parse
  (:require
    [clojure.java.io :as io]
    [clojure.edn :as edn]
    [clojure.string :as string]
    [com.rpl.specter :as x]))

;; exercises are clj files that follow a special format:
;;   - files are split into chunks, using a line ";; --- [a path]" as the delimiter
;;   - the first chunk is parsed as EDN and expected to return a map
;;   - the rest of the chunks are parsed as text, and injected into the map at the given [path]
;;     - these additional chunks are meant for code
;;   - in the end, the result should follow the schema defined in ./schema.clj
;;
;;   Design Note:
;;     - wanted to achieve:
;;         - easily parseable metadata
;;         - easy to run solution code (ex. with babashka)

(defn parse-exercise
  [s]
  (->> (string/split s #"\n")
       (reduce (fn [memo line]
                 (if-let [[_ path] (re-matches #"^;; --- (.+)" line)]
                   (->> memo
                        (x/setval [x/END] [^{::path (edn/read-string path)} []]))
                   (->> memo
                        (x/setval [x/LAST x/END] [line]))))
               [^{::path ::metadata} []])
       (map (fn [lines]
              {:chunk/path (::path (meta lines))
               :chunk/text (string/join "\n" lines)}))
       (reduce (fn [memo {:keys [chunk/path chunk/text]}]
                 ;; metadata parsed as edn
                 ;; others just as text
                 (if (= ::metadata path)
                   (-> (edn/read-string text)
                       (assoc :solution []))
                   (assoc-in memo path text)))
               {})))

(defn parse-exercise-file
  [f]
  (let [f (io/file f)]
    (with-meta
      (parse-exercise (slurp f))
      {:file-id (string/replace (.getName f) #"\.clj" "")
       :file-path (.getAbsolutePath f)})))

#_(parse-exercise-file "../clojure-camp-exercises/exercises/poker_rank.clj")
#_(parse-exercise-file "../clojure-camp-exercises/exercises/morse-code.clj")

(defn normalize-exercise
  [e]
  ;; expects each exercise to have meta: file-id, file-path
  (-> e
      ;; add exercise namespace to top level keys
      (update-keys (fn [k]
                     (keyword "exercise" (name k))))
      (assoc :exercise/id (:file-id (meta e)))
      (update :exercise/category
              (fn [k]
                (keyword "exercise.category" (name k))))
      (update :exercise/difficulty
              (fn [k]
                (keyword "exercise.difficulty" (name k))))
      (update :exercise/related
              (fn [v] (or v #{})))
      (update :exercise/teaches
              (fn [v] (or v #{})))
      (update :exercise/solution
              (fn [v]
                (cond
                  (nil? v)
                  nil
                  (vector? v)
                  v
                  :else
                  [v])))
      (update :exercise/test-cases (fn [v] (or v [])))
      (dissoc :exercise/source)
      (update :exercise/function-template
              (fn [node]
                (cond
                  (vector? node)
                  node
                  (string? node)
                  [node])))))

#_(normalize-exercise
   (parse-exercise-file "../clojure-camp-exercises/exercises/poker_rank.clj"))
