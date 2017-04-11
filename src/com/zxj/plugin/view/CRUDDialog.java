package com.zxj.plugin.view;

import com.zxj.plugin.config.CRUDDialogConfig;

import javax.swing.*;
import java.awt.event.*;

public class CRUDDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JCheckBox select_mothedCheckBox;
    private JCheckBox delete_mothedCheckBox;
    private JCheckBox update_mothedCheckBox;
    private JCheckBox create_return_idCheckBox;
    private JTextField textField1;


    public interface Resulet{
        void result(CRUDDialogConfig crudDialogConfig);
    }


    Resulet resulet;

    public CRUDDialog(Resulet resulet) {
        this.resulet=resulet;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK(e);
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK(ActionEvent event) {
        // add your code here
        dispose();
        CRUDDialogConfig crudDialogConfig=new CRUDDialogConfig(textField1.getText(),select_mothedCheckBox.isSelected(),delete_mothedCheckBox.isSelected(),update_mothedCheckBox.isSelected(),create_return_idCheckBox.isSelected());
        resulet.result(crudDialogConfig);
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        CRUDDialog dialog = new CRUDDialog(new Resulet() {
            @Override
            public void result(CRUDDialogConfig crudDialogConfig) {

            }
        });
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
