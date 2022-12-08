(ns exercise-ui.client.ui.index-page
  (:require
    [bloom.commons.pages :refer [path-for]]
    [exercise-ui.client.ui.clojure-logo :refer [clojure-logo]]))

(defn index-page-view []
  [:div#index.page
   [:header
    [clojure-logo]
    [:h1 "Clojure Toronto Beginners Workshop"]
    [:p "A monthly remote meetup for anyone new to Clojure."]
    [:p "Check " [:a {:href "https://www.meetup.com/Clojure-Toronto"} "our Meetup page"]  " for upcoming dates."]

    [:p "If you don't have Clojure on your computer, follow " [:a {:href (path-for [:setup])} "the setup instructions"] ", then you can get started on " [:a {:href (path-for [:exercises])} " the exercises"] "."]]])
