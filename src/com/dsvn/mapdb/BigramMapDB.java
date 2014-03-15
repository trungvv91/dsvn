/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dsvn.mapdb;

import com.dsvn.ngrams.BigramModel;
import com.dsvn.ngrams.WordLabel;
import java.util.Map.Entry;
import java.util.*;
import java.util.concurrent.ConcurrentNavigableMap;
import org.mapdb.*;

/**
 *
 * @author TRUNG
 */
class BiIterator implements Iterator<Fun.Tuple2<Fun.Tuple2<String, String>, Fun.Tuple2<Double, Integer>>> {

    /**
     * max number of elements to import
     */
    ArrayList<BigramModel> list;
    int counter;

    public BiIterator(ArrayList<BigramModel> list) {
        this.list = list;
        counter = 0;
    }

    @Override
    public boolean hasNext() {
        return counter < list.size();
    }

    @Override
    public Fun.Tuple2<Fun.Tuple2<String, String>, Fun.Tuple2<Double, Integer>> next() {
        BigramModel bModel = list.get(counter);
        Fun.Tuple2<String, String> key = Fun.t2(bModel.word1, bModel.word2);
        Fun.Tuple2<Double, Integer> value = Fun.t2(bModel.probability, bModel.count);
        Fun.Tuple2<Fun.Tuple2<String, String>, Fun.Tuple2<Double, Integer>> next = Fun.t2(key, value);
        counter++;
        return next;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

public class BigramMapDB extends NgramsMapDB {

    private BTreeMap<Fun.Tuple2<String, String>, Fun.Tuple2<Double, Integer>> bimap;

    public BigramMapDB() {
        this.mapname = NgramsMapDB.BIDB_MAPNAME;
    }

    @Override
    public void openDB() {
        if (db == null || bimap == null) {
            db = DBMaker.newFileDB(DBFile).make();
            bimap = db.getTreeMap(mapname);
            System.out.println("there are " + bimap.size() + " items in bimap");
        }
    }

    @Override
    public void closeDB() {
        //map.close();
        db.close();
        bimap = null;
        db = null;
    }

    @Override
    public boolean checkProbability() {
        openDB();
        double p = 0.0;
        float c = 0;
        for (Entry<Fun.Tuple2<String, String>, Fun.Tuple2<Double, Integer>> entrySet : bimap.entrySet()) {
            p += entrySet.getValue().a;
            c += entrySet.getValue().b;
        }
        System.out.println("P = " + p + " ; N = " + c);
        BTreeMap<String, Fun.Tuple2<Double, Integer>> unimap = db.getTreeMap(NgramsMapDB.UNIDB_MAPNAME);
        Fun.Tuple2<Double, Integer> value = unimap.get(WordLabel.N);
        boolean rs = (Math.abs(p + 2 - unimap.size()) < 1e-3) && (Math.abs(c + value.a - value.b) < 1);  // p == V
        closeDB();
        return rs;
    }

    @Override
    public double getProbability(String... words) {
        Fun.Tuple2<String, String> key = Fun.t2(words[0], words[1]);
        double value;
        try {
            value = bimap.get(key).a;
        } catch (Exception e) {
            value = 0;
        }
        return value;
    }

    @Override
    public float getCount(String... words) {
        Fun.Tuple2<String, String> key = Fun.t2(words[0], words[1]);
        float count;
        try {
            count = bimap.get(key).b;
        } catch (Exception e) {
            count = 0;
        }
        return count;
    }

    @Override
    public Fun.Tuple2<Double, Integer> getValue(String... words) {
        Fun.Tuple2<String, String> key = Fun.t2(words[0], words[1]);
        Fun.Tuple2<Double, Integer> value;
        try {
            value = bimap.get(key);
        } catch (Exception e) {
            value = null;
        }
        return value;
    }

    @Override
    public ArrayList<BigramModel> getAll() {
        ArrayList<BigramModel> data = new ArrayList<>();

        openDB();
        Set<Entry<Fun.Tuple2<String, String>, Fun.Tuple2<Double, Integer>>> entrySet = bimap.entrySet();
        for (Map.Entry<Fun.Tuple2<String, String>, Fun.Tuple2<Double, Integer>> entry : entrySet) {
            Fun.Tuple2<String, String> key = entry.getKey();
            BigramModel bModel = new BigramModel();
            bModel.word1 = key.a;
            bModel.word2 = key.b;
            Fun.Tuple2<Double, Integer> value = entry.getValue();
            bModel.probability = value.a;
            bModel.count = value.b;
            data.add(bModel);
        }
        closeDB();
        return data;
    }

    @Override
    public void printMap() {
        for (Map.Entry<Fun.Tuple2<String, String>, Fun.Tuple2<Double, Integer>> entry : bimap.entrySet()) {
            System.out.println("Key : " + entry.getKey().toString() + " Value : " + entry.getValue().toString());
        }
    }

    /**
     * Tính n_c = N_c (..) = số bigram xuất hiện c lần
     *
     * @return
     */
    public int[] n() {
        int[] n = new int[5];
        n[0] = 0;
        openDB();
        for (Fun.Tuple2<Double, Integer> value : bimap.values()) {
            if (value.b < 5) {
                n[value.b] += 1;
            }
        }
        closeDB();
        return n;
    }

    /**
     * Tính N_c = N_c (w_{i-1} , .) = số bigram bắt đầu là w_{i-1} và xuất hiện
     * c lần DB must be opened already
     *
     * @param w1
     * @return
     */
    public int[] N(String w1) {
        int[] N = new int[4];
        ConcurrentNavigableMap<Fun.Tuple2, Fun.Tuple2> map = db.getTreeMap(mapname);
        ConcurrentNavigableMap<Fun.Tuple2, Fun.Tuple2> subMap = map.subMap(Fun.t2(w1, null), Fun.t2(w1, Fun.HI));
//        ConcurrentNavigableMap<Fun.Tuple2<String, String>, Fun.Tuple2<Double, Integer>> subMap = bimap.subMap(Fun.t2(w1, (String)null), Fun.t2(w1, (String)Fun.HI));
        for (Fun.Tuple2<Double, Integer> value : subMap.values()) {
            if (value.b < 3) {
                N[value.b] += 1;
            } else {
                N[3] += 1;
            }
//            N[0] += value.b;
        }
        return N;
    }

    public static void main(String[] args) {
//        System.out.println(-Double.MAX_VALUE - 1000 == -Double.MAX_VALUE);

        BigramMapDB bigramMapDB = new BigramMapDB();
//        bigramMapDB.createMap("data/myBigramModel.txt");
        bigramMapDB.openDB();
        for (int i : bigramMapDB.N("và_0")) {
            System.out.println(i);
        }
        bigramMapDB.closeDB();

//        bigramMapDB.openDB();
//        BTreeMap<String, Double> map = bigramMapDB.db.getTreeMap(mapname);
//        System.out.println(map.size());
//        System.out.println(map.get("bò_0 còn_3"));
//        bigramMapDB.closeDB();
    }
}
