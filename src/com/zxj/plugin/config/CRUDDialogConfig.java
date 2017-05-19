package com.zxj.plugin.config;

import org.apache.commons.lang.text.StrBuilder;

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
    boolean deleteFlag;

    String deleteFlagStr;
    String deletedStr;
    String unDeletedStr;

    public CRUDDialogConfig(String tableName,boolean select,boolean delete,boolean update,boolean create,boolean deleteFlag,String deleteFlagStr,String deletedStr,String unDeletedStr){
        this.select=select;
        this.delete=delete;
        this.update=update;
        this.create=create;
        this.deleteFlag=deleteFlag;
        this.tableName=tableName;
        this.deleteFlagStr=deleteFlagStr;
        this.deletedStr=deletedStr;
        this.unDeletedStr=unDeletedStr;
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

    public boolean isDeleteFlag() {
        return deleteFlag;
    }


    public String getDeletedStr() {
        return deletedStr;
    }

    public String getDeleteFlagStr() {
        return deleteFlagStr;
    }

    public String getUnDeletedStr() {
        return unDeletedStr;
    }
}
