(ns exercise-ui.client.ui.welcome
  (:require
    [exercise-ui.client.ui.login :refer [login-view]]
    [exercise-ui.client.ui.clojure-logo :refer [clojure-logo]]))

(defn welcome-view []
  [:div#welcome
   [:header
    [clojure-logo]
    [:h1 "Clojure Toronto Beginners Workshop"]
    [:p "A monthly remote meetup for anyone new to Clojure."]
    [:p "Check " [:a {:href "https://www.meetup.com/Clojure-Toronto"} "our Meetup page"]  " for upcoming dates."]]

   [:div.rooms
    [:section.room
     [:header
      [:div.titles
       [:h1 "Virtual Room 1"]
       [:h2 "Getting Started"]]
      [:a {:href "https://meet.google.com/xes-kjis-rto?hs=112"} "Join"]]
     [:p "For first-timers at the meetup; discuss Clojure basics, get set-up with Clojure, and start working on exercises."]
     [:div.schedule
      [:table
       [:tbody
        [:tr
         [:td "6:30pm"]
         [:td
          [:div "Welcome & Introductions"]
          [:div "Q&A re: Clojure"]
          [:div "Clojure 101"]
          [:div "Getting Set-Up w/ Clojure"]
          [:div "Getting Started w/ Exercises"]]]]]]]

    [:div.gap]

    [:section.room
     [:header
      [:div.titles
       [:h1 "Virtual Room 2"]
       [:h2 "Applied Practice"]]
      [:a {:href "https://meet.google.com/afn-fbhp-bbo?hs=122"
           :rel "nofollow"} "Join"]]
     [:p "For returning attendees; keep working on exercises and small projects, with peers and a few experienced Clojurians around to help you."]
     [:div.schedule
      [:table
       [:tbody
        [:tr
         [:td "6:30pm"]
         [:td
          [:div "work on exercises and get help from mentors"]
          [:div "(including help getting your IDE set up, code-reviews, debugging help on whatever other Clojure projects you're working on)"]]]
        [:tr
         [:td
          "8:00pm"]
         [:td
          [:div "group code-review of someone's exercise"]
          [:div "mob programming of an exercise"]
          [:div "(sometimes) live code intro to some library"]]]]]]]]
   [login-view]])

