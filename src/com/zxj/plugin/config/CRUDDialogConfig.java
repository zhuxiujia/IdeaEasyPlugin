package com.zxj.plugin.config;

/**
 * @author zhuxiujie
 * @since 2017/4/11
 */

public class CRUDDialogConfig {
    String  tableName;
    boolean select;
    boolean delete;
    boolean update;
    boolean create;
    public CRUDDialogConfig(String tableName,boolean select,boolean delete,boolean update,boolean create){
        this.select=select;
        this.delete=delete;
        this.update=update;
        this.create=create;
        this.tableName=tableName;
    }

    public boolean isCreate() {
        return create;
    }

    public boolean isDelete() {
        return delete;
    }

    public boolean isSelect() {
        return select;
    }

    public boolean isUpdate() {
        return update;
    }

    public String getTableName() {
        return tableName;
    }
}
