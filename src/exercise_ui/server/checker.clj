(ns exercise-ui.server.checker
  (:require
   [clojure.spec.alpha :as s]
   [exercise-ui.server.db :as db]))

(s/def ::title string?)
(s/def ::category #{:starter :learning-functions :synthesis})
(s/def ::instructions (s/or :simple string?
                            :complex vector?))
(s/def ::tests (s/or :single string? :complex (s/coll-of string?)))
(s/def ::solution (s/or :single string? :complex (s/coll-of string?)))
(s/def ::teaches (s/and set?
                        (s/coll-of (s/or :concept keyword?
                                         :func symbol?
                                         :other string?))))
(s/def ::uses (s/and set?
                     (s/coll-of (s/or :concept keyword?
                                      :func symbol?
                                      :other string?))))

(s/def ::exercise
  (s/keys :req-un [::title ::category ::instructions ::uses]
          :opt-un [::tests ::solution ::teaches]))

(defn print-problem
  [{:keys [pred path val]}]
  (println
    (if (empty? path)
      "overall"
      path)
    (if (coll? pred)
      (let [[f parms [contains-call arg k]] pred]
        (if (and (= f 'clojure.core/fn)
                 (= parms '[%])
                 (= contains-call 'clojure.core/contains?)
                 (= arg '%)
                 (keyword? k))
          (str "missing key " k)
          pred))
      pred)))

(defn check-exercises
  []
  (doseq [exercise (db/get-exercises)]
    (when-let [probs (s/explain-data ::exercise exercise)]
      (println "Issues in " (exercise :id))
      (doseq [prob (::s/problems probs)]
        (print-problem prob)))))
