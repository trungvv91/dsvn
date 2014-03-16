/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dsvn.dict;

import com.dsvn.mapdb.MapDBModel;
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
public class DictionaryMapDB extends MapDBModel<Fun.Tuple2<String, String>, Integer> {

    /**
     * final String represents dictmap's database filename
     */
    public static final String DICTDB_FILENAME = "data/dict.mapdb";
    public static final File DictDBFile = new File(DICTDB_FILENAME);

    public DictionaryMapDB(String mapname) {
        this.mapname = mapname;
    }
    
    @Override
    protected void deleteOldFile() {
        if (DictDBFile != null && DictDBFile.exists()) {
            DictDBFile.delete();
        }
    }
    
    @Override
    public void openDB() {
        if (db == null || map == null) {
            db = DBMaker.newFileDB(DictDBFile).make();
            map = db.getTreeMap(mapname);
        }
        System.out.println("there are " + map.size() + " items in map");
    }

    @Override
    public boolean checkProbability() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Integer getValue(String... words) {
        Fun.Tuple2<String, String> key = Fun.t2(words[0], words[1]);
        Integer value;
        try {
            value = map.get(key);
        } catch (Exception e) {
            value = null;
        }
        return value;
    }

    @Override
    public void printMap() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ArrayList<Object[]> getAll() {
        ArrayList<Object[]> data = new ArrayList<>();
        openDB();
        Set<Map.Entry<Fun.Tuple2<String, String>, Integer>> entrySet = map.entrySet();
        for (Map.Entry<Fun.Tuple2<String, String>, Integer> entry : entrySet) {
            Fun.Tuple2<String, String> key = entry.getKey();
            Object[] dictModel = new Object[]{key.a, key.b, entry.getValue()};
            data.add(dictModel);
        }
        closeDB();
        return data;
    }

    /**
     * Add words from a txt file to a dictionary map in database. Assumed each
     * line in Dictionary file is a word. If the map's name (dictname) has
     * already existed, words are added to this map, else a new map is created
     * automatically.
     *
     * @param dictPath
     * @param dictname
     * @param isLabeled indicate whether words is labeled
     */
    public static void AddDictionary(String dictPath, String dictname, boolean isLabeled) {
        DB db = DBMaker.newFileDB(DictDBFile).transactionDisable().make();
        BTreeMap<Fun.Tuple2<String, String>, Integer> dictmap;
        if (db.exists(dictname)) {
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

    public static void main(String[] args) {
//        DictionaryMapDB.AddDictionary("data/DSVNDict.txt", "dsvnDict", false);
        DictionaryMapDB dictMapDB = new DictionaryMapDB("dsvnDict");
        dictMapDB.openDB();
        System.out.println(dictMapDB.getValue("Nguyễn_1", "Tấn_2"));
        dictMapDB.closeDB();
    }

}
