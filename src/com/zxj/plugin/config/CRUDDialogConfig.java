package com.zxj.plugin.config;

/**
 * @author zhuxiujie
 * @since 2017/4/11
 */

public class CRUDDialogConfig {
    String  tableName;
    boolean select=false;
    boolean delete=false;
    boolean update=false;
    boolean create=false;
    boolean deleteFlag=false;

    boolean selectByCondition=false;
    boolean countByCondition=false;

    boolean timeSelect=false;


    boolean selectBy=false;

    boolean updateBy=false;

    boolean deleteBy=false;

    boolean limitIndexParam=false;

    boolean desc=false;

    boolean orderBy=false;


    String orderByValue ="";

    String timeSelectText;

    String deleteFlagStr;
    String deletedStr;
    String unDeletedStr;


    String selectByCloumnTextField;
    String countByCloumnTextField;


    String selectByTextField;
    String updateByTextField;
    String deleteByTextField;

    String baseResultMap="BaseResultMap";

    String indexStr="";
    String sizeStr="";

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

    public void setTimeSelect(boolean timeSelect) {
        this.timeSelect = timeSelect;
    }

    public boolean isTimeSelect() {
        return timeSelect;
    }

    public void setTimeSelectText(String timeSelectText) {
        this.timeSelectText = timeSelectText;
    }

    public String getTimeSelectText() {
        return timeSelectText;
    }

    public void setBaseResultMap(String baseResultMap) {
        this.baseResultMap = baseResultMap;
    }

    public String getBaseResultMap() {
        return baseResultMap;
    }

    public void setSelectByCloumnTextField(String selectByCloumnTextField) {
        this.selectByCloumnTextField = selectByCloumnTextField;
    }

    public String getSelectByCloumnTextField() {
        return selectByCloumnTextField;
    }

    public void setCountByCloumnTextField(String countByCloumnTextField) {
        this.countByCloumnTextField = countByCloumnTextField;
    }

    public String getCountByCloumnTextField() {
        return countByCloumnTextField;
    }

    public boolean isSelectBy() {
        return selectBy;
    }

    public void setSelectBy(boolean selectBy) {
        this.selectBy = selectBy;
    }

    public boolean isUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(boolean updateBy) {
        this.updateBy = updateBy;
    }

    public boolean isDeleteBy() {
        return deleteBy;
    }

    public void setDeleteBy(boolean deleteBy) {
        this.deleteBy = deleteBy;
    }

    public void setLimitIndexParam(boolean limitIndexParam) {
        this.limitIndexParam = limitIndexParam;
    }

    public boolean isLimitIndexParam() {
        return limitIndexParam;
    }

    public void setLimitIndexParam(String index, String size) {
        this.indexStr =index;
        this.sizeStr =size;
    }

    public String getIndexStr() {
        return indexStr;
    }

    public String getSizeStr() {
        return sizeStr;
    }

    public void setDesc(boolean desc) {
        this.desc = desc;
    }

    public boolean isDesc() {
        return desc;
    }

    public void setOrderByValue(String orderByValue) {
        this.orderByValue = orderByValue;
    }

    public String getOrderByValue() {
        return orderByValue;
    }

    public void setOrderBy(boolean orderBy) {
        this.orderBy = orderBy;
    }

    public boolean isOrderBy() {
        return orderBy;
    }
}
