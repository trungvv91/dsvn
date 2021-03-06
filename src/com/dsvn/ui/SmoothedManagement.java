/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dsvn.ui;

import com.dsvn.smoothing.BiKNMapDB;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author trung
 */
public class SmoothedManagement extends MapDBManagement {

    /**
     * Creates new form DictionaryManagement
     */
    public SmoothedManagement() {
        initComponents();
        model = new DefaultTableModel();
        model.addColumn("Word1");
        model.addColumn("Word2");
        model.addColumn("P_KN");
        tblSmoothed.setModel(model);

        sorter = new TableRowSorter<TableModel>(model);
        tblSmoothed.setRowSorter(sorter);

        loadData();
        setCenterScreen();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        txtSearch1 = new javax.swing.JTextField();
        txtSearch2 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        lbStatus = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblSmoothed = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Smoothed Bigram Management");

        jLabel1.setText("Search 1:");

        txtSearch1.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtSearch1CaretUpdate(evt);
            }
        });

        txtSearch2.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtSearch2CaretUpdate(evt);
            }
        });

        jLabel2.setText("Search 2:");

        lbStatus.setText("No. Col:");

        tblSmoothed.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tblSmoothed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtSearch1, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtSearch2, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 656, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtSearch1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtSearch2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbStatus))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtSearch1CaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtSearch1CaretUpdate
        setFilter();
    }//GEN-LAST:event_txtSearch1CaretUpdate

    private void txtSearch2CaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtSearch2CaretUpdate
        setFilter();
    }//GEN-LAST:event_txtSearch2CaretUpdate

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Metal".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SmoothedManagement.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new SmoothedManagement().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbStatus;
    private javax.swing.JTable tblSmoothed;
    private javax.swing.JTextField txtSearch1;
    private javax.swing.JTextField txtSearch2;
    // End of variables declaration//GEN-END:variables

    @Override
    protected final void loadData() {
        model.setRowCount(0);
        BiKNMapDB dictMapDB = new BiKNMapDB();
        ArrayList<Object[]> dictData = dictMapDB.getAll();
        for (Object[] rowData : dictData) {
            model.addRow(rowData);
        }
        lbStatus.setText("Col No.  " + model.getRowCount());
    }

    protected void setFilter() {
        super.setFilter(txtSearch1.getText(), txtSearch2.getText());
        lbStatus.setText("Col No.  " + tblSmoothed.getRowCount() + " / " + model.getRowCount());
    }
}
