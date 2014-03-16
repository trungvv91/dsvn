/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dsvn.mapdb;

import com.dsvn.wordtoken.WordLabel;
import com.dsvn.util.IOUtil;
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
 * @param <K> key
 * @param <V> value
 */
public abstract class MapDBModel<K, V> {

    /**
     * final String represents database's name
     */
    public static final String DB_FILENAME = "data/ngrams.mapdb";
    public static final File DBFile = new File(DB_FILENAME);

    /**
     * final String represents unimap's name
     */
    public static final String UNIDB_MAPNAME = "unimap";

    /**
     * final String represents bimap's name
     */
    public static final String BIDB_MAPNAME = "bimap";

    /**
     * String represents map's name
     */
    protected String mapname;

    protected DB db;
    protected BTreeMap<K, V> map;

    private static void deleteOldFile() {
        if (DBFile != null && DBFile.exists()) {
            DBFile.delete();
        }
    }

    /**
     * Open database
     */
    public void openDB() {
        if (db == null || map == null) {
            db = DBMaker.newFileDB(DBFile).make();
            map = db.getTreeMap(mapname);
        }
        System.out.println("there are " + map.size() + " items in map");
    }

    public void closeDB() {
        //map.close();
        db.close();
        map = null;
        db = null;
    }

    public abstract boolean checkProbability();

    /**
     * DB must be opened already
     *
     * @param words
     * @return
     */
    public abstract V getValue(String... words);

    public abstract ArrayList<Object[]> getAll();

    public abstract void printMap();

    /**
     * Create new database from New Corpus, each line in Corpus is assumed a
     * sentence. Uni-gram and Bi-gram are stored in corresponding maps.
     *
     * @param corpusPath
     */
    public static void CreateDBFromCorpus(String corpusPath) {
        final BTreeMap<String, Integer> tempUnimap = DBMaker.newTempTreeMap();
        final BTreeMap<Fun.Tuple2<String, String>, Integer> tempBimap = DBMaker.newTempTreeMap();

        ArrayList<String> lines = IOUtil.ReadFile(corpusPath);
        int nlines = lines.size();      // number of lines
        System.out.println("there are " + nlines + " lines in corpus");
        int nwords = 0;      // number of words
        Integer count;
        for (String line : lines) {
            line = WordLabel.START + " " + line + " " + WordLabel.END;
            String[] words = line.split("\\s+");
            for (int i = 0; i < words.length - 1; i++) {
                String word1 = words[i];
                count = tempUnimap.get(word1);
                if (count == null) {
                    tempUnimap.put(word1, 1);
                } else {
                    tempUnimap.put(word1, count + 1);
                }

                String word2 = words[i + 1];
                Fun.Tuple2<String, String> key = Fun.t2(word1, word2);
                count = tempBimap.get(key);
                if (count == null) {
                    tempBimap.put(key, 1);
                } else {
                    tempBimap.put(key, count + 1);
                }
            }
            nwords += words.length;
        }
        tempUnimap.put(WordLabel.END, nlines);
        System.out.println("there are " + nwords + " words in corpus");

        deleteOldFile();
        DB db = DBMaker.newFileDB(DBFile).transactionDisable().make();

        final double N = nwords;
        Iterator<String> uniSource = tempUnimap.keySet().iterator();
        uniSource = Pump.sort(uniSource, true, 100000,
                Collections.reverseOrder(BTreeMap.COMPARABLE_COMPARATOR), db.getDefaultSerializer());
        Fun.Function1<Fun.Tuple2<Double, Integer>, String> univalueExtractor = new Fun.Function1<Fun.Tuple2<Double, Integer>, String>() {
            @Override
            public Fun.Tuple2<Double, Integer> run(String s) {
                int counter = tempUnimap.get(s);
                double prob = counter / N;
                return Fun.t2(prob, counter);
            }
        };
        final BTreeMap<String, Fun.Tuple2<Double, Integer>> unimap
                = db.createTreeMap(MapDBModel.UNIDB_MAPNAME)
                .pumpSource(uniSource, univalueExtractor)
                .keySerializer(BTreeKeySerializer.STRING)
                .make();
        unimap.put(WordLabel.N, Fun.t2((double) nlines, nwords));
        System.out.println("there are " + unimap.size() + " items in unimap");        // V = map.size() - 1;

        Iterator<Fun.Tuple2<String, String>> biSource = tempBimap.keySet().iterator();
        biSource = Pump.sort(biSource, true, 100000,
                Collections.reverseOrder(BTreeMap.COMPARABLE_COMPARATOR), db.getDefaultSerializer());
        Fun.Function1<Fun.Tuple2<Double, Integer>, Fun.Tuple2<String, String>> bivalueExtractor = new Fun.Function1<Fun.Tuple2<Double, Integer>, Fun.Tuple2<String, String>>() {
            @Override
            public Fun.Tuple2<Double, Integer> run(Fun.Tuple2<String, String> key) {
                int counter = tempBimap.get(key);
                double prob = counter / (double) unimap.get(key.a).b;
                return Fun.t2(prob, counter);
            }
        };
        BTreeMap<Fun.Tuple2<String, String>, Fun.Tuple2<Double, Integer>> bimap
                = db.createTreeMap(MapDBModel.BIDB_MAPNAME)
                .pumpSource(biSource, bivalueExtractor) //.pumpPresort(100000) // for presorting data we could also use this method
                .keySerializer(BTreeKeySerializer.TUPLE2)
                .make();
        System.out.println("there are " + bimap.size() + " items in bimap");        // V = map.size();

        tempUnimap.close();
        tempBimap.close();
        db.close();
    }

}
