package com.zxj.plugin.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.SAXWriter;
import org.dom4j.io.XMLWriter;

/**
 * Created by zhuxiujie
 */
public class ReaderXML {
   private static  final   SAXReader reader = new SAXReader();
    public static void main(String[] args) {

    }

    public static void read(String filePath, XMLInterface xmlInterface) {
        try {
            // 3。获取文件
            System.out.println("load xml path:" + filePath);
            File file = new File(filePath);
            Document document = reader.read(file);
            document.setXMLEncoding("UTF-8");
            xmlInterface.update(document);
        } catch (Exception e) {
            xmlInterface.error(e.getMessage());
            e.printStackTrace();
            System.out.println("load file fail:" + filePath);
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
            // 紧凑的格式
             //OutputFormat format = OutputFormat.createCompactFormat();
            // 排版缩进的格式
            OutputFormat format = OutputFormat.createPrettyPrint();
            // 设置编码
            format.setEncoding("UTF-8");
            format.setTrimText(false);
            format.setIndent(true); // 设置是否缩进
            format.setIndent("    "); // 以四个空格方式实现缩进
            format.setNewlines(true); // 设置是否换行
            format.setOmitEncoding(true);
            // 创建XMLWriter对象,指定了写出文件及编码格式
            XMLWriter writer = new XMLWriter(
                    new OutputStreamWriter(new FileOutputStream(new File(fileName)), "UTF-8"), format);
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

    public interface XMLInterface {
        void update(Document document);
        void error(String info);
    }
}