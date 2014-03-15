/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dsvn.dict;

import com.dsvn.util.IOUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import org.mapdb.BTreeKeySerializer;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Fun;

/**
 *
 * @author trung
 */
public class DictionaryMapDB {

    /**
     * final String represents dictmap's name
     */
    public static final String DICTDB_FILENAME = "data/dict.mapdb";
    public static final File DBFile = new File(DICTDB_FILENAME);

    private BTreeMap<Fun.Tuple2<String, String>, Integer> dictmap;
    protected String mapname;
    protected DB dictDB;

    public DictionaryMapDB(String mapname) {
        this.mapname = mapname;
    }

    /**
     * Add words from a txt file to a dictionary map in database. Assumed each line in
     * Dictionary file is a word. If the map's name (dictname) has already
     * existed, words are added to this map, else a new map is created
     * automatically.
     *
     * @param dictPath
     * @param dictname
     * @param isLabeled indicate whether words is labeled
     */
    public static void addDictionary(String dictPath, String dictname, boolean isLabeled) {
        DB db = DBMaker.newFileDB(DBFile).transactionDisable().make();
        BTreeMap<Fun.Tuple2<String, String>, Integer> dictmap;
        if (db.exists(dictname)) {
//            System.out.println("exist");
//            db.delete(dictname);
            dictmap = db.getTreeMap(dictname);
        } else {
            dictmap = db.createTreeMap(dictname)
                    .keySerializer(BTreeKeySerializer.TUPLE2).make();
        }

        ArrayList<String> lines = IOUtil.ReadFile(dictPath);
        int nlines = lines.size();      // number of lines
        System.out.println("there are " + nlines + " words in dictionary");
        System.out.println("there are " + dictmap.size() + " items in current dictionary");
        Integer count;
        for (String line : lines) {
            String[] words = line.trim().split("\\s+");
            if (words.length > 1) {
                if (!isLabeled) {
                    words[0] += "_1";
                    words[words.length - 1] += "_3";
                    for (int i = 1; i < words.length - 1; i++) {
                        words[i] += "_2";
                    }
                }
                Fun.Tuple2<String, String> key = Fun.t2(words[0], words[1]);
                count = dictmap.get(key);
                if (count == null) {
                    dictmap.put(key, 1);
                } else {
                    dictmap.put(key, count + 1);
                }
                if (words.length > 2) {
                    key = Fun.t2(words[words.length - 2], words[words.length - 1]);
                    count = dictmap.get(key);
                    if (count == null) {
                        dictmap.put(key, 1);
                    } else {
                        dictmap.put(key, count + 1);
                    }
                }
            }

//            for (int i = 0; i < words.length - 1; i++) {
//                String word1 = words[i];
//                String word2 = words[i + 1];
//                Fun.Tuple2<String, String> key = Fun.t2(word1, word2);
//                count = dictmap.get(key);
//                if (count == null) {
//                    dictmap.put(key, 1);
//                } else {
//                    dictmap.put(key, count + 1);
//                }
//            }
//            }
        }
        System.out.println("there are " + dictmap.size() + " items in updated dictionary");
        db.close();
    }

    public void openDB() {
        if (dictDB == null || dictmap == null) {
            dictDB = DBMaker.newFileDB(DBFile).make();
            dictmap = dictDB.getTreeMap(mapname);
            System.out.println("there are " + dictmap.size() + " items in dictmap");
        }
    }

    public void closeDB() {
        dictDB.close();
        dictmap = null;
        dictDB = null;
    }

    /**
     * DB must be opened already
     *
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
//        DictionaryMapDB.addDictionary("data/DSVNDict.txt", "dsvnDict", false);
        DictionaryMapDB dictMapDB = new DictionaryMapDB("dsvnDict");
        dictMapDB.openDB();
        System.out.println(dictMapDB.getCount("Nguyễn_1", "Tấn_2"));
        dictMapDB.closeDB();
    }
}
