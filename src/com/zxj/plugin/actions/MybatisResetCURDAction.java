package com.zxj.plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.zxj.plugin.config.CRUDDialogConfig;
import com.zxj.plugin.util.*;
import com.zxj.plugin.view.CRUDDialog;
import org.apache.commons.lang.text.StrBuilder;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.tree.BaseElement;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuxiujie
 */
public class MybatisResetCURDAction extends AnAction {
    private static String contentBlank = "        ";
    private static String contentBlank2 = "      ";
    private static String outSideBlank = "    ";

    private static String newLine = "\n";
    CRUDDialog crudDialog = null;

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
                        writeData(eventFile, crudDialogConfig);
                        crudDialog.getProgressBar().setValue(100);
                        ProjectUtil.invate();
                        crudDialog.onCancel();
                    }
                }

                @Override
                public void onLoad() {
                    startJobSetData(crudDialog, eventFile);
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

    private void startJobSetData(CRUDDialog crudDialog, VirtualFile virtualFile) {
        new Thread() {
            @Override
            public void run() {
                crudDialog.getProgressBar().setValue(30);
                try {
                    ReaderXML.read(virtualFile.getInputStream(), new ReaderXML.XMLInterface() {
                        @Override
                        public void update(Document document) {
                            try {
                                crudDialog.getProgressBar().setValue(100);
                                Element rootElement = document.getRootElement();
                                List<Element> attributes = findBaseResultMap(rootElement, crudDialog.getBaseResultMapTextField().getText().toString()).elements("result");
                                StrBuilder strBuilder = new StrBuilder();
                                for (int i = 0; i < attributes.size(); i++) {
                                    Element element = attributes.get(i);
                                    String column = element.attributeValue("column");
                                    String jdbcType = element.attributeValue("jdbcType");
                                    String property = element.attributeValue("property");
                                    strBuilder.append(property);
                                    if (i != (attributes.size() - 1)) {
                                        strBuilder.append(",");
                                    }
                                }
                                crudDialog.getSelectByTextField().setText(strBuilder.toString());
                                crudDialog.getUpdateByTextField().setText(strBuilder.toString());
                                crudDialog.getDeleteByTextField().setText(strBuilder.toString());
                                crudDialog.getSelectByCloumnTextField().setText(strBuilder.toString());
                                crudDialog.getCountByCloumnTextField().setText(strBuilder.toString());
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


    private static void runConfigure(Document document, CRUDDialogConfig crudDialogConfig) throws Exception {

        Element rootElement = document.getRootElement();
//        List<Element> elementList = rootElement.elements();
//        for (Element element : elementList) {// <result <select
//            if (checkNeedRemove(element)) {// remove
//                // delete
//                element.getParent().remove(element);
//                System.out.println("delete element:" + element.attribute("id").getValue());
//            }
//        }

        if (crudDialogConfig.isSelect()) addSelect(rootElement, crudDialogConfig);
        if (crudDialogConfig.isDelete()) addDelete(rootElement, crudDialogConfig);
        if (crudDialogConfig.isUpdate()) addUpdate(rootElement, crudDialogConfig);
        if (crudDialogConfig.isCreate()) addInsert(rootElement, crudDialogConfig);

        if (crudDialogConfig.isSelectByCondition()) addSelectByColumn(document.getRootElement(), crudDialogConfig);
        if (crudDialogConfig.isCountByCondition()) addCountByColumn(document.getRootElement(), crudDialogConfig);

        if (crudDialogConfig.isSelectBy()) {
            addSelectBy(rootElement, crudDialogConfig);
        }
        if (crudDialogConfig.isUpdateBy()) {
            addUpdateBy(rootElement, crudDialogConfig);
        }
        if (crudDialogConfig.isDeleteBy()) {
            addDeleteBy(rootElement, crudDialogConfig);
        }


        System.out.println("success");
    }


    private static void addDeleteBy(Element rootElement, CRUDDialogConfig crudDialogConfig) {
        String[] strings = crudDialogConfig.getSelectByTextField().split(",");
        List<Element> attributes = findBaseResultMap(rootElement, crudDialogConfig.getBaseResultMap()).elements("result");
        String returnObj = "INTEGER";
        Element select = null;
        if (crudDialogConfig.isDeleteFlag()) {
            select = new BaseElement("delete");
        } else {
            select = new BaseElement("update");
        }
        StrBuilder nameBuilder = new StrBuilder();
        for (int f = 0; f < strings.length; f++) {
            if (f == 0) {
                nameBuilder.append("deleteBy").append(StringUtil.upFirstChar(strings[f]));
            } else {
                nameBuilder.append("And").append(StringUtil.upFirstChar(strings[f]));
            }
        }
        select.addAttribute("id", nameBuilder.toString());
        select.addAttribute("resultType", JDBC2JAVA.getJAVAValue("INTEGER"));

        StrBuilder sqlBuilder = new StrBuilder();
        for (int i = 0; i < strings.length; i++) {
            if (i == 0) {
                sqlBuilder.append(newLine + contentBlank + "where " + getColumnByProperty(attributes, strings[i]) + " = #{" + strings[i] + "}");
            } else {
                sqlBuilder.append(newLine + contentBlank + "and " + getColumnByProperty(attributes, strings[i]) + " = #{" + strings[i] + "}");
            }
        }
        StringBuilder paramBuilder = createParamBuilder(strings, rootElement, crudDialogConfig);
        rootElement.addComment("build by plugin: " + returnObj + " " + nameBuilder.toString() + "(" + paramBuilder.toString() + ");");

        StrBuilder setBuilder = new StrBuilder();
        String logicDeleteSql = "";
        String action = null;
        if (!crudDialogConfig.isDeleteFlag()) {
            action = "update * from ";
            setBuilder.append(newLine + contentBlank + "set " + crudDialogConfig.getDeleteFlagStr() + " = " + crudDialogConfig.getDeletedStr());
        } else {
            action = "delete  from ";
            logicDeleteSql = newLine + contentBlank + "and " + crudDialogConfig.getDeleteFlagStr() + " = " + crudDialogConfig.getUnDeletedStr() + newLine;
        }
        select.setText(newLine + contentBlank + action + crudDialogConfig.getTableName() + contentBlank + setBuilder.toString() + sqlBuilder.toString() + logicDeleteSql + newLine + outSideBlank);
        rootElement.add(select);
    }

    private static void addUpdateBy(Element rootElement, CRUDDialogConfig crudDialogConfig) {
        String[] strings = crudDialogConfig.getSelectByTextField().split(",");
        List<Element> attributes = findBaseResultMap(rootElement, crudDialogConfig.getBaseResultMap()).elements("result");
        Element select = new BaseElement("update");
        StrBuilder nameBuilder = new StrBuilder();
        for (int f = 0; f < strings.length; f++) {
            if (f == 0) {
                nameBuilder.append("updateBy").append(StringUtil.upFirstChar(strings[f]));
            } else {
                nameBuilder.append("And").append(StringUtil.upFirstChar(strings[f]));
            }
        }
        select.addAttribute("id", nameBuilder.toString());
        select.addAttribute("resultType", JDBC2JAVA.getJAVAValue("INTEGER"));
        String logicDeleteCode = "";
        if (!crudDialogConfig.isDeleteFlag()) {
            logicDeleteCode = newLine + contentBlank + "and " + crudDialogConfig.getDeleteFlagStr() + " = " + crudDialogConfig.getUnDeletedStr() + newLine;
        }
        StrBuilder sqlBuilder = new StrBuilder();
        for (int i = 0; i < strings.length; i++) {
            if (i == 0) {
                sqlBuilder.append(newLine + contentBlank + "where " + getColumnByProperty(attributes, strings[i]) + " = #{" + strings[i] + "}");
            } else {
                sqlBuilder.append(newLine + contentBlank + "and " + getColumnByProperty(attributes, strings[i]) + " = #{" + strings[i] + "}");
            }
        }
        StringBuilder paramBuilder = createParamBuilder(strings, rootElement, crudDialogConfig);
        Element setBuilder = createSetBuilder2(attributes);
        rootElement.addComment("build by plugin: " + "INTEGER" + " " + nameBuilder.toString() + "(" + paramBuilder.toString() + ");");

        select.addText("update * from " + crudDialogConfig.getTableName());
        select.add(setBuilder);
        select.addText(sqlBuilder.toString() + logicDeleteCode + outSideBlank);
        rootElement.add(select);
    }


    private static Element createSetBuilder2(List<Element> attributes) {
        Element set = new BaseElement("set");
        for (int index = 0; index < attributes.size(); index++) {
            Element _if = new BaseElement("if");
            Element element = attributes.get(index);
            String column = element.attributeValue("column");
            String jdbcType = element.attributeValue("jdbcType");
            String property = element.attributeValue("property");
            _if.addAttribute("test", property + " != null");
            _if.setText(column + " = #{" + property + ",jdbcType=" + jdbcType + "},");
            set.add(_if);
        }
        return set;
    }

    private static void addSelectBy(Element rootElement, CRUDDialogConfig crudDialogConfig) {
        String[] strings = crudDialogConfig.getSelectByTextField().split(",");
        List<Element> attributes = findBaseResultMap(rootElement, crudDialogConfig.getBaseResultMap()).elements("result");
        String returnObj = findBaseResultMap(rootElement, crudDialogConfig.getBaseResultMap()).attributeValue("type");
        Element select = new BaseElement("select");
        StrBuilder nameBuilder = new StrBuilder();
        for (int f = 0; f < strings.length; f++) {
            if (f == 0) {
                nameBuilder.append("selectBy").append(StringUtil.upFirstChar(strings[f]));
            } else {
                nameBuilder.append("And").append(StringUtil.upFirstChar(strings[f]));
            }
        }
        select.addAttribute("id", nameBuilder.toString());
        select.addAttribute("resultMap", "BaseResultMap");
        String logicDeleteCode = "";
        if (!crudDialogConfig.isDeleteFlag()) {
            logicDeleteCode = newLine + contentBlank + "and " + crudDialogConfig.getDeleteFlagStr() + " = " + crudDialogConfig.getUnDeletedStr() + newLine;
        }
        StrBuilder sqlBuilder = new StrBuilder();
        for (int i = 0; i < strings.length; i++) {
            if (i == 0) {
                sqlBuilder.append(newLine + contentBlank + "where " + getColumnByProperty(attributes, strings[i]) + " = #{" + strings[i] + "}");
            } else {
                sqlBuilder.append(newLine + contentBlank + "and " + getColumnByProperty(attributes, strings[i]) + " = #{" + strings[i] + "}");
            }
        }
        StringBuilder paramBuilder = createParamBuilder(strings, rootElement, crudDialogConfig);
        rootElement.addComment("  build by plugin: " + returnObj + " " + nameBuilder.toString() + "(" + paramBuilder.toString() + "); ");

        select.setText("  " + newLine + contentBlank + "\n select * from " + crudDialogConfig.getTableName() + contentBlank + sqlBuilder.toString() + logicDeleteCode + outSideBlank);
        rootElement.add(select);
    }

    private static StringBuilder createParamBuilder(String[] strings, Element rootElement, CRUDDialogConfig crudDialogConfig) {
        List<Element> attributes = findBaseResultMap(rootElement, crudDialogConfig.getBaseResultMap()).elements("result");
        StringBuilder paramBuilder = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {

            String addValue = strings[i].replace("*", "");
            String relaValue = strings[i].replace("*Start", "").replace("*End", "");
            if (i == 0) {
                paramBuilder.append("@Param(\"").append(addValue).append("\")");
                paramBuilder.append(JDBC2JAVA.getJAVAValueShort(getJdbcTypeByProperty(attributes, relaValue)) + " " + addValue + "");
            } else {
                String type = JDBC2JAVA.getJAVAValueShort(getJdbcTypeByProperty(attributes, relaValue));
                appendValue(paramBuilder, addValue, type);
            }
        }
        return paramBuilder;
    }

    private static StringBuilder appendValue(StringBuilder paramBuilder, String addValue, String type) {
        paramBuilder.append(",\n");
        paramBuilder.append(outSideBlank);
        paramBuilder.append("@Param(\"").append(addValue).append("\")");
        paramBuilder.append(type + " " + addValue + "");
        return paramBuilder;
    }

    private static String getColumnByProperty(List<Element> attributes, String _property) {
        for (Element element : attributes) {
            String column = element.attributeValue("column");
            String jdbcType = element.attributeValue("jdbcType");
            String property = element.attributeValue("property");
            if (_property.equals(property)) {
                return column;
            }
        }
        return null;
    }

    private static String getJdbcTypeByProperty(List<Element> attributes, String _property) {
        for (Element element : attributes) {
            String column = element.attributeValue("column");
            String jdbcType = element.attributeValue("jdbcType");
            String property = element.attributeValue("property");
            if (_property.equals(property)) {
                return jdbcType;
            }
        }
        return null;
    }

    private static boolean checkNeedRemove(Element element) {
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
        String type = findBaseResultMap(rootElement, crudDialogConfig.getBaseResultMap()).element("id").attribute("jdbcType").getValue();
        Element select = new BaseElement("select");
        select.addAttribute("id", "selectById");
        select.addAttribute("parameterType", JDBC2JAVA.getJAVAValue(type));
        select.addAttribute("resultMap", "BaseResultMap");
        String logicDeleteCode = "";
        if (crudDialogConfig.isDeleteFlag()) {
            logicDeleteCode = contentBlank + "and " + crudDialogConfig.getDeleteFlagStr() + " = " + crudDialogConfig.getUnDeletedStr() + newLine;
        }
        select.setText(newLine + contentBlank + "select * from " + crudDialogConfig.getTableName() + newLine + contentBlank + "where id = #{id,jdbcType=INTEGER}" + newLine + logicDeleteCode + outSideBlank);
        rootElement.add(select);
    }

    private static void addDelete(Element rootElement, CRUDDialogConfig crudDialogConfig) {
        String type = findBaseResultMap(rootElement, crudDialogConfig.getBaseResultMap()).element("id").attribute("jdbcType").getValue();
        Element select = null;
        if (crudDialogConfig.isDeleteFlag()) {
            select = new BaseElement("update");
            select.addAttribute("id", "deleteById");
            select.addAttribute("parameterType", JDBC2JAVA.getJAVAValue(type));
            //select.addAttribute("resultMap", "BaseResultMap");
            String logicDeleteCode = newLine + contentBlank + "set " + crudDialogConfig.getDeleteFlagStr() + " = " + crudDialogConfig.getDeletedStr() + newLine;
            select.setText(newLine + contentBlank + "update " + crudDialogConfig.getTableName() + logicDeleteCode + contentBlank + "where id = #{id,jdbcType=INTEGER}" + newLine + outSideBlank);
        } else {
            select = new BaseElement("delete");
            select.addAttribute("id", "deleteById");
            select.addAttribute("parameterType", JDBC2JAVA.getJAVAValue(type));
            select.setText(newLine + contentBlank + "delete from " + crudDialogConfig.getTableName() + " where id = #{id,jdbcType=INTEGER}" + newLine + outSideBlank);
        }
        rootElement.add(select);
    }

    private static void addUpdate(Element rootElement, CRUDDialogConfig crudDialogConfig) {
        String type = findBaseResultMap(rootElement, crudDialogConfig.getBaseResultMap()).element("id").attribute("jdbcType").getValue();
        List<Element> attributes = findBaseResultMap(rootElement, crudDialogConfig.getBaseResultMap()).elements("result");

        Element update = new BaseElement("update");
        update.addAttribute("id", "updateById");
        update.addAttribute("parameterType", JDBC2JAVA.getJAVAValue(type));
        // update.addAttribute("resultType", JDBC2JAVA.getJAVAValue("INTEGER"));
        Element setBuilder = createSetBuilder2(attributes);
        String logicDeleteCode = "";
        if (crudDialogConfig.isDeleteFlag()) {
            logicDeleteCode = " and " + crudDialogConfig.getDeleteFlagStr() + " = " + crudDialogConfig.getUnDeletedStr();
        }
        update.addText(newLine + contentBlank + "update " + crudDialogConfig.getTableName() + " ");
        update.add(setBuilder);
        update.addText(newLine + contentBlank + "where id = #{id,jdbcType=INTEGER}" + logicDeleteCode + newLine + " " + outSideBlank);
        rootElement.add(update);
    }

    private static void addInsert(Element rootElement, CRUDDialogConfig crudDialogConfig) {
        String type = findBaseResultMap(rootElement, crudDialogConfig.getBaseResultMap()).attribute("type").getValue();
        List<Element> attributes = findBaseResultMap(rootElement, crudDialogConfig.getBaseResultMap()).elements("result");

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
        insert.addText(newLine + contentBlank + "insert into " + crudDialogConfig.getTableName());
        insert.add(trim1);
        insert.addText(newLine);
        insert.add(trim2);
        insert.addText(newLine + outSideBlank);
        rootElement.add(insert);
    }

    private static void addSelectByColumn(Element rootElement, CRUDDialogConfig crudDialogConfig) {
        String idCloumn = findBaseResultMap(rootElement, crudDialogConfig.getBaseResultMap()).element("id").getName();
        Element select = new BaseElement("select");
        select.addAttribute("id", "selectByCondition");
        //select.addAttribute("parameterType", JDBC2JAVA.getJAVAValue(type));
        select.addAttribute("resultMap", "BaseResultMap");
        String logicDeleteCode = "";//逻辑删除
        if (crudDialogConfig.isDeleteFlag()) {
            logicDeleteCode = crudDialogConfig.getDeleteFlagStr() + " = " + crudDialogConfig.getUnDeletedStr() + newLine;
        } else {
            logicDeleteCode = idCloumn + " > -1" + newLine;
        }

        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(newLine).append(contentBlank).append("select * from ").append(crudDialogConfig.getTableName()).append(" where ").append(logicDeleteCode);
        select.addText(sqlBuilder.toString());
        addIfHaveTime(rootElement, select, crudDialogConfig);
        addOrderBy(select, crudDialogConfig);
        addDesc(select, crudDialogConfig);
        addSizeLimit(rootElement, select, crudDialogConfig);

        String[] strings = addStartAndEnd(crudDialogConfig.getSelectByCloumnTextField().split(","), crudDialogConfig);
        StringBuilder paramBuilder = createParamBuilder(strings, rootElement, crudDialogConfig);
        String returnObj = findBaseResultMap(rootElement, crudDialogConfig.getBaseResultMap()).attributeValue("type");

        if (crudDialogConfig.isLimitIndexParam()) {
            appendValue(paramBuilder, crudDialogConfig.getIndexStr(), "Integer");
            appendValue(paramBuilder, crudDialogConfig.getSizeStr(), "Integer");
        }
        rootElement.addComment("build by plugin: List<" + returnObj + "> selectByCondition(" + paramBuilder.toString() + ");");
        rootElement.add(select);
    }

    private static void addDesc(Element select, CRUDDialogConfig crudDialogConfig) {
        if (crudDialogConfig.isDesc()) {
            select.addText(" desc");
        }
    }

    private static void addOrderBy(Element select, CRUDDialogConfig crudDialogConfig) {
        if (crudDialogConfig.isOrderBy()) {
            select.addText("order by " + crudDialogConfig.getOrderByValue());
        }
    }

    private static String[] addStartAndEnd(String[] split, CRUDDialogConfig crudDialogConfig) {
        if (split == null) return new String[0];
        List<String> strings = new ArrayList<String>();
        for (String string : split) {
            if (string.endsWith(crudDialogConfig.getTimeSelectText().replace("*", ""))) {
                strings.add(string + "*Start");
                strings.add(string + "*End");
            } else {
                strings.add(string);
            }
        }
        String[] args = new String[strings.size()];
        for (int i = 0; i < strings.size(); i++) {
            args[i] = strings.get(i);
        }
        return args;
    }

    private static Element findBaseResultMap(Element rootElement, String findMapStr) {
        List<Element> element = rootElement.elements("resultMap");
        for (Element arg : element) {
            String name = arg.attribute("id").getName();
            String value = arg.attribute("id").getStringValue();
            if (value.equals(findMapStr)) {
                return arg;
            }
        }
        return null;
    }

    private static void addCountByColumn(Element rootElement, CRUDDialogConfig crudDialogConfig) {
        String idCloumn = findBaseResultMap(rootElement, crudDialogConfig.getBaseResultMap()).element("id").getName();
        Element select = new BaseElement("select");
        select.addAttribute("id", "countByCondition");
        //select.addAttribute("parameterType", JDBC2JAVA.getJAVAValue(type));
        select.addAttribute("resultType", JDBC2JAVA.getJAVAValue("INTEGER"));
        String logicDeleteCode = null;//逻辑删除
        if (crudDialogConfig.isDeleteFlag()) {
            logicDeleteCode = crudDialogConfig.getDeleteFlagStr() + " = " + crudDialogConfig.getUnDeletedStr() + newLine;
        } else {
            logicDeleteCode = idCloumn + " > -1" + newLine;
        }
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(newLine).append(contentBlank).append("select count(id) from ").append(crudDialogConfig.getTableName()).append(" where ").append(logicDeleteCode);
        select.addText(sqlBuilder.toString());
        addIfHaveTime(rootElement, select, crudDialogConfig);

        String[] strings = addStartAndEnd(crudDialogConfig.getSelectByCloumnTextField().split(","), crudDialogConfig);
        StringBuilder paramBuilder = createParamBuilder(strings, rootElement, crudDialogConfig);
        rootElement.addComment("build by plugin: int countByCondition(" + paramBuilder.toString() + ");");
        rootElement.add(select);
    }

    private static void addSizeLimit(Element root, Element select, CRUDDialogConfig crudDialogConfig) {
        //<if test="index != null and size != null"> limit #{index}, #{size} </if>
        if (crudDialogConfig.isLimitIndexParam() == false || crudDialogConfig.getIndexStr().equals("") || crudDialogConfig.getSizeStr().equals("")) {
            return;
        }
        Element _if = new BaseElement("if");
        _if.addAttribute("test", crudDialogConfig.getIndexStr() + " != null and " + crudDialogConfig.getSizeStr() + " != null");
        _if.setText("limit #{" + crudDialogConfig.getIndexStr() + "},#{" + crudDialogConfig.getSizeStr() + "}");
        select.add(_if);
    }

    private static void addIfHaveTime(Element root, Element target, CRUDDialogConfig crudDialogConfig) {
        List<Element> attributes = findBaseResultMap(root, crudDialogConfig.getBaseResultMap()).elements("result");
        for (Element element : attributes) {
            String property = element.attributeValue("property");
            if (crudDialogConfig.isTimeSelect() && crudDialogConfig.getTimeSelectText() != null && property.endsWith(crudDialogConfig.getTimeSelectText().replace("*", ""))) {
                addIfMore(element, target, crudDialogConfig.getDeleteFlagStr());
                addIfLess(element, target, crudDialogConfig.getDeleteFlagStr());
            } else {
                addIfEqual(element, target, crudDialogConfig.getDeleteFlagStr());
            }
        }
    }


    private static void addIfEqual(Element element, Element target, String deleteFlagStr) {
        Element _if = new BaseElement("if");
        String column = element.attributeValue("column");
        String jdbcType = element.attributeValue("jdbcType");
        String property = element.attributeValue("property");
        _if.addAttribute("test", property + " != null");
        _if.setText("and " + column + " = #{" + property + ",jdbcType=" + jdbcType + "}");
        if (deleteFlagStr != null && deleteFlagStr.equals(column)) {
        } else {
            target.add(_if);
        }
    }

    private static void addIfMore(Element data, Element target, String deleteFlagStr) {
        Element _if = new BaseElement("if");
        String column = data.attributeValue("column");
        String jdbcType = data.attributeValue("jdbcType");
        String property = data.attributeValue("property");
        _if.addAttribute("test", property + "Start != null");
        _if.setText("and " + column + " >= #{" + property + "Start,jdbcType=" + jdbcType + "}");
        if (deleteFlagStr != null && deleteFlagStr.equals(column)) {
        } else {
            target.add(_if);
        }
    }

    private static void addIfLess(Element data, Element element, String deleteFlagStr) {
        Element _if = new BaseElement("if");
        String column = data.attributeValue("column");
        String jdbcType = data.attributeValue("jdbcType");
        String property = data.attributeValue("property");
        _if.addAttribute("test", property + "End != null");
        _if.setText("and " + column + " &lt;= #{" + property + "End,jdbcType=" + jdbcType + "}");
        if (deleteFlagStr != null && deleteFlagStr.equals(column)) {
        } else {
            element.add(_if);
        }
    }

    private static boolean removeAllButCloumn(Document document) {
        Element rootElement = document.getRootElement();
        List<Element> elementList = rootElement.elements();
        for (Element element : elementList) {// <result <select
            if (!element.getName().equals("resultMap")) {
                element.getParent().remove(element);
            }
        }
        return true;
    }

    public static void main(String[] arsg) throws FileNotFoundException {
        String file = "D:\\java\\remot\\IdeaEasyPlugin\\test\\UserWithdrawalMapperNew.xml";
        FileInputStream fileInputStream = new FileInputStream(file);
        ReaderXML.read(fileInputStream, new ReaderXML.XMLInterface() {
            @Override
            public void update(Document document) {
                try {
                    CRUDDialogConfig crudDialogConfig = new CRUDDialogConfig("tables", false, false, false, false, false, "delete_flag", "0", "1");
                    crudDialogConfig.setTimeSelect(true);
                    crudDialogConfig.setSelectByCloumnTextField("serial_number");
                    crudDialogConfig.setCountByCloumnTextField("serial_number");
                    crudDialogConfig.setSelectByCondition(true);
                    crudDialogConfig.setCountByCondition(true);

//                    crudDialogConfig.setSelectByTextField("amount,thirdpayType");
//                    crudDialogConfig.setDeleteByTextField("amount,thirdpayType");
//                    crudDialogConfig.setUpdateByTextField("amount,thirdpayType");
                    // removeAllButCloumn(document);
                    runConfigure(document, crudDialogConfig);

                    ReaderXML.writer(document, file);
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