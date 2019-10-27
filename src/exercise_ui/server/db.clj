(ns exercise-ui.server.db
  (:require
    [clojure.java.io :as io]
    [clojure.string :as string]
    [bloom.commons.env :as env]
    [rewrite-clj.parser :as rw.parser]
    [rewrite-clj.node :as rw.node]))

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
  (->> (file-seq (io/file (env/get :exercise-data-path)))
       (filter (fn [f]
                 (and (.isFile f)
                      (string/ends-with? (.getName f) ".edn"))))
       (map (juxt (memfn getName) slurp))
       (map (fn [[file-name s]]
              (-> (parse-exercise s)
                  (assoc :id (string/replace file-name #"\.edn$" "")))))))

(defn user-file [user-id]
  (io/file (env/get :user-data-path) (str user-id ".edn")))

(defn get-user [user-id]
  (let [f (user-file user-id)]
    (when-not (.exists f)
      (spit f (pr-str {:user-id user-id
                       :progress {}})))
    (read-string (slurp f))))

(defn set-exercise-status!
  [user-id exercise-id status]
  (spit (user-file user-id)
        (-> (get-user user-id)
            (assoc-in [:progress exercise-id :status] status)
            (cond->
              (= :started status)
              (assoc-in [:progress exercise-id :started-at] (java.util.Date.))
              (= :completed status)
              (assoc-in [:progress exercise-id :completed-at] (java.util.Date.))
              (= :reviewed status)
              (assoc-in [:progress exercise-id :reviewed-at] (java.util.Date.))))))

(defn users-progress
  []
  (->> (file-seq (io/file (env/get :user-data-path)))
      (filter (fn [f] (and (.isFile f)
                          (string/ends-with? (.getName f) ".edn"))))
      (map (comp read-string slurp))))
