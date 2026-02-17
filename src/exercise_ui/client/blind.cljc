(ns exercise-ui.client.blind
  (:require
   [clojure.walk :as walk]))

(defn parse
  "Given some code, identifies potential nodes that can be blanked ('holes'), and returns a map with:
     :code - the code with potential blanks replaced by symbols like _0, _1 (which also have metadata indicating they are a hole)
     :holes - a vector of the original nodes that were replaced with holes
     :shuffled-holes - a shuffled version of the holes"
  [code]
  (let [mode (rand-nth [::seqs ::symbols])
        candidate-holes (atom [])
        result (walk/postwalk
                (fn [node]
                  ;; if allowing for symbols, rarely get seqs
                  ;; because there is a very high chance of the seq containing a hole
                  (if (or (and
                           (seq? node)
                           ;; doesn't have any holes as children
                           ;; TODO this should check if any *descendants* are holes
                           ;; not just children
                           (not (some (fn [x] (::index (meta x))) node))
                           ;; doesn't have any child seqs
                           (not (some seq? node))
                           ;; doesn't start with some special cases
                           (not (contains? #{'defn 'def 'require} (first node))))
                          (and (= mode ::symbols)
                               (symbol? node)))
                    (do (swap! candidate-holes conj
                               (vary-meta node
                                          assoc ::index (count @candidate-holes)))
                        (vary-meta
                         (symbol (str "_" (dec (count @candidate-holes))))
                         assoc ::index (dec (count @candidate-holes))))
                    node))
                code)
        target-hole-count (rand-nth [3 4 5])
        holes (take target-hole-count (distinct (shuffle @candidate-holes)))
        hole-indexes (set (map (fn [hole] (::index (meta hole))) holes))]
    {:code (->> result
                ;; put back the holes we didn't use
                (walk/postwalk
                 (fn [node]
                   (if-let [index (::index (meta node))]
                     (if (contains? hole-indexes index)
                       node
                       (get @candidate-holes index))
                     node))))
     :hole-indexes hole-indexes
     ;; must be in order
     :holes (->> @candidate-holes
                 (map-indexed vector)
                 (keep (fn [[index hole]]
                         (when (contains? hole-indexes index)
                           hole)))
                 vec)
     :shuffled-holes holes}))

#_(parse '(defn average [coll]
           (/ (reduce + coll)
              (count coll))))
#_{:code (defn average [coll]
           (/ _0 _1))
   :holes [(reduce + coll)
           (count coll)]
   :shuffled-holes [(reduce + coll)
                    (count coll)]}
