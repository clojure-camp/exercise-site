(ns exercise-ui.exercises.schema)

(def Code
  :any
  #_[:or [:vector :any] :string])

(defn I18N [val-schema]
  [:map-of :keyword val-schema])

(def Topic
  [:or :keyword :symbol :string])

(def ExerciseInput
  ;; raw exercise files, pre-normalization
  [:map {:closed true}
   [:title
    ;; i18n ex. {:en-US "Title"}
    (I18N :string)]
   [:difficulty
    ;; a rough indicator of how much time and mental effort is involved
    ;; perhaps "complexity" would be a better term
    ;; correlated with category
    [:enum
     ;; low - 1 function of up to 3 or 4 “steps”
     :low
     ;; mid - something in the middle
     :mid
     ;; high - multiple functions, many steps, complex logic (~recursion, ...)
     :high]]
   [:category
    ;; which section on the website this exercise is grouped into
    [:enum
     ;; "First Steps" on the site
     ;; exercises that teach the very basics of Clojure
     ;; (no prerequisites)
     ;; should also appear in order.edn
     :first-steps
     ;; "Exploring Functions" on the site
     ;; exercises that teach/explore a single function
     :learning-functions
     ;; "More Practice" on the site
     ;; exercise that don't fit anywhere else
     :starter
     ;; "Putting Things Together" on the site
     ;; exercises that are more open-ended, less hand-holding,
     ;; involve multiple functions, no single 'right answer'
     ;; some are 'micro-projects'
     :synthesis]]
   [:instructions
    ;; i18n ex. {:en-US ["..."]}
    ;; the vector can contain:
    ;;  "string..."               - wrapped in <p>, can have `backticks` for inline code
    ;;  [:code "string" "string"] - rendered as a code block, w/ clj syntax highlighting
    ;;  (code ...)                - rendered as a code block, w/ clj syntax highlighting
    ;;  [:file "string" "string"] - rendered as a code block, w/ no syntax highlighting
    (I18N [:vector :any])]
   ;; optional:
   [:related {:optional true}
    ;; ids of closely related exercises
    [:set :string]]
   [:function-template {:optional true} Code]
   [:source {:optional true}
    ;; if this exercise was adapted from another source,
    ;; a url to that source (or some relevant text)
    :string]
   [:test-cases {:optional true} [:vector [:map
                                           [:input Code]
                                           [:output Code]]]]

   [:uses {:optional true}
    ;; topics and functions that ~all solutions to this exercise likely use
    ;; run `lein run -m reports/topics` to see frequencies of topics across exercises
    [:set Topic]]
   [:teaches {:optional true}
    ;; like :uses above, but for topics this exercise explicilty tries to teach
    [:set Topic]]
   ;; solutions to the exercise
   ;; usually as a seperate chunk
   ;; ex. ;; -- [:solution 0]
   [:solution {:optional true}
    [:vector :string]]])

(def Exercise
  ;; parsed, normalized exercise
  [:map {:closed true}
   [:exercise/id :string]
   [:exercise/title (I18N :string)]
   [:exercise/category [:enum
                        :exercise.category/starter
                        :exercise.category/learning-functions
                        :exercise.category/synthesis]]
   [:exercise/difficulty [:enum
                          :exercise.difficulty/high
                          :exercise.difficulty/mid
                          :exercise.difficulty/low]]
   [:exercise/instructions
    [:or
     :string
     [:vector Code]
     [:map-of :keyword [:vector Code]]]]
   [:exercise/uses [:set [:or :keyword :symbol :string]]]
   [:exercise/related [:set :string]]
   [:exercise/function-template [:maybe [:vector Code]]]
   [:exercise/test-cases [:vector [:map
                                   [:input Code]
                                   [:output Code]]]]
   [:exercise/solution [:maybe [:vector Code]]]
   [:exercise/teaches [:set [:or :keyword :symbol :string]]]])
