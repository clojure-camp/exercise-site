(ns exercise-ui.client.ui.exercise-page
  (:require
    [clojure.string :as string]
    [bloom.commons.pages :refer [path-for]]
    [re-frame.core :refer [subscribe dispatch]]
    [exercise-ui.utils :refer [parse-backticks]]
    [exercise-ui.client.ui.code-view :refer [code-view]]
    [exercise-ui.client.ui.teachable :refer [teachable-view]]))

(defn exercise-page-view [exercise-id]
  (when-let [exercise @(subscribe [:exercise exercise-id])]
    (let [exercise-status @(subscribe [:exercise-status exercise-id])]
      [:div.page.exercise
       [:h1 (exercise :title)]

       [:div.status
        (case exercise-status
          nil
          [:div
           [:button
            {:on-click
             (fn [_]
               (dispatch [:set-exercise-status! exercise-id :started]))}
            "Start"]]
          :started
          [:div
           "In Progress"
           [:button
            {:on-click
             (fn [_]
               (dispatch [:set-exercise-status! exercise-id :completed]))}
            "Complete"]]
          :completed
          [:div "Complete"]
          :reviewed
          [:div "Reviewed"])]

       [:div.instructions
        (into [:<>]
              (for [node (exercise :instructions)]
                (if (or (not (string? node))
                        (and
                          (string/starts-with? node "(")
                          (string/ends-with? node ")")))
                  [code-view node "code" true]
                  (into [:p] (parse-backticks node)))))]

       (when (seq (exercise :tests))
         [:div.tests
          [:h2 "Tests"]
          [code-view (exercise :tests) "code"]])

       [:div.functions
        (into [:<>]
              (for [f (filter symbol? (exercise :teaches))]
                [teachable-view f "taught"]))
        (into [:<>]
              (for [f (filter symbol? (exercise :uses))]
                [teachable-view f "used"]))]

       (when (exercise :solution)
         [:details.solution
          [:summary [:h2 "Solution"]]
          [:div.solution
           [code-view (exercise :solution) "code"]]])

       (when (exercise :related)
         [:div.related
          [:h2 "Related"]
          (for [id (exercise :related)]
            ^{:key id}
            [:a {:href (path-for :exercise {:exercise-id id})} id])])])))
