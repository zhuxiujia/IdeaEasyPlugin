package com.zxj.plugin.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhuxiujie
 */
public class JDBC2JAVA {
    static Map<String, String> JDBCs = new HashMap<>();

    static {
        JDBCs.put("BIGINT", "java.lang.long");
        JDBCs.put("BINARY", " byte[]");
        JDBCs.put("BIT", "java.lang.Boolean");
        JDBCs.put("BLOB", "byte[]");
        JDBCs.put("CHAR", "java.lang.String");
        JDBCs.put("CLOB", "java.lang.String");
        JDBCs.put("DATE", "java.sql.Date");
        JDBCs.put("DECIMAL", "java.math.BigDecimal");
        JDBCs.put("DOUBLE", "java.lang.Double");
        JDBCs.put("FLOAT", "java.lang.Double");
        JDBCs.put("INTEGER", "java.lang.Integer");
        JDBCs.put("JAVA_OBJECT", "java.lang.Object");
        JDBCs.put("LONGVARBINARY", "byte[]");
        JDBCs.put("LONGVARCHAR", "java.lang.String");
        JDBCs.put("NUMERIC", "java.math.BigDecimal");
        JDBCs.put("OTHER", "java.lang.Object");
        JDBCs.put("REAL", "java.lang.Float");
        JDBCs.put("SMALLINT", "java.lang.Integer");
        JDBCs.put("TIME", "java.sql.Time");
        JDBCs.put("TIMESTAMP", "java.sql.Timestamp");
        JDBCs.put("TINYINT", "java.lang.Bute");
        JDBCs.put("VARBINARY", "byte[]");
        JDBCs.put("VARCHAR", "java.lang.String");
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

    public static String getJAVAValue(String jdbcTYPE) {
        return JDBCs.get(jdbcTYPE);
    }
}
