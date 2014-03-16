/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dsvn.tokenization;

import com.dsvn.wordtoken.WordLabel;
import com.dsvn.ngrams.BigramMapDB;
import com.dsvn.dict.DictionaryMapDB;
import com.dsvn.ngrams.UnigramMapDB;

/**
 *
 * @author TRUNG
 */
public class Evaluation {
    
    static UnigramMapDB unigramMapDB;
    static BigramMapDB bigramMapDB;
    static DictionaryMapDB dictMapDB;
    
    public static void Init() {
        unigramMapDB = new UnigramMapDB();
        bigramMapDB = new BigramMapDB();
        dictMapDB = new DictionaryMapDB("dsvnDict");
        unigramMapDB.openDB();
        bigramMapDB.openDB();
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
        double eval = bigramMapDB.getValue(word_1, word_2).a;
        if (dictMapDB.getValue(word_1, word_2) > 0) {
            eval += 0.5;
//            eval = (eval == 0.0) ? .10 : eval*2;
        }
        eval = (eval == 0.0) ? -Math.log10(unigramMapDB.getValue(WordLabel.N).b) : Math.log10(eval);
        return eval;
    }
    
    public static void Destroy() {
        unigramMapDB.closeDB();
        bigramMapDB.closeDB();
        dictMapDB.closeDB();
//        unigramMapDB = null;
//        bigramMapDB = null;
//        dictMapDB = null;
    }
}
