package com.sgottard.tvdemoapp;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

public class CustomFrameLayout extends FrameLayout {

	public interface OnFocusSearchListener {
		public View onFocusSearch(View focused, int direction);
	}

	public interface OnChildFocusListener {
		public boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect);
		public void onRequestChildFocus(View child, View focused);
	}

	public CustomFrameLayout(Context context) {
		this(context, null, 0);
	}

	public CustomFrameLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CustomFrameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	private OnFocusSearchListener mListener;
	private OnChildFocusListener mOnChildFocusListener;

	public void setOnFocusSearchListener(OnFocusSearchListener listener) {
		mListener = listener;
	}

	public void setOnChildFocusListener(OnChildFocusListener listener) {
		mOnChildFocusListener = listener;
	}

	@Override
	protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
		if (mOnChildFocusListener != null) {
			return mOnChildFocusListener.onRequestFocusInDescendants(direction, previouslyFocusedRect);
		}
		return super.onRequestFocusInDescendants(direction, previouslyFocusedRect);
	}

	@Override
	public View focusSearch(View focused, int direction) {
		if (mListener != null) {
			View view = mListener.onFocusSearch(focused, direction);
			if (view != null) {
				return view;
			}
		}
		return super.focusSearch(focused, direction);
	}

	@Override
	public void requestChildFocus(View child, View focused) {
		super.requestChildFocus(child, focused);
		if (mOnChildFocusListener != null) {
			mOnChildFocusListener.onRequestChildFocus(child, focused);
		}
	}
}

