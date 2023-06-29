(ns exercise-ui.client.ui.app
  (:require
    [bloom.commons.pages :refer [path-for ]]
    [re-frame.core :refer [subscribe]]
    [exercise-ui.client.pages :as pages]))

(defonce favicon
 (let [element (.createElement js/document "link")]
   (.setAttribute element "rel" "icon")
   (.setAttribute element "href" "/logomark.svg")
   (.appendChild (.querySelector js/document "head") element)
   nil))

(defn header-view []
  [:div.header {:tw "font-header bg-accent"}
   [:nav
    [:a {:tw "inline-flex items-center gap-2 p-2 text-light hover:bg-accent-light"
         :href (path-for [:exercises])}
     [:img {:src "/logomark.svg"
            :tw "w-6 h-6"}]
     "clojure camp exercises"]]])

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
