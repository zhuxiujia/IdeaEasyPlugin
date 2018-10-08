package com.zxj.plugin.util.lang.go;

import java.util.List;

public class ResultMap {
     String id;
     String type;

     ResultItem idItem;
     List<ResultItem> resultItems;



    public void setResultItems(List<ResultItem> resultItems) {
        this.resultItems = resultItems;
    }

    public List<ResultItem> getResultItems() {
        return resultItems;
    }

    public void setIdItem(ResultItem idItem) {
        this.idItem = idItem;
    }

    public ResultItem getIdItem() {
        return idItem;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}


 class ResultItem{
     private  String column;
     private  String property;
     private  String jdbcType;

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
