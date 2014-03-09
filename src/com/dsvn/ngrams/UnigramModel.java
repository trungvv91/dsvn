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
public class UnigramModel {

    public String word1;
    public double probability;
    public float count;

    public UnigramModel() {
    }

    public Object[] toObjects() {
        return new Object[]{word1, probability, count};
    }

    /**
     * Each line in raw file is in form of 2 or 3 items: w_1 prob [count]
     *
     * @param filename
     * @return
     */
    public static ArrayList<UnigramModel> readRawUnigramFile(String filename) {
        ArrayList<UnigramModel> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(filename), StandardCharsets.UTF_8))) {
            String line;
            UnigramModel uModel;
            while ((line = br.readLine()) != null) {
                if (!"".equals(line)) {
                    String[] items = line.trim().split("\\s+");
                    uModel = new UnigramModel();
                    uModel.word1 = items[0];
                    uModel.probability = Double.parseDouble(items[1]);
                    if (items.length > 2) {
                        uModel.count = Float.parseFloat(items[2]);
                    } else {
                        uModel.count = 0;
                    }
                    list.add(uModel);
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
