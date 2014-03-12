/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dsvn.ngrams;

/**
 *
 * @author trung
 */
public class DictionaryModel {
    public String word1;
    public String word2;
    public float count;

    public DictionaryModel() {
    }

    public Object[] toObjects() {
        return new Object[]{word1, word2, count};
    }
}
