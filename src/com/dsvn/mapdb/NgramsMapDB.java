/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dsvn.mapdb;

import com.dsvn.ngrams.UnigramModel;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.mapdb.DB;

/**
 *
 * @author TRUNG
 */
public abstract class NgramsMapDB {

    /**
     * final String represents database's name
     */
    protected String DB_FILENAME;

    /**
     * final String represents map's name
     */
    protected String MAP_NAME;

    protected File DBFile;
    protected DB db;

    public NgramsMapDB(String DB_FILENAME, String MAP_NAME) {
        this.DB_FILENAME = DB_FILENAME;
        this.MAP_NAME = MAP_NAME;
        DBFile = new File(DB_FILENAME);
    }
    
    

    protected void deleteOldFile() {
        if (DBFile != null && DBFile.exists()) {
            DBFile.delete();
        }
    }

    public abstract void createMap(String filename) throws IOException;

    public abstract void openDB();

    public abstract void closeDB();
    
    public abstract double getProbability(String... words);
    
    public abstract ArrayList<? extends UnigramModel> getAll();
    
    public abstract void printMap();
}
