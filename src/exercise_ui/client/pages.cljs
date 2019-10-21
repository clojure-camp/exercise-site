(ns exercise-ui.client.pages
  (:require
    [bloom.commons.pages]
    [exercise-ui.client.ui.home-page :refer [home-page-view]]
    [exercise-ui.client.ui.pastebin-page :refer [pastebin-page-view]]
    [exercise-ui.client.ui.exercise-page :refer [exercise-page-view]]))

(def pages
  [{:id :home
    :view (fn [data] [home-page-view])
    :path "/"}

   {:id :exercise
    :view (fn [data]
            [exercise-page-view (:exercise-id data)])
    :path "/exercises/:exercise-id"
    :coerce {:exercise-id str}}

   {:id :pastebin
    :view (fn [data]
            [pastebin-page-view])
    :path "/pastebin"}])

(def current-page-view bloom.commons.pages/current-page-view)
