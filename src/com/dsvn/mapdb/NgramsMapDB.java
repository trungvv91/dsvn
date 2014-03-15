/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dsvn.mapdb;

import com.dsvn.ngrams.UnigramModel;
import java.io.File;
import java.util.ArrayList;
import org.mapdb.DB;
import org.mapdb.Fun;

/**
 *
 * @author TRUNG
 */
public abstract class NgramsMapDB {

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

    public NgramsMapDB() {
    }

    protected void deleteOldFile() {
        if (DBFile != null && DBFile.exists()) {
            DBFile.delete();
        }
    }

    /**
     * Open database
     */
    public abstract void openDB();

    public abstract void closeDB();

    public abstract boolean checkProbability();

    /**
     * DB must be opened already
     *
     * @param words
     * @return
     */
    public abstract double getProbability(String... words);

    /**
     * DB must be opened already
     *
     * @param words
     * @return
     */
    public abstract float getCount(String... words);

    /**
     * DB must be opened already
     *
     * @param words
     * @return
     */
    public abstract Fun.Tuple2<Double, Integer> getValue(String... words);

    public abstract ArrayList<? extends UnigramModel> getAll();

    public abstract void printMap();
}
