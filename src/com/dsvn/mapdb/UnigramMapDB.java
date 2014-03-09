/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dsvn.mapdb;

import com.dsvn.ngrams.UnigramModel;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import org.mapdb.*;

/**
 *
 * @author TRUNG
 */
class UniIterator implements Iterator<Fun.Tuple2<String, Fun.Tuple2<Double, Float>>> {

    /**
     * max number of elements to import
     */
    ArrayList<UnigramModel> list;
    int counter;

    public UniIterator(ArrayList<UnigramModel> list) {
        this.list = list;
        counter = 0;
    }

    @Override
    public boolean hasNext() {
        return counter < list.size();
    }

    @Override
    public Fun.Tuple2<String, Fun.Tuple2<Double, Float>> next() {
        UnigramModel uModel = list.get(counter);
        Fun.Tuple2<Double, Float> value = Fun.t2(uModel.probability, uModel.count);
        Fun.Tuple2<String, Fun.Tuple2<Double, Float>> next = Fun.t2(uModel.word1, value);
        counter++;
        return next;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

public class UnigramMapDB extends NgramsMapDB {

    private BTreeMap<String, Fun.Tuple2<Double, Float>> unimap;

    public UnigramMapDB() {
        DB_FILENAME = "data/unigram.mapdb";
        MAP_NAME = "unigram_map";
        DBFile = new File(DB_FILENAME);

    }

    @Override
    public void createMap(String filename) throws IOException {
        deleteOldFile();

        /**
         * Open database, disabling Write Ahead Log makes import much faster
         */
        db = DBMaker.newFileDB(DBFile).transactionDisable().make();

        long time = System.currentTimeMillis();

        Iterator<Fun.Tuple2<String, Fun.Tuple2<Double, Float>>> source = new UniIterator(UnigramModel.readRawUnigramFile(filename));

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
        unimap = db.createTreeMap(MAP_NAME).pumpSource(source) //.pumpPresort(100000) // for presorting data we could also use this method
                .keySerializer(BTreeKeySerializer.STRING).make();

        System.out.println("Finished; total time: " + (System.currentTimeMillis() - time) / 1000 + "s; there are " + unimap.size() + " items in map");

        closeDB();
    }

    @Override
    public void openDB() {
        db = DBMaker.newFileDB(DBFile).make();
        unimap = db.getTreeMap(MAP_NAME);
    }

    @Override
    public void closeDB() {
        //map.close();
        db.close();
        unimap = null;
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
        double value;
        try {
            value = unimap.get(words[0]).a;
        } catch (Exception e) {
            value = 0;
        }
        return value;
    }

    @Override
    public ArrayList<UnigramModel> getAll() {
        openDB();
        Set<Entry<String, Fun.Tuple2<Double, Float>>> entrySet = unimap.entrySet();

        ArrayList<UnigramModel> data = new ArrayList<>();
        for (Map.Entry<String, Fun.Tuple2<Double, Float>> entry : entrySet) {
            UnigramModel uModel = new UnigramModel();
            uModel.word1 = entry.getKey();
            Fun.Tuple2<Double, Float> value = entry.getValue();
            uModel.probability = value.a;
            uModel.count = value.b;
            data.add(uModel);
        }
        closeDB();
        return data;
    }
    
    @Override
    public void printMap() {
        for (Map.Entry<String, Fun.Tuple2<Double, Float>> entry : unimap.entrySet()) {
            System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue().toString());
        }
    }

    public static void main(String[] args) throws IOException {
        UnigramMapDB bigramMapDB = new UnigramMapDB();
        bigramMapDB.createMap("data/myUnigramModel.txt");
    }
}
