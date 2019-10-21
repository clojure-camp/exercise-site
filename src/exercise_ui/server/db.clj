(ns exercise-ui.server.db
  (:require
    [clojure.java.io :as io]
    [clojure.string :as string]))

(def data-path "./data/exercises")

(defn get-exercises []
  (->> (file-seq (io/file data-path))
       (filter (fn [f]
                 (.isFile f)))
       (map (juxt (memfn getName) slurp))
       (map (fn [[file-name s]]
              (-> (read-string s)
                  (assoc :id (string/replace file-name #"\.edn$" ""))
                  (update :solution pr-str))))))

