(ns sc.nrepl.test.impl
  (:require [clojure.test :as test :refer :all]
            [sc.nrepl.impl :as i]))

(test/deftest read-tl-forms
  (is (=
        (i/read-tl-forms "(in-ns 'myapp.ns)\n\n\n\n\n\n@a")
        ["(in-ns 'myapp.ns)"
         "\n\n\n\n\n\n@a"]))

  (is (=
        (i/read-tl-forms " \n\n(defn foo [x y] (+ x y)  \n) ")
        [" \n\n(defn foo [x y] (+ x y)  \n)"]))
  (is (=
        (i/read-tl-forms "\n(defn foo [x] ;; comment\ny ;; comment\n#_(dsfsfs)\n;; comment z )\n(+ x ; comment\n y)\n ;; comment\n ) ;; comment\n\n ;;comment\n (foo 23)\n")
        ["\n(defn foo [x] ;; comment\ny ;; comment\n#_(dsfsfs)\n;; comment z )\n(+ x ; comment\n y)\n ;; comment\n )"
         " ;; comment\n\n;;comment\n(foo 23)"]))
  )

(deftest rewrite-args-example
  (is
    (=
      (i/rewrite-args 7
        [{:file "(in-ns 'myapp.foo)\n\n\n\n\n\n\n\n\n(+ 1 2)",
          :file-name "myapp.foo.cljc",
          :file-path "/Users/val/projects/myproject/src/myapp/foo.cljc",
          :id "65172bfb-6638-4b40-a8b7-85b9932d7c09",
          :op "load-file",
          :session "144d968e-5ccb-4f41-9f98-69c76ac73e7b", :transport nil}])
      [{:file "(sc.api/letsc 7 (in-ns 'myapp.foo))(sc.api/letsc 7 \n\n\n\n\n\n\n\n\n(+ 1 2))",
        :file-name "myapp.foo.cljc",
        :file-path "/Users/val/projects/myproject/src/myapp/foo.cljc",
        :id "65172bfb-6638-4b40-a8b7-85b9932d7c09",
        :op "load-file",
        :session "144d968e-5ccb-4f41-9f98-69c76ac73e7b",
        :transport nil}]))

  (is
    (=
      (i/rewrite-args 7
        [{:file "\n(defn foo [x] ;; comment\ny ;; comment\n#_(dsfsfs)\n;; comment z )\n(+ x ; comment\n y)\n ;; comment\n ) ;; comment\n\n ;;comment\n (foo 23)\n",
          :file-name "myapp.foo.cljc",
          :file-path "/Users/val/projects/myproject/src/myapp/foo.cljc",
          :id "65172bfb-6638-4b40-a8b7-85b9932d7c09",
          :op "load-file",
          :session "144d968e-5ccb-4f41-9f98-69c76ac73e7b", :transport nil}])
      [{:file "(sc.api/letsc 7 \n(defn foo [x] ;; comment\ny ;; comment\n#_(dsfsfs)\n;; comment z )\n(+ x ; comment\n y)\n ;; comment\n ))(sc.api/letsc 7  ;; comment\n\n ;;comment\n (foo 23))",
        :file-name "myapp.foo.cljc",
        :file-path "/Users/val/projects/myproject/src/myapp/foo.cljc",
        :id "65172bfb-6638-4b40-a8b7-85b9932d7c09",
        :op "load-file",
        :session "144d968e-5ccb-4f41-9f98-69c76ac73e7b",
        :transport nil}]
      ))

  (is
    (=
      (i/rewrite-args [7 -3]
        [{:file "(in-ns 'myapp.foo)\n\n\n\n\n\n\n\n\n(+ 1 2)",
          :file-name "myapp.foo.cljc",
          :file-path "/Users/val/projects/myproject/src/myapp/foo.cljc",
          :id "65172bfb-6638-4b40-a8b7-85b9932d7c09",
          :op "load-file",
          :session "144d968e-5ccb-4f41-9f98-69c76ac73e7b", :transport nil}])
      [{:file "(sc.api/letsc [7 -3] (in-ns 'myapp.foo))(sc.api/letsc [7 -3] \n\n\n\n\n\n\n\n\n(+ 1 2))",
        :file-name "myapp.foo.cljc",
        :file-path "/Users/val/projects/myproject/src/myapp/foo.cljc",
        :id "65172bfb-6638-4b40-a8b7-85b9932d7c09",
        :op "load-file",
        :session "144d968e-5ccb-4f41-9f98-69c76ac73e7b",
        :transport nil}])))

(deftest add-letsc-example
  (is
    (-> (i/add-letsc 7 "\n(defn foo [x] ;; comment\ny ;; comment\n#_(dsfsfs)\n;; comment z )\n(+ x ; comment\n y)\n ;; comment\n ) ;; comment\n\n ;;comment\n (foo 23)\n")
      read-string
      (= '(sc.api/letsc 7 (defn foo [x] y (+ x y))))))
  (is
    (-> (i/add-letsc [7 -3] " ;; comment\n\n;;comment\n(foo 23)")
      read-string
      (= '(sc.api/letsc [7 -3] (foo 23))))))

