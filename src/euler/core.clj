(ns euler.core
  (:gen-class))

(defn running-sum [n]
  (/ (* n (+ n 1)) 2))

(defn make-summer [n]
  (fn [i]
    (* (running-sum (Math/floor (/ i n)))
       n)))

(defn project1 [n]
  "Sum of numbers less than 1000 that are multiples of 3 or 5"
  (let [threes (make-summer 3)
        fives (make-summer 5)
        fiveteens (make-summer 15)]
    (- (+ (threes n) (fives n)) (fiveteens n))))

(defn project1-with-loop [n]
  (loop [i 1 sum 0]
    (if (>= i n)
      sum
      (if (or (= 0 (mod i 3)) (= 0 (mod i 5)))
        (recur (inc i) (+ sum i))
        (recur (inc i) sum)))))

(defn threes-and-fives
  ([] (threes-and-fives 1))
  ([i] (if (or (zero? (mod i 3)) (zero? (mod i 5)))
    (cons i (lazy-seq (threes-and-fives (inc i))))
    (recur (inc i)))))

(defn project1-with-seq [n]
  (reduce + (take-while #(< % n) (threes-and-fives)))
)

(defn project2 [n]
  "Sum of even Fibonacci numbers less that 4000000"
  (loop [fib1 1 fib2 2 sum 0]
    (if (>= fib2 n)
      sum
      (recur fib2 (+ fib2 fib1) (if (= 0 (mod fib2 2))
                                      (+ sum fib2)
                                      sum)))))

(defn fibonacci-seq
  ([] (fibonacci-seq 1 1))
  ([f1 f2] (cons f2 (lazy-seq (fibonacci-seq f2 (+ f1 f2))))))

(defn project2-with-seq [n]
  (reduce + (filter even? (take-while #(< % n) (fibonacci-seq))))
)

(defn primes
  ;I found a more efficient algorithm online, but since I am learning I thought I would write one myself
  ([] (primes (set nil) 2))
  ([found_primes n]
    (if (some #(= 0 (mod n %)) found_primes)
      (recur found_primes (inc n))
      (cons n (lazy-seq (primes (conj found_primes n) (inc n)))))))

(defn project3
  ([n] (project3 n (primes)))
  ([n p] (let [f (first p)]
    (cond
      (= f n) n
      (= 0 (mod n f)) (recur (/ n f) p)
      :else (recur n (rest p))))))

(require '[clojure.string :as str])

(defn palindromic? "test whether a number is palindromic"
  [n]
  (let [a (rest (str/split (str n) #""))]
    (= a (reverse a))))

(defn palindromic2? [n]
  (let [a (seq (str n)) b (reverse a)]
    (= a b)))
 
(defn has-3-digit-factors
  [n]
  (let [sqrt-n (Math/floor (Math/sqrt n))]
    (if
      (> sqrt-n 999) false
      (loop [i sqrt-n]
        (cond
          (< i 100) false
          (> (/ n i) 999) false
          (and (= 0.0 (mod n i)) (= (/ n i) (Math/floor (/ n i)))) true
          :else (recur (dec i)))))))

(defn project4 []
  (loop [n (* 999 999)]
    (if (and (palindromic? n) (has-3-digit-factors n))
      n
      (recur (dec n)))))

(defn factor [n]
  (loop [working-n n factors '() p (primes)]
    (let [f (first p)]
      (cond 
        (= f working-n) (conj factors f)
        (= 0 (mod working-n f)) (recur (/ working-n f) (conj factors f) p)
        :else (recur working-n factors (rest p))))))

(defn lazy-factor
  ([n] (lazy-factor n (primes)))
  ([n p] (let [f (first p)]
      (cond
        (= n 1) nil
        (zero? (mod n f)) (cons f (lazy-seq (lazy-factor (/ n f) p)))
        :else (recur n (rest p))))))

(defn factor-seq
  ([] (factor-seq 1))
  ([n] (cons (lazy-factor n) (lazy-seq (factor-seq (inc n))))))

(defn int-pow [n x]
  (reduce * (repeat x n)))

(defn project5 [n]
  (reduce (fn [sum [a b]] (* sum (int-pow a b))) 1
    (reduce (fn [new-hash [k v]]
        (into new-hash {k (max (get new-hash k 0) v)}))
      {}
      (mapcat (fn [h] (map identity h)) (map frequencies (take n (factor-seq)))))))

(defn project6 [n]
  (- (+ (* (running-sum n) (running-sum n))) (reduce + (map #(* % %) (range 1 (inc n)))))
)

(defn project7 [n]
  (last (take n (primes))))

(defn -main
  [& args]
  (println "Project 1 - " (project1 (- 1000 1)))
  (println "Project 1 using loop - " (project1-with-loop 1000))
  (println "Project 1 using seq - " (project1-with-seq 1000))
  (println "Project 2 - " (project2 4000000))
  (println "Project 2 advanced - " (project2-with-seq 4000000))
  (println "Project 3 - " (project3 6857))
  (println "Project 4 - " (project4))
  (println "Project 5 - " (project5 20))
  (println "Project 6 - " (project6 100))
  (println "Project 7 - " (project7 10001))
  )
