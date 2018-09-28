package com.app.tuan88291.testapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnItemTouchListener;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

public class RecyclerItemClickListener implements OnItemTouchListener {
    GestureDetector mGestureDetector;
    private OnItemClickListener mListener;

    /* renamed from: com.app.tuan88291.testapp.RecyclerItemClickListener.1 */
    class C03371 extends SimpleOnGestureListener {
        final /* synthetic */ RecyclerView val$recyclerView;

        C03371(RecyclerView recyclerView) {
            this.val$recyclerView = recyclerView;
        }

        public boolean onSingleTapUp(MotionEvent e) {
            return true;
        }

        public void onLongPress(MotionEvent e) {
            View child = this.val$recyclerView.findChildViewUnder(e.getX(), e.getY());
            if (child != null && RecyclerItemClickListener.this.mListener != null) {
                RecyclerItemClickListener.this.mListener.onLongItemClick(child, this.val$recyclerView.getChildAdapterPosition(child));
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int i);

        void onLongItemClick(View view, int i);
    }

    public RecyclerItemClickListener(Context context, RecyclerView recyclerView, OnItemClickListener listener) {
        this.mListener = listener;
        this.mGestureDetector = new GestureDetector(context, new C03371(recyclerView));
    }

    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView == null || this.mListener == null || !this.mGestureDetector.onTouchEvent(e)) {
            return false;
        }
        this.mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
        return true;
    }

    public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
    }

    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }
}
