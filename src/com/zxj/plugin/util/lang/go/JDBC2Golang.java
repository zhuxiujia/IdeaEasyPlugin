package com.zxj.plugin.util.lang.go;

import com.intellij.openapi.util.text.StringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhuxiujie
 */
public class JDBC2Golang {
    static Map<String, String> JDBCs = new HashMap<>();

    static {
        JDBCs.put("BIGINT", "int64");
        JDBCs.put("BINARY", " byte[]");
        JDBCs.put("BIT", "bool");
        JDBCs.put("BLOB", "byte[]");
        JDBCs.put("CHAR", "string");
        JDBCs.put("CLOB", "string");
        JDBCs.put("DATE", "time.Time");
        JDBCs.put("DECIMAL", "Decimal");
        JDBCs.put("DOUBLE", "float64");
        JDBCs.put("FLOAT", "float64");
        JDBCs.put("INTEGER", "int");
        JDBCs.put("JAVA_OBJECT", "interface{}");
        JDBCs.put("LONGVARBINARY", "byte[]");
        JDBCs.put("LONGVARCHAR", "string");
        JDBCs.put("NUMERIC", "Decimal");
        JDBCs.put("OTHER", "interface{}");
        JDBCs.put("REAL", "float64");
        JDBCs.put("SMALLINT", "int");
        JDBCs.put("TIME", "time.Time");
        JDBCs.put("TIMESTAMP", "time.Time");
        JDBCs.put("TINYINT", "java.lang.Bute");
        JDBCs.put("VARBINARY", "byte[]");
        JDBCs.put("VARCHAR", "string");
    }

    public static Map<String, String> getJDBCs() {
        return JDBCs;
    }

    public static String getJDBCValue(String javaType) {
        for (Map.Entry<String, String> entry : JDBCs.entrySet()) {
            if (javaType.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static String getGolangValue(String jdbcTYPE) {
        return JDBCs.get(jdbcTYPE);
    }

    public static String getGolangValueShort(String jdbcTYPE) {
        String va = JDBCs.get(jdbcTYPE);
        if (va != null) va = va.substring(va.lastIndexOf(".") + 1, va.length());
        return va;
    }

    public static String convertString(String jdbcTYPE, String keyName) {
        if (jdbcTYPE != null && jdbcTYPE.equals("") == false) {
            if (jdbcTYPE.equalsIgnoreCase("CHAR") ||
                    jdbcTYPE.equalsIgnoreCase("CLOB") ||
                    jdbcTYPE.equalsIgnoreCase("LONGVARCHAR") ||
                    jdbcTYPE.equalsIgnoreCase("VARCHAR")) {
                return keyName;
            } else if (jdbcTYPE.equals("TIMESTAMP") ||
                    jdbcTYPE.equals("TIME")) {
                return keyName + ".Format(`2006-01-02 15:04:05`)";
            } else if (jdbcTYPE.equals("BIGINT")) {
                return "strconv.FormatInt(" + keyName + ", 10)";
            } else if (jdbcTYPE.equals("INTEGER") ||
                    jdbcTYPE.equals("SMALLINT")) {
                return "strconv.Itoa(" + keyName + ")";
            }else if (jdbcTYPE.equals("DOUBLE") ||
                    jdbcTYPE.equals("FLOAT")||
                    jdbcTYPE.equals("REAL")) {
                return "strconv.FormatFloat("+keyName+", 'f', 8, 64)";
            }else {
                return keyName;
            }
        }else {
            return keyName;
        }
    }
    public static String convertIsNullString(String jdbcTYPE, String keyName) {
        if (jdbcTYPE != null && jdbcTYPE.equals("") == false) {
            if (jdbcTYPE.equalsIgnoreCase("CHAR") ||
                    jdbcTYPE.equalsIgnoreCase("CLOB") ||
                    jdbcTYPE.equalsIgnoreCase("LONGVARCHAR") ||
                    jdbcTYPE.equalsIgnoreCase("VARCHAR")) {
                return keyName+" == ``";
            } else if (jdbcTYPE.equals("TIMESTAMP") ||
                    jdbcTYPE.equals("TIME")) {
                return keyName + ".IsZero()";
            } else if (jdbcTYPE.equals("BIGINT")) {
                return  keyName + " == 0";
            } else if (jdbcTYPE.equals("INTEGER") ||
                    jdbcTYPE.equals("SMALLINT")) {
                return  keyName + " == 0";
            }else if (jdbcTYPE.equals("DOUBLE") ||
                    jdbcTYPE.equals("FLOAT")||
                    jdbcTYPE.equals("REAL")) {
                return keyName+" == 0";
            }else {
                return keyName+" == ``";
            }
        }else {
            return keyName+" == ``";
        }
    }
}
