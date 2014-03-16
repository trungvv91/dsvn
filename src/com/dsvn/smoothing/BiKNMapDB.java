/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dsvn.smoothing;

import com.dsvn.mapdb.MapDBModel;
import static com.dsvn.smoothing.KneserNey.BIKN_MAPNAME;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import org.mapdb.Fun;

/**
 *
 * @author TRUNG
 */
public class BiKNMapDB extends MapDBModel<Fun.Tuple2<String, String>, Double> {

    public BiKNMapDB() {
        this.mapname = KneserNey.BIKN_MAPNAME;
    }

    @Override
    public boolean checkProbability() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Double getValue(String... words) {
        Fun.Tuple2<String, String> key = Fun.t2(words[0], words[1]);
        Double value;
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
        Set<Map.Entry<Fun.Tuple2<String, String>, Double>> entrySet = map.entrySet();
        for (Map.Entry<Fun.Tuple2<String, String>, Double> entry : entrySet) {
            Fun.Tuple2<String, String> key = entry.getKey();
            Object[] bModel = new Object[]{key.a, key.b, entry.getValue()};
            data.add(bModel);
        }
        closeDB();
        return data;
    }

    @Override
    public void printMap() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static void main(String[] args) {
        BiKNMapDB kneserNeyMapDB = new BiKNMapDB();
        kneserNeyMapDB.openDB();
        if (kneserNeyMapDB.db.exists(BIKN_MAPNAME)) {
            System.out.println("ok");
        }
//        System.out.println(kneserNeyMapDB.getAll().size());        
        kneserNeyMapDB.closeDB();
    }
}
