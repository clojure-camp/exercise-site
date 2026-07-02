(ns exercise-ui.client.blind
  (:require
   [rewrite-clj.node :as rw.node]
   [rewrite-clj.zip :as rw.z]))

;; use rewrite-clj for parsing because:
;;   clojure.edn/read-string - does not support #"regex" notation, #(fn ) notation and others
;;   cljs.reader/read-string - converts syntax like @foo to (deref foo) (and many similar other cases)

(defn ->hole-index [zloc]
  (::hole-index (meta (rw.z/node zloc))))

(defn child-locs [zloc]
  (loop [child (rw.z/down zloc)
         acc []]
    (if child
      (recur (rw.z/right child) (conj acc child))
      acc)))

(defn has-hole-child? [zloc]
  (some ->hole-index (child-locs zloc)))

(defn has-list-child? [zloc]
  (some (fn [c] (= :list (rw.z/tag c))) (child-locs zloc)))

(defn first-child-symbol [zloc]
  (when-let [c (->> (child-locs zloc)
                    (remove rw.z/whitespace-or-comment?)
                    first)]
    (when (= :token (rw.z/tag c))
      (let [v (rw.z/sexpr c)]
        (when (symbol? v) v)))))

(defn distinct-by
  "Distinct, but using f; modified version of distinct from core"
  [f coll]
  (let [step (fn step [xs seen]
               (lazy-seq
                ((fn [[v :as xs] seen]
                   (when-let [s (seq xs)]
                     (let [v' (f v)]
                       (if (contains? seen v')
                         (recur (rest s) seen)
                         (cons v (step (rest s) (conj seen v')))))))
                 xs seen)))]
    (step coll #{})))

#_(distinct-by :foo [{:foo 1 :bar :a} {:foo 2 :bar :b} {:foo 1 :bar :c}])

(defn parse
  "Given some code as a string, identifies potential nodes that can be blanked ('holes'), and returns a map with:
     :blind/code - a code string with potential blanks replaced by symbols like _0, _1
     :blind/tree - the parsed code, with metadata on the holes
     :blind/shuffled-holes - a vector of hole maps {:hole/index _ :hole/node _ :hole/string _}"
  [code-string]
  ;; - select a mode: blanking whole (leaf) seqs or individual symbols
  ;; - tree walk 1: identify every candidate node as a hole, add metadata
  ;; - choose a random subset of holes
  ;; - tree walk 2: replace chosen holes with _N symbol
  (let [mode (rand-nth [::seqs ::symbols])
        *candidate-holes (atom [])
        mark-candidate! (fn [*candidate-holes zloc]
                          (let [index (count @*candidate-holes)
                                node (rw.z/node zloc)]
                            (swap! *candidate-holes conj
                                   {:hole/index index
                                    :hole/node node
                                    :hole/string (rw.z/string zloc)})
                            (rw.z/replace zloc (vary-meta node assoc ::hole-index index))))
        tree-with-hole-candidates (-> code-string
                                      rw.z/of-string
                                      (rw.z/postwalk
                                       (fn [zloc]
                                         (let [tag (rw.z/tag zloc)]
                                           (cond
                                             (and (= :list tag)
                                                  (not (has-hole-child? zloc))
                                                  (not (has-list-child? zloc))
                                                  (not (contains? #{'defn 'def 'require}
                                                                  (first-child-symbol zloc))))
                                             (mark-candidate! *candidate-holes zloc)

                                             (and (= mode ::symbols)
                                                  (= :token tag)
                                                  (rw.z/sexpr-able? zloc)
                                                  (symbol? (rw.z/sexpr zloc))
                                                  (not (contains? #{'defn 'def 'require}
                                                                  (rw.z/sexpr zloc))))
                                             (mark-candidate! *candidate-holes zloc)

                                             :else
                                             zloc)))))

        target-hole-count (rand-nth [3 4 5])
        holes (->> @*candidate-holes
                   shuffle
                   (distinct-by :hole/string)
                   (take target-hole-count))
        chosen-hole-indexes (set (map :hole/index holes))
        tree (-> tree-with-hole-candidates
                 (rw.z/postwalk
                  (fn [zloc]
                    (if-let [hole-index (->hole-index zloc)]
                      (if (contains? chosen-hole-indexes hole-index)
                        (rw.z/replace zloc (-> (rw.node/token-node (symbol (str "_" hole-index)))
                                               (vary-meta assoc ::hole-index hole-index)))
                        (rw.z/replace zloc (-> (rw.z/node zloc)
                                               (vary-meta dissoc ::hole-index))))
                      zloc))))]

    {:blind/code (rw.z/root-string tree)
     :blind/tree tree
     :blind/shuffled-holes (vec holes)}))

#_(parse "(defn find-text [haystack needle]
            (re-matches #\"x\" haystack))")

#_(parse "(defn average [coll]
           (/ (reduce + coll)
              (count coll)))")

(defn preview
  "Given blinded-problem and {hole-index → hole-index} guesses, returns code with filled-in holes."
  [{:blind/keys [tree shuffled-holes]} hole->guess]
  (let [hole-index->node (zipmap (map :hole/index shuffled-holes)
                                 (map :hole/node shuffled-holes))]
    (-> tree
        (rw.z/postwalk
         (fn [zloc]
           (if-let [hole-index (->hole-index zloc)]
             (if-let [guess-index (hole->guess hole-index)]
               (rw.z/replace zloc (hole-index->node guess-index))
               zloc)
             zloc)))
        rw.z/root-string)))
