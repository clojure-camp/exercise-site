(ns exercise-ui.client.ui.index-page
  (:require
    [bloom.commons.pages :refer [path-for]]))

(defn index-page-view []
  [:div.index.page
   [:h1 "Welcome"]
   [:p "If you're here during one of the workshops, you can join in to one of the live video calls:"]
   [:ul
    [:li [:a {:href "https://meet.google.com/xes-kjis-rto?hs=112"} "Room 1"] " for first timers"]
    [:li [:a {:href "https://meet.google.com/afn-fbhp-bbo?hs=122"} "Room 2"] " for returning attendees"]]

   [:p "Key links:"]
   [:ul
    [:li [:a {:href (path-for :setup)}
          "Setup"]
     " How to install Clojure and set up VSCode (or other editors)"]
    [:li [:a {:href (path-for :exercises)}
          "Exercises"]]]])
