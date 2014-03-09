/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dsvn.mapdb;

import com.dsvn.ngrams.BigramModel;
import com.dsvn.util.CorpusUtil;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import org.mapdb.*;

/**
 *
 * @author TRUNG
 */
class BiIterator implements Iterator<Fun.Tuple2<Fun.Tuple2<String, String>, Fun.Tuple2<Double, Float>>> {

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
    public Fun.Tuple2<Fun.Tuple2<String, String>, Fun.Tuple2<Double, Float>> next() {
        BigramModel bModel = list.get(counter);
        Fun.Tuple2<String, String> key = Fun.t2(bModel.word1, bModel.word2);
        Fun.Tuple2<Double, Float> value = Fun.t2(bModel.probability, bModel.count);
        Fun.Tuple2<Fun.Tuple2<String, String>, Fun.Tuple2<Double, Float>> next = Fun.t2(key, value);
        counter++;
        return next;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

public class BigramMapDB extends NgramsMapDB {

    private BTreeMap<Fun.Tuple2<String, String>, Fun.Tuple2<Double, Float>> bimap;

    public BigramMapDB(String DB_FILENAME, String MAP_NAME) {
        super(DB_FILENAME, MAP_NAME);
    }

    @Override
    public void createMap(String filename) throws IOException {
        /**
         * Open database in temporary directory
         */
        db = DBMaker.newFileDB(DBFile) /**
                 * disabling Write Ahead Log makes import much faster
                 */
                .transactionDisable().make();

        long time = System.currentTimeMillis();

        Iterator<Fun.Tuple2<Fun.Tuple2<String, String>, Fun.Tuple2<Double, Float>>> source = new BiIterator(BigramModel.readRawBigramFile(filename));

        /**
         * BTreeMap Data Pump requires data source to be pre-sorted in reverse
         * order (highest to lowest). There is method in Data Pump we can use to
         * sort data. It uses temporarily files and can handle fairly large data
         * sets.
         */
        source = Pump.sort(source,
                true, 100000,
                Collections.reverseOrder(BTreeMap.COMPARABLE_COMPARATOR), //reverse  order comparator
                db.getDefaultSerializer());

        /**
         * Create BTreeMap and fill it with data
         */
        bimap = db.createTreeMap(MAP_NAME).pumpSource(source) //.pumpPresort(100000) // for presorting data we could also use this method
                .keySerializer(BTreeKeySerializer.TUPLE2).make();

        System.out.println("Finished; total time: " + (System.currentTimeMillis() - time) / 1000 + "s; there are " + bimap.size() + " items in map");
        //PrintMap(map);

        closeDB();
    }

    @Override
    public void openDB() {
        db = DBMaker.newFileDB(DBFile).make();
        bimap = db.getTreeMap(MAP_NAME);
    }

    @Override
    public void closeDB() {
        //map.close();
        db.close();
        bimap = null;
        db = null;
    }

    /**
     * DB must be opened already
     *
     * @param words
     * @return
     */
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
    public ArrayList<BigramModel> getAll() {
        openDB();
        Set<Entry<Fun.Tuple2<String, String>, Fun.Tuple2<Double, Float>>> entrySet = bimap.entrySet();

        ArrayList<BigramModel> data = new ArrayList<>();
        for (Map.Entry<Fun.Tuple2<String, String>, Fun.Tuple2<Double, Float>> entry : entrySet) {
            Fun.Tuple2<String, String> key = entry.getKey();
            BigramModel bModel = new BigramModel();
            bModel.word1 = key.a;
            bModel.word2 = key.b;
            Fun.Tuple2<Double, Float> value = entry.getValue();
            bModel.probability = value.a;
            bModel.count = value.b;
            data.add(bModel);
        }
        closeDB();
        return data;
    }

    @Override
    public void printMap() {
        for (Map.Entry<Fun.Tuple2<String, String>, Fun.Tuple2<Double, Float>> entry : bimap.entrySet()) {
            System.out.println("Key : " + entry.getKey().toString() + " Value : " + entry.getValue().toString());
        }
    }

    public static void main(String[] args) throws IOException {
//        System.out.println(-Double.MAX_VALUE - 1000 == -Double.MAX_VALUE);

        BigramMapDB bigramMapDB = new BigramMapDB(CorpusUtil.BIDB_FILENAME, CorpusUtil.BIDB_MAPNAME);
//        bigramMapDB.createMap("data/myBigramModel.txt");

//        bigramMapDB.openDB();
//        BTreeMap<String, Double> map = bigramMapDB.db.getTreeMap(MAP_NAME);
//        System.out.println(map.size());
//        System.out.println(map.get("bò_0 còn_3"));
//        bigramMapDB.closeDB();
    }
}
