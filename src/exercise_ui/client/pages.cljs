(ns exercise-ui.client.pages
  (:require
    [reagent.core :as r]
    [bloom.commons.pages]
    [exercise-ui.client.ui.exercises-page :refer [exercises-page-view]]
    [exercise-ui.client.ui.exercise-page :refer [exercise-page-view]]))

(def pages
  [{:page/id :exercises
    :page/view #'exercises-page-view
    :page/path "/"}

   {:page/id :exercise
    :page/view (fn [[_ {:keys [exercise-id]}]]
                 [exercise-page-view exercise-id])
    :page/path "/exercises/:exercise-id"
    :page/parameters [:map
                      [:exercise-id string?]]}])

(def current-page-view bloom.commons.pages/current-page-view)

(def current-page-id
  (r/cursor bloom.commons.pages/current-page [:data :config :page/id]))
