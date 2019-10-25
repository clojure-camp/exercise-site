(ns exercise-ui.client.ui.exercises-page
  (:require
    [clojure.string :as string]
    [re-frame.core :refer [subscribe]]
    [bloom.commons.pages :refer [path-for]]
    [exercise-ui.client.ui.exercise-status :refer [exercise-status-view]]))

(defn docs-link
  [fn-symbol]
  (str "https://clojuredocs.org/"
       (if-let [fn-ns (namespace fn-symbol)]
         fn-ns
         "clojure.core")
       "/" (name fn-symbol)))

(defn display-teachable
  [teachable]
  (cond
    (keyword? teachable)
    [:span.teachable.concept (name teachable)]
    (symbol? teachable)
    [:a.teachable.function {:href (docs-link teachable)} (str teachable)]
    :else
    [:span.teachable.function (str teachable)]))

(def difficulty->n {:low 1 :mid 2 :high 3})

(defn sort-exercises
  [exercises]
  (->> exercises
      (sort-by :title)
      (sort-by (comp difficulty->n :difficulty))))

(defn exercises-view
  [exercises]
  [:table.exercises
   [:thead
    [:tr
     [:th "Status"]
     [:th]
     [:th "Exercise"]
     [:th "Teaches"]
     [:th "Uses"]]]
   [:tbody
    (doall
      (for [exercise (sort-exercises exercises)]
        ^{:key (exercise :id)}
        [:tr.exercise
         [:td.status
          [exercise-status-view (exercise :id)]]
         [:td.difficulty
          (repeat (difficulty->n (exercise :difficulty)) "★")]
         [:td
          [:a {:href (path-for :exercise {:exercise-id (exercise :id)})}
           (exercise :title)]]
         (into
           [:td]
           (interpose " " (map display-teachable (exercise :teaches))))
         (into
           [:td]
           (interpose " " (map display-teachable (exercise :uses))))]))]])

(defn exercises-page-view [params]
  (let [grouped-exercises (group-by :category @(subscribe [:exercises]))]
    [:div.page.exercises
     [:h2 "Learning Functions"]
     [exercises-view (:learning-functions grouped-exercises)]
     [:h2 "Starter Exercises"]
     [exercises-view (:starter grouped-exercises)]
     [:h2 "Synthesis"]
     [exercises-view (:synthesis grouped-exercises)]]))
