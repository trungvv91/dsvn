/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dsvn.tokenization;

import com.dsvn.wordtoken.WordLabel;
import com.dsvn.wordtoken.SpecialNode;
import com.dsvn.wordtoken.MyNode;
import com.dsvn.wordtoken.WordNode;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author TRUNG
 */
public class Tokenization {

    ArrayList<MyNode> wordlist;
    ArrayList<Integer> tracking;
    double perplexity;
    int N;

    public Tokenization() {
        wordlist = new ArrayList<>();
        tracking = new ArrayList<>();
        perplexity = N = 0;
    }

    public static boolean IsValidPath(int label1, int label2) {
        boolean result;
        switch (label1) {
            case WordLabel.O:
                result = (label2 == WordLabel.O) || (label2 == WordLabel.S);
                break;
            case WordLabel.S:
                result = (label2 == WordLabel.I) || (label2 == WordLabel.E);
                break;
            case WordLabel.I:
                result = (label2 == WordLabel.I) || (label2 == WordLabel.E);
                break;
            case WordLabel.E:
                result = (label2 == WordLabel.O) || (label2 == WordLabel.S);
                break;
            default:
                result = false;
                break;
        }
        return result;
    }

    public void addWord(String word) {
        WordNode newWord = new WordNode(word);
        MyNode prevNode = wordlist.get(wordlist.size() - 1);
        if (MyNode.isStartNode(prevNode)) {
            SpecialNode startNode = (SpecialNode) prevNode;

            double g;
            // Outside
            g = Evaluation.Eval(startNode.word, WordLabel.NONE, newWord.word.toLowerCase(), WordLabel.O);
            newWord.values[WordLabel.O] = startNode.value + g;
            newWord.pnodeLabels[WordLabel.O] = WordLabel.O;

            // Start
            g = Evaluation.Eval(startNode.word, WordLabel.NONE, newWord.word.toLowerCase(), WordLabel.S);
            newWord.values[WordLabel.S] = startNode.value + g;
            newWord.pnodeLabels[WordLabel.S] = WordLabel.O;
        } else {
            WordNode prevWord = (WordNode) prevNode;
            for (int i = 0; i < WordLabel.NUMBER_OF_LABELS; i++) {
                for (int j = 0; j < WordLabel.NUMBER_OF_LABELS; j++) {
                    double g = Evaluation.Eval(prevWord.word, j, newWord.word, i);
                    if (Tokenization.IsValidPath(j, i) && newWord.values[i] < prevWord.values[j] + g) {
                        newWord.values[i] = prevWord.values[j] + g;
                        newWord.pnodeLabels[i] = j;
                    }
                }

                // default trỏ đến Outside của node cha
                if (newWord.pnodeLabels[i] == WordLabel.NONE) {
                    newWord.pnodeLabels[i] = WordLabel.O;
                }
            }
        }
        wordlist.add(newWord);
    }

    public void addStartOfSentence() {
        wordlist.add(SpecialNode.CreateStartNode());
    }

    public void addEndOfSentence() {
        SpecialNode endNode = SpecialNode.CreateEndNode();
        WordNode prevWord = (WordNode) wordlist.get(wordlist.size() - 1);
        double g = Evaluation.Eval(prevWord.word, WordLabel.O, endNode.word, WordLabel.NONE);
        endNode.value = prevWord.values[WordLabel.O] + g;
        endNode.pnodeLabel = WordLabel.O;

        g = Evaluation.Eval(prevWord.word, WordLabel.E, endNode.word, WordLabel.NONE);
        if (endNode.value < prevWord.values[WordLabel.E] + g) {
            endNode.value = prevWord.values[WordLabel.E] + g;
            endNode.pnodeLabel = WordLabel.E;
        }
        wordlist.add(endNode);

        // store new sentence track everywhen reaching the end of each sentence
        int index = tracking.size();
        tracking.add(index, WordLabel.NONE);
        int currLabel = endNode.pnodeLabel;
        for (int i = wordlist.size() - 2;; i--) {
            MyNode currNode = wordlist.get(i);
            if (currNode instanceof WordNode) {
                tracking.add(index, currLabel);
                currLabel = ((WordNode) currNode).pnodeLabels[currLabel];
            } else {        // START node
                tracking.add(index, WordLabel.NONE);
//                System.out.println("Perplexity of sentence: " + Math.pow(10, endNode.value / (index + 1 - tracking.size())));
                perplexity += endNode.value;
                N += (tracking.size() - index - 1);
                return;
            }
        }
    }

    /**
     * Add sentences ["hôm", "nay", "trời", "đẹp"] with automatically insert
     * "BEGIN" and "END" nodes
     *
     * @param words
     */
    public void addSentence(String[] words) {
        addStartOfSentence();
        for (String word : words) {
            addWord(word);
        }
        addEndOfSentence();
    }

    public String getTokenizedResult() {
        String result = "";
        for (int i = 0; i < tracking.size(); i++) {

            switch (tracking.get(i)) {
                case WordLabel.O:
                case WordLabel.E:
                    result += wordlist.get(i).word + " ";
                    break;
                case WordLabel.S:
                case WordLabel.I:
                    result += wordlist.get(i).word + "_";
                    break;
                default:
//                    result += "\n";
                    result += "<.> ";
                    break;
            }
//            System.out.println(tracking.get(i));
        }
        return result.trim();
    }

    public String tokenize(String input) {
        String[] sentences = input.split("[';!,\\.\\?]");
        for (String sentence : sentences) {
            addSentence(sentence.trim().split("\\s+"));
        }
        System.out.println("Perplexity of corpus: " + Math.pow(10, -perplexity / N));
        return getTokenizedResult();
    }
    
    public double getPerplexity() {
        return Math.pow(10, -perplexity / N);
    }

    public static void main(String[] args) throws IOException {
        Evaluation.Init();
        Tokenization tokenization = new Tokenization();

        String input = "Cả sân Allianz Arena đã nổ tung vì sung sướng.";
        System.out.println(tokenization.tokenize(input));
        Evaluation.Destroy();
    }
}
