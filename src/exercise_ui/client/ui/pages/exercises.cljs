(ns exercise-ui.client.ui.pages.exercises
  (:require
    [clojure.set :as set]
    [bloom.commons.pages :refer [path-for]]
    [re-frame.core :refer [subscribe]]
    [exercise-ui.client.ui.partials.teachable :refer [teachable-view]]))

(def difficulty->n {:low 1 :mid 2 :high 3})

(defn intersects?
  [a b]
  (some? (seq (set/intersection a b))))

(defn depends?
  "Does exercise `a` depend on exercise `b` being learnt first? If
  both exercises use things the other teaches, don't count that as a
  dependency."
  [a b]
  (and (intersects? (:uses a) (:teaches b))
       (not (intersects? (:teaches a) (:uses b)))))

(defn group-dependencies
  [exercises]
  (map
    (fn [exercise]
      (assoc exercise
             :dependencies (set (map :id (filter (partial depends? exercise) exercises)))))
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

      (empty? (:dependencies (get exercises i)))
      (recur exercises (inc i))

      :else
      (let [exercise (get exercises i)
            prev (subvec exercises 0 i)]
        (if-let [idx (first-index-where (fn [ex] (contains? (:dependencies ex) (:id exercise))) prev)]
          (recur (into (conj (subvec exercises 0 idx) exercise)
                       (into (subvec exercises idx i)
                             (subvec exercises (inc i))))
                 (inc idx))
          (recur exercises (inc i)))))))

(defn sort-exercises
  [exercises]
  (->> exercises
      (sort-by :title)
      (sort-by (comp difficulty->n :difficulty))
      group-dependencies
      (sort-by (comp count :dependencies))
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
        ^{:key (exercise :id)}
        [:tr.exercise
         [:td {:tw "p-1"}
          [:a {:tw "font-bold color-accent hover:underline visited:color-accent-extralight"
               :href (path-for [:exercise {:exercise-id (exercise :id)}])}
           (exercise :title)]]
         [:td {:tw "p-1 opacity-25"}
          (into [:<>]
                (interpose " " (map teachable-view (exercise :teaches))))]
         #_[:td {:tw "p-1"}
          (into [:<>]
                (interpose " " (map teachable-view (exercise :uses))))]
         #_[:td.difficulty
          (repeat (difficulty->n (exercise :difficulty)) "★")]]))]])

(defn exercises-page-view [_params]
  (let [grouped-exercises (group-by :category @(subscribe [:unordered-exercises]))]
    [:div.page.exercises
     [:table {:tw "border-collapse"}
      [exercises-view "First Steps" @(subscribe [:ordered-exercises])]
      [exercises-view "Exploring Functions" (sort-exercises (:learning-functions grouped-exercises))]
      [exercises-view "More Practice" (sort-exercises (:starter grouped-exercises))]
      [exercises-view "Putting Things Together" (sort-exercises (:synthesis grouped-exercises))]]]))
