(ns exercise-ui.client.ui.exercise-page
  (:require
    [clojure.string :as string]
    [bloom.commons.fontawesome :as fa]
    [bloom.commons.pages :refer [path-for]]
    [reagent.core :as r]
    [re-frame.core :refer [subscribe dispatch]]
    [exercise-ui.utils :refer [parse-backticks]]
    [exercise-ui.client.ui.code-view :refer [code-view]]
    [exercise-ui.client.ui.exercise-status :refer [exercise-status-view]]
    [exercise-ui.client.ui.teachable :refer [teachable-view]]))

(defn solution-view [exercise]
  (let [open? (r/atom false)]
    (fn []
      [:section.solution
       [:header {:on-click (fn []
                             (swap! open? not))}
        [:h2 "example solution"]
        (if @open?
          [fa/fa-chevron-down-solid]
          [fa/fa-chevron-right-solid])]
       (when @open?
         [code-view (exercise :solution) "code"])])))

(defn test-case-view [exercise]
  (let [table-view? (r/atom true)]
    (fn []
      [:section.test-cases
       [:header [:h2 "sample tests"]
        [:button {:on-click (fn [_] (swap! table-view? not))}
         (if @table-view?
          [fa/fa-code-solid]
          [fa/fa-table-solid])]]

       (if @table-view?
         [:table {:cellpadding 0}
          #_[:thead
           [:tr
            [:td "Input"]
            [:td]
            [:td "Output"]]]
          [:tbody
           (into [:<>]
                 (for [{:keys [input output]} (exercise :test-cases)]
                   [:tr
                    [:td
                     [code-view [input] "code"]]
                    [:td
                     [:code "=>"]]
                    [:td
                     [code-view [output] "code"]]]))]]

         (into [:<>]
               (for [{:keys [input output]} (exercise :test-cases)]
                 [:div
                  [code-view [(list 'is (list '= output input))] "code"]])))])))

(defn exercise-page-view [exercise-id]
  (when-let [exercise @(subscribe [:exercise exercise-id])]
    (let [exercise-status @(subscribe [:exercise-status exercise-id])]
      [:div.page.exercise
       [:header
        [exercise-status-view (exercise :id)]
        [:h1 (exercise :title)]
        [:div.gap]
        (when (not= exercise-status :completed)
          [:button.action
           {:on-click
            (fn [_]
              (dispatch [:set-exercise-status! exercise-id :completed]))}
           "Mark as Finished"
           [fa/fa-flag-checkered-solid]])]

       [:<>
        [:section.instructions
         (into [:<>]
               (for [node (exercise :instructions)]
                 (if (or (not (string? node))
                         (and
                           (string/starts-with? node "(")
                           (string/ends-with? node ")")))
                   [code-view node "code" true]
                   (into [:p] (parse-backticks node)))))]

        (let [fns (->> (concat (map (fn [x] [x :teaches]) (exercise :teaches))
                               (map (fn [x] [x :uses]) (exercise :uses)))
                       (filter (fn [[f _]] (symbol? f))))]
          (when (seq fns)
            [:section.functions
             [:header
              [:h2 "related functions"]]
             [:div.body
              (into [:<>]
                    (->> fns
                         (map (fn [[f category]] [teachable-view f (name category)]))
                         (interpose " ")))]]))

        (when (seq (exercise :tests))
          [:section.tests
           [:header [:h2 "sample tests"]]
           [code-view (exercise :tests) "code"]])

        (when (seq (exercise :test-cases))
          [test-case-view exercise])

        (when true #_(and (exercise :solution) (= exercise-status :completed))
          [solution-view exercise])

        (when (exercise :related)
          [:div.related
           [:h2 "See also:"]
           [:div.exercises
            (for [id (exercise :related)]
              ^{:key id}
              [:div.exercise
               [exercise-status-view id]
               [:a {:href (path-for :exercise {:exercise-id id})} id]])]])]])))
