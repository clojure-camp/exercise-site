(ns exercise-ui.client.ui.setup-page
  (:require
    [clojure.set :as set]
    [clojure.string :as string]
    [reagent.core :as r]
    [exercise-ui.utils :refer [parse-backticks]]))

(def setups [:front-end :back-end])
(def oses [:mac :ubuntu :windows])
(def editors [:vscode :emacs :intellij :vim :atom])

; more: https://clojurebridgelondon.github.io/workshop/development-tools/install-guides/vscode-calva.html

(defn plugin-view [plugins]
  [:div.plugins {:style {:margin-left "2em"}}
   [:h3 "Editor Plugins"]
   (for [plugin plugins]
     ^{:key (plugin :name)}
     [:div.plugin {:style {:margin-bottom "1em"}}
      [:div
       [:span.name {:style {:font-weight "bold"}}
        (if (plugin :url)
          [:a {:href (plugin :url)}
           (plugin :name)]
          (plugin :name))]
      (when (plugin :group)
        [:span
         " (" (plugin :group) ")"])
      " "
      [:span {:style {:font-style "italic"}}
       (case (plugin :status)
         :recommended "★ recommended"
         "")]]

      [:div (plugin :note)]])])

(def steps
  [{:heading {:front-end "Install Node"
              :back-end "Install Java (JDK)"}
    :front-end "To get started quickly, we can just use a Node library called shadow-cljs that compiles ClojureScript. Eventually, if you want to write server-side Clojure, you should follow the full setup instructions, but for most of our exercises and front-end projects, the quick setup is sufficient.\n\nFirst, check if you have Node installed.\n\tIn a terminal, run: `node -v`. If the command runs, then you're fine."
    :back-end "Clojure runs on the JVM (the Java Virtual Machine). You need to have a JDK installed to develop and run Clojure programs.\n\nFirst, check if you have Java installed.\n\tIn a terminal, run: `java -version`. If the command runs and reports a JDK version > 8 (or 1.8), then you're fine."
    :steps [{#{:front-end :all} "To install Node:\n\tinstall from your package manager:\n\t\thttps://nodejs.org/en/download/package-manager/\n\tor download and install:\n\t\thttps://nodejs.org/en/download/"
             #{:back-end :windows} "To install Java:\n\tvia AdoptOpenJDK (recommended):\n\t\thttps://adoptopenjdk.net/ (choose OpenJDK 8 (LTS) & Hotspot)\n\tvia chocolatey:\n\t\thttps://chocolatey.org/\n\t\t`choco install openjdk`\n\tvia other methods:\n\t\thttps://stackoverflow.com/questions/52511778/how-to-install-openjdk-11-on-windows\n\n\tAfter installing, check if Java is installed correctly as noted above."
             #{:back-end :mac} "To install Java:\n\tvia homebrew:\n\t\t`brew update`\n\t\t`brew cask install java`\n\tvia AdoptOpenJDK:\n\t\thttps://adoptopenjdk.net/releases.html (choose OpenJDK 11 (LTS) & Hotspot)\n\n\tAfter installing, check if Java is installed correctly as noted above."
             #{:back-end :ubuntu}
             "To install Java:\n\tvia apt:\n\t\t`sudo apt-get install default-jre`\n\n\tAfter installing, check if Java is installed correctly as noted above."}]}
   {:heading {:front-end "Install shadow-cljs"
              :back-end "Install Leiningen"}
    :front-end "shadow-cljs is a commandline tool for compiling ClojureScript to Javascript."
    :back-end "Leiningen is a commandline tool for working with Clojure projects (similar to Javascript's npm).\n\nFirst, check if you have Leiningen installed.\n\tIn a terminal, run: `lein -v`. If the command runs and reports a Leiningen version > 2.8, then you're fine."
    :steps [{#{:front-end :all}
             "To install shadow-cljs:\n\t`npm install -g shadow-cljs`"
             #{:back-end :windows}
             "To install Leiningen:\n\tvia leiningen.org:\n\t\tFollow instructions at: https://leiningen.org/\n\t\t(you can save the lein.bat file wherever, perhaps: `C:/Program Files/Leiningen/lein.bat`)\n\t\tyou will need to add the directory you put `lein.bat` in to your `PATH`\n\t\t\tinstructions for Win 10: https://www.architectryan.com/2018/03/17/add-to-the-path-on-windows-10/\n\t\t\tyou will need to close/reopen your cmd.exe/PowerShell for the path to update\n\tvia chocolatey\n\t\t`choco install lein`\n\n\tAfter installing, check if Leiningen installed correctly as noted above."
             #{:back-end :mac}
             "To install Leiningen:\n\tvia homebrew:\n\t\t`brew install leiningen`\n\tvia leiningen.org:\n\t\tFollow instructions at: https://leiningen.org/\n\n\tAfter installing, check if Leiningen is installed correctly as noted above."
             #{:back-end :ubuntu}
             "To install Leiningen:\n\tvia apt:\n\t\t`sudo apt-get install leiningen-clojure` (or `sudo apt-get install leiningen`)\n\tvia leiningen.org:\n\t\tFollow instructions at: https://leiningen.org/\n\t\t\tyou can save the lein.bat file wherever, perhaps: `/usr/bin/lein`\n\t\t\t\tthe directory needs to be in your path, you can check if it's there by running `echo $PATH`\n\t\t\t\tif it's not in your PATH, ask for help\n\t\t\t\tyou will need to close/reopen your shell/terminal for the path to update\n\n\tAfter installing, check if Leiningen is installed correctly as noted above."}]}
    {:heading {:all "Setup Your Editor"}
     :steps [{:vscode "Download VSCode from: https://code.visualstudio.com/\n\n\tTo install plugins/extensions...\n\t\topen the extensions search (via the extensions icon in the left sidebar, or via Ctrl+Shift+X)"
              :emacs "To consider:
                       CIDER
                       Spacemacs
                       Emacs Live
                       inf-clojure: https://clojurescript.org/tools/emacs-inf"
              :atom "To download Atom: https://atom.io/"}
             {;; plugins
              :vscode [plugin-view [{:name "Calva"
                                     :group "REPL integration, etc."
                                     :url "https://marketplace.visualstudio.com/items?itemName=betterthantomorrow.calva"
                                     :status :recommended}]]
              :atom [plugin-view [{:name "Chlorine"
                                   :url "https://atom.io/packages/chlorine"
                                   :group "REPL integration"
                                   :status :recommended}
                                  {:name "parinfer"
                                   :url "https://atom.io/packages/parinfer"
                                   :group "structural editing"
                                   :status :recommended}]]
              :emacs [plugin-view [{:name "clj-refactor"
                                    :url "https://github.com/clojure-emacs/clj-refactor.el"}
                                   {:name "paredit"
                                    :url "http://mumble.net/~campbell/emacs/paredit.html"}
                                   {:name "smartparens"
                                    :url "https://github.com/Fuco1/smartparens"}
                                   {:name "rainbow-delimiters"
                                    :url "https://github.com/Fanael/rainbow-delimiters"}]]
              :intellij [plugin-view [{:name "Cursive"
                                       :url "https://cursive-ide.com/"
                                       :status :recommended}]]
              :vim [plugin-view [{:name "vim-clojure-static"
                                  :url "https://github.com/guns/vim-clojure-static"
                                  :group "syntax support"
                                  :note "syntax higlighting (static), indentation, and autocompletion"
                                  :status :recommended}
                                 {:name "vim-clojure-highlight"
                                  :url "https://github.com/guns/vim-clojure-highlight"
                                  :group "syntax support"
                                  :note "syntax highlighting (dynamic) based on runtime information"
                                  :status :optional}
                                 {:name "vim-clojure"
                                  :group "syntax support"
                                  :status :unknown}
                                 {:name "vim-fireplace"
                                  :url "https://github.com/tpope/vim-fireplace"
                                  :group "repl integration"
                                  :note "repl integration, dynamic autocompletion"
                                  :status :recommended}
                                 {:name "vim-sexp"
                                  :url "https://github.com/guns/vim-sexp"
                                  :group "structural editing"
                                  :note "paredit-style editing"
                                  :status :recommended}
                                 {:url "https://github.com/tpope/vim-sexp-mappings-for-regular-people"
                                  :group "structural editing"
                                  :note "mnemonic keyboard shortcuts for vim-sexp"
                                  :status :recommended}
                                 {:url "https://github.com/bhurlow/vim-parinfer"
                                  :group "structural editing"
                                  :note "brackets based on indentation, don't use with vim-sexp"
                                  :status :recommended}]]}]}
     {:heading {:front-end "Clone the Starter Project"
                :back-end "Setup a New Clojure Project"}
      :front-end "In a terminal, navigate to some directory in which you'll store your projects.\n\nThen run: `git clone git@github.com:clojurecraft/cljs-starter.git`\n\n\t(or `git clone https://github.com/clojurecraft/cljs-starter.git`)\n\n\t(or if you don't have git, download and unzip: https://github.com/clojurecraft/cljs-starter/archive/master.zip)"
      :back-end "In a terminal, navigate to some directory in which you'll store your projects.\n\nThen run: `lein new exercises` to create a new clojure project called \"exercises\""}
     {:heading {:all "Run the Project"}
      #{:front-end :vscode}
      "In VScode open the created `cljs-starter` folder (File > Open Folder...)

      Open `src/demo/core.clj` (via the file tree in the sidebar, or, via Ctrl+P and fuzzy search for it)

      'Jack In', ie. start and connect to a project REPL via Ctrl+Alt+C Ctrl+Alt+J , or by clicking on 'nREPL' in the blue bottom bar
      choose 'Start a REPL...' when prompted
      choose ':dev' when prompted
      (wait a bit...)
      choose ':dev' when prompted (yes, again)
      this should open up a sidebar on the right with the REPL
      (which we won't be using much, mostly we'll be interacting with the running application from our code window)
      open http://localhost:8080 in a browser (you should see 'Hello World!')

      Somewhere in `core.cljs` write `(+ 1 2)` then evaluate it by Ctrl+Alt+C E (that is, while hold Control and Alt press C, release, then press V) (Ctrl+Option+C V on a Windows)
      You should see '2' appear. You're ready to go."

      #{:back-end :vscode}
      "In VScode open the created `exercises` folder (File > Open Folder...)

      Open `src/exercises/core.clj` (via the file tree in the sidebar, or, via Ctrl+P and fuzzy search for it)

      'Jack In', ie. start and connect to a project REPL via Ctrl+Alt+C Ctrl+Alt+J , or by clicking on nREPL in the bottom left
      choose Leiningen when prompted
      this should open up a sidebar on the right with the REPL
      (which we won't be using much, mostly we'll be interacting with the running application from our code window)

      Somewhere in `core.clj` write `(+ 1 2)` then evaluate it by Ctrl+Alt+C V (that is, while hold Control and Alt press C, release, then press V) (Ctrl+Option+C E on a Mac)"
      :fallback "No instructions for your editor yet (sorry!). Ask for help.
                Maybe this will help: https://clojurebridgelondon.github.io/workshop/development-tools/editor-guides/"}
     ])



(defn steps-view [steps selected-setup selected-os selected-editor]
  (let [select-alternative (fn [x]
                             (when x
                               (if-let [matching-key-set (->> (keys x)
                                                              (filter set?)
                                                              (filter (fn [keyset]
                                                                        (set/subset? keyset #{selected-setup selected-os selected-editor :all})))
                                                              first)]
                                 (x matching-key-set)
                                 (or (x :all)
                                     (x selected-setup)
                                     (x selected-os)
                                     (x selected-editor)
                                     (x :fallback)))))]
    [:div.steps {:style {:white-space "pre-wrap"}}
     (into [:<>]
           (for [step steps]
             [:div.step
              [:h2 (select-alternative (step :heading))]
              (when-let [instructions (select-alternative step)]
                (if (string? instructions)
                  (into [:<>]
                        (parse-backticks instructions))
                  instructions))
              (when (step :steps)
                [steps-view (step :steps) selected-setup selected-os selected-editor])]))]))

(defonce selected-setup (r/atom :front-end))
(defonce selected-os (r/atom :mac))
(defonce selected-editor (r/atom :vscode))

(defn setup-page-view []
  [:div#setup.page
   [:h1 "Getting Started"]

   #_[:p "Below are instructions for a full setup (with java). If you have Node, for a quick-setup "
     [:a {:href "https://github.com/clojurecraft/cljs-starter"} "go here"] "."]

   [:div.options
    [:p "Select your Setup Type, OS and preferred Editor:"]

    [:div.setup.option
     [:span.name "Set Up Type:"]
     (doall
       (for [setup setups]
         ^{:key setup}
         [:button {:class (when (= setup @selected-setup) "active")
                   :on-click (fn []
                               (reset! selected-setup setup))}
          (name setup)]))
     [:span.note "(if you don't have any preference, we recommend 'front-end')"]]

    [:div.oses.option
     [:span.name "OS:"]
     (doall
       (for [os oses]
         ^{:key os}
         [:button {:class (when (= os @selected-os) "active")
                   :on-click (fn []
                               (reset! selected-os os))}
          (name os)]))]

    [:div.editors.option
     [:span.name "Editor:"]
     (doall
       (for [editor editors]
         ^{:key editor}
         [:button {:class (when (= editor @selected-editor) "active")
                   :on-click (fn []
                               (reset! selected-editor editor))}
          (name editor)]))
     [:span.note "(if you don't have any preference, we recommend starting with VSCode)"]]]

   [steps-view steps @selected-setup @selected-os @selected-editor]])
