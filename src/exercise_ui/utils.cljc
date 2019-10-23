(ns exercise-ui.utils
  (:require
    [clojure.string :as string]))

(defn sanitize-name [name]
  (string/replace name #"[^a-zA-Z0-9]" ""))
