package com.yy.cnt2.store;

/**
 * @author xlg
 * @since 2017/7/11
 */
public class ConfigValue {
    String value;
    long version;

    public ConfigValue() {
    }

    public ConfigValue(String value, long version) {
        this.value = value;
        this.version = version;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "ConfigValue{" + "value='" + value + '\'' + ", version=" + version + '}';
    }
}
