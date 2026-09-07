package xyz.zzzxb.toolkit.entity;

import com.badlogic.gdx.math.Interpolation;

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
    private int option;

    // 合并动画
    private float scale = 1f;
    private boolean isMerging = false;
    private float mergeProgress = 0f;
    private boolean isBeingRemoved = false;  // 被合并的方块缩小消失

    public TileData(int value, float x, float y) {
        setValue(value);
        setPosition(x, y);
        setTargetPosition(x, y);
        enableMerge();
    }

    public void reset() {
        scale = 1f;
        isMerging = false;
        mergeProgress = 0f;
        isBeingRemoved = false;
        setValue(0);
        setOption(0);
        setPosition(0, 0);
        setTargetPosition(0, 0);
        enableMerge();
    }

    public void startMergeAnimation() {
        this.isMerging = true;
        this.mergeProgress = 0f;
    }

    public void startRemoveAnimation() {
        this.isBeingRemoved = true;
        this.isMerging = true;
        this.mergeProgress = 0f;
        this.scale = 1f;
    }

    public void updateMergeAnimation(float delta) {
        if (!isMerging) return;

        mergeProgress += delta / 0.15f;
        if (mergeProgress >= 1f) {
            mergeProgress = 1f;
            isMerging = false;
            scale = 1f;
            return;
        }

        if (isBeingRemoved) {
            // 被合并的方块：缩小的同时淡出（scale 0.8 时消失）
            scale = 1f - 0.2f * Interpolation.smooth.apply(mergeProgress);
            if (scale < 0.01f) scale = 0.01f;
        } else {
            // 升级的方块：放大再弹回
            if (mergeProgress < 0.5f) {
                float p = mergeProgress / 0.5f;
                scale = 1f + 0.3f * Interpolation.smooth.apply(p);
            } else {
                float p = (mergeProgress - 0.5f) / 0.5f;
                scale = 1.3f - 0.3f * Interpolation.smooth.apply(p);
            }
        }
    }

    public float getScale() { return scale; }
    public boolean isMerging() { return isMerging; }
    public boolean isBeingRemoved() { return isBeingRemoved; }

    public void setTargetPosition(float targetX, float targetY) {
        this.fromX = this.x;
        this.fromY = this.y;
        this.targetX = targetX;
        this.targetY = targetY;
    }

    public void disableMerge() { setAllowMerge(false); }
    public void enableMerge() { setAllowMerge(true); }
    public void setAllowMerge(boolean bool) { allowMerge = bool; }
    public void setPosition(float x, float y) { this.x = x; this.y = y; }

    public boolean isAllowMerge() { return allowMerge; }
    public float getX() { return x; }
    public float getY() { return y; }
    public float getTargetY() { return targetY; }
    public float getTargetX() { return targetX; }
    public float getFromY() { return fromY; }
    public float getFromX() { return fromX; }
    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }
    public int getOption() { return option; }
    public void setOption(int option) { this.option = option; }
}
