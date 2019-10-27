(ns exercise-ui.client.pages
  (:require
    [bloom.commons.pages]
    [exercise-ui.client.ui.exercises-page :refer [exercises-page-view]]
    [exercise-ui.client.ui.pastebin-page :refer [pastebin-page-view]]
    [exercise-ui.client.ui.exercise-page :refer [exercise-page-view]]
    [exercise-ui.client.ui.setup-page :refer [setup-page-view]]))

(def pages
  [{:id :exercises
    :view (fn [data] [exercises-page-view])
    :path "/"}

   {:id :exercise
    :view (fn [data]
            [exercise-page-view (:exercise-id data)])
    :path "/exercises/:exercise-id"
    :coerce {:exercise-id str}}

   {:id :pastebin
    :view (fn [data]
            [pastebin-page-view])
    :path "/pastebin"}

   {:id :setup
    :view (fn [data]
            [setup-page-view])
    :path "/instructions/setup"}])

(def current-page-view bloom.commons.pages/current-page-view)
