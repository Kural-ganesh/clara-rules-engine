(ns rules.clojava
  (:require [instaparse.core :as insta]
            [schema.core :as s]
            [clara.rules :refer :all]
            [clara.rules.engine :as eng]
            [clara.rules.compiler :as com]
            [clara.rules.memory :as mem]
            [clojure.pprint])
  (:import [com.rbm Subject Condition]
           [clara.rules WorkingMemory QueryResult])
  (:gen-class
    :name rules.clojava
    :methods [[javamethod [String] Object]]))


(deftype JavaQueryResult [result]
  QueryResult
  (getResult [_ fieldName]
    (get result (keyword fieldName)))
  Object
  (toString [_]
    (.toString result)))

(defn- run-query [session name args]
  (let [query-var (or (resolve (symbol name))
                      (throw (IllegalArgumentException.
                              (str "Unable to resolve symbol to query: " name))))

        ;; Keywordize string keys from Java.
        keyword-args (into {}
                           (for [[k v] args]
                             [(keyword k) v]))
        results (eng/query session (deref query-var) keyword-args)]
    (map #(JavaQueryResult. %) results)))

(deftype JavaWorkingMemory [session]
  WorkingMemory

  (insert [this facts]
    (JavaWorkingMemory. (apply insert session facts)))

  (retract [this facts]
    (JavaWorkingMemory. (apply retract session facts)))

  (fireRules [this]
    (JavaWorkingMemory. (fire-rules session)))

  (query [this name args]
   (run-query session name args))

  (query [this name]
     (run-query session name {})))

(def ae-grammer
    (insta/parser      
        "<S> = [INFERENCES]+;
        INFERENCES = <'has a'> FACTTYPE AE <'when'> COND <'.'>;
        FACTTYPE = 'subject' | 'condition';
        AE = STRING;
        NUMBER = #'[0-9]+' ;
        <STRING> = #'[A-Za-z][A-Za-z0-9_-]+' ;
        COND = FACTTYPE FIELD OPERATOR VALUE;
        FIELD = STRING ;
        OPERATOR = 'is' | '>' | '<' | '=' ;
        <VALUE> = STRING | NUMBER;"

        :auto-whitespace :standard))


(def operators {"is" `=
    ">" `>
    "<" `<
    "=" `=
    })

(def fact-types
    {"subject" Subject
     "condition" Condition})

(def ae-transforms
    {:NUMBER #(Integer/parseInt %)
    :OPERATOR operators
    :FIELD symbol
    :FACTTYPE fact-types
    :AE str
    :COND (fn [fact-type field operator value]
      {:type fact-type
       :constraints [(list operator field value)
       (list (operators "=") (symbol "?subName") (symbol "subName"))]})
    :INFERENCES (fn [facttype ae & con]
        {:name "infer-conditions"
        :lhs con
        :rhs `(insert! (Condition. ~ae ~(symbol "?subName")))
            
        }
    )
    })

(defquery get-conditions
  []
  [?con <- Condition])

(s/defn ^:always-validate load-user-rules :- [clara.rules.schema/Production]
        "Converts a business rule string into Clara productions."
        [business-rules :- s/Str]
        (println "---------------")
        (println "Message:")
        (clojure.pprint/pprint business-rules)
        (let [parse-tree (ae-grammer business-rules)]
            (println "---------------")
            (println "Parse- tree:")
            (clojure.pprint/pprint parse-tree)
          (when (insta/failure? parse-tree)
            (throw (ex-info (print-str parse-tree) {:failure parse-tree})))
          
          (insta/transform ae-transforms parse-tree)))

(defn -javamethod 
  [this b]
  (println "---------------")
  (println "Generated rules:")
  (def rules (load-user-rules b))
  (clojure.pprint/pprint rules)
  (let [session (mk-session 'rules.clojava rules)]
    (JavaWorkingMemory. session)
    ))







