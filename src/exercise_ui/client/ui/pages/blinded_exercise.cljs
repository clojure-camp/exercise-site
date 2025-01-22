(ns exercise-ui.client.ui.pages.blinded-exercise
  (:require
   [clojure.string :as string]
   [clojure.walk :as walk]
   [clojure.edn :as edn]
   [clojure.set :as set]
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
     :leaves @leaves}))

(defn blinded-exercise-view [exercise]
  (r/with-let [;; assuming leaves are all unique
               leaf->index (r/atom {})]
    [:section.blinded
     [:header
      [:h2 "Blinded Exercise"]]
     [:div.body
      (let [index->leaf (set/map-invert @leaf->index)
            {:keys [code leaves]} (parse-blind (edn/read-string (exercise :solution)))]
        [:<>
         [code-view {:class "code"}
          (string/replace (str code) #"_(\d+)" (fn [[match index]]
                                                 (or (index->leaf (js/parseInt index))
                                                     match)))]
         [:table
          [:tbody
           (doall
            (for [leaf leaves
                  :let [current-index (@leaf->index leaf)]]
              ^{:key leaf}
              [:tr
               [:td (str leaf)]
               [:td [:button {:on-click (fn []
                                          (swap! leaf->index dissoc leaf))} "x"]]
               (for [leaf-index (range (count leaves))]
                 ^{:key leaf-index}
                 [:td [:button {:on-click (fn []
                                            (swap! leaf->index dissoc (index->leaf leaf-index))
                                            (swap! leaf->index assoc leaf leaf-index))
                                :style {:font-weight (when (= current-index leaf-index)
                                                       "bold")}}
                       leaf-index]])]))]]])]]))
