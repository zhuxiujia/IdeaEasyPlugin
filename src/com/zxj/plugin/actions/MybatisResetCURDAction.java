package com.zxj.plugin.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import com.intellij.diagnostic.IdeErrorsDialog;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.vfs.VcsVirtualFolder;
import com.intellij.openapi.vfs.VirtualFileSystem;
import com.intellij.openapi.vfs.local.CoreLocalVirtualFile;
import com.intellij.openapi.vfs.newvfs.impl.VirtualDirectoryImpl;
import com.intellij.openapi.vfs.newvfs.impl.VirtualFileImpl;
import com.zxj.plugin.component.MybatisProjectComponent;
import com.zxj.plugin.config.CRUDDialogConfig;
import com.zxj.plugin.util.*;
import com.zxj.plugin.view.CRUDDialog;
import org.apache.commons.lang.text.StrBuilder;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.tree.BaseElement;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * Created by zhuxiujie
 */
public class MybatisResetCURDAction extends AnAction {
   private static   String contentBlank="        ";
   private static String contentBlank2="            ";
    private static  String outSideBlank="    ";

    @Override
    public void update(AnActionEvent event) {
        event.getPresentation().setEnabledAndVisible(true);
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        // Does nothing
        VirtualFile eventFile = FileUtil.getFile(event.getDataContext());
        String extension = eventFile.getExtension();

        if (extension != null && extension.equals("xml")) {
            CRUDDialog crudDialog = new CRUDDialog(new CRUDDialog.Resulet() {
                @Override
                public void result(CRUDDialogConfig crudDialogConfig) {
                    if (crudDialogConfig.getTableName() == null || crudDialogConfig.getTableName().equals("")) {
                        Messages.showMessageDialog(
                                "表名不可为空！",
                                "error",
                                Messages.getErrorIcon()
                        );
                    } else {
                        readData(eventFile, event, crudDialogConfig);
                    }
                }
            });
            crudDialog.pack();
            crudDialog.setVisible(true);
        } else {
            Messages.showMessageDialog(
                    eventFile.getName() + " is not a .xml file!",

                    "error",

                    Messages.getErrorIcon()

            );
        }
    }



    private void readData(VirtualFile file, AnActionEvent event, CRUDDialogConfig crudDialogConfig) {
        ReaderXML.read(file.getPath(), new ReaderXML.XMLInterface() {
            @Override
            public void update(Document document) {
                try {
                    runConfigure(document, crudDialogConfig);
                    ReaderXML.writer(document, file.getParent().getPath() + "/" + file.getName());
                    ProjectUtil.invate();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }


    private static void runConfigure(Document document,  CRUDDialogConfig crudDialogConfig) throws Exception {

        Element rootElement = document.getRootElement();
        List<Element> elementList = rootElement.elements();
        for (Element element : elementList) {// <result <select
            if (checkNeedRemove(element)) {// remove
                // delete
                element.getParent().remove(element);
                System.out.println("delete element:" + element.attribute("id").getValue());
            }
        }
        // add select
        if (crudDialogConfig.isSelect()) addSelect(rootElement, crudDialogConfig);
        if (crudDialogConfig.isDelete()) addDelete(rootElement, crudDialogConfig);
        if (crudDialogConfig.isUpdate()) addUpdate(rootElement, crudDialogConfig);
        if (crudDialogConfig.isCreate()) addInsert(rootElement, crudDialogConfig);

        if (crudDialogConfig.isSelectByCondition())addSelectByColumn(document.getRootElement(),crudDialogConfig);
        if (crudDialogConfig.isCountByCondition())addCountByColumn(document.getRootElement(),crudDialogConfig);
        System.out.println(document.toString());
        System.out.println("success");
    }

    private static   boolean checkNeedRemove(Element element) {
        //if(!attributeStr.equals("BaseResultMap"))return true;
        Attribute attribute = element.attribute("id");
        String attributeStr = attribute.getValue();
        if (attributeStr.equals("selectById")
                || attributeStr.equals("deleteById")
                || attributeStr.equals("updateById")
                || attributeStr.equals("insert")
                || attributeStr.equals("selectByPrimaryKey")
                || attributeStr.equals("deleteByPrimaryKey")
                || attributeStr.equals("insertSelective")
                || attributeStr.equals("updateByPrimaryKeySelective")
                || attributeStr.equals("updateByPrimaryKey")
                ) {
            return true;
        }
        return false;
    }

    private static void addSelect(Element rootElement, CRUDDialogConfig crudDialogConfig) {
        String type = rootElement.element("resultMap").element("id").attribute("jdbcType").getValue();
        Element select = new BaseElement("select");
        select.addAttribute("id", "selectById");
        select.addAttribute("parameterType", JDBC2JAVA.getJAVAValue(type));
        select.addAttribute("resultMap", "BaseResultMap");
        String logicDeleteCode = "";
        if (crudDialogConfig.isDeleteFlag()) {
            logicDeleteCode = contentBlank+"and " + crudDialogConfig.getDeleteFlagStr() + " = " + crudDialogConfig.getUnDeletedStr() + "\n";
        }
        select.setText("\n"+contentBlank+"select * from " + crudDialogConfig.getTableName() + "\n"+contentBlank+"where id = #{id,jdbcType=INTEGER}\n" + logicDeleteCode+outSideBlank);
        rootElement.add(select);
    }

    private static void addDelete(Element rootElement, CRUDDialogConfig crudDialogConfig) {
        String type = rootElement.element("resultMap").element("id").attribute("jdbcType").getValue();
        Element select =null;
        if (!crudDialogConfig.isDeleteFlag()) {
             select = new BaseElement("update");
            select.addAttribute("id", "deleteById");
            select.addAttribute("parameterType", JDBC2JAVA.getJAVAValue(type));
            //select.addAttribute("resultMap", "BaseResultMap");
            String logicDeleteCode = "\n" + contentBlank + "set " + crudDialogConfig.getDeleteFlagStr() + " = " + crudDialogConfig.getDeletedStr() + "\n";
            select.setText("\n" + contentBlank + "update " + crudDialogConfig.getTableName() + logicDeleteCode + contentBlank + "where id = #{id,jdbcType=INTEGER}\n" + outSideBlank);
        }else {
             select = new BaseElement("delete");
            select.addAttribute("id", "deleteById");
            select.addAttribute("parameterType", JDBC2JAVA.getJAVAValue(type));
            select.setText("\n" + contentBlank + "delete from " + crudDialogConfig.getTableName()   + " where id = #{id,jdbcType=INTEGER}\n" + outSideBlank);
        }
        rootElement.add(select);
    }

    private static void addUpdate(Element rootElement, CRUDDialogConfig crudDialogConfig) {
        String type = rootElement.element("resultMap").element("id").attribute("jdbcType").getValue();
        List<Element> attributes = rootElement.element("resultMap").elements("result");

        Element update = new BaseElement("update");
        update.addAttribute("id", "updateById");
        update.addAttribute("parameterType", JDBC2JAVA.getJAVAValue(type));
        //update.addAttribute("resultMap", "BaseResultMap");
        StrBuilder strBuilder=new StrBuilder().append("\n").append(contentBlank).append("<set>");
        for (Element element : attributes) {
            Element _if = new BaseElement("if");
            String column = element.attributeValue("column");
            String jdbcType = element.attributeValue("jdbcType");
            String property = element.attributeValue("property");
            _if.addAttribute("test", property + " != null");
            _if.setText(column + " = #{" + property + ",jdbcType=" + jdbcType + "},");
            strBuilder.append("\n").append(contentBlank2).append(_if.asXML().toString());
        }
        strBuilder.append("\n").append(contentBlank).append("</set>").append("\n");

        String logicDeleteCode = "";
        if (crudDialogConfig.isDeleteFlag()) {
            logicDeleteCode = " and " + crudDialogConfig.getDeleteFlagStr() + " = " + crudDialogConfig.getUnDeletedStr() ;
        }
        update.setText("\n"+contentBlank+"update " + crudDialogConfig.getTableName() + " " + strBuilder.toString() + "\t\twhere id = #{id,jdbcType=INTEGER}"+ logicDeleteCode+ "\n "+outSideBlank);
        rootElement.add(update);
    }

    private  static void addInsert(Element rootElement, CRUDDialogConfig crudDialogConfig) {
        String type = rootElement.element("resultMap").attribute("type").getValue();
        List<Element> attributes = rootElement.element("resultMap").elements("result");

        Element insert = new BaseElement("insert");
        insert.addAttribute("id", "insert");
        insert.addAttribute("useGeneratedKeys", "true");
        insert.addAttribute("keyProperty", "id");
        insert.addAttribute("keyColumn", "id");
        insert.addAttribute("parameterType", type);

        // trim1

        Element trim1 = new BaseElement("trim");
        trim1.addAttribute("prefix", "(");
        trim1.addAttribute("suffix", ")");
        trim1.addAttribute("suffixOverrides", ",");
        for (Element element : attributes) {
            Element _if = new BaseElement("if");
            String property = element.attributeValue("property");
            String column = element.attributeValue("column");
            _if.addAttribute("test", property + " != null");
            _if.setText(column + ",");
            trim1.add(_if);
        }
        String trims1 = trim1.asXML().toString();
        trims1 = trims1.replaceAll("<if", "\n"+contentBlank2+"<if");
        trims1 = trims1.replaceAll("<trim", "\n"+contentBlank+"<trim");
        trims1 = trims1.replaceAll("</trim>", "\n"+contentBlank+"</trim>");

        // trim 2
        Element trim2 = new BaseElement("trim");
        trim2.addAttribute("prefix", "values (");
        trim2.addAttribute("suffix", ")");
        trim2.addAttribute("suffixOverrides", ",");
        for (Element element : attributes) {
            Element _if = new BaseElement("if");
            String column = element.attributeValue("column");
            String jdbcType = element.attributeValue("jdbcType");
            String property = element.attributeValue("property");
            _if.addAttribute("test", property + " != null");
            _if.setText("#{" + property + ",jdbcType=" + jdbcType + "},");
            trim2.add(_if);
        }
        String trims2 = trim2.asXML().toString();
        trims2 = trims2.replaceAll("<if", "\n"+contentBlank2+"<if");
        trims2 = trims2.replaceAll("<trim", "\n"+contentBlank+"<trim");
        trims2 = trims2.replaceAll("</trim>", "\n"+contentBlank+"</trim>");

        insert.setText("\n"+contentBlank+"insert into " + crudDialogConfig.getTableName()  + trims1 + "\n" + trims2+"\n"+outSideBlank);
        rootElement.add(insert);

    }

    private static   void addSelectByColumn(Element rootElement, CRUDDialogConfig crudDialogConfig){
        List<Element> attributes = rootElement.element("resultMap").elements("result");
        String idCloumn=rootElement.element("resultMap").element("id").getName();
        Element select = new BaseElement("select");
        select.addAttribute("id", "selectByCondition");
        //select.addAttribute("parameterType", JDBC2JAVA.getJAVAValue(type));
        select.addAttribute("resultMap", "BaseResultMap");
        String logicDeleteCode = "";//逻辑删除
        if (crudDialogConfig.isDeleteFlag()) {
            logicDeleteCode = crudDialogConfig.getDeleteFlagStr() + " = " + crudDialogConfig.getUnDeletedStr() + "\n";
        }else {
            logicDeleteCode=idCloumn+" > -1"+ "\n";
        }
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("\n").append(contentBlank).append("select * from ").append(crudDialogConfig.getTableName()) .append(" where ") .append(logicDeleteCode);
        addAllAttributes(attributes,stringBuilder,crudDialogConfig.getDeleteFlagStr());
        select.setText(stringBuilder.toString()+outSideBlank);
        rootElement.add(select);
    }
    private static   void addCountByColumn(Element rootElement, CRUDDialogConfig crudDialogConfig){

        List<Element> attributes = rootElement.element("resultMap").elements("result");
        String idCloumn=rootElement.element("resultMap").element("id").getName();
        Element select = new BaseElement("select");
        select.addAttribute("id", "countByCondition");
        //select.addAttribute("parameterType", JDBC2JAVA.getJAVAValue(type));
        select.addAttribute("resultMap", "BaseResultMap");
        String logicDeleteCode = null;//逻辑删除
        if (crudDialogConfig.isDeleteFlag()) {
            logicDeleteCode = crudDialogConfig.getDeleteFlagStr() + " = " + crudDialogConfig.getUnDeletedStr() + "\n";
        }else {
            logicDeleteCode=idCloumn+" > -1"+ "\n";
        }
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("\n").append(contentBlank).append("select count(id) from ").append(crudDialogConfig.getTableName()) .append(" where ") .append(logicDeleteCode);
        addAllAttributes(attributes,stringBuilder,crudDialogConfig.getDeleteFlagStr());
        select.setText(stringBuilder.toString()+outSideBlank);
        rootElement.add(select);
        System.out.println(rootElement.toString());
    }

    private static void addAllAttributes(List<Element> attributes, StringBuilder stringBuilder,String deleteFlagStr) {
        for (Element element : attributes) {
            Element _if = new BaseElement("if");
            String column = element.attributeValue("column");
            String jdbcType = element.attributeValue("jdbcType");
            String property = element.attributeValue("property");
            _if.addAttribute("test", property + " != null");
            _if.setText("and "+column + " = #{" + property + ",jdbcType=" + jdbcType + "},");
            if(deleteFlagStr!=null&&deleteFlagStr.equals(column)){
            }else {
                stringBuilder.append(contentBlank).append(_if.asXML().toString()).append("\n");
            }
        }
    }

    private static boolean removeAllButCloumn(Document document) {
        Element rootElement = document.getRootElement();
        List<Element> elementList = rootElement.elements();
        for (Element element : elementList) {// <result <select
           if(!element.getName().equals("resultMap")) {
               element.getParent().remove(element);
           }
        }
        return true;
    }

    public static void main(String[] arsg){
        ReaderXML.read("H:\\JAVA\\remote\\IdeaEasyPlugin\\test\\ProductMapper_new.xml", new ReaderXML.XMLInterface() {
            @Override
            public void update(Document document) {
                try {
                    //CRUDDialogConfig crudDialogConfig=new CRUDDialogConfig("table",true,true,true,true,true,"delete_flag","0","1");
                   // removeAllButCloumn(document);
                    //runConfigure(document,crudDialogConfig);

                   // ReaderXML.writer(document, "H:\\JAVA\\remote\\IdeaEasyPlugin\\test\\ProductMapper_new.xml");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

}