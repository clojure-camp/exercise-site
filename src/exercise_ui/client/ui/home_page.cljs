(ns exercise-ui.client.ui.home-page
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

(defn exercises-view
  [exercises]
  [:table.exercises
   [:thead
    [:tr
     [:th "Status"]
     [:th "Exercise"]
     [:th "Teaches"]
     [:th "Uses"]]]
   [:tbody
    (doall
      (for [exercise (sort-by (comp count :uses) exercises)]
        ^{:key (exercise :id)}
        [:tr
         [:td
          [exercise-status-view (exercise :id)]]
         [:td
          [:a {:href (path-for :exercise {:exercise-id (exercise :id)})}
           (exercise :id)]]
         (into
           [:td]
           (interpose " " (map display-teachable (exercise :teaches))))
         (into
           [:td]
           (interpose " " (map display-teachable (exercise :uses))))]))]])

(defn home-page-view [params]
  (let [grouped-exercises (group-by :category @(subscribe [:exercises]))]
    [:div.page.home
     [:h2 "Learning Functions"]
     [exercises-view (:learning-functions grouped-exercises)]
     [:h2 "Starter Exercises"]
     [exercises-view (:starter grouped-exercises)]
     [:h2 "Synthesis"]
     [exercises-view (:synthesis grouped-exercises)]]))
