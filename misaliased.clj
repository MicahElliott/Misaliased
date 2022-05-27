#!/usr/bin/env bb

;; misaliased — identify inconsistenly named NS aliases across a project

;; NOTE This style with meta info will fail. But these are silly to
;;      have in your code base, so just fix them.
#_(ns ^{:doc    "Some functions to copy data to a new field for a one time job."
        :author "Someone"}
      my.ns
    (:require [cheshire.core :as json]))


(require '[babashka.pods :as pods]
         '[clojure.pprint :as pp])
(pods/load-pod "pod-babashka-parcera")
(require '[pod.babashka.parcera :as parcera])

(println "Looking for NSs")

(defn gather []
  (println "Gathering into structure.")
  (for [file *command-line-args*
        :let  [parsed (parcera/ast (slurp file))]]
    (let [this-ns (second (nth (second parsed) 3))]
      {this-ns
      (remove empty?
              (first
               (for [node (rest parsed)]
                 (when (and (= :list (first node))
                            (= '(:symbol "ns") (second node)))
                   (let [req (first (filter #(= :list (first %)) (rest node)))]
                     (when (= (second req) '(:keyword ":require"))
                       (let [vecs (filter #(= (first %) :vector) (rest req))]
                         (remove nil?
                                 (for [v vecs]
                                   (let [vs (remove #(= :whitespace (first %)) (rest v))
                                         n  (second (first vs))
                                         a  (second (first (filter #(= (first %) :symbol) (rest vs))))]
                                     (when a
                                       [n a this-ns])))))))))))})))

(defn print-inconsistencies []
  (let [all   (gather)
        flat1 (apply concat
                     (mapv #(first (vals %)) all))
        ;; _     (mapv prn flat1)
        flat  (sort flat1)]
    ;; (mapv prn flat)

    ;; (mapv prn (group-by first flat))

    (mapv (fn [[ref entries]]
            (when (< 1 (count (distinct (map second entries))))
              (println "\nERROR:" ref)
              (mapv (fn [entries]
                      (let [[_ n a] entries]
                        (println a "\t->\t" n)))
                    entries)))
          (group-by first flat))))

(print-inconsistencies)

;; TODO Generate list of consistently named NSs to facilitate making a
;;      standardized list. This can then be used with tools like
;;      clj-refactor's "magic require"
;;      (https://github.com/clojure-emacs/clj-refactor.el/wiki#magic-requires)
