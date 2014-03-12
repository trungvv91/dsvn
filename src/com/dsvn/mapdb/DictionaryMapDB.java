/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dsvn.mapdb;

import com.dsvn.ngrams.DictionaryModel;
import com.dsvn.util.CorpusUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Fun;

/**
 *
 * @author trung
 */
public class DictionaryMapDB {

    private BTreeMap<Fun.Tuple2<String, String>, Integer> dictmap;
    protected String DICTDB_FILENAME;
    protected String MAP_NAME;
    protected File DBFile;
    protected DB dictDB;

    public DictionaryMapDB(String DICTDB_FILENAME, String MAP_NAME) {
        this.DICTDB_FILENAME = DICTDB_FILENAME;
        this.MAP_NAME = MAP_NAME;
        DBFile = new File(DICTDB_FILENAME);
    }

    public void openDB() {
        dictDB = DBMaker.newFileDB(DBFile).make();
        dictmap = dictDB.getTreeMap(MAP_NAME);
        System.out.println("there are " + dictmap.size() + " items in dictmap");
    }

    public void closeDB() {
        dictDB.close();
        dictmap = null;
        dictDB = null;
    }

    /**
     * DB must be opened already
     * @param words
     * @return 
     */
    public float getCount(String... words) {
//        boolean isClosed = false;
//        if (dictDB.isClosed()) {
//            isClosed = true;
//            openDB();
//        }
        Fun.Tuple2<String, String> key = Fun.t2(words[0], words[1]);
        float count;
        try {
            count = dictmap.get(key);
        } catch (Exception e) {
            count = 0;
        }
//        if (isClosed) {
//            closeDB();
//        }
        return count;
    }

    public ArrayList<DictionaryModel> getAll() {
        openDB();
        Set<Map.Entry<Fun.Tuple2<String, String>, Integer>> entrySet = dictmap.entrySet();

        ArrayList<DictionaryModel> data = new ArrayList<>();
        for (Map.Entry<Fun.Tuple2<String, String>, Integer> entry : entrySet) {
            Fun.Tuple2<String, String> key = entry.getKey();
            DictionaryModel dictModel = new DictionaryModel();
            dictModel.word1 = key.a;
            dictModel.word2 = key.b;
            dictModel.count = entry.getValue();
            data.add(dictModel);
        }
        closeDB();
        return data;
    }

    public static void main(String[] args) {
        DictionaryMapDB dictMapDB = new DictionaryMapDB(CorpusUtil.DICTDB_FILENAME, "dsvnDict");
        dictMapDB.openDB();
        System.out.println(dictMapDB.getCount("Nguyễn_1","Tấn_2"));
        dictMapDB.closeDB();
    }
}
