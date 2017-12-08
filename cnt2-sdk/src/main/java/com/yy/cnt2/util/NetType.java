package com.yy.cnt2.util;

public enum NetType {
    CTL(1, "CTL", "电信"), //
    CNC(2, "CNC", "联通/网通"), //
    EDU(3, "EDU", "教育"), //
    MOB(4, "MOB", "移动"), //
    CNII(5, "CNII", "铁通"), //
    WBN(6, "WBN", "长城"), //
    INTRANET(7, "INTRANET", "内网"), //
    BMC(8,"BMC","管理网"),
    ;
    private int value;
    private String shortName;
    private String name;

    private NetType(int value, String shortName, String name) {
        this.name = name;
        this.value = value;
        this.shortName = shortName;
    }

    public int getValue() {
        return value;
    }

    public String getShortName() {
        return shortName;
    }

    public String getName() {
        return name;
    }

    public static void main(String[] args) {
        System.out.println(NetType.valueOf("CTL"));
    }

}
