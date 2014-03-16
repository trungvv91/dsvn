/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dsvn.wordtoken;

/**
 *
 * @author TRUNG
 */
public class WordNode extends MyNode {

    public double[] values;
    public int[] pnodeLabels;

    /**
     * By default, values of a word-node is -1
     *
     * @param word
     */
    public WordNode(String word) {
        super(word);
        values = new double[WordLabel.NUMBER_OF_LABELS];
        for (int i = 0; i < WordLabel.NUMBER_OF_LABELS; i++) {
            values[i] = -Double.MAX_VALUE;
        }

        pnodeLabels = new int[WordLabel.NUMBER_OF_LABELS];
        for (int i = 0; i < WordLabel.NUMBER_OF_LABELS; i++) {
            pnodeLabels[i] = WordLabel.NONE;
        }
    }
}
