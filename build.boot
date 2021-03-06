; vim: syntax=clojure
(set-env!
  :project 'irresponsible/unscrew
  :version "0.2.2"
  :resource-paths #{"src" "resources"}
  :source-paths #{"src"}
  :description "A toolkit for processing jar files"
  :url "https://github.com/irresponsible/unscrew"
  :scm {:url "https://github.com/irresponsible/unscrew"}
  :developers {"James Laver" "james@seriesofpipes.com"}
  :license {"MIT" "https://en.wikipedia.org/MIT_License"}
  :dependencies '[[org.clojure/clojure "1.9.0-alpha15" :scope "provided"]
                  [byte-streams "0.2.2"]
                  [me.raynes/fs     "1.4.6"    :scope "test"]
                  [boot/core "2.7.1"           :scope "test"]
                  [adzerk/boot-test "1.2.0"    :scope "test"]])

;; https://github.com/boot-clj/boot/blob/master/doc/boot.task.built-in.md

(require '[adzerk.boot-test :as boot-test])

(task-options!
  pom {:url         (get-env :url)
       :scm         (get-env :scm)
       :project     (get-env :project)
       :version     (get-env :version)
       :license     (get-env :license)
       :description (get-env :description)
       :developers  (get-env :developers)}
  push {:tag            true
        :ensure-branch  "master"
        :ensure-release true
        :ensure-clean   true
        :gpg-sign       true
        :repo     "clojars"
        :repo-map [["clojars" {:url "https://clojars.org/repo/"}]]}
  target  {:dir #{"target"}})

(deftask testing []
  (set-env! :resource-paths #(conj % "test"))
  (set-env! :source-paths #(conj % "test")))
  
(deftask test [] ;; the tests rely on having the jar kicking about!
  (testing)
  (boot-test/test))

(deftask autotest []
  (comp (watch) (test)))

(deftask installdeps []
  identity)

;; RMG Only stuff
(deftask make-jar []
  (comp (pom) (jar) (target)))

(deftask release []
  (comp (pom) (jar) (push)))

;; Travis Only stuff
(deftask travis []
  (testing)
  (boot-test/test))

(deftask travis-installdeps []
  (testing) identity)

(deftask jitpak-deploy []
  (task-options! pom {
   :project (symbol (System/getenv "ARTIFACT"))
  })
  (comp
    (pom)
    (jar)
    (target)      ; Must install to build dir
    (install)     ; And to .m2 https://jitpack.io/docs/BUILDING/#build-customization
  )
)
