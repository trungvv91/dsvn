/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dsvn.tokenization;

import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author TRUNG
 */
public class Tokenization {

    ArrayList<WordNode> wordlist;
    ArrayList<Integer> tracking;

    public Tokenization() {
        wordlist = new ArrayList<>();
        tracking = new ArrayList<>();
    }

    public void addWord(String word) {
        WordNode newWord = new WordNode(word);
        WordNode prevWord = wordlist.get(wordlist.size() - 1);
        if (WordNode.IsBeginNode(prevWord)) {
            for (int i = 0; i < WordLabel.NUMBER_OF_LABELS; i++) {
                newWord.pnodeLabels[i] = WordLabel.O;
                double g = Evaluation.Eval(prevWord.word, WordLabel.NONE, newWord.word.toLowerCase(), i);
                if (newWord.values[i] < prevWord.values[WordLabel.O] + g) {
                    newWord.values[i] = prevWord.values[WordLabel.O] + g;
                }
            }
        } else {
            for (int i = 0; i < WordLabel.NUMBER_OF_LABELS; i++) {
                for (int j = 0; j < WordLabel.NUMBER_OF_LABELS; j++) {
                    double g = Evaluation.Eval(prevWord.word, j, newWord.word, i);
                    if (newWord.values[i] < prevWord.values[j] + g) {
                        newWord.values[i] = prevWord.values[j] + g;
                        newWord.pnodeLabels[i] = j;
                    }
                }
                if (newWord.pnodeLabels[i] == WordLabel.NONE) {
                    newWord.pnodeLabels[i] = WordLabel.O;
                }
            }
        }
        wordlist.add(newWord);
    }

    public void addBeginNode() {
        wordlist.add(WordNode.CreateBeginNode());
    }

    public void addEndNode() {
        WordNode endNode = WordNode.CreateEndNode();
        WordNode prevWord = wordlist.get(wordlist.size() - 1);
        double g = Evaluation.Eval(prevWord.word, WordLabel.O, endNode.word, WordLabel.NONE);
        endNode.values[WordLabel.O] = prevWord.values[WordLabel.O] + g;
        endNode.pnodeLabels[WordLabel.O] = WordLabel.O;
        
        g = Evaluation.Eval(prevWord.word, WordLabel.E, endNode.word, WordLabel.NONE);
        if (endNode.values[WordLabel.O] < prevWord.values[WordLabel.E] + g) {
            endNode.values[WordLabel.O] = prevWord.values[WordLabel.E] + g;
            endNode.pnodeLabels[WordLabel.O] = WordLabel.E;
        }
        wordlist.add(endNode);

        // store new sentence track everywhen reaching the end of each sentence
        int index = tracking.size();
        int currLabel = endNode.pnodeLabels[WordLabel.O];
        for (int i = wordlist.size() - 2;; i--) {
            tracking.add(index, currLabel);
            if (currLabel == WordLabel.NONE) {      // START node
                break;
            }
            WordNode currNode = wordlist.get(i);
            currLabel = currNode.pnodeLabels[currLabel];
        }
    }

    /**
     * Add sentences ["hôm", "nay", "trời", "đẹp"] with automatically insert
     * "BEGIN" and "END" nodes
     *
     * @param words
     */
    public void addSentence(String[] words) {
        addBeginNode();
        for (String word : words) {
            addWord(word);
        }
        addEndNode();
    }

    public String getTokenizedResult() {
        String result = "";
        boolean isBEGIN = false;
        for (int i = 0; i < tracking.size(); i++) {
            if (isBEGIN) {
                isBEGIN = false;
            } else {
                switch (tracking.get(i)) {
                    case WordLabel.O:
                    case WordLabel.E:
                        result += wordlist.get(i - 1).word + " ";
                        break;
                    case WordLabel.S:
                    case WordLabel.I:
                        result += wordlist.get(i - 1).word + "_";
                        break;
                    default:
                        isBEGIN = true;
                        break;
                }
            }
        }
        return result;
    }

    public static void main(String[] args) throws IOException {
        Evaluation.Init();
        Tokenization tokenization = new Tokenization();
        String[] sentence = {"cô", "ấy", "thích", "cầu", "thủ", "bóng", "rổ"};
        tokenization.addSentence(sentence);
        System.out.println(tokenization.getTokenizedResult());
    }
}
