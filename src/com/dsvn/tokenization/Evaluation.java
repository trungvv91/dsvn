/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dsvn.tokenization;

import com.dsvn.mapdb.UnigramMapDB;

/**
 *
 * @author TRUNG
 */
public class Evaluation {
    
    static UnigramMapDB unigramMapDB;
    
    public static void Init() {
        unigramMapDB = new UnigramMapDB();
        //unigramMapDB.CreateUnigramMap("data/MyUnigramModel.txt");
        unigramMapDB.openDB();
    }
    
    /**
     * Eval(uni, "hôm", 1, "nay", 3) = value("nay_3 hôm_1")
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
        return unigramMapDB.getMapValue(word_1, word_2);
    }
    
    public static void Destroy() {
        unigramMapDB.closeDB();
        unigramMapDB = null;
    }
}
