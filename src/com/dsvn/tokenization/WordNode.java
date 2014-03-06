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
final class WordLabel {

    public static final int NUMBER_OF_LABELS = 4;

    /**
     * NONE = -1
     */
    public static final int NONE = -1;      //NONE = -1;

    /**
     * OUTSIDE = 0
     */
    public static final int O = 0;      //OUTSIDE = 0;

    /**
     * START = 1
     */
    public static final int S = 1;      //START = 1;

    /**
     * INSIDE = 2
     */
    public static final int I = 2;      //INSIDE = 2;

    /**
     * END = 3
     */
    public static final int E = 3;      //END = 3;
}

public class WordNode {

    public String word;
    public double[] values;
    public int[] pnodeLabels;

    public WordNode(String word) {
        this.word = word;
        values = new double[WordLabel.NUMBER_OF_LABELS];
        for (int i = 0; i < WordLabel.NUMBER_OF_LABELS; i++) {
            values[i] = 0;
        }

        pnodeLabels = new int[WordLabel.NUMBER_OF_LABELS];
        for (int i = 0; i < WordLabel.NUMBER_OF_LABELS; i++) {
            pnodeLabels[i] = WordLabel.NONE;
        }
    }

    /**
     * By default, begin-node's value is stored in OUTSIDE label
     *
     * @return
     */
    public static WordNode CreateBeginNode() {
        WordNode start = new WordNode("BEGIN");
        for (int i = 0; i < WordLabel.NUMBER_OF_LABELS; i++) {
            start.values[i] = -1;
        }
        start.values[WordLabel.O] = 0;
        return start;
    }

    /**
     * By default, end-node's value is stored in OUTSIDE label
     *
     * @return
     */
    public static WordNode CreateEndNode() {
        WordNode end = new WordNode("END");
        for (int i = 0; i < WordLabel.NUMBER_OF_LABELS; i++) {
            end.values[i] = -1;
        }
        end.values[WordLabel.O] = 0;
        end.pnodeLabels[WordLabel.O] = WordLabel.O;
        return end;
    }

    public static boolean IsBeginNode(WordNode node) {
        for (int i = 0; i < WordLabel.NUMBER_OF_LABELS; i++) {
            if (node.pnodeLabels[i] == WordLabel.NONE) {
                return true;
            }
        }
        return false;
    }
}
