package com.zxj.plugin.util.lang.go;

/**
 * @author zhuxiujie
 * @since 2018/10/8
 */

public  class ResultItem{
    public   String column;
    public  String property;
    public  String jdbcType;

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(String jdbcType) {
        this.jdbcType = jdbcType;
    }
}
