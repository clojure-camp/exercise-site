(ns exercise-ui.exercises.parse
  (:require
    [clojure.java.io :as io]
    [clojure.edn :as edn]
    [clojure.string :as string]
    [com.rpl.specter :as x]
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

#_(defn parse-exercise
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
