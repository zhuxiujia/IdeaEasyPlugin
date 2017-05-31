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
    private JCheckBox deleteFlagCheckBox;
    private JTextField delete_flagTextField;
    private JTextField unDeleteTextField;
    private JTextField deletedTextField;
    private JCheckBox selectByConditionCheckBox;
    private JCheckBox countByConditionCheckBox;
    private JTextField selectByTextField;
    private JTextField updateByTextField;
    private JTextField deleteByTextField;
    private JProgressBar progressBar;
    private JLabel progressLabel;
    private JButton loadButton;
    private JTextField selectByCloumnTextField;
    private JTextField countByCloumnTextField;
    private JCheckBox timeSelectCheckBox;
    private JTextField timeTextField;
    private JTextField baseResultMapTextField;
    private JCheckBox selectByCheckBox;
    private JCheckBox updateByCheckBox;
    private JCheckBox deleteByCheckBox;


    private JTextField indexTextField;

    private JTextField sizeTextField;
    private JCheckBox limitIndexParamCheckBox;
    private JCheckBox orderByCheckBox;
    private JTextField orderByTextField;
    private JCheckBox descCheckBox;


    private void createUIComponents() {
        // TODO: place custom component creation code here
    }


    public interface Resulet{
        void result(CRUDDialogConfig crudDialogConfig);
        void onLoad();
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
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resulet.onLoad();
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
        //dispose();
        CRUDDialogConfig crudDialogConfig=new CRUDDialogConfig(textField1.getText(),select_mothedCheckBox.isSelected(),delete_mothedCheckBox.isSelected(),update_mothedCheckBox.isSelected(),create_return_idCheckBox.isSelected(),deleteFlagCheckBox.isSelected(),delete_flagTextField.getText(),deletedTextField.getText(),unDeleteTextField.getText());
        crudDialogConfig.setCountByCondition(countByConditionCheckBox.isSelected());
        crudDialogConfig.setSelectByCondition(selectByConditionCheckBox.isSelected());

        crudDialogConfig.setSelectByTextField(selectByTextField.getText().toString());
        crudDialogConfig.setUpdateByTextField(updateByTextField.getText().toString());
        crudDialogConfig.setDeleteByTextField(deleteByTextField.getText().toString());

        crudDialogConfig.setSelectByCloumnTextField(selectByCloumnTextField.getText().toString());
        crudDialogConfig.setCountByCloumnTextField(countByCloumnTextField.getText().toString());


        crudDialogConfig.setTimeSelectText(timeTextField.getText().toString());

        crudDialogConfig.setTimeSelect(timeSelectCheckBox.isSelected());

        crudDialogConfig.setBaseResultMap(baseResultMapTextField.getText());


        crudDialogConfig.setSelectBy(selectByCheckBox.isSelected());
        crudDialogConfig.setUpdateBy(selectByCheckBox.isSelected());
        crudDialogConfig.setDeleteBy(selectByCheckBox.isSelected());

        crudDialogConfig.setLimitIndexParam(limitIndexParamCheckBox.isSelected());
        crudDialogConfig.setLimitIndexParam(indexTextField.getText().toString(),sizeTextField.getText().toString());

        crudDialogConfig.setDesc(descCheckBox.isSelected());
        crudDialogConfig.setOrderByValue(orderByTextField.getText().toString());
        crudDialogConfig.setOrderBy(orderByCheckBox.isSelected());

        resulet.result(crudDialogConfig);
    }

    public void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        CRUDDialog dialog = new CRUDDialog(new Resulet() {
            @Override
            public void result(CRUDDialogConfig crudDialogConfig) {

                System.out.println();
            }

            @Override
            public void onLoad() {

            }
        });
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    public JTextField getSelectByTextField() {
        return selectByTextField;
    }

    public JTextField getUpdateByTextField() {
        return updateByTextField;
    }

    public JTextField getDeleteByTextField() {
        return deleteByTextField;
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    public JLabel getProgressLabel() {
        return progressLabel;
    }

    public JTextField getSelectByCloumnTextField() {
        return selectByCloumnTextField;
    }

    public JTextField getCountByCloumnTextField() {
        return countByCloumnTextField;
    }

    public JTextField getBaseResultMapTextField() {
        return baseResultMapTextField;
    }

    public JCheckBox getSelectByCheckBox() {
        return selectByCheckBox;
    }

    public JCheckBox getUpdateByCheckBox() {
        return updateByCheckBox;
    }

    public JCheckBox getDeleteByCheckBox() {
        return deleteByCheckBox;
    }
}
