(ns exercise-ui.server.db
  (:require
    [clojure.java.io :as io]
    [clojure.string :as string]
    [rewrite-clj.parser :as rw.parser]
    [rewrite-clj.node :as rw.node]))

(def user-data-path "./data/users")
(def exercise-data-path "./data/exercises")

(defn collapse-leading-whitespace [s]
  (string/replace s #"\n[ ]+" "\n  "))

(defn parse-node [v]
  (case (rw.node/tag v)
    (:token :set)
    (rw.node/sexpr v)
    :list
    (collapse-leading-whitespace (rw.node/string v))
    :vector
    (->> (mapv parse-node (rw.node/children v))
         (filterv some?))
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
  (->> (file-seq (io/file exercise-data-path))
       (filter (fn [f]
                 (.isFile f)))
       (map (juxt (memfn getName) slurp))
       (map (fn [[file-name s]]
              (-> (parse-exercise s)
                  (assoc :id (string/replace file-name #"\.edn$" "")))))))

(defn user-file [name]
  (io/file user-data-path (str name ".edn")))

(defn get-user [name]
  (let [f (user-file name)]
    (when-not (.exists f)
      (spit f (pr-str {:name name
                       :progress {}})))
    (read-string (slurp f))))

(defn set-exercise-status!
  [user-name exercise-id status]
  (spit (user-file user-name)
        (-> (get-user user-name)
            (assoc-in [:progress exercise-id] status))))
