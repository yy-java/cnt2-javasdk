package com.yy.cnt.recipes.db.common;

public abstract class BaseConfig {

    protected String backupJdbcUrl;

    public String getBackupJdbcUrl() {
        return backupJdbcUrl;
    }

    public void setBackupJdbcUrl(String backupJdbcUrl) {
        this.backupJdbcUrl = backupJdbcUrl;
    }

    public abstract BaseConfig changeBackupConfig();

    public abstract String getMasterJdbcUrl();
}
