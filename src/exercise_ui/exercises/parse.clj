(ns exercise-ui.exercises.parse
  (:require
    [clojure.string :as string]
    [rewrite-clj.parser :as rw.parser]
    [rewrite-clj.node :as rw.node]))

(defn collapse-leading-whitespace [s]
  (string/replace s #"\n[ ]+" "\n  "))

(defn parse-node [v]
  (case (rw.node/tag v)
    (:token :set)
    (rw.node/sexpr v)
    :comment
    (rw.node/string v)
    :list
    (collapse-leading-whitespace (rw.node/string v))
    :vector
    (->> (mapv parse-node (rw.node/children v))
         (filterv some?))
    :map
    (rw.node/sexpr v)
    :multi-line
    (collapse-leading-whitespace (rw.node/sexpr v))
    (:whitespace :newline)
    nil
    v))

(defn parse-exercise
  [s]
  ;; edn/read-string does not support:
  ;;  - regex literals (ex #"[a-z]")
  ;;  - ...
  (->> s
       rw.parser/parse-string
       rw.node/children
       ;; remove top-level whitespace, newlines and comments
       (remove (fn [node]
                 (#{:whitespace :newline :comment} (rw.node/tag node))))
       ;; transform into list of kv pairs
       (partition 2)
       (map (fn [[k v]]
              [(rw.node/sexpr k) (parse-node v)]))
       ;; convert into a map
       (into {})))

#_(parse-exercise (slurp "../clojure-camp-exercises/exercises/morse-code.edn"))

(defn normalize-exercise
  [e]
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

