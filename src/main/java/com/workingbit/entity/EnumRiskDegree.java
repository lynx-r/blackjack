package com.workingbit.entity;

/**
 * Created by Aleksey Popryaduhin on 16:58 20/06/2017.
 */
public enum EnumRiskDegree {
    VERY_HIGH("Очень высокий", 0.8, 1),
    HIGH("Высокий", 0.6, 0.8),
    MEDIUM("Средний", 0.4, 0.6),
    LOW("Низкая", 0.2, 0.4),
    VERY_LOW("Очень низкий", 0, 0.2);

    private String displayName;
    private double top;
    private double bottom;

    EnumRiskDegree(String displayName, double bottom, double top) {
        this.displayName = displayName;
        this.top = top;
        this.bottom = bottom;
    }

    public static boolean inInterval(EnumRiskDegree enumRiskDegree, double possiblity) {
        return possiblity > enumRiskDegree.bottom && possiblity <= enumRiskDegree.top;
    }

    public boolean inInterval(double possiblity) {
        return possiblity > bottom && possiblity <= top;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public double getTop() {
        return top;
    }

    public void setTop(double top) {
        this.top = top;
    }

    public double getBottom() {
        return bottom;
    }

    public void setBottom(double bottom) {
        this.bottom = bottom;
    }
}
