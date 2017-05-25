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

    boolean selectByCondition;
    boolean countByCondition;

    String deleteFlagStr;
    String deletedStr;
    String unDeletedStr;


    String  selectByTextField;
    String updateByTextField;
    String deleteByTextField;

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
        return tableName==null?null:tableName.trim().replaceAll("\n","");
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

    public boolean isCountByCondition() {
        return countByCondition;
    }

    public boolean isSelectByCondition() {
        return selectByCondition;
    }

    public void setCountByCondition(boolean countByCondition) {
        this.countByCondition = countByCondition;
    }

    public void setSelectByCondition(boolean selectByCondition) {
        this.selectByCondition = selectByCondition;
    }

    public void setDeleteByTextField(String deleteByTextField) {
        this.deleteByTextField = deleteByTextField;
    }

    public void setUpdateByTextField(String updateByTextField) {
        this.updateByTextField = updateByTextField;
    }

    public void setSelectByTextField(String selectByTextField) {
        this.selectByTextField = selectByTextField;
    }

    public String getDeleteByTextField() {
        return deleteByTextField;
    }

    public String getSelectByTextField() {
        return selectByTextField;
    }

    public String getUpdateByTextField() {
        return updateByTextField;
    }
}
