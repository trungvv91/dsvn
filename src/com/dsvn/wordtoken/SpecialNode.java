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
public class SpecialNode extends MyNode {

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
