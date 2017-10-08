(ns sc.nrepl.impl
  (:require [sc.api])
  (:import (java.io PushbackReader StringReader StringWriter)
           (java.util ArrayList)))


(defonce current-ep-id (atom nil))

;; ------------------------------------------------------------------------------
;; nREPL middleware

(defn read-tl-forms
  [^String s]
  (let [rdr (PushbackReader. (StringReader. s))
        eof (Object.)]
    (loop [forms []]
      ;; breaking the file String into one String per form so that we may wrap while preserving line numbers as much as possible.
      ;; HACK doing that by using clojure.core/read on a transformed reader that keeps track of where it is in the String.
      (let [sb (StringBuilder.)
            ;; TODO make a more complete implementation, maybe in a Java class. (Val, 30 Aug 2017)
            pbr (proxy
                  [PushbackReader] [rdr]
                  (read []
                    (let [c (.read rdr)]
                      (when-not (= (int c) (int -1))
                        (.append sb (char c)))
                      c))
                  (unread [x]
                    (.deleteCharAt sb
                      (int (dec (.length sb))))
                    (.unread rdr (int x))))
            v (read pbr false eof)]
        (if (identical? v eof)
          forms
          (recur (conj forms (.toString sb))))
        ))))

(defn add-letsc
  [ep-id s]
  (if (some? ep-id)
    (let [forms (read-tl-forms s)]
      (->> forms
        (map (fn [s1]
               (str "(sc.api/letsc " (pr-str ep-id) " " s1 ")")))
        (apply str)))
    s))

(defn rewrite-args
  [ep-id args]
  (if
    (nil? ep-id)
    args
    (update args 0
      (fn [arg]
        (cond
          (and
            (-> arg :op (= "load-file"))
            (string? (:file arg)))
          (update arg :file #(add-letsc ep-id %))

          (and
            (-> arg :op (= "eval"))
            (string? (:code arg)))
          (update arg :code #(add-letsc ep-id %))

          :else
          arg)))))

(defn wrap-handle
  [handler args]
  ;(prn :nrepl args)
  ;(swap! a conj (-> args first (dissoc :transport)))
  (apply handler #_args (rewrite-args @current-ep-id args)))

(comment
  (reset! current-ep-id nil)

  (rewrite-args @current-ep-id
    [{:file "(in-ns 'bf.nrepl)\n\n\n\n\n\n\n\n\n(+ 1 2)", :file-name "nrepl.cljc", :file-path "/Users/val/projects/breakform/src/bf/nrepl.cljc", :id "65172bfb-6638-4b40-a8b7-85b9932d7c09", :op "load-file", :session "144d968e-5ccb-4f41-9f98-69c76ac73e7b", :transport nil}])

  (def s "(in-ns 'bf.nrepl)\n\n\n\n\n\n@a")

  (read-tl-forms "(in-ns 'bf.nrepl)\n\n\n\n\n\n@a")
  (read-tl-forms "(in-ns 'bf.nrepl)\n\n\n\n\n\n\n\n\n(+ 1 2)")

  )




