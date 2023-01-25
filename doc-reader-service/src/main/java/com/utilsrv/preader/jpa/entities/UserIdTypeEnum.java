package com.utilsrv.preader.jpa.entities;

public enum UserIdTypeEnum {
    UserName(1), DeviceId(2);

    private int id;

    UserIdTypeEnum(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}
