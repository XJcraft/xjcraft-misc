package org.xjcraft.misc.feature;

import lombok.Getter;

/**
 * 特性
 */
public abstract class AFeature {
    /**
     * 是否启用此特性
     */
    @Getter
    private boolean enable = false;

    /**
     * 启用特性
     */
    public final void enableFeature() {
        if (this.enable) { // 不完全可靠，但非得作死的话 hmmmm，谁管它呢
            return;
        }

        this.enable = true;
        this.enable();
    }

    /**
     * 启用特性
     */
    public abstract void enable();
}
