package cn.levey.bannerlib.manager;

import android.content.Context;
import android.view.View;

/**
 * An implementation of {@link ViewPagerLayoutManager}
 * which zooms the center item
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public class ScaleLayoutManager extends ViewPagerLayoutManager {

    private int itemSpace;
    private float itemScale;
    private float itemMoveSpeed;
    private float centerAlpha;
    private float sideAlpha;

    public ScaleLayoutManager(Context context, int itemSpace) {
        this(new Builder(context, itemSpace));
    }

    public ScaleLayoutManager(Context context, int itemSpace, int orientation) {
        this(new Builder(context, itemSpace).setOrientation(orientation));
    }

    public ScaleLayoutManager(Context context, int itemSpace, int orientation, boolean reverseLayout) {
        this(new Builder(context, itemSpace).setOrientation(orientation).setReverseLayout(reverseLayout));
    }

    public ScaleLayoutManager(Builder builder) {
        this(builder.context, builder.itemSpace, builder.minScale, builder.maxAlpha, builder.minAlpha,
                builder.orientation, builder.moveSpeed, builder.maxVisibleItemCount, builder.distanceToBottom,
                builder.reverseLayout);
    }

    private ScaleLayoutManager(Context context, int itemSpace, float itemScale, float centerAlpha, float sideAlpha,
                               int orientation, float itemMoveSpeed, int maxVisibleItemCount, int distanceToBottom,
                               boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        setDistanceToBottom(distanceToBottom);
        setMaxVisibleItemCount(maxVisibleItemCount);
        this.itemSpace = itemSpace;
        this.itemScale = itemScale;
        this.itemMoveSpeed = itemMoveSpeed;
        this.centerAlpha = centerAlpha;
        this.sideAlpha = sideAlpha;
    }

    public boolean isCanSwipeWhenSingle() {
        return super.isCanSwipeWhenSingle();
    }

    public void setCanSwipeWhenSingle(boolean canSwipeWhenSingle) {
        super.setCanSwipeWhenSingle(canSwipeWhenSingle);
    }


    public int getItemSpace() {
        return itemSpace;
    }

    public float getItemScale() {
        return itemScale;
    }

    public float getItemMoveSpeed() {
        return itemMoveSpeed;
    }

    public float getCenterAlpha() {
        return centerAlpha;
    }

    public float getSideAlpha() {
        return sideAlpha;
    }

    public void setItemSpace(int itemSpace) {
        assertNotInLayoutOrScroll(null);
        if (this.itemSpace == itemSpace) return;
        this.itemSpace = itemSpace;
        removeAllViews();
    }

    public void setItemScale(float itemScale) {
        assertNotInLayoutOrScroll(null);
        if (this.itemScale == itemScale) return;
        this.itemScale = itemScale;
        removeAllViews();
    }

    public void setCenterAlpha(float centerAlpha) {
        assertNotInLayoutOrScroll(null);
        if (centerAlpha > 1) centerAlpha = 1;
        if (this.centerAlpha == centerAlpha) return;
        this.centerAlpha = centerAlpha;
        requestLayout();
    }

    public void setSideAlpha(float sideAlpha) {
        assertNotInLayoutOrScroll(null);
        if (sideAlpha < 0) sideAlpha = 0;
        if (this.sideAlpha == sideAlpha) return;
        this.sideAlpha = sideAlpha;
        requestLayout();
    }

    public void setItemMoveSpeed(float itemMoveSpeed) {
        assertNotInLayoutOrScroll(null);
        if (this.itemMoveSpeed == itemMoveSpeed) return;
        this.itemMoveSpeed = itemMoveSpeed;
    }

    @Override
    protected float setInterval() {
        return itemSpace + mDecoratedMeasurement;
    }

    @Override
    protected void setItemViewProperty(View itemView, float targetOffset) {
        float scale = calculateScale(targetOffset + mSpaceMain);
        itemView.setScaleX(scale);
        itemView.setScaleY(scale);
        final float alpha = calAlpha(targetOffset);
        itemView.setAlpha(alpha);
    }

    private float calAlpha(float targetOffset) {
        final float offset = Math.abs(targetOffset);
        float alpha = (sideAlpha - centerAlpha) / mInterval * offset + centerAlpha;
        if (offset >= mInterval) alpha = sideAlpha;
        return alpha;
    }

    @Override
    protected float getDistanceRatio() {
        if (itemMoveSpeed == 0) return Float.MAX_VALUE;
        return 1 / itemMoveSpeed;
    }

    /**
     * @param x start positon of the view you want scale
     * @return the scale rate of current scroll mOffset
     */
    private float calculateScale(float x) {
        float deltaX = Math.abs(x - mSpaceMain);
        if (deltaX - mDecoratedMeasurement > 0) deltaX = mDecoratedMeasurement;
        return 1f - deltaX / mDecoratedMeasurement * (1f - itemScale);
    }

    public static class Builder {
        private static final float SCALE_RATE = 1f;
        private static final float DEFAULT_SPEED = 1f;
        private static float MIN_ALPHA = 1f;
        private static float MAX_ALPHA = 1f;

        private int itemSpace;
        private int orientation;
        private float minScale;
        private float moveSpeed;
        private float maxAlpha;
        private float minAlpha;
        private boolean reverseLayout;
        private Context context;
        private int maxVisibleItemCount;
        private int distanceToBottom;

        public Builder(Context context, int itemSpace) {
            this.itemSpace = itemSpace;
            this.context = context;
            orientation = HORIZONTAL;
            minScale = SCALE_RATE;
            this.moveSpeed = DEFAULT_SPEED;
            maxAlpha = MAX_ALPHA;
            minAlpha = MIN_ALPHA;
            reverseLayout = false;
            distanceToBottom = ViewPagerLayoutManager.INVALID_SIZE;
            maxVisibleItemCount = ViewPagerLayoutManager.DETERMINE_BY_MAX_AND_MIN;
        }

        public Builder setOrientation(int orientation) {
            this.orientation = orientation;
            return this;
        }

        public Builder setMinScale(float minScale) {
            this.minScale = minScale;
            return this;
        }

        public Builder setReverseLayout(boolean reverseLayout) {
            this.reverseLayout = reverseLayout;
            return this;
        }

        public Builder setMaxAlpha(float maxAlpha) {
            if (maxAlpha > 1) maxAlpha = 1;
            this.maxAlpha = maxAlpha;
            return this;
        }

        public Builder setMinAlpha(float minAlpha) {
            if (minAlpha < 0) minAlpha = 0;
            this.minAlpha = minAlpha;
            return this;
        }

        public Builder setMoveSpeed(float moveSpeed) {
            this.moveSpeed = moveSpeed;
            return this;
        }

        public Builder setMaxVisibleItemCount(int maxVisibleItemCount) {
            this.maxVisibleItemCount = maxVisibleItemCount;
            return this;
        }

        public Builder setDistanceToBottom(int distanceToBottom) {
            this.distanceToBottom = distanceToBottom;
            return this;
        }

        public ScaleLayoutManager build() {
            return new ScaleLayoutManager(this);
        }
    }
}

