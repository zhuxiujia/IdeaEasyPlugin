package com.zxj.plugin.actions;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import com.intellij.openapi.ui.Messages;
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
   private static String contentBlank2="      ";
    private static  String outSideBlank="    ";

    private static String newLine="\n";
    CRUDDialog crudDialog =null;
    @Override
    public void update(AnActionEvent event) {
        event.getPresentation().setEnabledAndVisible(true);
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        // Does nothing
        ProjectUtil.invate();
        VirtualFile eventFile = FileUtil.getFile(event.getDataContext());
        String extension = eventFile.getExtension();

        if (extension != null && extension.equals("xml")) {

             crudDialog = new CRUDDialog(new CRUDDialog.Resulet() {
                @Override
                public void result(CRUDDialogConfig crudDialogConfig) {
                    if (crudDialogConfig.getTableName() == null || crudDialogConfig.getTableName().equals("")) {
                        Messages.showMessageDialog(
                                "表名不可为空！",
                                "error",
                                Messages.getErrorIcon()
                        );
                    } else {
                        crudDialog.getProgressLabel().setText("开始写入..");
                        crudDialog.getProgressBar().setValue(30);
                        writeData(eventFile,crudDialogConfig);
                        crudDialog.getProgressBar().setValue(100);
                        ProjectUtil.invate();
                        crudDialog.onCancel();
                    }
                }
                 @Override
                 public void onLoad() {
                     startJobSetData(crudDialog,eventFile);
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

    private void startJobSetData(CRUDDialog crudDialog,VirtualFile virtualFile) {
        new Thread(){
            @Override
            public void run() {
                crudDialog.getProgressBar().setValue(30);
                try {
                    ReaderXML.read(virtualFile.getInputStream(), new ReaderXML.XMLInterface() {
                        @Override
                        public void update(Document document) {
                            try {
                                crudDialog.getProgressBar().setValue(100);
                                Element rootElement=document.getRootElement();
                                String type = rootElement.element("resultMap").element("id").attribute("jdbcType").getValue();
                                List<Element> attributes = rootElement.element("resultMap").elements("result");
                                StrBuilder strBuilder=new StrBuilder();
                                for (int i=0;i<attributes.size();i++){
                                    Element element=attributes.get(i);
                                    String column = element.attributeValue("column");
                                    String jdbcType = element.attributeValue("jdbcType");
                                    String property = element.attributeValue("property");
                                    strBuilder.append(property);
                                    if(i!=(attributes.size()-1)){
                                        strBuilder.append(",");
                                    }
                                }
                                crudDialog.getSelectByTextField().setText(strBuilder.toString());
                                crudDialog.getUpdateByTextField().setText(strBuilder.toString());
                                crudDialog.getDeleteByTextField().setText(strBuilder.toString());
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }

                        @Override
                        public void error(String info) {
                            crudDialog.getProgressLabel().setText("读取失败!");
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                super.run();
            }
        }.start();
    }


    private void writeData(VirtualFile file, CRUDDialogConfig crudDialogConfig) {
        try {
            ReaderXML.read(file.getInputStream(), new ReaderXML.XMLInterface() {
                @Override
                public void update(Document document) {
                    try {
                        runConfigure(document, crudDialogConfig);
                        ReaderXML.writer(document, file.getParent().getPath() + "/" + file.getName());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void error(String info) {
                    crudDialog.getProgressLabel().setText("读取失败!");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void runConfigure(Document document,  CRUDDialogConfig crudDialogConfig) throws Exception {

        Element rootElement = document.getRootElement();
//        List<Element> elementList = rootElement.elements();
//        for (Element element : elementList) {// <result <select
//            if (checkNeedRemove(element)) {// remove
//                // delete
//                element.getParent().remove(element);
//                System.out.println("delete element:" + element.attribute("id").getValue());
//            }
//        }

        deleteH(rootElement);
        if (crudDialogConfig.isSelect()) addSelect(rootElement, crudDialogConfig);
        if (crudDialogConfig.isDelete()) addDelete(rootElement, crudDialogConfig);
        if (crudDialogConfig.isUpdate()) addUpdate(rootElement, crudDialogConfig);
        if (crudDialogConfig.isCreate()) addInsert(rootElement, crudDialogConfig);

        if (crudDialogConfig.isSelectByCondition())addSelectByColumn(document.getRootElement(),crudDialogConfig);
        if (crudDialogConfig.isCountByCondition())addCountByColumn(document.getRootElement(),crudDialogConfig);

        if(crudDialogConfig.getSelectByTextField()!=null&&!crudDialogConfig.getSelectByTextField().equals("")){addSelectBy(rootElement,crudDialogConfig);}
        if(crudDialogConfig.getUpdateByTextField()!=null&&!crudDialogConfig.getUpdateByTextField().equals("")){addUpdateBy(rootElement,crudDialogConfig);}
        if(crudDialogConfig.getDeleteByTextField()!=null&&!crudDialogConfig.getDeleteByTextField().equals("")){addDeleteBy(rootElement,crudDialogConfig);}


        System.out.println("success");
    }

    private static void deleteH(Element rootElement) {
        List<Element> elements= rootElement.elements();
       if(elements.size()!=0)for (Element element:elements){
            deal(element);
            if(element.elements().size()!=0)deleteH(element);
       }
    }

    private static void deal(Element element) {
        if(element.elements().size()>0) {
            element.setText(element.getText().replaceAll(newLine, ""));
        }
    }

    private static void addDeleteBy(Element rootElement, CRUDDialogConfig crudDialogConfig) {
        String[] strings=crudDialogConfig.getSelectByTextField().split(",");
        List<Element> attributes = rootElement.element("resultMap").elements("result");
        String returnObj=rootElement.element("resultMap").attributeValue("type");
        Element select = null;
        if(crudDialogConfig.isDeleteFlag()){
            select=new BaseElement("delete");
        }else {
            select=new BaseElement("update");
        }
        StrBuilder nameBuilder =new StrBuilder();
        for (int f=0;f<strings.length;f++){
            if(f==0){
                nameBuilder.append("deleteBy").append(StringUtil.upFirstChar(strings[f]));
            }else {
                nameBuilder.append("And").append(StringUtil.upFirstChar(strings[f]));
            }
        }
        select.addAttribute("id", nameBuilder.toString());
        select.addAttribute("resultType", JDBC2JAVA.getJAVAValue("INTEGER"));

        StrBuilder sqlBuilder =new StrBuilder();
        for (int i=0;i<strings.length;i++){
            if(i==0){
                sqlBuilder.append(newLine+contentBlank+"where "+ getColumnByProperty(attributes,strings[i])+" = #{"+strings[i]+"}");}
            else {
                sqlBuilder.append(newLine+contentBlank+"and "+getColumnByProperty(attributes,strings[i])+" = #{"+strings[i]+"}");
            }
        }
        StringBuilder paramBuilder=createParamBuilder(strings,rootElement);
        rootElement.addText(newLine+outSideBlank+"<!--build by plugin: "+returnObj+" "+ nameBuilder.toString()+"("+paramBuilder.toString()+");-->"+newLine);

        StrBuilder setBuilder =new StrBuilder();
        String logicDeleteSql = "";
        String action=null;
        if(!crudDialogConfig.isDeleteFlag()){
            action="update * from ";
            setBuilder.append(newLine+contentBlank+"set "+ crudDialogConfig.getDeleteFlagStr()+" = "+crudDialogConfig.getDeletedStr());
        }else {
            action="delete  from ";
            logicDeleteSql = newLine+contentBlank+"and " + crudDialogConfig.getDeleteFlagStr() + " = " + crudDialogConfig.getUnDeletedStr() + newLine;
        }
        select.setText(newLine+contentBlank+action + crudDialogConfig.getTableName()+contentBlank+ setBuilder.toString()+sqlBuilder.toString()+ logicDeleteSql +newLine+outSideBlank);
        rootElement.add(select);
    }

    private static void addUpdateBy(Element rootElement, CRUDDialogConfig crudDialogConfig) {
        String[] strings=crudDialogConfig.getSelectByTextField().split(",");
        List<Element> attributes = rootElement.element("resultMap").elements("result");
        String returnObj=rootElement.element("resultMap").attributeValue("type");
        Element select = new BaseElement("update");
        StrBuilder nameBuilder =new StrBuilder();
        for (int f=0;f<strings.length;f++){
            if(f==0){
                nameBuilder.append("selectBy").append(StringUtil.upFirstChar(strings[f]));
            }else {
                nameBuilder.append("And").append(StringUtil.upFirstChar(strings[f]));
            }
        }
        select.addAttribute("id", nameBuilder.toString());
        select.addAttribute("resultType", JDBC2JAVA.getJAVAValue("INTEGER"));
        String logicDeleteCode = "";
        if (!crudDialogConfig.isDeleteFlag()) {
            logicDeleteCode = newLine+contentBlank+"and " + crudDialogConfig.getDeleteFlagStr() + " = " + crudDialogConfig.getUnDeletedStr() + newLine;
        }
        StrBuilder sqlBuilder =new StrBuilder();
        for (int i=0;i<strings.length;i++){
            if(i==0){
                sqlBuilder.append(newLine+contentBlank+"where "+ getColumnByProperty(attributes,strings[i])+" = #{"+strings[i]+"}");}
            else {
                sqlBuilder.append(newLine+contentBlank+"and "+getColumnByProperty(attributes,strings[i])+" = #{"+strings[i]+"}");
            }
        }
        StringBuilder paramBuilder=createParamBuilder(strings,rootElement);
        StrBuilder setBuilder=createSetBuilder(attributes);
        rootElement.addText(newLine+outSideBlank+"<!--build by plugin: "+returnObj+" "+ nameBuilder.toString()+"("+paramBuilder.toString()+");-->"+newLine);
        select.setText(newLine+contentBlank+"update * from " + crudDialogConfig.getTableName()+setBuilder.toString()+ sqlBuilder.toString()+ logicDeleteCode+outSideBlank);
        rootElement.add(select);
    }

    private static StrBuilder createSetBuilder(List<Element> attributes) {
        StrBuilder setBuilder =new StrBuilder();
        for (int index=0;index<attributes.size();index++) {
            Element _if = new BaseElement("if");
            Element element=attributes.get(index);
            String column = element.attributeValue("column");
            String jdbcType = element.attributeValue("jdbcType");
            String property = element.attributeValue("property");
            _if.addAttribute("test", property + " != null");
            _if.setText(column + " = #{" + property + ",jdbcType=" + jdbcType + "},");
            if(index==0) setBuilder.append(newLine).append(contentBlank).append("<set>");
            setBuilder.append(newLine).append(contentBlank2).append(_if.asXML().toString());
            if(index==(attributes.size()-1)) setBuilder.append(newLine).append(contentBlank).append("</set>");
        }
        return setBuilder;
    }

    private static void addSelectBy(Element rootElement, CRUDDialogConfig crudDialogConfig) {
        String[] strings=crudDialogConfig.getSelectByTextField().split(",");
        List<Element> attributes = rootElement.element("resultMap").elements("result");
        String returnObj=rootElement.element("resultMap").attributeValue("type");
        Element select = new BaseElement("select");
        StrBuilder nameBuilder =new StrBuilder();
        for (int f=0;f<strings.length;f++){
            if(f==0){
               nameBuilder.append("selectBy").append(StringUtil.upFirstChar(strings[f]));
            }else {
                nameBuilder.append("And").append(StringUtil.upFirstChar(strings[f]));
            }
        }
        select.addAttribute("id", nameBuilder.toString());
        select.addAttribute("resultMap", "BaseResultMap");
        String logicDeleteCode = "";
        if (!crudDialogConfig.isDeleteFlag()) {
            logicDeleteCode = newLine+contentBlank+"and " + crudDialogConfig.getDeleteFlagStr() + " = " + crudDialogConfig.getUnDeletedStr() + newLine;
        }
       StrBuilder sqlBuilder =new StrBuilder();
        for (int i=0;i<strings.length;i++){
            if(i==0){
                sqlBuilder.append(newLine+contentBlank+"where "+ getColumnByProperty(attributes,strings[i])+" = #{"+strings[i]+"}");}
            else {
                sqlBuilder.append(newLine+contentBlank+"and "+getColumnByProperty(attributes,strings[i])+" = #{"+strings[i]+"}");
            }
        }
        StringBuilder paramBuilder=createParamBuilder(strings,rootElement);
        rootElement.addText(newLine+outSideBlank+"<!--build by plugin: "+returnObj+" "+ nameBuilder.toString()+"("+paramBuilder.toString()+");-->"+newLine);

        select.setText(newLine+contentBlank+"select * from " + crudDialogConfig.getTableName()+contentBlank+ sqlBuilder.toString()+ logicDeleteCode+outSideBlank);
        rootElement.add(select);
    }

    private static StringBuilder createParamBuilder(String[] strings,Element rootElement) {
        List<Element> attributes = rootElement.element("resultMap").elements("result");
        StringBuilder paramBuilder =new StringBuilder();
        for (int i=0;i<strings.length;i++){
            if(i==0){
                paramBuilder.append("Param(\"").append(strings[i]).append("\")");
                paramBuilder.append(JDBC2JAVA.getJAVAValueShort(getJdbcTypeByProperty(attributes,strings[i]))+" "+strings[i]+"");}
            else {
                paramBuilder.append(",");
                paramBuilder.append("Param(\"").append(strings[i]).append("\")");
                paramBuilder.append(JDBC2JAVA.getJAVAValueShort(getJdbcTypeByProperty(attributes,strings[i]))+" "+strings[i]+"");
            }
        }
        return paramBuilder;
    }

    private static String getColumnByProperty(List<Element> attributes, String _property) {
        for (Element element:attributes){
            String column = element.attributeValue("column");
            String jdbcType = element.attributeValue("jdbcType");
            String property = element.attributeValue("property");
            if(_property.equals(property)){
                return column;
            }
        }
        return null;
    }
    private static String getJdbcTypeByProperty(List<Element> attributes, String _property) {
        for (Element element:attributes){
            String column = element.attributeValue("column");
            String jdbcType = element.attributeValue("jdbcType");
            String property = element.attributeValue("property");
            if(_property.equals(property)){
                return jdbcType;
            }
        }
        return null;
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
            logicDeleteCode = contentBlank+"and " + crudDialogConfig.getDeleteFlagStr() + " = " + crudDialogConfig.getUnDeletedStr() + newLine;
        }
        select.setText(newLine+contentBlank+"select * from " + crudDialogConfig.getTableName() + newLine+contentBlank+"where id = #{id,jdbcType=INTEGER}"+newLine + logicDeleteCode+outSideBlank);
        rootElement.add(select);
    }

    private static void addDelete(Element rootElement, CRUDDialogConfig crudDialogConfig) {
        String type = rootElement.element("resultMap").element("id").attribute("jdbcType").getValue();
        Element select =null;
        if (crudDialogConfig.isDeleteFlag()) {
             select = new BaseElement("update");
            select.addAttribute("id", "deleteById");
            select.addAttribute("parameterType", JDBC2JAVA.getJAVAValue(type));
            //select.addAttribute("resultMap", "BaseResultMap");
            String logicDeleteCode = newLine + contentBlank + "set " + crudDialogConfig.getDeleteFlagStr() + " = " + crudDialogConfig.getDeletedStr() + newLine;
            select.setText(newLine + contentBlank + "update " + crudDialogConfig.getTableName() + logicDeleteCode + contentBlank + "where id = #{id,jdbcType=INTEGER}"+newLine + outSideBlank);
        }else {
             select = new BaseElement("delete");
            select.addAttribute("id", "deleteById");
            select.addAttribute("parameterType", JDBC2JAVA.getJAVAValue(type));
            select.setText(newLine + contentBlank + "delete from " + crudDialogConfig.getTableName()   + " where id = #{id,jdbcType=INTEGER}"+newLine + outSideBlank);
        }
        rootElement.add(select);
    }

    private static void addUpdate(Element rootElement, CRUDDialogConfig crudDialogConfig) {
        String type = rootElement.element("resultMap").element("id").attribute("jdbcType").getValue();
        List<Element> attributes = rootElement.element("resultMap").elements("result");

        Element update = new BaseElement("update");
        update.addAttribute("id", "updateById");
        update.addAttribute("parameterType", JDBC2JAVA.getJAVAValue(type));
        update.addAttribute("resultType", JDBC2JAVA.getJAVAValue("INTEGER"));
        StrBuilder setBuilder =createSetBuilder(attributes);
        String logicDeleteCode = "";
        if (crudDialogConfig.isDeleteFlag()) {
            logicDeleteCode = " and " + crudDialogConfig.getDeleteFlagStr() + " = " + crudDialogConfig.getUnDeletedStr() ;
        }
        update.setText(newLine+contentBlank+"update " + crudDialogConfig.getTableName() + " " + setBuilder.toString() + newLine+contentBlank+"where id = #{id,jdbcType=INTEGER}"+ logicDeleteCode+ newLine+" "+outSideBlank);
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
        trims1 = trims1.replaceAll("<if", newLine+contentBlank2+"<if");
        trims1 = trims1.replaceAll("<trim", newLine+contentBlank+"<trim");
        trims1 = trims1.replaceAll("</trim>", newLine+contentBlank+"</trim>");

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
        trims2 = trims2.replaceAll("<if", newLine+contentBlank2+"<if");
        trims2 = trims2.replaceAll("<trim", newLine+contentBlank+"<trim");
        trims2 = trims2.replaceAll("</trim>", newLine+contentBlank+"</trim>");
        insert.setText(newLine+contentBlank+"insert into " + crudDialogConfig.getTableName()  + trims1 + newLine + trims2+newLine+outSideBlank);
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
            logicDeleteCode = crudDialogConfig.getDeleteFlagStr() + " = " + crudDialogConfig.getUnDeletedStr() + newLine;
        }else {
            logicDeleteCode=idCloumn+" > -1"+ newLine;
        }
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(newLine).append(contentBlank).append("select * from ").append(crudDialogConfig.getTableName()) .append(" where ") .append(logicDeleteCode);
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
        select.addAttribute("resultType", JDBC2JAVA.getJAVAValue("INTEGER"));
        String logicDeleteCode = null;//逻辑删除
        if (crudDialogConfig.isDeleteFlag()) {
            logicDeleteCode = crudDialogConfig.getDeleteFlagStr() + " = " + crudDialogConfig.getUnDeletedStr() + newLine;
        }else {
            logicDeleteCode=idCloumn+" > -1"+ newLine;
        }
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(newLine).append(contentBlank).append("select count(id) from ").append(crudDialogConfig.getTableName()) .append(" where ") .append(logicDeleteCode);
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
                stringBuilder.append(contentBlank).append(_if.asXML().toString()).append(newLine);
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

    public static void main(String[] arsg) throws FileNotFoundException {
        FileInputStream fileInputStream=new FileInputStream("H:\\JAVA\\remote\\IdeaEasyPlugin\\test\\UserWithdrawalMapperNew.xml");
        ReaderXML.read(fileInputStream, new ReaderXML.XMLInterface() {
            @Override
            public void update(Document document) {
                try {
                    CRUDDialogConfig crudDialogConfig=new CRUDDialogConfig("tables",true,true,true,true,true,"delete_flag","0","1");
                    crudDialogConfig.setSelectByTextField("name,deadline");
                    crudDialogConfig.setDeleteByTextField("name,deadline");
                    crudDialogConfig.setUpdateByTextField("name,deadline");
                   // removeAllButCloumn(document);
                    runConfigure(document,crudDialogConfig);

                    ReaderXML.writer(document, "H:\\JAVA\\remote\\IdeaEasyPlugin\\test\\UserWithdrawalMapperNew.xml");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void error(String info) {

            }
        });
    }

}