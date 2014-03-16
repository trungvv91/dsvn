/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dsvn.ngrams;

import com.dsvn.mapdb.MapDBModel;
import com.dsvn.wordtoken.WordLabel;
import java.util.Map.Entry;
import java.util.*;
import org.mapdb.*;

/**
 *
 * @author TRUNG
 */
public class BigramMapDB extends MapDBModel<Fun.Tuple2<String, String>, Fun.Tuple2<Double, Integer>> {

    public BigramMapDB() {
        this.mapname = MapDBModel.BIDB_MAPNAME;
    }

    @Override
    public boolean checkProbability() {
        openDB();
        double p = 0.0;
        float c = 0;
        for (Entry<Fun.Tuple2<String, String>, Fun.Tuple2<Double, Integer>> entrySet : map.entrySet()) {
            p += entrySet.getValue().a;
            c += entrySet.getValue().b;
        }
        System.out.println("P = " + p + " ; N = " + c);
        BTreeMap<String, Fun.Tuple2<Double, Integer>> unimap = db.getTreeMap(MapDBModel.UNIDB_MAPNAME);
        Fun.Tuple2<Double, Integer> value = unimap.get(WordLabel.N);
        boolean rs = (Math.abs(p + 2 - unimap.size()) < 1e-3) && (Math.abs(c + value.a - value.b) < 1);  // p == V
        closeDB();
        return rs;
    }

    @Override
    public Fun.Tuple2<Double, Integer> getValue(String... words) {
        Fun.Tuple2<String, String> key = Fun.t2(words[0], words[1]);
        Fun.Tuple2<Double, Integer> value;
        try {
            value = map.get(key);
        } catch (Exception e) {
            value = null;
        }
        return value;
    }

    @Override
    public ArrayList<Object[]> getAll() {
        ArrayList<Object[]> data = new ArrayList<>();
        openDB();
        Set<Entry<Fun.Tuple2<String, String>, Fun.Tuple2<Double, Integer>>> entrySet = map.entrySet();
        for (Map.Entry<Fun.Tuple2<String, String>, Fun.Tuple2<Double, Integer>> entry : entrySet) {
            Fun.Tuple2<String, String> key = entry.getKey();
            Fun.Tuple2<Double, Integer> value = entry.getValue();
            Object[] bModel = new Object[]{key.a, key.b, value.a, value.b};
            data.add(bModel);
        }
        closeDB();
        return data;
    }

    @Override
    public void printMap() {
        for (Map.Entry<Fun.Tuple2<String, String>, Fun.Tuple2<Double, Integer>> entry : map.entrySet()) {
            System.out.println("Key : " + entry.getKey().toString() + " Value : " + entry.getValue().toString());
        }
    }

    public static void main(String[] args) {
//        System.out.println(-Double.MAX_VALUE - 1000 == -Double.MAX_VALUE);

        BigramMapDB bigramMapDB = new BigramMapDB();
        bigramMapDB.getAll();
//        bigramMapDB.createMap("data/myBigramModel.txt");

//        bigramMapDB.openDB();
//        BTreeMap<String, Double> map = bigramMapDB.db.getTreeMap(mapname);
//        System.out.println(map.size());
//        System.out.println(map.get("bò_0 còn_3"));
//        bigramMapDB.closeDB();
    }
}
