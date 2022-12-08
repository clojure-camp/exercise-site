(ns exercise-ui.utils
  (:require
    [clojure.string :as string]))

(defn sanitize-user-id [id]
  (string/replace id #"[^a-zA-Z0-9]" ""))

(defn parse-backticks
  [string]
  (->> (string/split string #"`")
       (map-indexed (fn [i s]
                      (if (even? i)
                        s
                        [:code s])))))


