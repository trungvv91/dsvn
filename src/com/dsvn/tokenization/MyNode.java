/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dsvn.tokenization;

import com.dsvn.ngrams.WordLabel;

/**
 *
 * @author TRUNG
 */
public abstract class MyNode {

    public String word;

    public MyNode(String word) {
        this.word = word;
    }
    
    public static boolean isStartNode(MyNode node) {
        return (node instanceof SpecialNode) && (node.word.equals(WordLabel.START));
    }
    
    public static boolean isEndNode(MyNode node) {
        return (node instanceof SpecialNode) && (node.word.equals(WordLabel.END));
    }

}

class SpecialNode extends MyNode {

    public double value;
    public int pnodeLabel;

    public SpecialNode(String word) {
        super(word);
        value = 0;
    }

    public SpecialNode(String word, double value, int pnodeLabels) {
        super(word);
        this.value = value;
        this.pnodeLabel = pnodeLabels;
    }

    public static SpecialNode CreateStartNode() {
        SpecialNode start = new SpecialNode(WordLabel.START);
        return start;
    }

    public static SpecialNode CreateEndNode() {
        SpecialNode end = new SpecialNode(WordLabel.END);
        return end;
    }
}

class WordNode extends MyNode {

    public double[] values;
    public int[] pnodeLabels;

    /**
     * By default, values of a word-node is -1
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
