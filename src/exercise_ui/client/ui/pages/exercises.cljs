(ns exercise-ui.client.ui.pages.exercises
  (:require
   [bloom.commons.pages :as pages]
   [clojure.set :as set]
   [exercise-ui.client.i18n :as i18n]
   [exercise-ui.client.ui.partials.teachable :refer [teachable-view]]
   [re-frame.core :refer [subscribe]]))

(def difficulty->n {:exercise.difficulty/low 1 :exercise.difficulty/mid 2 :exercise.difficulty/high 3})

(defn intersects?
  [a b]
  (some? (seq (set/intersection a b))))

(defn depends?
  "Does exercise `a` depend on exercise `b` being learnt first? If
  both exercises use things the other teaches, don't count that as a
  dependency."
  [a b]
  (and (intersects? (:exercise/uses a) (:exercise/teaches b))
       (not (intersects? (:exercise/teaches a) (:exercise/uses b)))))

(defn group-dependencies
  [exercises]
  (map
    (fn [exercise]
      (assoc exercise
             :exercise/dependencies (set (map :exercise/id (filter (partial depends? exercise) exercises)))))
    exercises))

(defn first-index-where
  [f v]
  (loop [i 0]
    (cond
      (= i (count v)) nil
      (f (get v i)) i
      :else (recur (inc i)))))

(defn sort-by-deps
  [exercises]
  (loop [exercises (vec exercises)
         i 0]
    (cond
      (= i (count exercises))
      exercises

      (empty? (:exercise/dependencies (get exercises i)))
      (recur exercises (inc i))

      :else
      (let [exercise (get exercises i)
            prev (subvec exercises 0 i)]
        (if-let [idx (first-index-where (fn [ex] (contains? (:exercise/dependencies ex) (:exercise/id exercise))) prev)]
          (recur (into (conj (subvec exercises 0 idx) exercise)
                       (into (subvec exercises idx i)
                             (subvec exercises (inc i))))
                 (inc idx))
          (recur exercises (inc i)))))))

(defn sort-exercises
  [exercises]
  (->> exercises
      (sort-by (comp i18n/value :exercise/title))
      (sort-by (comp difficulty->n :exercise/difficulty))
      group-dependencies
      (sort-by (comp count :exercise/dependencies))
      sort-by-deps))

(defn exercises-view
  [heading exercises]
  [:<>
   [:thead
    [:tr [:th {:tw "pt-6"}]]
    [:tr
     [:th {:tw "p-1 text-left underline text-xl whitespace-nowrap "} heading]
     [:th {:tw "p-1 text-left underline text-xl"} #_"Teaches"]
     #_[:th {:tw "p-1 text-left underline text-xl"} "Uses"]
     #_[:th]]]
   [:tbody
    (doall
      (for [exercise exercises]
        ^{:key (:exercise/id exercise)}
        [:tr.exercise
         [:td {:tw "p-1"}
          [:a {:tw "font-bold color-accent hover:underline visited:color-accent-extralight"
               :href (pages/path-for [:exercise {:exercise-id (:exercise/id exercise)}])}
           (i18n/value (exercise :exercise/title))]]
         [:td {:tw "p-1 opacity-25"}
          (into [:<>]
                (interpose " " (map teachable-view (:exercise/teaches exercise))))]
         #_[:td {:tw "p-1"}
          (into [:<>]
                (interpose " " (map teachable-view (:exercise/uses exercise))))]
         #_[:td.difficulty
          (repeat (difficulty->n (:exercise/difficulty exercise)) "★")]]))]])

(defn exercises-page-view [_params]
  (let [grouped-exercises (group-by :exercise/category @(subscribe [:unordered-exercises]))]
    [:div.page.exercises
     [:table {:tw "border-collapse"}
      [exercises-view
       (i18n/value {:en-US "First Steps"
                    :pt-BR "Primeiros Passos"})
       @(subscribe [:ordered-exercises])]
      [exercises-view
       (i18n/value {:en-US "Exploring Functions"
                    :pt-BR "Explorando Funções"})
       (sort-exercises (:exercise.category/learning-functions grouped-exercises))]
      [exercises-view
       (i18n/value {:en-US "More Practice"
                    :pt-BR "Mais Prática"})
       (sort-exercises (:exercise.category/starter grouped-exercises))]
      [exercises-view
       (i18n/value {:en-US "Putting Things Together"
                    :pt-BR "Combinando Conceitos"})
       (sort-exercises (:exercise.category/synthesis grouped-exercises))]]]))
