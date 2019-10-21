(ns exercise-ui.server.db
  (:require
    [clojure.java.io :as io]
    [clojure.string :as string]
    [rewrite-clj.parser :as rw.parser]
    [rewrite-clj.node :as rw.node]))

(def data-path "./data/exercises")

(defn parse-node [v]
  (case (rw.node/tag v)
    (:token :set)
    (rw.node/sexpr v)
    :list
    (rw.node/string v)
    :vector
    (->> (mapv parse-node (rw.node/children v))
         (filterv some?))
    (:whitespace :newline)
    nil
    v))

(defn parse-exercise [s]
  (->> s
       rw.parser/parse-string
       rw.node/children
       (remove (fn [node]
                 (#{:whitespace :newline :comment} (rw.node/tag node))))
       (partition 2)
       (map (fn [[k v]]
              [(rw.node/sexpr k) (parse-node v)]))
       (into {})))

(defn get-exercises []
  (->> (file-seq (io/file data-path))
       (filter (fn [f]
                 (.isFile f)))
       (map (juxt (memfn getName) slurp))
       (map (fn [[file-name s]]
              (-> (parse-exercise s)
                  (assoc :id (string/replace file-name #"\.edn$" "")))))))
