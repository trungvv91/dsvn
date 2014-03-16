/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dsvn.tokenization;

import com.dsvn.wordtoken.WordLabel;
import com.dsvn.dict.DictionaryMapDB;
import com.dsvn.smoothing.BiKNMapDB;
import com.dsvn.smoothing.UniKNMapDB;
import com.dsvn.ngrams.BigramMapDB;
import com.dsvn.ngrams.UnigramMapDB;

/**
 *
 * @author TRUNG
 */
public class Evaluation {

//    static UnigramMapDB unigramMapDB;
//    static BigramMapDB bigramMapDB;
    static BiKNMapDB biKNMapDB;
    static UniKNMapDB uniKNMapDB;
    static DictionaryMapDB dictMapDB;

    public static void Init() {
//        unigramMapDB = new UnigramMapDB();
//        bigramMapDB = new BigramMapDB();
//        unigramMapDB.openDB();
//        bigramMapDB.openDB();
        
        uniKNMapDB = new UniKNMapDB();
        biKNMapDB = new BiKNMapDB();
        dictMapDB = new DictionaryMapDB("dsvnDict");
        uniKNMapDB.openDB();
        biKNMapDB.openDB();
        dictMapDB.openDB();
    }

    /**
     * Evaluate("hôm", 1, "nay", 3) = value("hôm_1 nay_3")
     *
     * @param word1 prev word
     * @param label1
     * @param word2 next word
     * @param label2
     * @return
     */
    public static double Eval(String word1, int label1, String word2, int label2) {
        String word_1 = (label1 == WordLabel.NONE) ? word1 : word1 + "_" + label1;
        String word_2 = (label2 == WordLabel.NONE) ? word2 : word2 + "_" + label2;
        double eval;
        Double biKNValue = biKNMapDB.getValue(word_1, word_2);
        if (biKNValue != null) {
            eval = biKNValue;
        } else {
            try {
                Double gamma = uniKNMapDB.getValue(word_1).a;
                Double uniKNValue = uniKNMapDB.getValue(word_2).b;
                eval = gamma * uniKNValue;
            } catch (NullPointerException e) {
                eval = 0;
            }
        }
        eval /= 2;
        Integer dictValue = dictMapDB.getValue(word_1, word_2);
        if (dictValue != null) {
            eval += 0.5;
        }
//        eval = Math.log10(eval);
        eval = (eval == 0) ? -Math.log10(1e7) : Math.log10(eval);
        return eval;
//        String word_1 = (label1 == WordLabel.NONE) ? word1 : word1 + "_" + label1;
//        String word_2 = (label2 == WordLabel.NONE) ? word2 : word2 + "_" + label2;
//        Fun.Tuple2<Double, Integer> value = bigramMapDB.getValue(word_1, word_2);
//        double eval = 0;
//        if (value != null) {
//            eval = value.a;
//        }
//        if (dictMapDB.getValue(word_1, word_2) != null) {
//            eval += 0.5;
//        }
//        eval = (eval == 0) ? -Math.log10(unigramMapDB.getValue(WordLabel.N).b) : Math.log10(eval);
//        return eval;
    }
    
    public static double SmoothEval(String word1, int label1, String word2, int label2) {
        String word_1 = (label1 == WordLabel.NONE) ? word1 : word1 + "_" + label1;
        String word_2 = (label2 == WordLabel.NONE) ? word2 : word2 + "_" + label2;
        double eval;
        Double biKNValue = biKNMapDB.getValue(word_1, word_2);
        if (biKNValue != null) {
            eval = biKNValue;
        } else {
            try {
                Double gamma = uniKNMapDB.getValue(word_1).a;
                Double uniKNValue = uniKNMapDB.getValue(word_2).b;
                eval = gamma * uniKNValue;
            } catch (NullPointerException e) {
                eval = 0;
            }
        }
        eval /= 2;
        Integer dictValue = dictMapDB.getValue(word_1, word_2);
        if (dictValue != null) {
            eval += 0.5;
        }
//        eval = Math.log10(eval);
        eval = (eval == 0) ? -Math.log10(1e7) : Math.log10(eval);
        return eval;
    }

    public static void Destroy() {
//        unigramMapDB.closeDB();
//        bigramMapDB.closeDB();
        
        uniKNMapDB.closeDB();
        biKNMapDB.closeDB();
        dictMapDB.closeDB();
    }

    public static void main(String[] args) {
        Init();
        double a = Eval("a", 1, "b", 2);
        System.out.println(a / 2);
        Destroy();
    }
}
