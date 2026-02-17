(ns exercise-ui.client.ui.pages.blinded-exercise
  (:require
   [clojure.string :as string]
   [clojure.edn :as edn]
   [clojure.set :as set]
   [bloom.commons.fontawesome :as fa]
   [reagent.core :as r]
   [exercise-ui.client.blind :as blind]
   [exercise-ui.client.ui.partials.code-view :refer [code-view]]))

(defn stateful-blinded-exercise-view
  [exercise]
  (r/with-let [regenerate (fn []
                            (blind/parse (edn/read-string
                                          (str
                                           "[" (first (:exercise/solution exercise))
                                           "]"))))
               blinded-data (r/atom (regenerate))
               index-hole->index-guess (r/atom {})
               result (r/atom nil)
               reinitialize! (fn []
                               (reset! blinded-data (regenerate))
                               (reset! index-hole->index-guess {})
                               (reset! result nil))
               check! (fn []
                        (let [correct? (every? (partial apply =) @index-hole->index-guess)]
                          (reset! result correct?)
                          (when correct?
                            (js/confetti #js{:particleCount 120
                                             :spread 90
                                             :angle 30
                                             :origin #js{:y 1.0
                                                         :x 0.0}})
                            (js/confetti #js{:particleCount 120
                                             :spread 90
                                             :angle 120
                                             :origin #js{:y 1.0
                                                         :x 1.0}}))))]
    [:section.blinded {:style {:width "calc(100vw - 4em)"}}
     [:header
      [:h2 "Fill in the Blanks (Alpha)"]
      [:button {:on-click (fn [_]
                            (reinitialize!))
                :title "Regenerate"}
       [fa/fa-recycle-solid {:tw "w-1em h-1em"}]]]
     [:div.body
      (let [{:keys [code holes shuffled-holes hole-indexes]} @blinded-data
            index-guess->index-hole (set/map-invert @index-hole->index-guess)]
        [:<>
         [:div.two-columns {:tw "flex bg-#2b2b2b"}
          [:div {:tw "w-1/2"}
           [code-view {:class "code"}
            (vec (cons ";; start" code))]]
          [:div {:tw "w-1/2"}
           [code-view {:class "code"}
            (vec (cons ";; preview"
                       (->> code
                            (map (fn [c]
                                   (string/replace (str c)
                                                   #"_(\d+)"
                                                   (fn [[match index]]
                                                     (or (->> holes
                                                              (filter (fn [hole]
                                                                        (= (::blind/index (meta hole))
                                                                           (index-guess->index-hole (js/parseInt index)))))
                                                              first)
                                                         match)))))
                            (map edn/read-string))))]]]

         [:div {:tw "p-4 bg-gray-200 space-y-2"}
          [:span "Select the correct code snippet for each blank:"]
          [:table
           [:tbody
            [:tr
             [:td]
             [:td]
             (for [hole-index (sort hole-indexes)]
               ^{:key hole-index}
               [:td {:tw "text-center"} "_" hole-index])
             [:td]]
            (doall
             (for [hole shuffled-holes
                   :let [current-index (::blind/index (meta hole))]]
               ^{:key current-index}
               [:tr
                [:td ;; for debugging
                   #_current-index]
                [:td
                 [code-view {:class "code"
                             :fragment? true}
                  (str hole)]]

                (doall
                 (for [guess-index (sort hole-indexes)
                       :let [selected? (= guess-index (@index-hole->index-guess current-index))]]
                   ^{:key guess-index}
                   [:td
                    [:input {:tw "m-2"
                             :type "radio"
                             :checked selected?
                             ;; not on-change, so we can deselect
                             :on-click (fn [_]
                                         (reset! result nil)
                                         (if selected?
                                           ;; deselect
                                           (swap! index-hole->index-guess dissoc current-index)
                                           ;; select
                                           (do
                                             (swap! index-hole->index-guess dissoc (index-guess->index-hole guess-index))
                                             (swap! index-hole->index-guess assoc current-index guess-index))))}]]))]))]]
          [:div {:tw "flex gap-2 items-center"}
           [:button {:tw ["p-2 bg-blue-500 text-white"
                          "disabled:bg-gray-400 disabled:cursor-not-allowed disabled:line-through"]
                     :disabled (not= (count @index-hole->index-guess)
                                     (count holes))
                     :on-click (fn []
                                 (check!))}
            "Check"]
           [:div
            (case @result
              nil nil
              true "Correct!"
              false "Incorrect. Change your selection and check again.")]]]])]]))

(defn blinded-exercise-view
  [exercise]
  (when (seq (:exercise/solution exercise))
    [stateful-blinded-exercise-view exercise]))

