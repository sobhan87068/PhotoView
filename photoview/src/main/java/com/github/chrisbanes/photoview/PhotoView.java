/*
 Copyright 2011, 2012 Chris Banes.
 <p>
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 <p>
 http://www.apache.org/licenses/LICENSE-2.0
 <p>
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package com.github.chrisbanes.photoview;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatImageView;

/**
 * A zoomable ImageView. See {@link PhotoViewAttacher} for most of the details on how the zooming
 * is accomplished
 */
@SuppressWarnings("unused")
public class PhotoView extends AppCompatImageView {

    private PhotoViewAttacher attacher;
    private ScaleType pendingScaleType;
    private GestureDetector.OnDoubleTapListener onDoubleTapListener;
    private PhotoView boundView;

    public PhotoView(Context context) {
        this(context, null);
    }

    public PhotoView(Context context, AttributeSet attr) {
        this(context, attr, 0);
    }

    public PhotoView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        init();
    }

    private void init() {
        attacher = new PhotoViewAttacher(this);
        //We always pose as a Matrix scale type, though we can change to another scale type
        //via the attacher
        super.setScaleType(ScaleType.MATRIX);
        //apply the previously applied scale type
        if (pendingScaleType != null) {
            setScaleType(pendingScaleType);
            pendingScaleType = null;
        }
    }

    private void setAttacher(PhotoViewAttacher attacher) {
        this.attacher.setBoundListener(attacher.getOnGestureListener());

        final OnClickListener oldListener = this.attacher.getOnClickListener();
        final OnClickListener boundListener = attacher.getOnClickListener();
        this.attacher.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                oldListener.onClick(v);
                boundListener.onClick(v);
            }
        });

        final GestureDetector.OnDoubleTapListener oldOnDoubleTapListener = getOnDoubleTapListener();
        final GestureDetector.OnDoubleTapListener boundOnDoubleTapListener = getOnDoubleTapListener();
        this.attacher.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (oldOnDoubleTapListener != null && boundOnDoubleTapListener != null) {
                    boolean res = oldOnDoubleTapListener.onSingleTapConfirmed(e);
                    boundOnDoubleTapListener.onSingleTapConfirmed(e);
                    return res;
                }
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (oldOnDoubleTapListener != null && boundOnDoubleTapListener != null) {
                    boolean res = oldOnDoubleTapListener.onDoubleTap(e);
                    boundOnDoubleTapListener.onDoubleTap(e);
                    return res;
                }
                return false;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                if (oldOnDoubleTapListener != null && boundOnDoubleTapListener != null) {
                    boolean res = oldOnDoubleTapListener.onDoubleTapEvent(e);
                    boundOnDoubleTapListener.onDoubleTapEvent(e);
                    return res;
                }
                return false;
            }
        });

        final OnLongClickListener oldOnLongClickListener = this.attacher.getOnLongClickListener();
        final OnLongClickListener boundOnLongClickListener = attacher.getOnLongClickListener();
        this.attacher.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (oldOnLongClickListener != null && boundOnLongClickListener != null) {
                    boolean res = oldOnLongClickListener.onLongClick(v);
                    boundOnLongClickListener.onLongClick(v);
                    return res;
                }
                return false;
            }
        });

        final OnMatrixChangedListener oldOnMatrixChangedListener = this.attacher.getonMatrixChangeListener();
        final OnMatrixChangedListener boundOnMatrixChangedListener = attacher.getonMatrixChangeListener();
        this.attacher.setOnMatrixChangeListener(new OnMatrixChangedListener() {
            @Override
            public void onMatrixChanged(RectF rect) {
                if (oldOnMatrixChangedListener != null && boundOnMatrixChangedListener != null) {
                    oldOnMatrixChangedListener.onMatrixChanged(rect);
                    boundOnMatrixChangedListener.onMatrixChanged(rect);
                }
            }
        });

        final OnOutsidePhotoTapListener oldOnOutsidePhotoTapListener = this.attacher.getOutsidePhotoTapListener();
        final OnOutsidePhotoTapListener boundOnOutsidePhotoTapListener = attacher.getOutsidePhotoTapListener();
        this.attacher.setOnOutsidePhotoTapListener(new OnOutsidePhotoTapListener() {
            @Override
            public void onOutsidePhotoTap(ImageView imageView) {
                if (oldOnOutsidePhotoTapListener != null && boundOnOutsidePhotoTapListener != null) {
                    oldOnOutsidePhotoTapListener.onOutsidePhotoTap(imageView);
                    boundOnOutsidePhotoTapListener.onOutsidePhotoTap(imageView);
                }
            }
        });

        final OnPhotoTapListener oldOnPhotoTapListener = this.attacher.getOnPhotoTapListener();
        final OnPhotoTapListener boundOnPhotoTapListener = attacher.getOnPhotoTapListener();
        this.attacher.setOnPhotoTapListener(new OnPhotoTapListener() {
            @Override
            public void onPhotoTap(ImageView view, float x, float y) {
                if (oldOnPhotoTapListener != null && boundOnPhotoTapListener != null) {
                    oldOnPhotoTapListener.onPhotoTap(view, x, y);
                    boundOnPhotoTapListener.onPhotoTap(view, x, y);
                }
            }
        });

        final OnScaleChangedListener oldOnScaleChangedListener = this.attacher.getOnScaleChangeListener();
        final OnScaleChangedListener boundOnScaleChangedListener = attacher.getOnScaleChangeListener();
        this.attacher.setOnScaleChangeListener(new OnScaleChangedListener() {
            @Override
            public void onScaleChange(float scaleFactor, float focusX, float focusY) {
                if (oldOnScaleChangedListener != null && boundOnScaleChangedListener != null) {
                    oldOnScaleChangedListener.onScaleChange(scaleFactor, focusX, focusY);
                    boundOnScaleChangedListener.onScaleChange(scaleFactor, focusX, focusY);
                }
            }
        });

        final OnSingleFlingListener oldOnSingleFlingListener = this.attacher.getOnSingleFlingListener();
        final OnSingleFlingListener boundOnSingleFlingListener = this.attacher.getOnSingleFlingListener();
        this.attacher.setOnSingleFlingListener(new OnSingleFlingListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (oldOnSingleFlingListener != null && boundOnSingleFlingListener != null) {
                    boolean res = oldOnSingleFlingListener.onFling(e1, e2, velocityX, velocityY);
                    boundOnSingleFlingListener.onFling(e1, e2, velocityX, velocityY);
                    return res;
                }
                return false;
            }
        });

        final OnViewDragListener oldOnViewDragListener = this.attacher.getOnViewDragListener();
        final OnViewDragListener boundOnViewDragListener = this.attacher.getOnViewDragListener();
        this.attacher.setOnViewDragListener(new OnViewDragListener() {
            @Override
            public void onDrag(float dx, float dy) {
                if (oldOnViewDragListener != null && boundOnViewDragListener != null) {
                    oldOnViewDragListener.onDrag(dx, dy);
                    boundOnViewDragListener.onDrag(dx, dy);
                }
            }
        });

        final OnViewTapListener oldOnViewTapListener = this.attacher.getOnViewTapListener();
        final OnViewTapListener boundOnViewTapListener = this.attacher.getOnViewTapListener();
        this.attacher.setOnViewTapListener(new OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                if (oldOnMatrixChangedListener != null && boundOnMatrixChangedListener != null) {
                    oldOnViewTapListener.onViewTap(view, x, y);
                    boundOnViewTapListener.onViewTap(view, x, y);
                }
            }
        });
    }

    /**
     * Get the current {@link PhotoViewAttacher} for this view. Be wary of holding on to references
     * to this attacher, as it has a reference to this view, which, if a reference is held in the
     * wrong place, can cause memory leaks.
     *
     * @return the attacher.
     */
    public PhotoViewAttacher getAttacher() {
        return attacher;
    }

    public void bindPhotoView(PhotoView photoView) {
        boundView = photoView;
        setAttacher(photoView.getAttacher());
    }

    @Override
    public ScaleType getScaleType() {
        return attacher.getScaleType();
    }

    @Override
    public Matrix getImageMatrix() {
        return attacher.getImageMatrix();
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        attacher.setOnLongClickListener(l);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        attacher.setOnClickListener(l);
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (attacher == null) {
            pendingScaleType = scaleType;
        } else {
            attacher.setScaleType(scaleType);
        }
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        // setImageBitmap calls through to this method
        if (attacher != null) {
            attacher.update();
        }
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        if (attacher != null) {
            attacher.update();
        }
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        if (attacher != null) {
            attacher.update();
        }
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        boolean changed = super.setFrame(l, t, r, b);
        if (changed) {
            attacher.update();
        }
        return changed;
    }

    public void setRotationTo(float rotationDegree) {
        attacher.setRotationTo(rotationDegree);
    }

    public void setRotationBy(float rotationDegree) {
        attacher.setRotationBy(rotationDegree);
    }

    public boolean isZoomable() {
        return attacher.isZoomable();
    }

    public void setZoomable(boolean zoomable) {
        attacher.setZoomable(zoomable);
    }

    public RectF getDisplayRect() {
        return attacher.getDisplayRect();
    }

    public void getDisplayMatrix(Matrix matrix) {
        attacher.getDisplayMatrix(matrix);
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean setDisplayMatrix(Matrix finalRectangle) {
        return attacher.setDisplayMatrix(finalRectangle);
    }

    public void getSuppMatrix(Matrix matrix) {
        attacher.getSuppMatrix(matrix);
    }

    public boolean setSuppMatrix(Matrix matrix) {
        return attacher.setDisplayMatrix(matrix);
    }

    public float getMinimumScale() {
        return attacher.getMinimumScale();
    }

    public float getMediumScale() {
        return attacher.getMediumScale();
    }

    public float getMaximumScale() {
        return attacher.getMaximumScale();
    }

    public float getScale() {
        return attacher.getScale();
    }

    public void setAllowParentInterceptOnEdge(boolean allow) {
        attacher.setAllowParentInterceptOnEdge(allow);
    }

    public void setMinimumScale(float minimumScale) {
        attacher.setMinimumScale(minimumScale);
    }

    public void setMediumScale(float mediumScale) {
        attacher.setMediumScale(mediumScale);
    }

    public void setMaximumScale(float maximumScale) {
        attacher.setMaximumScale(maximumScale);
    }

    public void setScaleLevels(float minimumScale, float mediumScale, float maximumScale) {
        attacher.setScaleLevels(minimumScale, mediumScale, maximumScale);
    }

    public void setOnMatrixChangeListener(OnMatrixChangedListener listener) {
        attacher.setOnMatrixChangeListener(listener);
    }

    public void setOnPhotoTapListener(OnPhotoTapListener listener) {
        attacher.setOnPhotoTapListener(listener);
    }

    public void setOnOutsidePhotoTapListener(OnOutsidePhotoTapListener listener) {
        attacher.setOnOutsidePhotoTapListener(listener);
    }

    public void setOnViewTapListener(OnViewTapListener listener) {
        attacher.setOnViewTapListener(listener);
    }

    public void setOnViewDragListener(OnViewDragListener listener) {
        attacher.setOnViewDragListener(listener);
    }

    public void setScale(float scale) {
        attacher.setScale(scale);
    }

    public void setScale(float scale, boolean animate) {
        attacher.setScale(scale, animate);
    }

    public void setScale(float scale, float focalX, float focalY, boolean animate) {
        attacher.setScale(scale, focalX, focalY, animate);
    }

    public void setZoomTransitionDuration(int milliseconds) {
        attacher.setZoomTransitionDuration(milliseconds);
    }

    public GestureDetector.OnDoubleTapListener getOnDoubleTapListener() {
        return onDoubleTapListener;
    }

    public void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener onDoubleTapListener) {
        attacher.setOnDoubleTapListener(onDoubleTapListener);
        this.onDoubleTapListener = onDoubleTapListener;
    }

    public void setOnScaleChangeListener(OnScaleChangedListener onScaleChangedListener) {
        attacher.setOnScaleChangeListener(onScaleChangedListener);
    }

    public void setOnSingleFlingListener(OnSingleFlingListener onSingleFlingListener) {
        attacher.setOnSingleFlingListener(onSingleFlingListener);
    }
}
