package com.zxj.plugin.util.lang.go;

import com.intellij.openapi.util.text.StringUtil;
import com.zxj.plugin.util.ReaderXML;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;
import org.dom4j.tree.DefaultText;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlToGolang {
   static final String regEx = "#\\{([^\\}]+)\\}";
    public static void main(String[] args) throws Exception {
        String file = "D:\\JAVA\\IdeaEasyPlugin\\test\\ActivityMapper.xml";
        FileInputStream fileInputStream = new FileInputStream(file);
        ReaderXML.read(fileInputStream, new ReaderXML.XMLInterface() {
            @Override
            public void update(Document document) {
                String s = toGolangCode(document.getRootElement());
                System.out.println(s);
            }

            @Override
            public void error(String info) {
                System.out.println(info);
            }
        });
    }

    public static String toGolangCode(Element rootElement) {
        BasicXmlInfo basicXmlInfo = decodeBasic(rootElement);
        Map<String, ResultMap> resultMaps = decodeResultMap(rootElement);
        decodeSelect(rootElement, basicXmlInfo, resultMaps);
        return rootElement.getText();
    }

    private static Map<String, ResultMap> decodeResultMap(Element rootElement) {
        Map<String, ResultMap> resultMaps = new HashMap<>();
        List<Element> elements = rootElement.elements("resultMap");
        if (elements.size() != 0) {
            for (Element resultMapElement : elements) {
                ResultMap resultMap = new ResultMap();
                resultMap.resultItems = new ArrayList<>();

                resultMap.setId(safeAttribute(resultMapElement, "id"));
                resultMap.setType(safeAttribute(resultMapElement, "type"));
                Element idElement = resultMapElement.element("id");
                if (idElement != null) {
                    resultMap.idItem = new ResultItem();
                    resultMap.idItem.setColumn(safeAttribute(idElement, "column"));
                    resultMap.idItem.setJdbcType(safeAttribute(idElement, "jdbcType"));
                    resultMap.idItem.setProperty(safeAttribute(idElement, "property"));
                }

                List<Element> results = resultMapElement.elements("result");
                for (Element element : results) {
                    ResultItem resultItem = new ResultItem();
                    resultItem.setColumn(safeAttribute(element, "column"));
                    resultItem.setJdbcType(safeAttribute(element, "jdbcType"));
                    resultItem.setProperty(safeAttribute(element, "property"));
                    resultMap.resultItems.add(resultItem);
                }
                resultMaps.put(resultMap.id, resultMap);
            }
        }
        return resultMaps;
    }

    private static BasicXmlInfo decodeBasic(Element rootElement) {
        Element element = rootElement;
        BasicXmlInfo basicXmlInfo = new BasicXmlInfo();
        basicXmlInfo.setNamespace(safeAttribute(element, "namespace"));
        basicXmlInfo.setModel(safeAttribute(element, "model"));
        return basicXmlInfo;
    }

    private static void decodeSelect(Element rootElement, BasicXmlInfo basicXmlInfo, Map<String, ResultMap> resultMaps) {
        List<Element> selects = rootElement.elements("select");
        for (Element element : selects) {
            String id = safeAttribute(element, "id");

            String returnType = "";
            String resultMap = safeAttribute(element, "resultMap");
            String collection = safeAttribute(element, "collection");
            if (StringUtil.isNotEmpty(resultMap)) {
                ResultMap map = resultMaps.get(resultMap);
                if (map != null) {
                    returnType = map.getType();
                }
            } else {
                returnType = safeAttribute(element, "resultType");
                ResultMap map = resultMaps.get(returnType);
                if (map != null) {
                    returnType = map.getType();
                }
            }
            Map<String, String> params = new HashMap<>();
            scanAllParams(element.getText(), params);
            List<Object> contents = element.content();
            String func = createSelectFunc(id, basicXmlInfo.getNamespace(), params, returnType, collection, contents);
            System.out.println(func);
        }
    }

    private static void scanAllParams(String text, Map<String, String> params) {
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String group = matcher.group().replaceAll(" ", "");
            String groupContext = group.replace("#{", "").replace("}", "");
            String[] args = groupContext.split(",");
            if (args.length > 0 && args.length <= 1) {
                params.put(args[0], "");
            } else {
                String jdbcType = args[1].replace("jdbcType=", "");
                params.put(args[0], jdbcType);
            }
        }
    }

    private static String createSelectFunc(String id, String mapperName, Map<String, String> params, String returnType, String collection, List<Object> contents) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("func (this ").append(mapperName).append(")").append(id).append("(");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String type = "string";
            String jdbcType = entry.getValue();
            if (!jdbcType.equals("")) {
                type = JDBC2Golang.getGolangValue(jdbcType);
            }
            stringBuilder.append(entry.getKey()).append(" ").append(type);
        }
        stringBuilder.append(") (");

        String funcReturnType = returnType;
        if (StringUtil.isNotEmpty(collection)) {
            if (collection.equalsIgnoreCase("array")) {
                funcReturnType = "[]" + funcReturnType;
            } else {
                throw new RuntimeException("集合类型暂不支持！collection=" + collection);
            }
        }
        stringBuilder.append(funcReturnType).append(", error) {\n");

        //create func body
        stringBuilder.append("\tvar ").append("result").append(" ").append(funcReturnType).append("\n");
        //create sql
        StringBuilder sql = new StringBuilder();
        sql=decodeContent(contents,params);

        stringBuilder.append("\tvar db = context.GetInstance().Raw(").append(sql).append(").Scan(&result)\n");
        stringBuilder.append("\tif db.Error != nil {\n" +
                "\t\treturn result, db.Error\n" +
                "\t}\n" +
                "\treturn result, nil\n}");
        return stringBuilder.toString();
    }

    private static StringBuilder decodeContent(List<Object> contents,Map<String, String> params) {
        StringBuilder sql=new StringBuilder();
        for (Object obj : contents) {
            if (obj.getClass().equals(DefaultText.class)) {
                //text
                DefaultText text = (DefaultText) obj;
                String raw=text.getText().replaceAll("\n","").replaceAll("  "," ");

                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String type = "string";
                    String jdbcType = entry.getValue();
                    if (!jdbcType.equals("")) {
                        type = JDBC2Golang.getGolangValue(jdbcType);
                    }
                    raw=raw.replaceAll("\\#\\{"+entry.getKey()+"\\}","` + "+entry.getKey()+" + `");
                    raw=raw.replaceAll("\\#\\{"+entry.getKey()+",jdbcType="+entry.getValue()+"\\}","` + "+entry.getKey()+" + `");
                }
                sql.append("`").append(raw).append("` +");
            } else if (obj.getClass().equals(DefaultElement.class)) {
                //if
                DefaultElement element = (DefaultElement) obj;
                String test = safeAttribute(element, "test");
                String[] andTests = test.split(" and ");
                if (andTests.length != 0) {
                    int i=0;
                    for (String item : andTests) {
                        sql.append("\n\tif ").append(item);
                        i++;
                        if(i<andTests.length)sql.append(" && ");
                    }
                    sql.append("{").append(decodeContent(element.content(),params)).append("}");
                }
            }
        }
        sql= com.zxj.plugin.util.StringUtil.deleteLastString(sql,"+");
        return sql;
    }




    private static String safeAttribute(Element element, String key) {
        Attribute a = element.attribute(key);
        if (a == null) {
            return null;
        }
        return a.getValue();
    }
}
