package xyz.zzzxb.toolkit.entity;

/**
 *
 * @author zzzxb
 * 2026/9/4
 */
public class TileData {
    public static final int NONE = 0;
    public static final int UPGRADE = 1;
    public static final int REMOVE = 2;

    private int value;
    private float x;
    private float y;
    private float fromX;
    private float fromY;
    private float targetX;
    private float targetY;
    private boolean allowMerge;
    // 0 none, 1 upgrade 2 remove
    private int option;

    public TileData(int value, float x, float y) {
        setValue(value);
        setPosition(x, y);
        setTargetPosition(x, y);
        enableMerge();
    }

    public void reset() {
        setValue(0);
        setOption(0);
        setPosition(0, 0);
        setTargetPosition(0, 0);
        enableMerge();
    }

    public void setTargetPosition(float targetX, float targetY) {
        this.fromX = this.x;
        this.fromY = this.y;
        this.targetX = targetX;
        this.targetY = targetY;
    }

    public void disableMerge() {
        setAllowMerge(false);
    }

    public void enableMerge() {
        setAllowMerge(true);
    }

    public void setAllowMerge(boolean bool) {
        allowMerge = bool;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public boolean isAllowMerge() {
        return allowMerge;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getTargetY() {
        return targetY;
    }

    public float getTargetX() {
        return targetX;
    }

    public float getFromY() {
        return fromY;
    }

    public float getFromX() {
        return fromX;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getOption() {
        return option;
    }

    public void setOption(int option) {
        this.option = option;
    }
}
