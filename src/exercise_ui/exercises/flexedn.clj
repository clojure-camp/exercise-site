(ns exercise-ui.exercises.flexedn
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

(defn parse
  "Parse string as a super-set of edn"
  ;; edn/read-string does not support:
  ;;  - regex literals (ex #"[a-z]")
  ;;  - @derefs
  ;;  - ...
  ;; using rewrite-clj to parse it as clojure
  [s]
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

