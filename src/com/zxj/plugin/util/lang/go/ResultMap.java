package com.zxj.plugin.util.lang.go;

import java.util.List;

public class ResultMap {
    public String id;
    public String type;

    public ResultItem idItem;
    public List<ResultItem> resultItems;


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



