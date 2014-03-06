/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dsvn.mapdb;

import com.dsvn.util.IOUtil;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import org.mapdb.*;

/**
 *
 * @author TRUNG
 */
public class UnigramMapDB {

    public static final String DB_FILENAME = "data/unigram.mapdb";
    public static final String MAP_NAME = "unimap";

    private final File dbFile;
    private DB db;
    private BTreeMap<String, Double> map;

    public UnigramMapDB() {
        dbFile = new File(DB_FILENAME);
    }

    public void CreateUnigramMap(String filename) throws IOException {
        /**
         * Open database in temporary directory
         */
        db = DBMaker
                .newFileDB(dbFile)
                /**
                 * disabling Write Ahead Log makes import much faster
                 */
                .transactionDisable()
                .make();

        long time = System.currentTimeMillis();

        /**
         * Source of data which randomly generates strings. In real world this
         * would return data from file.
         */
        Iterator<Fun.Tuple2<String, Double>> source = IOUtil.ReadFile(filename).iterator();

        /**
         * BTreeMap Data Pump requires data source to be pre-sorted in reverse
         * order (highest to lowest). There is method in Data Pump we can use to
         * sort data. It uses temporarily files and can handle fairly large data
         * sets.
         */
        source = Pump.sort(source,
                true, 100000,
                Collections.reverseOrder(BTreeMap.COMPARABLE_COMPARATOR), //reverse  order comparator
                db.getDefaultSerializer()
        );

        /**
         * Create BTreeMap and fill it with data
         */
        map = db.createTreeMap(MAP_NAME)
                .pumpSource(source)
                //.pumpPresort(100000) // for presorting data we could also use this method
                .keySerializer(BTreeKeySerializer.STRING)
                .make();

        System.out.println("Finished; total time: " + (System.currentTimeMillis() - time) / 1000 + "s; there are " + map.size() + " items in map");
        //PrintMap(map);

        closeDB();
        //return map;
    }

    public void openDB() {
        db = DBMaker.newFileDB(dbFile).make();
        map = db.getTreeMap(MAP_NAME);
    }

    /**
     * Get value of key in inverse form "word_2 word_1". E.g. The value of
     * "nay_3 hom_1" is resulted by calling getMapValue("hom_1", "nay_3")
     *
     * @param word_1 prev word
     * @param word_2 next word
     * @return
     */
    public double getMapValue(String word_1, String word_2) {
        String key = word_2 + " " + word_1;
        double value;
        try {
            value = map.get(key);
        } catch (Exception e) {
            value = 0;
        }
        return value;
    }

    public void closeDB() {
        //map.close();
        db.close();
        map = null;
        db = null;
    }

    public static void PrintMap(Map<String, Double> map) {
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
        }
    }

    public static void main(String[] args) throws IOException {
        UnigramMapDB unigramMapDB = new UnigramMapDB();
        unigramMapDB.CreateUnigramMap("data/MyUnigramModel.txt");

//        unigramMapDB.openDB();
//        BTreeMap<String, Double> map = unigramMapDB.db.getTreeMap(MAP_NAME);
//        System.out.println(map.size());
//        System.out.println(map.get("bò_0 còn_3"));
//        unigramMapDB.closeDB();
    }
}
