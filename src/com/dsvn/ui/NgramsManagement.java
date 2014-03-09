/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dsvn.ui;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author TRUNG
 */
public abstract class NgramsManagement extends javax.swing.JFrame {

    protected DefaultTableModel model;
    protected TableRowSorter<TableModel> sorter;
//    protected ArrayList<? extends UnigramModel> ngramsData;

    protected abstract void loadData();

    protected abstract void setFilter();

    protected void setCenterScreen() {
        // Get the size of the screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        // Determine the new location of the window
        int w = this.getSize().width;
        int h = this.getSize().height;
        int x = (dim.width - w) / 2;
        int y = (dim.height - h) / 2;

        // Move the window
        this.setLocation(x, y);
    }

    protected RowFilter<TableModel, Object> getFilter(String searchString, int column) {
        RowFilter<TableModel, Object> rf;
        //If current expression doesn't parse, don't update.
        try {
            rf = RowFilter.regexFilter(searchString, column);
        } catch (java.util.regex.PatternSyntaxException e) {
            return null;
        }

        return rf;
    }

}
