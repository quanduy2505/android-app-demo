package com.orangegangsters.github.swipyrefreshlayout.library;

public enum SwipyRefreshLayoutDirection {
    TOP(0),
    BOTTOM(1),
    BOTH(2);
    
    private int mValue;

    private SwipyRefreshLayoutDirection(int value) {
        this.mValue = value;
    }

    public static SwipyRefreshLayoutDirection getFromInt(int value) {
        for (SwipyRefreshLayoutDirection direction : values()) {
            if (direction.mValue == value) {
                return direction;
            }
        }
        return BOTH;
    }
}
