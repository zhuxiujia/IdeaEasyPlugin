package com.zxj.plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.zxj.plugin.util.FileUtil;
import com.zxj.plugin.util.ProjectUtil;
import com.zxj.plugin.util.PropertyUtil;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by zhuxiujie
 */
public class ModuleMybitsConfigAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        Properties pro = new Properties();
        pro.setProperty("tableName", "table");
        pro.setProperty("logicDeleteFlag", "delete_flag");
        pro.setProperty("unDeleteFlag", "1");
        pro.setProperty("deleteFlag", "0");

        String baseDir=e.getProject().getBaseDir().getPath();
        String fileName=baseDir+"/mybitsUpdateConfig.properties";
        System.out.println(fileName);
        try {
           File oldF= new File(fileName);
           if(oldF.exists()){
             int return_code=  Messages.showOkCancelDialog("file exits!in path:"+oldF.getPath()+". override this file?","zxj plugin",Messages.getErrorIcon());
             System.out.println(return_code);
             if(return_code==0){
                 store(pro,fileName);
             }
           }else{
               //store
               store(pro,fileName);
           }
        }catch (Exception e2){
            e2.printStackTrace();
        }
    }

    private void store(Properties pro, String fileName) {
        try {
            PropertyUtil.store(pro,fileName);
            ProjectUtil.invate();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}
