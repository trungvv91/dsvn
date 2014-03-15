/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dsvn.mapdb;

import com.dsvn.ngrams.UnigramModel;
import com.dsvn.ngrams.WordLabel;
import java.util.*;
import java.util.Map.Entry;
import org.mapdb.*;

/**
 *
 * @author TRUNG
 */
class UniIterator implements Iterator<Fun.Tuple2<String, Fun.Tuple2<Double, Integer>>> {

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
    public Fun.Tuple2<String, Fun.Tuple2<Double, Integer>> next() {
        UnigramModel uModel = list.get(counter);
        Fun.Tuple2<Double, Integer> value = Fun.t2(uModel.probability, uModel.count);
        Fun.Tuple2<String, Fun.Tuple2<Double, Integer>> next = Fun.t2(uModel.word1, value);
        counter++;
        return next;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

public class UnigramMapDB extends NgramsMapDB {

    private BTreeMap<String, Fun.Tuple2<Double, Integer>> unimap;

    public UnigramMapDB() {
        this.mapname = NgramsMapDB.UNIDB_MAPNAME;
    }

    @Override
    public void openDB() {
        if (db == null || unimap == null) {
            db = DBMaker.newFileDB(DBFile).make();
            unimap = db.getTreeMap(mapname);
            System.out.println("there are " + unimap.size() + " items in unimap (-1 NWORDS for metadata)");
        }
    }

    @Override
    public void closeDB() {
        //map.close();
        db.close();
        unimap = null;
        db = null;
    }

    @Override
    public boolean checkProbability() {
        openDB();
        double p = 0.0;
        float c = 0;
        for (Entry<String, Fun.Tuple2<Double, Integer>> entrySet : unimap.entrySet()) {
            p += entrySet.getValue().a;
            c += entrySet.getValue().b;
        }
        p -= unimap.get(WordLabel.N).a;
        c -= unimap.get(WordLabel.N).b;
        closeDB();
        System.out.println("P = " + p + " ; N = " + c);
        return Math.abs(p - 1.0) < 1e-6;
    }

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
    public float getCount(String... words) {
        float count;
        try {
            count = unimap.get(words[0]).b;
        } catch (Exception e) {
            count = 0;
        }
        return count;
    }

    @Override
    public Fun.Tuple2<Double, Integer> getValue(String... words) {
        Fun.Tuple2<Double, Integer> value;
        try {
            value = unimap.get(words[0]);
        } catch (Exception e) {
            value = null;
        }
        return value;
    }

    @Override
    public ArrayList<UnigramModel> getAll() {
        openDB();
        Set<Entry<String, Fun.Tuple2<Double, Integer>>> entrySet = unimap.entrySet();

        ArrayList<UnigramModel> data = new ArrayList<>();
        for (Map.Entry<String, Fun.Tuple2<Double, Integer>> entry : entrySet) {
            UnigramModel uModel = new UnigramModel();
            uModel.word1 = entry.getKey();
            Fun.Tuple2<Double, Integer> value = entry.getValue();
            uModel.probability = value.a;
            uModel.count = value.b;
            data.add(uModel);
        }
        closeDB();
        return data;
    }

    @Override
    public void printMap() {
        for (Map.Entry<String, Fun.Tuple2<Double, Integer>> entry : unimap.entrySet()) {
            System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue().toString());
        }
    }

    public static void main(String[] args) {
        UnigramMapDB unigramMapDB = new UnigramMapDB();
//        unigramMapDB.createMap("data/myUnigramModel.txt");
        System.out.println(unigramMapDB.checkProbability());
    }
}
