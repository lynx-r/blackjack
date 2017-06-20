package com.workingbit.entity;

/**
 * Created by Aleksey Popryaduhin on 15:09 20/06/2017.
 */
public enum EnumModel {
    CHESSER("Модель Чессера"),
    GDANOV("Модель Жданова"),
    JU("Модель Джу-Ха Техонга");

    private final String displayName;

    EnumModel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
