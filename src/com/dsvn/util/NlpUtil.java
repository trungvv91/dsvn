/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dsvn.util;

/**
 *
 * @author TRUNG
 */
public class NlpUtil {
    
    /**
     * Array lists all punctuations.
     */
    public static String[] Punctuations = {".", ";", "?", "!", ",", "'", "\""};

    public static boolean IsPunctuation(String s) {
        for (String p : Punctuations) {
            if (p.equals(s)) {
                return true;
            }
        }
        return false;
    }
}
