/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dsvn.ngrams;

import com.dsvn.mapdb.MapDBModel;
import com.dsvn.wordtoken.WordLabel;
import java.util.*;
import java.util.Map.Entry;
import org.mapdb.*;

/**
 *
 * @author TRUNG
 */
public class UnigramMapDB extends MapDBModel<String, Fun.Tuple2<Double, Integer>> {

    public UnigramMapDB() {
        this.mapname = MapDBModel.UNIDB_MAPNAME;
    }

    @Override
    public boolean checkProbability() {
        openDB();
        double p = 0.0;
        float c = 0;
        for (Entry<String, Fun.Tuple2<Double, Integer>> entrySet : map.entrySet()) {
            p += entrySet.getValue().a;
            c += entrySet.getValue().b;
        }
        p -= map.get(WordLabel.N).a;
        c -= map.get(WordLabel.N).b;
        closeDB();
        System.out.println("P = " + p + " ; N = " + c);
        return Math.abs(p - 1.0) < 1e-6;
    }

    @Override
    public Fun.Tuple2<Double, Integer> getValue(String... words) {
        Fun.Tuple2<Double, Integer> value;
        try {
            value = map.get(words[0]);
        } catch (Exception e) {
            value = null;
        }
        return value;
    }

    @Override
    public ArrayList<Object[]> getAll() {
        ArrayList<Object[]> data = new ArrayList<>();
        openDB();
        Set<Entry<String, Fun.Tuple2<Double, Integer>>> entrySet = map.entrySet();
        for (Map.Entry<String, Fun.Tuple2<Double, Integer>> entry : entrySet) {
            Fun.Tuple2<Double, Integer> value = entry.getValue();
            Object[] uModel = new Object[]{entry.getKey(), value.a, value.b};
            data.add(uModel);
        }
        closeDB();
        return data;
    }

    @Override
    public void printMap() {
        for (Map.Entry<String, Fun.Tuple2<Double, Integer>> entry : map.entrySet()) {
            System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue().toString());
        }
    }

    public static void main(String[] args) {
        UnigramMapDB unigramMapDB = new UnigramMapDB();
//        unigramMapDB.createMap("data/myUnigramModel.txt");
        System.out.println(unigramMapDB.checkProbability());
    }
}
