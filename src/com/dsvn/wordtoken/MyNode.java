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
