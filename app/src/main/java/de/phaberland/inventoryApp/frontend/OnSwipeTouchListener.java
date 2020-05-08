/*
 * Copyright 2020 Peter Haberland
 *
 * No licensing, you may use/alter that code as you wish.
 */

package de.phaberland.inventoryApp.frontend;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * OnSwipeTouchListener is a special OnTouchListener
 * which calculates from touches if the user swiped
 * left or right.
 * When using this class onSwipeLeft and
 * onSwipeRight need to be implemented.
 *
 * @author      Peter Haberland
 * @version     %I%, %G%
 */
class OnSwipeTouchListener implements OnTouchListener {

    private final GestureDetector gestureDetector;

    /**
     * creates a new instance of a GestureDetector and assigns
     * it to the member
     * @param ctx context to be forwarded to GestureDetector
     */
    OnSwipeTouchListener(Context ctx){
        gestureDetector = new GestureDetector(ctx, new GestureListener());
    }

    /**
     * checks the touch event and detects gestures and clicks
     * @param v a view that was touched
     * @param event the touch event
     * @return the result of onTouchEvent from GestureDetector
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            v.performClick();
        }
        return gestureDetector.onTouchEvent(event);
    }

    /**
     * inner class used to decide if a touch event was a swipe
     * and tries to determine if it was a right or left swipe.
     */
    private final class GestureListener extends SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                // only care about legt and right swipe
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                        result = true;
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }

    /**
     * needs to be implemented in instance.
     * User swiped right.
     */
    void onSwipeRight() { }

    /**
     * needs to be implemented in instance.
     * User swiped left.
     */
    void onSwipeLeft() { }
}
