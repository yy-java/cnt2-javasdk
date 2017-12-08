package com.yy.cnt.recipes.db.common;

import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("rawtypes")
public class DataSourceHealthChecker extends TimerTask {

    private static final Logger log = LoggerFactory.getLogger(DataSourceHealthChecker.class);

    private AbstractDataSourceFactory dataSourceFactory;

    private Map<String, Long> lastCheckTime = new ConcurrentHashMap<>();

    private AtomicInteger failedCount = new AtomicInteger(0);

    private static final int MIN_CHECK_TIME = 5 * 60 * 1000;// 5分钟

    public DataSourceHealthChecker(AbstractDataSourceFactory dataSourceFactory) {
        this.dataSourceFactory = dataSourceFactory;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void run() {
        if (dataSourceFactory.getConfig() instanceof BaseConfig) {
            BaseConfig bc = (BaseConfig) dataSourceFactory.getConfig();
            if (StringUtils.isBlank(bc.getBackupJdbcUrl())) {
                // 有配置备用链接的时候才进行定时检查
                return;
            }
            String master = bc.getMasterJdbcUrl();
            String backup = bc.getBackupJdbcUrl();

            this.removeCheckedInvalidUrls(bc);
            try {
                boolean valid = this.checkMasterIsValid(master);
                if (valid) {
                    return;
                }
                int fails = failedCount.incrementAndGet();
                if (lastCheckTime.containsKey(backup)) {
                    // 不频繁检查，最小检查间隔内已经检查过backupJdbcUrl，这时候不做切换
                    long lastCheck = lastCheckTime.get(backup);
                    if ((System.currentTimeMillis() - lastCheck) < MIN_CHECK_TIME) {
                        return;
                    }
                }
                if (dataSourceFactory.getBackupSwitchAfterFails() > 0
                        && fails >= dataSourceFactory.getBackupSwitchAfterFails()) {
                    log.warn("test of " + bc + " error,try to change to backServer " + bc.getBackupJdbcUrl());
                    bc.changeBackupConfig();
                    failedCount.set(0);
                    dataSourceFactory.init(bc.changeBackupConfig());
                }
            } catch (Exception e) {
                log.error("check health of " + bc + " error:", e);
            }
        }
    }

    private boolean checkMasterIsValid(String masterJdbcUrl) throws Exception {
        lastCheckTime.put(masterJdbcUrl, System.currentTimeMillis());
        boolean succ = dataSourceFactory.isValid(5);
        return succ;
    }

    /**
     * 从上次的检查时间记录中， 移除当前不存在的jdbcUrl
     * 
     * @param bc
     */
    private void removeCheckedInvalidUrls(BaseConfig bc) {
        String url1 = bc.getMasterJdbcUrl();
        String url2 = bc.getBackupJdbcUrl();
        for (String key : lastCheckTime.keySet()) {
            if (key.equals(url1) || key.equals(url2)) {
                continue;
            }
            lastCheckTime.remove(key);
        }
    }

}
