(ns exercise-ui.utils
  (:require
    [clojure.string :as string]))

(defn sanitize-user-id [id]
  (string/replace id #"[^a-zA-Z0-9]" ""))
