(ns exercise-ui.server.db
  (:require
    [clojure.java.io :as io]
    [clojure.string :as string]
    [rewrite-clj.parser :as rw.parser]
    [rewrite-clj.node :as rw.node]
    [exercise-ui.config :refer [config]]))

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
  (->> (file-seq (io/file (:exercise-data-path config) "./exercises/"))
       (filter (fn [f]
                 (and (.isFile f)
                      (string/ends-with? (.getName f) ".edn"))))
       (map (juxt (memfn getName) slurp))
       (map (fn [[file-name s]]
              (-> (parse-exercise s)
                  (assoc :id (string/replace file-name #"\.edn$" "")))))))

(defn get-exercise-order []
  (->> (io/file (:exercise-data-path config) "./order.edn")
       slurp
       read-string))


