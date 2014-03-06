/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dsvn.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mapdb.Fun;

/**
 *
 * @author TRUNG
 */
public class IOUtil {

    public static void WriteToFile(String filename, String text) {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(filename), StandardCharsets.UTF_8))) {
            bw.write(text);
        } catch (IOException ex) {
            Logger.getLogger(IOUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static ArrayList<Fun.Tuple2<String, Double>> ReadFile(String filename) {
        ArrayList<Fun.Tuple2<String, Double>> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(filename), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!"".equals(line)) {
                    String[] items = line.split("\\s+");        // 3 items: w_1 w_2 value                
                    lines.add(Fun.t2(items[0] + " " + items[1], Double.parseDouble(items[2])));
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(IOUtil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(IOUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        return lines;
    }

    public static void main(String[] args) {
        String[] items = "abc   cd e    fds".split("\\s+");
        for (String item : items) {
            //System.out.println(item);
        }
        System.out.println("abc   cd e    fds".replaceAll("\\s+", " "));
    }
}
