/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dsvn.ngrams;

/**
 *
 * @author TRUNG
 */
public final class WordLabel {
    public static final String START = "<START>";
    public static final String END = "<END>";
    public static final String UNKNOWN = "XXX_0";
    public static final String N = "NWORDS";

    public static final int NUMBER_OF_LABELS = 4;

    /**
     * NONE = -1
     */
    public static final int NONE = -1;      //NONE = -1;

    /**
     * OUTSIDE = 0
     */
    public static final int O = 0;      //OUTSIDE = 0;

    /**
     * START = 1
     */
    public static final int S = 1;      //START = 1;

    /**
     * INSIDE = 2
     */
    public static final int I = 2;      //INSIDE = 2;

    /**
     * END = 3
     */
    public static final int E = 3;      //END = 3;
}
