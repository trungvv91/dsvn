/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dsvn.ngrams;

import com.dsvn.mapdb.BigramMapDB;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author TRUNG
 */
public class BigramModel extends UnigramModel {

    public String word2;

    @Override
    public Object[] toObjects() {
        return new Object[]{word1, word2, probability, count};
    }

    /**
     * Each line in raw file is in form of 3 or 4 items: w_1 w_2 prob [count]
     *
     * @param filename
     * @return
     */
    public static ArrayList<BigramModel> readRawBigramFile(String filename) {
        ArrayList<BigramModel> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(filename), StandardCharsets.UTF_8))) {
            String line;
            BigramModel bModel;
            while ((line = br.readLine()) != null) {
                if (!"".equals(line)) {
                    String[] items = line.trim().split("\\s+");
                    bModel = new BigramModel();
                    bModel.word1 = items[1];
                    bModel.word2 = items[0];
                    bModel.probability = Double.parseDouble(items[2]);
                    if (items.length > 3) {
                        bModel.count = Float.parseFloat(items[3]);
                    } else {
                        bModel.count = 0;
                    }
                    list.add(bModel);
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BigramMapDB.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BigramMapDB.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;
    }

}
