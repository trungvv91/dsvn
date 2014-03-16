/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dsvn.smoothing;

import com.dsvn.ngrams.BigramMapDB;
import com.dsvn.mapdb.MapDBModel;
import com.dsvn.ngrams.UnigramMapDB;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentNavigableMap;
import org.mapdb.BTreeKeySerializer;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Fun;
import org.mapdb.Pump;

/**
 * Database for pre-process smoothing values
 *
 * @author TRUNG
 */
public class KneserNey {

    /**
     * final String represents kn-bimap's name
     */
    public static final String BIKN_MAPNAME = "bimap_kn";

    UnigramMapDB unigramMapDB;
    BigramMapDB bigramMapDB;
    DB db;
    ConcurrentNavigableMap<String, Fun.Tuple2> unimap;
    ConcurrentNavigableMap<Fun.Tuple2, Fun.Tuple2> bimap;
    ConcurrentNavigableMap<Fun.Tuple2, Double> bikn;
    Map<String, Fun.Tuple2<Double, Integer>> unikn;

    double[] D = new double[4];      // D_1, ... , D_3+;

    public KneserNey() {
        db = DBMaker.newFileDB(MapDBModel.DBFile).make();
        unimap = db.getTreeMap(MapDBModel.UNIDB_MAPNAME);
        bimap = db.getTreeMap(MapDBModel.BIDB_MAPNAME);
        setD();
    }

    /**
     * Tính n_c = N_c (..) = số bigram xuất hiện c lần
     *
     */
    private void setD() {
        int[] n = new int[5];   // n_1, ... , n_4
        n[0] = 0;
        for (Fun.Tuple2<Double, Integer> value : bimap.values()) {
            if (value.b < 5) {
                n[value.b] += 1;
            }
        }

        D[0] = 0;
        double Y = n[1] / (n[1] + 2.0 * n[2]);
        for (int i = 1; i <= 3; i++) {
            D[i] = i - (i + 1) * Y * n[i + 1] / n[i];
        }
    }

    /**
     * Tính N_c = N_c (w_{i-1} , .) = số bigram bắt đầu là w_{i-1} và xuất hiện
     * c lần DB must be opened already
     *
     * @param w1
     * @return
     */
    private int[] N(String w1) {
        int[] N = new int[4];

//        ConcurrentNavigableMap<Fun.Tuple2, Fun.Tuple2> subMap = bimap.subMap(Fun.t2(null, w1), Fun.t2(Fun.HI, w1));
        ConcurrentNavigableMap<Fun.Tuple2, Fun.Tuple2> subMap = bimap.subMap(Fun.t2(w1, null), Fun.t2(w1, Fun.HI));
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

    private void createNewUnikn() {
        if (unikn != null) {
            unikn.clear();
            unikn = null;
        }
        unikn = new HashMap<>();

        // set gamma w_{i-1}
        for (String w1 : unimap.keySet()) {
            int[] N = N(w1);
            double gamma = 0.0;
            for (int i = 1; i < N.length; i++) {
                gamma += (D[i] * N[i]);
            }
            gamma /= ((int) unimap.get(w1).b);
            unikn.put(w1, Fun.t2(gamma, 0));
        }

        // set P_KN w_i
        for (Fun.Tuple2 bikey : bimap.keySet()) {
            String w2 = bikey.b.toString();
            Fun.Tuple2<Double, Integer> value = unikn.get(w2);
            unikn.put(w2, Fun.t2(value.a, value.b + 1));
        }
    }

    private void createNewBikn() {
        if (db.exists(BIKN_MAPNAME)) {
            db.delete(BIKN_MAPNAME);
        }
        final int bisize = bimap.size();
        Iterator<Fun.Tuple2> biSource = bimap.keySet().iterator();
        biSource = Pump.sort(biSource, true, 100000,
                Collections.reverseOrder(BTreeMap.COMPARABLE_COMPARATOR), db.getDefaultSerializer());
        Fun.Function1<Double, Fun.Tuple2> bivalueExtractor = new Fun.Function1<Double, Fun.Tuple2>() {
            @Override
            public Double run(Fun.Tuple2 key) {
                int c_12 = (int) bimap.get(key).b;
                int c_1 = (int) unimap.get(key.a.toString()).b;
                int c = (c_12 >= 3) ? 3 : c_12;
                double prob = (c_12 - D[c]) / c_1;
                double gamma = unikn.get(key.a.toString()).a;
                int Ni = unikn.get(key.b.toString()).b;
                prob += gamma * Ni / bisize;
//                System.out.println(prob);
                return prob;
            }
        };
        bikn = db.createTreeMap(BIKN_MAPNAME)
                .pumpSource(biSource, bivalueExtractor) //.pumpPresort(100000) // for presorting data we could also use this method
                .keySerializer(BTreeKeySerializer.TUPLE2)
                .make();

        System.out.println("there are " + bikn.size() + " items in bikn map");
        db.close();
    }

    public void createKN() {
        createNewUnikn();
        createNewBikn();
    }

    public static void main(String[] args) {
        KneserNey kneserNey = new KneserNey();
        kneserNey.createKN();
//        int[] N = new KneserNey().N("và_3");
//        for (int i : N) {
//            System.out.println(i);
//        }

    }

}
