(ns exercise-ui.client.ui.pages.exercise
  (:require
    [clojure.set :as set]
    [clojure.string :as string]
    [bloom.commons.fontawesome :as fa]
    [bloom.commons.pages :as pages]
    [reagent.core :as r]
    [re-frame.core :refer [subscribe ]]
    [exercise-ui.client.ui.styles :as styles]
    [exercise-ui.utils :refer [parse-backticks]]
    [exercise-ui.client.blind :as blind]
    [exercise-ui.client.ui.partials.code-view :refer [code-view blinded-code-view format-code]]
    [exercise-ui.client.ui.partials.teachable :refer [teachable-view]]
    [exercise-ui.client.i18n :as i18n]))

(defn blinded-exercise-view
  [exercise]
  (r/with-let [open? (r/atom false)
               regenerate (fn []
                            (blind/parse
                             (first (:exercise/solution exercise))))
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
    [:section.blinded
     [:header {:on-click (fn []
                           (swap! open? not))}
      [:h2 "fill in the blanks"]
      (if @open?
        [fa/fa-chevron-down-solid]
        [fa/fa-chevron-right-solid])]
     (when @open?
       [:div.body {:style {:position "relative"}}
        [:button {:style {:position "absolute"
                          :right "1em"
                          :bottom "1em"}
                  :on-click (fn [_]
                              (reinitialize!))
                  :title "Regenerate exercise"}
         [fa/fa-recycle-solid {:tw "w-1em h-1em"}]]
        (let [{:blind/keys [shuffled-holes]} @blinded-data
              index-guess->index-hole (set/map-invert @index-hole->index-guess)]
          [:<>
           [:div.two-columns {:tw "flex bg-#2b2b2b"}
            [blinded-code-view
             @blinded-data @index-hole->index-guess]]
           [:div {:tw "p-4 bg-gray-200 space-y-2"}
            [:span "Select the correct code snippet for each blank:"]
            [:table
             [:tbody
              [:tr
               [:td]
               [:td]
               (for [hole-index (sort (map :hole/index shuffled-holes))]
                 ^{:key hole-index}
                 [:td {:tw "text-center"} hole-index])
               [:td]]
              (doall
               (for [{:hole/keys [index string]} shuffled-holes
                     :let [guess-index index]]
                 ^{:key guess-index}
                 [:tr
                  [:td ;; for debugging
                   #_guess-index]
                  [:td
                   [code-view {:class "code"
                               :fragment? true}
                    string]]
                  (doall
                   (for [hole-index (sort (map :hole/index shuffled-holes))
                         :let [selected? (= guess-index (get @index-hole->index-guess hole-index))]]
                     ^{:key hole-index}
                     [:td
                      [:input {:tw "m-2"
                               :type "radio"
                               :checked selected?
                               ;; not on-change, so we can deselect
                               :on-click (fn [_]
                                           (reset! result nil)
                                           (if selected?
                                             ;; deselect
                                             (swap! index-hole->index-guess dissoc hole-index)
                                             ;; select
                                             (do
                                               (swap! index-hole->index-guess dissoc (index-guess->index-hole guess-index))
                                               (swap! index-hole->index-guess assoc hole-index guess-index))))}]]))]))]]
            [:div {:tw "flex gap-2 items-center"}
             [:button {:tw ["p-2 bg-blue-500 text-white"
                            "disabled:bg-gray-400 disabled:cursor-not-allowed disabled:line-through"]
                       :disabled (not= (count @index-hole->index-guess)
                                       (count shuffled-holes))
                       :on-click (fn []
                                   (check!))}
              "Check"]
             [:div
              (case @result
                nil nil
                true "Correct!"
                false "Incorrect. Change your selection and check again.")]]]])])]))

(defn solution-view [exercise]
  (r/with-let
   [open? (r/atom false)]
   [:section.solution
    [:header {:on-click (fn []
                          (swap! open? not))}
     [:h2 (i18n/value {:en-US "example solution(s)"
                       :pt-BR "solução exemplo" })]
     (if @open?
       [fa/fa-chevron-down-solid]
       [fa/fa-chevron-right-solid])]
    (when @open?
      [:div {:tw "space-y-2"}
       (for [solution (:exercise/solution exercise)]
         [code-view {:class "code"}
          solution])])]))

(defn test-case-view [exercise]
  (r/with-let [active-mode (r/atom :mode/table)]
    [:section.test-cases
     [:header {:tw "gap-2"}
      [:h2 {:tw "grow"} (i18n/value {:en-US "sample tests"
                                     :pt-BR "testes de exemplo"})]
      (doall
       (for [[mode icon] [[:mode/table [fa/fa-table-solid]]
                          [:mode/rcf "RCF"]
                          [:mode/clj "CLJ"]]]
         ^{:key mode}
         [:button
          {:on-click (fn [_] (reset! active-mode mode))
           :tw [(when (= @active-mode mode)
                  "bg-white")]}
          icon]))]

     (case @active-mode
       :mode/table
       [:table {:cellPadding 0
                :tw "w-full bg-#2b2b2b border-collapse"}
        [:tbody
         (into [:<>]
               (for [{:keys [input output]} (:exercise/test-cases exercise)]
                 [:tr
                  [:td
                   [code-view {:class "code"} (pr-str input)]]
                  [:td
                   [:code {:style {:font-family styles/code-font
                                   :color "white"
                                   :font-size "0.8em"}} ":="]]
                  [:td
                   [code-view {:class "code"} (pr-str output)]]]))]]
       :mode/clj
       [code-view {:class "code"}
        (str
         "(ns exercises." (:exercise/id exercise) "\n"
         "  (:require\n"
         "    [clojure.test :refer [is testing]]))\n\n"
         (when (seq (:exercise/function-template exercise))
           (str (string/join "\n\n" (:exercise/function-template exercise)) "\n\n"))
         (string/join "\n\n"
                      (for [{:keys [input output]} (:exercise/test-cases exercise)]
                        (list 'is (list '= output input))))

         "(clojure.test/run-tests)")]

       :mode/rcf
       [:div
        [code-view {:class "code"
                    :pre-formatted? true}
         (str
           "(ns exercises." (:exercise/id exercise) "\n"
           "  (:require\n"
           "    [hyperfiddle.rcf :as rcf]))\n\n"
           "(rcf/enable!)\n\n"
           (when (seq (:exercise/function-template exercise))
             (str (string/join "\n\n" (:exercise/function-template exercise)) "\n\n"))
           "(rcf/tests\n"
           (string/join "\n\n"
                        (for [{:keys [input output]} (:exercise/test-cases exercise)]
                          (str "  " (format-code input) " := \n"
                               "  "
                               (if (string? output)
                                 (pr-str output)
                                 (string/replace (format-code output)
                                                 "\n" "\n  ")))))
           ")")]])]))

(defn exercise-page-view [exercise-id]
  (when-let [exercise @(subscribe [:exercise exercise-id])]
    [:div.page.exercise
     [:header
      [:h1 (i18n/value (:exercise/title exercise))]]

     [:<>
      [:section.instructions
       (into [:<>]
             (for [node (i18n/value (:exercise/instructions exercise))]
               (cond
                 ;; "a string" => [:p "a string"]
                 (string? node)
                 (into [:p] (parse-backticks node))

                 ;; [:code ...] => [code-view ...]
                 (and
                  (seq node)
                  (= :code (first node)))
                 [code-view {:class "code"
                             :fragment? true}
                  (string/join "\n" (rest node))]

                 ;; [:file ...] => [code-view ...]
                 (and
                  (seq node)
                  (= :file (first node)))
                 [code-view {:class "code"
                             :lang "text/plain"
                             :pre-formatted? true}
                  (string/join "\n" (rest node))]

                 ;; (... ) => [code-view ...]
                 (and
                  (string/starts-with? (str node) "(")
                  (string/ends-with? (str node) ")"))
                 [code-view {:class "code"
                             :fragment? true} node]

                 :else
                 node)))]

      (when (:exercise/function-template exercise)
        [:section.starter-code
         [:header
          [:h2 (i18n/value {:en-US "starter code"
                            :pt-BR "código inicial"})]]
         [:div.body
          [code-view {:class "code"}
           (:exercise/function-template exercise)]]])

      (let [{:keys [fns concepts]}
            (->> (concat (map (fn [x] [x :teaches]) (:exercise/teaches exercise))
                         (map (fn [x] [x :uses]) (:exercise/uses exercise)))
                 (group-by (fn [[f _]]
                             (if (symbol? f)
                               :fns
                               :concepts))))]
        (for [[values title] [[fns (i18n/value {:en-US "related functions"
                                                :pt-BR "funções relacionadas"})]
                              [concepts (i18n/value {:en-US "related concepts"
                                                     :pt-BR "conceitos relacionadas"})]]]
          (when (seq values)
            [:section.functions
             [:header
              [:h2 title]]
             [:div.body
              (into [:<>]
                    (->> values
                         (map (fn [[f category]] [teachable-view f (name category)]))
                         (interpose " ")))]])))

      (when (seq (:exercise/test-cases exercise))
        [test-case-view exercise])

      [blinded-exercise-view exercise]

      [solution-view exercise]

      (when (seq (:exercise/related exercise))
        [:div.related
         [:h2 (i18n/value {:en-US "See also:"
                           :pt-BR "Veja também:"})]
         [:div.exercises
          (for [id (:exercise/related exercise)]
            ^{:key id}
            [:div.exercise
             [:a {:href (pages/path-for [:exercise {:exercise-id id}])} id]])]])]]))
