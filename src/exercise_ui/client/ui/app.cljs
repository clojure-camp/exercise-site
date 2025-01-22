(ns exercise-ui.client.ui.app
  (:require
   [bloom.commons.fontawesome :as fa]
   [bloom.commons.pages :refer [path-for]]
   [exercise-ui.client.i18n :as i18n]
   [exercise-ui.client.pages :as pages]
   [re-frame.core :refer [subscribe]]))

(defonce favicon
 (let [element (.createElement js/document "link")]
   (.setAttribute element "rel" "icon")
   (.setAttribute element "href" "/logomark.svg")
   (.appendChild (.querySelector js/document "head") element)
   nil))

(defn language-picker-view []
  [:div {:tw "flex items-center gap-2"}
   [fa/fa-language-solid {:tw "w-6 h-6 text-light"}]
   [:select
    {:tw "p-1"
     :value (name @i18n/language)
     :on-change (fn [e] (i18n/set-language! (-> e .-target .-value keyword)))}
    (doall
     (for [[lang-code lang-name] i18n/languages]
       ^{:key lang-code}
       [:option {:value (name lang-code)}
        lang-name]))]])

(defn header-view []
  [:div.header {:tw "font-header bg-accent flex justify-between"}
   [:nav
    [:a {:tw "inline-flex items-center gap-2 p-2 text-light hover:bg-accent-light"
         :href (path-for [:exercises])}
     [:img {:src "/logomark.svg"
            :tw "w-6 h-6"}]
     "clojure camp exercises"]]
   [language-picker-view]])

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
