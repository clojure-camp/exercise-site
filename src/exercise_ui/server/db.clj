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
  (->> (file-seq (io/file (env/get :exercise-data-path)))
       (filter (fn [f]
                 (and (.isFile f)
                      (string/ends-with? (.getName f) ".edn"))))
       (map (juxt (memfn getName) slurp))
       (map (fn [[file-name s]]
              (-> (parse-exercise s)
                  (assoc :id (string/replace file-name #"\.edn$" "")))))))

(defn get-exercise-order []
  (->> (io/file (env/get :exercise-data-path) "../order.edn")
       slurp
       read-string))

(defn user-file [user-id]
  (io/file (env/get :user-data-path) (str user-id ".edn")))

(defn get-user [user-id]
  (let [f (user-file user-id)]
    (when-not (.exists f)
      (spit f (pr-str {:user-id user-id
                       :progress {}})))
    (read-string (slurp f))))

(defn -file-agent
  [file-name]
  (add-watch
    (agent nil) :file-writer
    (fn [key agent old new]
      (spit file-name new))))

(def file-agent (memoize -file-agent))

(defn async-spit
  [file-name content]
  (send (file-agent file-name) (constantly content)))

(defn set-exercise-status!
  [user-id exercise-id status]
  (let [now (java.util.Date.)]
    (async-spit
      (user-file user-id)
      (-> (get-user user-id)
          (assoc-in [:progress exercise-id :status] status)
          (cond->
            (= :started status)
            (assoc-in [:progress exercise-id :started-at] now)
            (= :completed status)
            (-> (assoc-in [:progress exercise-id :completed-at] now)
                (update-in [:progress exercise-id :started-at] (fnil identity now)))
            (= :reviewed status)
            (-> (assoc-in [:progress exercise-id :reviewed-at] now)
                (update-in [:progress exercise-id :completed-at] (fnil identity now))
                (update-in [:progress exercise-id :started-at] (fnil identity now))))))))

(defn users-progress
  []
  (->> (file-seq (io/file (env/get :user-data-path)))
      (filter (fn [f] (and (.isFile f)
                          (string/ends-with? (.getName f) ".edn"))))
      (map (comp read-string slurp))))
