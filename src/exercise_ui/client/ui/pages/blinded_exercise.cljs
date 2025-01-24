(ns exercise-ui.client.ui.pages.blinded-exercise
  (:require
   [clojure.string :as string]
   [clojure.walk :as walk]
   [clojure.edn :as edn]
   [clojure.set :as set]
   [bloom.commons.fontawesome :as fa]
   [reagent.core :as r]
   [exercise-ui.client.ui.partials.code-view :refer [code-view format-code]]))

(defn parse-blind [code]
  (let [leaves (atom [])
        result (walk/postwalk
                (fn [node]
                  (if (and (seq? node)
                           ;; doesn't have any leaves as children
                           (not (some (fn [x] (:leaf (meta x))) node))
                           ;; doesn't have any child seqs
                           (not (some seq? node)))
                    (do (swap! leaves conj node)
                        (vary-meta
                         (symbol (str "_" (dec (count @leaves))))
                         assoc :leaf (dec (count @leaves))))
                    node))
                code)]
    {:code result
     :leaves @leaves
     :shuffled-leaves (shuffle @leaves)}))

(defn blinded-exercise-view [exercise]
  (r/with-let [regenerate (fn []
                            (parse-blind (edn/read-string (exercise :solution))))
               blinded-data (r/atom (regenerate))
               ;; assuming leaves are all unique
               leaf->index (r/atom {})
               result (r/atom nil)
               check! (fn []
                        (let [correct? (= (sort-by @leaf->index (keys @leaf->index))
                                          (:leaves @blinded-data))]
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
    [:section.blinded
     [:header
      [:h2 "Blinded Exercise"]
      [:button {:on-click (fn [_]
                            (reset! blinded-data (regenerate)))
                :title "Regenerate"}
       [fa/fa-recycle-solid {:tw "w-1em h-1em"}]]]
     [:div.body
      (let [index->leaf (set/map-invert @leaf->index)
            {:keys [code leaves shuffled-leaves]} @blinded-data]
        [:<>
         [code-view {:class "code"}
          (str code)]

         [:div {:tw "p-4 bg-gray-200 space-y-2"}
          [:span "Select the correct code snippet for each blank:"]
          [:table
           [:tbody
            [:tr
             [:td]
             (for [leaf-index (range (count leaves))]
               ^{:key leaf-index}
               [:td {:tw "text-center"} "_" leaf-index])
             [:td]]
            (doall
             (for [leaf shuffled-leaves
                   :let [current-index (@leaf->index leaf)]]
               ^{:key leaf}
               [:tr
                [:td
                 [code-view {:class "code"
                             :fragment? true}
                  (str leaf)]]

                (for [leaf-index (range (count leaves))]
                  ^{:key leaf-index}
                  [:td
                   [:input {:tw "m-2"
                            :type "radio"
                            :checked (= current-index leaf-index)
                            :on-change (fn []
                                         (if (= current-index leaf-index)
                                           ;; deselect
                                           (swap! leaf->index dissoc leaf)
                                           ;; select
                                           (do
                                             (swap! leaf->index dissoc (index->leaf leaf-index))
                                             (swap! leaf->index assoc leaf leaf-index))))}]])]))]]
          [:button {:tw ["p-2 bg-blue-500 text-white"
                         "disabled:bg-gray-400 disabled:cursor-not-allowed disabled:line-through"]
                    :disabled (not= (count @leaf->index)
                                    (count leaves))
                    :on-click (fn []
                                (check!))}
           "Check"]
          [:div
           (case @result
             nil nil
             true "Correct!"
             false "Incorrect. Change your selection and check again.")]]
         [code-view {:class "code"}
          (string/replace (str code) #"_(\d+)" (fn [[match index]]
                                                 (or (index->leaf (js/parseInt index))
                                                     match)))]])]]))
