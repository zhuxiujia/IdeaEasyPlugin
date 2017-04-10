package com.zxj.plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.vfs.VirtualFile;
import com.zxj.plugin.util.FileUtil;
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
            new File(fileName).delete();
        }catch (Exception e2){
            e2.printStackTrace();
        }
        try {
            PropertyUtil.store(pro,fileName);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}
