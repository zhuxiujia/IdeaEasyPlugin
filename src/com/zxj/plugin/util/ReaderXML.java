package com.zxj.plugin.util;

import java.io.*;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.SAXWriter;
import org.dom4j.io.XMLWriter;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by zhuxiujie
 */
public class ReaderXML {
   private static  final   SAXReader reader = new SAXReader();
    public static void main(String[] args) {

    }

    public static void read(InputStream inputStream, XMLInterface xmlInterface) {
        try {
            // 3。获取文件
            Document document = reader.read(inputStream);
            document.setXMLEncoding("UTF-8");
            xmlInterface.update(document);
        } catch (Exception e) {
            xmlInterface.error(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 把document对象写入新的文件
     *
     * @param document
     * @throws Exception
     */
    public static void writer(Document document, String fileName) {
        try {

            document =repleaceAllN(document);

            System.out.print(document.asXML().toString());

            // 紧凑的格式
             OutputFormat format = OutputFormat.createCompactFormat();
            // 排版缩进的格式
            //OutputFormat format = OutputFormat.createPrettyPrint();
            // 设置编码
            format.setEncoding("UTF-8");
            format.setTrimText(true);
            format.setIndent(true); // 设置是否缩进
            format.setIndent("    "); // 以四个空格方式实现缩进
            format.setNewlines(true); // 设置是否换行
            format.setNewLineAfterDeclaration(true);
            format.setSuppressDeclaration(true); //是否生产xml头
            format.setNewLineAfterNTags(0);
            format.setOmitEncoding(false);
            // 创建XMLWriter对象,指定了写出文件及编码格式
            XMLWriter writer = new XMLWriter(
                    new OutputStreamWriter(new FileOutputStream(new File(fileName)), "UTF-8"),format);
            writer.setEscapeText(false);
            // 写入
            writer.write(document);
            // 立即写入
            writer.flush();
            // 关闭操作
            writer.close();
            System.out.println("out file success:" + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Document repleaceAllN(Document document) {
        deleteH(document.getRootElement());
        return document;
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
            //element.setText(element.getText().replaceAll(newLine, ""));
            String text=element.getText();
            if(text.startsWith("select")||text.startsWith("update")||text.startsWith("count")||text.startsWith("delete")){
                element.addEntity("\n","\n");
            }
        }
    }


    public interface XMLInterface {
        void update(Document document);
        void error(String info);
    }

}