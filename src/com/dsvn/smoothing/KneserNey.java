/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dsvn.smoothing;

import com.dsvn.mapdb.BigramMapDB;
import com.dsvn.mapdb.UnigramMapDB;

/**
 * Database for pre-process smoothing values
 *
 * @author TRUNG
 */
public class KneserNey {

    /**
     * final String represents kn-unimap's name
     */
    public static final String UNIKN_MAPNAME = "unimap_kn";

    /**
     * final String represents kn-bimap's name
     */
    public static final String BIKN_MAPNAME = "bimap_kn";

    UnigramMapDB unigramMapDB;
    BigramMapDB bigramMapDB;
    double[] D;
    int[] n;
    double Y;

    public KneserNey() {
        unigramMapDB = new UnigramMapDB();
        bigramMapDB = new BigramMapDB();

        D = new double[4];      // D_1, ... , D_3+
        n = bigramMapDB.n();      // n_1, ... , n_4
        D[0] = 0;
        Y = n[1] / (n[1] + 2.0 * n[2]);
        for (int i = 1; i <= 3; i++) {
            D[i] = i - (i + 1) * Y * n[i + 1] / n[i];
        }
    }
    
    void createRawKNDB() {
        
    }

}
