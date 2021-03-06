/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application.presentation;

import java.io.IOException;
import server.events.ClientServerConnectionMessageEvent;
import server.helpers.ConnectionLogger;
import server.net.Server;
import server.listeners.ClientServerConnectionMessageListener;

/**
 *
 * @author Paul
 */
public class ServerPresentation extends javax.swing.JFrame implements ClientServerConnectionMessageListener {

    private Server server;
    private boolean state = false;

    /**
     * Creates new form ServerPresentation
     */
    public ServerPresentation() {
        initComponents();
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
        btnInitServer = new javax.swing.JToggleButton();
        txtPort = new java.awt.TextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtMensajes = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("SERVIDOR");

        btnInitServer.setText("ON");
        btnInitServer.setName(""); // NOI18N
        btnInitServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInitServerActionPerformed(evt);
            }
        });

        txtPort.setText("9090");

        txtMensajes.setColumns(20);
        txtMensajes.setRows(5);
        jScrollPane2.setViewportView(txtMensajes);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnInitServer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(txtPort, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnInitServer, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPort, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnInitServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInitServerActionPerformed
        // TODO add your handling code here:
        if (!state) {
            btnInitServer.setText("OFF");
            connectServer();
        } else {
            btnInitServer.setText("ON");
            disconnectServer();
        }

        state = !state;

    }//GEN-LAST:event_btnInitServerActionPerformed

    private void connectServer() {
        try {
            int port = Integer.parseInt(txtPort.getText());
            server = new Server("127.0.0.1", port);
            server.connect();
            server.addSimpleMessageListener(this);
            appendMessage("Servidor iniciado");
        } catch (NumberFormatException e) {
            appendMessage("El puerto ingresado no es un numero");
        } catch (IOException ex) {
            appendMessage("El servidor no puede iniciar");
        }

    }

    private void disconnectServer() {
        try {
            server.disconnect();
            appendMessage("Servidor finalizado");

        } catch (Exception e) {
            e.printStackTrace();
            appendMessage("El servidor no puede finalizar");
        }
    }

    private void appendMessage(String message) {
        txtMensajes.setText(txtMensajes.getText() + "\n" + message);

    }

    @Override
    public void onNewMessage(ClientServerConnectionMessageEvent event) {
        String message = (String) event.getSource();
        appendMessage(message);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ServerPresentation.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ServerPresentation.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ServerPresentation.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ServerPresentation.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ServerPresentation().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnInitServer;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea txtMensajes;
    private java.awt.TextField txtPort;
    // End of variables declaration//GEN-END:variables

}
