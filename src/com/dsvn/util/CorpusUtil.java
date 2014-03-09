/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dsvn.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import org.mapdb.BTreeKeySerializer;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Fun;
import org.mapdb.Pump;

/**
 *
 * @author TRUNG
 */
public class CorpusUtil {

    public static final String UNIDB_FILENAME = "data/unigram.mapdb";
    public static final String UNIDB_MAPNAME = "unimap";

    public static final String BIDB_FILENAME = "data/bigram.mapdb";
    public static final String BIDB_MAPNAME = "bimap";

    public void createUniMap() {
        final BTreeMap<String, Integer> tempMap = DBMaker.newTempTreeMap();
        ArrayList<String> lines = IOUtil.ReadFile("data/NewCorpus.txt");
        for (String line : lines) {
            String[] words = line.split("\\s+");
            for (String word : words) {
                Integer count = tempMap.get(word);
                if (count == null) {
                    tempMap.put(word, 1);
                } else {
                    tempMap.put(word, count + 1);
                }
            }
        }
        System.out.println("there are " + tempMap.size() + " items in temp-map");

        File dbFile = new File(CorpusUtil.UNIDB_FILENAME);
        if (dbFile.exists()) {
            dbFile.delete();
        }
        DB db = DBMaker.newFileDB(dbFile).transactionDisable().make();
        Iterator<String> source = tempMap.keySet().iterator();
        source = Pump.sort(source, true, 100000,
                Collections.reverseOrder(BTreeMap.COMPARABLE_COMPARATOR), db.getDefaultSerializer()
        );
        Fun.Function1<Fun.Tuple2<Double, Float>, String> valueExtractor = new Fun.Function1<Fun.Tuple2<Double, Float>, String>() {
            @Override
            public Fun.Tuple2<Double, Float> run(String s) {
                float counter = (float) tempMap.get(s);
                double prob = counter / tempMap.size();
                return Fun.t2(prob, counter);
            }
        };
        BTreeMap<String, Fun.Tuple2<Double, Float>> map = db.createTreeMap(CorpusUtil.UNIDB_MAPNAME)
                .pumpSource(source, valueExtractor)
                //.pumpPresort(100000) // for presorting data we could also use this method
                .keySerializer(BTreeKeySerializer.STRING)
                .make();

        System.out.println("there are " + map.size() + " items in map");
        tempMap.close();
        db.close();
    }

    public void createBiMap() {
        final BTreeMap<Fun.Tuple2<String, String>, Integer> tempMap = DBMaker.newTempTreeMap();
        ArrayList<String> lines = IOUtil.ReadFile("data/NewCorpus.txt");
        for (String line : lines) {
            String[] words = line.split("\\s+");
            for (int i = 0; i < words.length - 1; i++) {
                String word1 = words[i];
                String word2 = words[i + 1];
                Fun.Tuple2<String, String> key = Fun.t2(word1, word2);
                Integer count = tempMap.get(key);
                if (count == null) {
                    tempMap.put(key, 1);
                } else {
                    tempMap.put(key, count + 1);
                }
            }
        }
        System.out.println("there are " + tempMap.size() + " items in temp-map");
        
        DB uniDB = DBMaker.newFileDB(new File(UNIDB_FILENAME)).make();
        final BTreeMap<String, Fun.Tuple2<Double, Float>> unimap = uniDB.getTreeMap(UNIDB_MAPNAME);

        File dbFile = new File(CorpusUtil.BIDB_FILENAME);
        if (dbFile.exists()) {
            dbFile.delete();
        }
        DB db = DBMaker.newFileDB(dbFile).transactionDisable().make();
        Iterator<Fun.Tuple2<String, String>> source = tempMap.keySet().iterator();
        source = Pump.sort(source, true, 100000,
                Collections.reverseOrder(BTreeMap.COMPARABLE_COMPARATOR), db.getDefaultSerializer()
        );
        Fun.Function1<Fun.Tuple2<Double, Float>, Fun.Tuple2<String, String>> valueExtractor = new Fun.Function1<Fun.Tuple2<Double, Float>, Fun.Tuple2<String, String>>() {
            @Override
            public Fun.Tuple2<Double, Float> run(Fun.Tuple2<String, String> key) {
                float counter = (float) tempMap.get(key);
                Fun.Tuple2<Double, Float> uniValues = unimap.get(key.a);
                double prob = counter / uniValues.b;
                return Fun.t2(prob, counter);
            }
        };
        BTreeMap<Fun.Tuple2<String, String>, Fun.Tuple2<Double, Float>> map = db.createTreeMap(CorpusUtil.BIDB_MAPNAME)
                .pumpSource(source, valueExtractor)
                //.pumpPresort(100000) // for presorting data we could also use this method
                .keySerializer(BTreeKeySerializer.TUPLE2)
                .make();

        System.out.println("there are " + map.size() + " items in map");
        tempMap.close();
        uniDB.close();
        db.close();
    }

    public static void main(String[] args) {
//        new CorpusUtil().createUniMap();
        new CorpusUtil().createBiMap();
    }
}
