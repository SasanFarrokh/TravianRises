package ir.cdesign.travianrises;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.*;
import android.view.animation.AnimationUtils;
import android.widget.OverScroller;
import android.widget.Scroller;

import java.util.List;

public class ZoomableViewGroup extends ViewGroup {

    private static final int INVALID_POINTER_ID = 1;
    private int mActivePointerId = INVALID_POINTER_ID;

    private float mScaleFactor = 1;
    private ScaleGestureDetector mScaleDetector;
    private Matrix mScaleMatrix = new Matrix();
    private Matrix mScaleMatrixInverse = new Matrix();

    private float mPosX;
    private float mPosY;
    private Matrix mTranslateMatrix = new Matrix();
    private Matrix mTranslateMatrixInverse = new Matrix();

    private float mLastTouchX;
    private float mLastTouchY;

    private float mFocusY;

    private float mFocusX;

    private float[] mInvalidateWorkingArray = new float[6];
    private float[] mDispatchTouchEventWorkingArray = new float[2];
    private float[] mOnTouchEventWorkingArray = new float[2];
    private Scroller mScroller;
    long mLastScroll;
    private boolean mTwoDScrollViewMovedFocus;
    private VelocityTracker mVelocityTracker;
    private int mTouchSlop;
    private int mMinimumVelocity;
    private int mMaximumVelocity;


    public ZoomableViewGroup(Context context) {
        super(context);
        mScroller = new Scroller(context);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        mTranslateMatrix.setTranslate(0, 0);
        mScaleMatrix.setScale(1, 1);
    }
    public ZoomableViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(context);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        mTranslateMatrix.setTranslate(0, 0);
        mScaleMatrix.setScale(1, 1);
    }

    public ZoomableViewGroup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mScroller = new Scroller(context);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        mTranslateMatrix.setTranslate(0, 0);
        mScaleMatrix.setScale(1, 1);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                child.layout(l, t, l+child.getMeasuredWidth(), t + child.getMeasuredHeight());
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
            }
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(mPosX, mPosY);
        canvas.scale(mScaleFactor, mScaleFactor, mFocusX, mFocusY);
        super.dispatchDraw(canvas);
        canvas.restore();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mDispatchTouchEventWorkingArray[0] = ev.getX();
        mDispatchTouchEventWorkingArray[1] = ev.getY();
        mDispatchTouchEventWorkingArray = screenPointsToScaledPoints(mDispatchTouchEventWorkingArray);
        ev.setLocation(mDispatchTouchEventWorkingArray[0],
                mDispatchTouchEventWorkingArray[1]);
        return super.dispatchTouchEvent(ev);
    }

    /**
     * Although the docs say that you shouldn't override this, I decided to do
     * so because it offers me an easy way to change the invalidated area to my
     * likening.
     */
    @Override
    public ViewParent invalidateChildInParent(int[] location, Rect dirty) {

        mInvalidateWorkingArray[0] = dirty.left;
        mInvalidateWorkingArray[1] = dirty.top;
        mInvalidateWorkingArray[2] = dirty.right;
        mInvalidateWorkingArray[3] = dirty.bottom;


        mInvalidateWorkingArray = scaledPointsToScreenPoints(mInvalidateWorkingArray);
        dirty.set(Math.round(mInvalidateWorkingArray[0]), Math.round(mInvalidateWorkingArray[1]),
                Math.round(mInvalidateWorkingArray[2]), Math.round(mInvalidateWorkingArray[3]));

        location[0] *= mScaleFactor;
        location[1] *= mScaleFactor;
        return super.invalidateChildInParent(location, dirty);
    }

    private float[] scaledPointsToScreenPoints(float[] a) {
        mScaleMatrix.mapPoints(a);
        mTranslateMatrix.mapPoints(a);
        return a;
    }

    private float[] screenPointsToScaledPoints(float[] a){
        mTranslateMatrixInverse.mapPoints(a);
        mScaleMatrixInverse.mapPoints(a);
        return a;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mOnTouchEventWorkingArray[0] = ev.getX();
        mOnTouchEventWorkingArray[1] = ev.getY();

        mOnTouchEventWorkingArray = scaledPointsToScreenPoints(mOnTouchEventWorkingArray);

        ev.setLocation(mOnTouchEventWorkingArray[0], mOnTouchEventWorkingArray[1]);
        mScaleDetector.onTouchEvent(ev);

        final int action = ev.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {

                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                final float x = ev.getX();
                final float y = ev.getY();

                mLastTouchX = x;
                mLastTouchY = y;

                // Save the ID of this pointer
                mActivePointerId = ev.getPointerId(0);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                // Find the index of the active pointer and fetch its position
                final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                final float x = ev.getX(pointerIndex);
                final float y = ev.getY(pointerIndex);

                final float dx = x - mLastTouchX;
                final float dy = y - mLastTouchY;

                mPosX += dx;
                mPosY += dy;
                mTranslateMatrix.preTranslate(dx, dy);
                mTranslateMatrix.invert(mTranslateMatrixInverse);

                mLastTouchX = x;
                mLastTouchY = y;

                invalidate();
                break;
            }

            case MotionEvent.ACTION_UP: {

                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int initialXVelocity = (int) velocityTracker.getXVelocity();
                int initialYVelocity = (int) velocityTracker.getYVelocity();
                if ((Math.abs(initialXVelocity) + Math.abs(initialYVelocity) > mMinimumVelocity) && getChildCount() > 0) {
                    fling(-initialXVelocity, -initialYVelocity);
                }
                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                // Extract the index of the pointer that left the touch sensor
                final int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = ev.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = ev.getX(newPointerIndex);
                    mLastTouchY = ev.getY(newPointerIndex);
                    mActivePointerId = ev.getPointerId(newPointerIndex);
                }
                break;
            }
        }
        return true;
    }
    public void fling(int velocityX, int velocityY) {
        if (getChildCount() > 0) {
            int height = getHeight() - getPaddingBottom() - getPaddingTop();
            int bottom = getChildAt(0).getHeight();
            int width = getWidth() - getPaddingRight() - getPaddingLeft();
            int right = getChildAt(0).getWidth();

            mScroller.fling(getScrollX(), getScrollY(), velocityX, velocityY, 0, right - width, 0, bottom - height);

            final boolean movingDown = velocityY > 0;
            final boolean movingRight = velocityX > 0;

            View newFocused = findFocusableViewInMyBounds(movingRight, mScroller.getFinalX(), movingDown, mScroller.getFinalY(), findFocus());
            if (newFocused == null) {
                newFocused = this;
            }

            if (newFocused != findFocus() && newFocused.requestFocus(movingDown ? View.FOCUS_DOWN : View.FOCUS_UP)) {
                mTwoDScrollViewMovedFocus = true;
                mTwoDScrollViewMovedFocus = false;
            }

            awakenScrollBars(mScroller.getDuration());
            invalidate();
        }
    }
    private View findFocusableViewInMyBounds(final boolean topFocus, final int top, final boolean leftFocus, final int left, View preferredFocusable) {
   /*
   * The fading edge's transparent side should be considered for focus
   * since it's mostly visible, so we divide the actual fading edge length
   * by 2.
   */
        final int verticalFadingEdgeLength = getVerticalFadingEdgeLength() / 2;
        final int topWithoutFadingEdge = top + verticalFadingEdgeLength;
        final int bottomWithoutFadingEdge = top + getHeight() - verticalFadingEdgeLength;
        final int horizontalFadingEdgeLength = getHorizontalFadingEdgeLength() / 2;
        final int leftWithoutFadingEdge = left + horizontalFadingEdgeLength;
        final int rightWithoutFadingEdge = left + getWidth() - horizontalFadingEdgeLength;

        if ((preferredFocusable != null)
                && (preferredFocusable.getTop() < bottomWithoutFadingEdge)
                && (preferredFocusable.getBottom() > topWithoutFadingEdge)
                && (preferredFocusable.getLeft() < rightWithoutFadingEdge)
                && (preferredFocusable.getRight() > leftWithoutFadingEdge)) {
            return preferredFocusable;
        }
        return findFocusableViewInBounds(topFocus, topWithoutFadingEdge, bottomWithoutFadingEdge, leftFocus, leftWithoutFadingEdge, rightWithoutFadingEdge);
    }
    private View findFocusableViewInBounds(boolean topFocus, int top, int bottom, boolean leftFocus, int left, int right) {
        List<View> focusables = getFocusables(View.FOCUS_FORWARD);
        View focusCandidate = null;

   /*
   * A fully contained focusable is one where its top is below the bound's
   * top, and its bottom is above the bound's bottom. A partially
   * contained focusable is one where some part of it is within the
   * bounds, but it also has some part that is not within bounds.  A fully contained
   * focusable is preferred to a partially contained focusable.
   */
        boolean foundFullyContainedFocusable = false;

        int count = focusables.size();
        for (int i = 0; i < count; i++) {
            View view = focusables.get(i);
            int viewTop = view.getTop();
            int viewBottom = view.getBottom();
            int viewLeft = view.getLeft();
            int viewRight = view.getRight();

            if (top < viewBottom && viewTop < bottom && left < viewRight && viewLeft < right) {
       /*
       * the focusable is in the target area, it is a candidate for
       * focusing
       */
                final boolean viewIsFullyContained = (top < viewTop) && (viewBottom < bottom) && (left < viewLeft) && (viewRight < right);
                if (focusCandidate == null) {
         /* No candidate, take this one */
                    focusCandidate = view;
                    foundFullyContainedFocusable = viewIsFullyContained;
                } else {
                    final boolean viewIsCloserToVerticalBoundary =
                            (topFocus && viewTop < focusCandidate.getTop()) ||
                                    (!topFocus && viewBottom > focusCandidate.getBottom());
                    final boolean viewIsCloserToHorizontalBoundary =
                            (leftFocus && viewLeft < focusCandidate.getLeft()) ||
                                    (!leftFocus && viewRight > focusCandidate.getRight());
                    if (foundFullyContainedFocusable) {
                        if (viewIsFullyContained && viewIsCloserToVerticalBoundary && viewIsCloserToHorizontalBoundary) {
             /*
              * We're dealing with only fully contained views, so
              * it has to be closer to the boundary to beat our
              * candidate
              */
                            focusCandidate = view;
                        }
                    } else {
                        if (viewIsFullyContained) {
             /* Any fully contained view beats a partially contained view */
                            focusCandidate = view;
                            foundFullyContainedFocusable = true;
                        } else if (viewIsCloserToVerticalBoundary && viewIsCloserToHorizontalBoundary) {
             /*
              * Partially contained view beats another partially
              * contained view if it's closer
              */
                            focusCandidate = view;
                        }
                    }
                }
            }
        }
        return focusCandidate;
    }
    public final void smoothScrollBy(int dx, int dy) {

        long duration = AnimationUtils.currentAnimationTimeMillis() - mLastScroll;
        if (duration > 250) {
            mScroller.startScroll(getScrollX(), getScrollY(), dx, dy);
            awakenScrollBars(mScroller.getDuration());
            invalidate();
        } else {
            if (!mScroller.isFinished()) {
                mScroller.abortAnimation();
            }
            scrollBy(dx, dy);
        }
        mLastScroll = AnimationUtils.currentAnimationTimeMillis();
    }
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int oldX = getScrollX();
            int oldY = getScrollY();
            int x = mScroller.getCurrX();
            int y = mScroller.getCurrY();
            if (getChildCount() > 0) {
                View child = getChildAt(0);
                scrollTo(clamp(x, getWidth() - getPaddingRight() - getPaddingLeft(), child.getWidth()),
                        clamp(y, getHeight() - getPaddingBottom() - getPaddingTop(), child.getHeight()));
            } else {
                scrollTo(x, y);
            }
            if (oldX != getScrollX() || oldY != getScrollY()) {
                onScrollChanged(getScrollX(), getScrollY(), oldX, oldY);
            }

            // Keep on drawing until the animation has finished.
            postInvalidate();
        }
    }
    private int clamp(int n, int my, int child) {
        if (my >= child || n < 0) {
            return 0;
        }
        if ((my+n) > child) {
            return child-my;
        }
        return n;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            if (detector.isInProgress()) {
                mFocusX = detector.getFocusX();
                mFocusY = detector.getFocusY();
            }

            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));
            mScaleMatrix.setScale(mScaleFactor, mScaleFactor,
                    mFocusX, mFocusY);

            mScaleMatrix.invert(mScaleMatrixInverse);
            invalidate();
            requestLayout();


            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            super.onScaleEnd(detector);
            mScaleMatrix.setScale(mScaleFactor,mScaleFactor,mFocusX,mFocusY);
        }
    }
}