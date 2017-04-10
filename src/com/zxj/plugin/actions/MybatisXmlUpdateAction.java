package com.zxj.plugin.actions;

import java.util.List;
import java.util.Properties;

import com.zxj.plugin.config.MybatisXmlConfig;
import com.zxj.plugin.util.PropertyUtil;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.tree.BaseElement;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.vfs.VirtualFile;
import com.zxj.plugin.util.FileUtil;
import com.zxj.plugin.util.JDBC2JAVA;
import com.zxj.plugin.util.ReaderXML;

/**
 * Created by zhuxiujie
 */
public class MybatisXmlUpdateAction extends AnAction {
	
	MybatisXmlConfig mybatisXmlConfig;
	
	@Override
	public void update(AnActionEvent event) {
		event.getPresentation().setEnabledAndVisible(true);
	}
	
	@Override
	public void actionPerformed(AnActionEvent event) {
		// Does nothing
		VirtualFile virtualFile = FileUtil.getFile(event.getDataContext());
		String extension = virtualFile.getExtension();
		if (extension!=null&&extension.equals("xml")) {
			ReaderXML.read(virtualFile.getPath(), new ReaderXML.XMLInterface() {
				@Override
				public void update(Document document) {
                            try {
                                runConfigure(document,event);
                                ReaderXML.writer(document,event.getProject().getBasePath()+"/mapper_"+mybatisXmlConfig.getTableName()+".xml");

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
				}
			});
			
		} else {
			System.out.println(virtualFile.getName() + " is not a .xml file!");
		}
	}

    private void runConfigure(Document document,AnActionEvent e) throws Exception {
        String baseDir=e.getProject().getBaseDir().getPath();
        String fileName=baseDir+"/mybitsUpdateConfig.properties";
        Properties properties= new Properties();
        PropertyUtil.read(properties,fileName);
        mybatisXmlConfig=new MybatisXmlConfig();
        mybatisXmlConfig.setDeleteFlag(properties.getProperty("deleteFlag"));
        mybatisXmlConfig.setUnDeleteFlag(properties.getProperty("unDeleteFlag"));
        mybatisXmlConfig.setLogicDeleteFlag(properties.getProperty("logicDeleteFlag"));
        mybatisXmlConfig.setTableName(properties.getProperty("tableName"));

        Element rootElement = document.getRootElement();
        List<Element> elementList = rootElement.elements();
        for (Element element : elementList) {// <result <select
            if (checkNeedRemove(element)) {// remove
                // delete
                element.getParent().remove(element);
                System.out.println("delete element:"+element.attribute("id").getValue());
            }
        }
        // add select
        addSelect(rootElement);
        addDelete(rootElement);
        addUpdate(rootElement);
        addInsert(rootElement);
        rootElement.addText("\n");
        System.out.println(document.toString());

        System.out.println("success ,");
    }

    private boolean checkNeedRemove(Element element) {
        //if(!attributeStr.equals("BaseResultMap"))return true;
        Attribute attribute = element.attribute("id");
        String attributeStr=attribute.getValue();
        if(attributeStr.equals("selectById")
                ||attributeStr.equals("deleteById")
                ||attributeStr.equals("updateById")
                ||attributeStr.equals("insert")
                ||attributeStr.equals("selectByPrimaryKey")
                ||attributeStr.equals("deleteByPrimaryKey")
                ||attributeStr.equals("insertSelective")
                ||attributeStr.equals("updateByPrimaryKeySelective")
                ||attributeStr.equals("updateByPrimaryKey")
                ){
            return true;
        }
        return false;
    }

    private void addSelect(Element rootElement) {
		String type = rootElement.element("resultMap").element("id").attribute("jdbcType").getValue();
		
		Element select = new BaseElement("select");
		select.addAttribute("id", "selectById");
		select.addAttribute("parameterType", JDBC2JAVA.getJAVAValue(type));
		select.addAttribute("resultMap", "BaseResultMap");
		String logicDeleteCode = null;
		if (mybatisXmlConfig.getLogicDeleteFlag() != null) {
			logicDeleteCode = "     and " + mybatisXmlConfig.getLogicDeleteFlag() + " = " + mybatisXmlConfig.getUnDeleteFlag() + "\n ";
		}
		select.setText("\n     select * from " + mybatisXmlConfig.getTableName() + "\n\t\twhere id = #{id,jdbcType=INTEGER}\n" + logicDeleteCode);
		rootElement.add(select);
	}
	
	private void addDelete(Element rootElement) {
		String type = rootElement.element("resultMap").element("id").attribute("jdbcType").getValue();
		
		Element select = new BaseElement("update");
		select.addAttribute("id", "deleteById");
		select.addAttribute("parameterType", JDBC2JAVA.getJAVAValue(type));
		select.addAttribute("resultMap", "BaseResultMap");
		String logicDeleteCode = null;
		if (mybatisXmlConfig.getLogicDeleteFlag() != null) {
			logicDeleteCode = "\n\t\tset " + mybatisXmlConfig.getLogicDeleteFlag() + " = " + mybatisXmlConfig.getDeleteFlag();
		}
		select.setText("\n     update " + mybatisXmlConfig.getTableName() + logicDeleteCode + "\n\t\twhere id = #{id,jdbcType=INTEGER} \n ");
		rootElement.add(select);
	}
	
	private void addUpdate(Element rootElement) {
		String type = rootElement.element("resultMap").element("id").attribute("jdbcType").getValue();
		List<Element> attributes = rootElement.element("resultMap").elements("result");
		
		Element update = new BaseElement("update");
		update.addAttribute("id", "updateById");
		update.addAttribute("parameterType", JDBC2JAVA.getJAVAValue(type));
		update.addAttribute("resultMap", "BaseResultMap");
		
		Element set = new BaseElement("set");
		for (Element element : attributes) {
			Element _if = new BaseElement("if");
			String column = element.attributeValue("column");
			String jdbcType = element.attributeValue("jdbcType");
			String property = element.attributeValue("property");
			_if.addAttribute("test", property + " != null");
			_if.setText(column + " = #{" + property + ",jdbcType=" + jdbcType + "},");
			set.add(_if);
		}
		String sets = set.asXML().toString();
		sets = sets.replaceAll("<if", "\n        <if");
		sets = sets.replaceAll("<set>", "\n   <set>");
		sets = sets.replaceAll("</set>", "\n   </set>");
		
		String logicDeleteCode = null;
		if (mybatisXmlConfig.getLogicDeleteFlag() != null) {
			logicDeleteCode = " and " + mybatisXmlConfig.getLogicDeleteFlag() + " = " + mybatisXmlConfig.getUnDeleteFlag() + "\n ";
		}
		update.setText(
				" \n     update " + mybatisXmlConfig.getTableName() + " " + sets + "\t\twhere id = #{id,jdbcType=INTEGER}" + logicDeleteCode);
		rootElement.add(update);
	}
	
	private void addInsert(Element rootElement) {
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
		trims1 = trims1.replaceAll("<if", "\n        <if");
		trims1 = trims1.replaceAll("<trim>", "\n   <trim>\n");
		trims1 = trims1.replaceAll("</trim>", "\n   </trim>\n");
		
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
		trims2 = trims2.replaceAll("<if", "\n        <if");
		trims2 = trims2.replaceAll("<trim>", "\n   <trim>\n");
		trims2 = trims2.replaceAll("</trim>", "\n   </trim>\n");
		
		insert.setText("\n     insert into " + mybatisXmlConfig.getTableName() + "\n " + trims1 + "\n" + trims2);
		rootElement.add(insert);

	}
}