package com.zxj.plugin.config;

/**
 * Created by zhuxiujie
 */
public class MybatisXmlConfig {
    String tableName = "table";
    String logicDeleteFlag = "delete_flag";
    String unDeleteFlag = "1";
    String deleteFlag = "0";

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getLogicDeleteFlag() {
        return logicDeleteFlag;
    }

    public void setLogicDeleteFlag(String logicDeleteFlag) {
        this.logicDeleteFlag = logicDeleteFlag;
    }

    public String getUnDeleteFlag() {
        return unDeleteFlag;
    }

    public void setUnDeleteFlag(String unDeleteFlag) {
        this.unDeleteFlag = unDeleteFlag;
    }

    public String getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(String deleteFlag) {
        this.deleteFlag = deleteFlag;
    }
}
