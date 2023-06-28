(ns exercise-ui.client.ui.app
  (:require
    [bloom.commons.pages :refer [path-for active?]]
    [bloom.commons.fontawesome :as fa]
    [re-frame.core :refer [dispatch subscribe]]
    [exercise-ui.client.pages :as pages]))

(defn header-view []
  [:div.header
   [:nav
    [:a {:href (path-for [:exercises])
         :class (when (active? [:exercises])
                  "active")}
     "exercises"]]])

(defn main-view []
  [:div
   [header-view]
   [pages/current-page-view]])

(defn app-view []
  [:div
   (cond
     @(subscribe [:loading?])
     [:div.loading "Loading..."]

     :else
     [main-view])])
