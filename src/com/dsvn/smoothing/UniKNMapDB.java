/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dsvn.smoothing;

import com.dsvn.mapdb.MapDBModel;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import org.mapdb.Fun;

/**
 *
 * @author TRUNG
 */
public class UniKNMapDB extends MapDBModel<String, Fun.Tuple2<Double, Double>> {

    public UniKNMapDB() {
        this.mapname = KneserNey.UNIKN_MAPNAME;
    }

    @Override
    public boolean checkProbability() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Fun.Tuple2<Double, Double> getValue(String... words) {
        Fun.Tuple2<Double, Double> value;
        try {
            value = map.get(words[0]);
        } catch (Exception e) {
            value = null;
        }
        return value;
    }

    @Override
    public ArrayList<Object[]> getAll() {
        ArrayList<Object[]> data = new ArrayList<>();
        openDB();        
        Set<Map.Entry<String, Fun.Tuple2<Double, Double>>> entrySet = map.entrySet();
        for (Map.Entry<String, Fun.Tuple2<Double, Double>> entry : entrySet) {
            Fun.Tuple2<Double, Double> value = entry.getValue();
            Object[] bModel = new Object[]{entry.getKey(), value.a, value.b};
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
        UniKNMapDB uniKNMapDB = new UniKNMapDB();
        uniKNMapDB.openDB();        
        uniKNMapDB.closeDB();
    }
}
