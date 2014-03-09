/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dsvn.tokenization;

import com.dsvn.mapdb.BigramMapDB;

/**
 *
 * @author TRUNG
 */
public class Evaluation {
    
    static BigramMapDB bigramMapDB;
    
    public static void Init() {
        bigramMapDB = new BigramMapDB();
        bigramMapDB.openDB();
    }
    
    /**
     * Eval("hôm", 1, "nay", 3) = value("hôm_1 nay_3")
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
        return bigramMapDB.getProbability(word_1, word_2);
    }
    
    public static void Destroy() {
        bigramMapDB.closeDB();
        bigramMapDB = null;
    }
}
