package me.mgottein;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Layout;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by mgottein on 3/16/15.
 *
 * Adds line numbers to a {@see android.widget.TextView} inside the padding
 */
public class LineNumberTextView extends TextView {

    /**
     * Delegate interface that controls display of line numbers
     */
    public interface Controller {
        /**
         * @param layoutOnLeft is the line number displayed in the left column
         * @param line line number
         * @return formatted line number string to be displayed
         */
        public String getLineNumberText(boolean layoutOnLeft, int line);

        /**
         * @param line line number
         * @return if this line number will be shown
         */
        public boolean showLineNumber(int line);
    }

    private Paint mTextPaint;
    private int mLeftPadding;
    private int mRightPadding;
    private boolean mLayoutOnLeft;
    private boolean mHugLine;
    private Controller mController;
    //save the padding we add for the line numbers
    private int mCachedLineNumberPadding;

    public LineNumberTextView(Context context) {
        super(context);
        init(null);
    }

    public LineNumberTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public LineNumberTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LineNumberTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        mTextPaint = new Paint();
        mTextPaint.setColor(Color.GRAY);
        mTextPaint.setTypeface(getTypeface());
        mTextPaint.setTextSize(getTextSize());
        mTextPaint.setAntiAlias(true);
        mTextPaint.setSubpixelText(true);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mController = getDefaultLineNumberController();
        mLeftPadding = getPaddingLeft();
        mRightPadding = getPaddingRight();
        mCachedLineNumberPadding = 0;
        
        mLayoutOnLeft = true;
        mHugLine = false;
        fixLineNumberPadding();
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        if(mTextPaint == null) {
            //HACK: this gets called before the constructor finishes
            post(new Runnable() {
                @Override
                public void run() {
                    fixLineNumberPadding();
                }
            });
        } else {
            fixLineNumberPadding();
        }
    }

    @Override
    public boolean isPaddingOffsetRequired() {
        return true;
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        boolean fixPadding = mLeftPadding != left || mRightPadding != right;
        mLeftPadding = left;
        mRightPadding = right;
        if(fixPadding) {
            fixLineNumberPadding();
        }
    }

    /**
     * Change how line numbers are laid out
     * @param layoutLineNumbersOnLeft if line numbers should be placed in the left column
     */
    public void layoutLineNumbersOnLeft(boolean layoutLineNumbersOnLeft) {
        boolean oldLayoutDirection = mLayoutOnLeft;
        mLayoutOnLeft = layoutLineNumbersOnLeft;
        if(oldLayoutDirection != mLayoutOnLeft) {
            fixLineNumberPadding();
        }
    }

    /*
     * @return if line numbers should be placed in the left column
     */
    public boolean layoutLineNumbersOnLeft() {
        return mLayoutOnLeft;
    }

    /**
     * Change the controller used to display line numbers
     * @param controller
     */
    public void setLineNumberController(Controller controller) {
        if(controller == null) {
            throw new IllegalStateException("controller must not be null");
        }
        mController = controller;
        invalidate();
    }

    /**
     * @return get the controller used to display line numbers
     */
    public Controller getLineNumberController() {
        return mController;
    }

    /**
     * @return if line numbers are hugging the line or in the left or right column
     */
    public boolean doLineNumbersHugLine() {
        return mHugLine;
    }

    /**
     * Set if line numbers hug the line or are in the left or right column
     * @param hugLine
     */
    public void doLineNumbersHugLine(boolean hugLine) {
        boolean doInvalidate = hugLine != mHugLine;
        mHugLine = hugLine;
        if(doInvalidate) {
            invalidate();
        }
    }

    /**
     * Change line number color
     * @param color
     */
    public void setLineNumberColor(int color) {
        boolean doInvalidate = color != mTextPaint.getColor();
        mTextPaint.setColor(color);
        if(doInvalidate) {
            invalidate();
        }
    }

    /**
     * Change line number typeface
     * @param typeface
     */
    public void setLineNumberTypeface(Typeface typeface) {
        boolean doInvalidate = typeface != mTextPaint.getTypeface();
        mTextPaint.setTypeface(typeface);
        if(doInvalidate) {
            invalidate();
        }
    }

    /**
     * Change line number size (in px, uses {@see android.graphics.Paint#setTextSize(int)})
     * @param size
     */
    public void setLineNumberSize(int size) {
        boolean doInvalidate = size != mTextPaint.getTextSize();
        mTextPaint.setTextSize(size);
        if(doInvalidate) {
            invalidate();
        }
    }

    /**
     * @return the default controller used to display line numbers
     */
    protected Controller getDefaultLineNumberController() {
        return new Controller() {
            @Override
            public String getLineNumberText(boolean layoutOnLeft, int line) {
                return Integer.toString(line);
            }

            @Override
            public boolean showLineNumber(int line) {
                return true;
            }
        };
    }

    /**
     * Setup a slightly modified version of the clipping bounds {@see android.widget.TextView} uses
     * @param canvas
     */
    private void textViewClip(Canvas canvas) {
        int scrollX = getScrollX(), scrollY = getScrollY(),
                left = getLeft(), right = getRight(),
                top = getTop(), bottom = getBottom();
        int maxScrollY = getLayout().getHeight() - bottom - top - getCompoundPaddingBottom() - getCompoundPaddingTop();

        //ignore left and right padding
        float clipLeft = scrollX;
        float clipTop = (scrollY == 0) ? 0 : getExtendedPaddingTop() + scrollY;
        float clipRight = right - left + scrollX;
        float clipBottom = bottom - top + scrollY -
                ((scrollY == maxScrollY) ? 0 : getExtendedPaddingBottom());

        //account for shadow if it exists
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            float shadowRadius = getShadowRadius();
            if (shadowRadius != 0) {
                float shadowDx = getShadowDx(), shadowDy = getShadowDy();
                clipLeft += Math.min(0, shadowDx - shadowRadius);
                clipRight += Math.max(0, shadowDx + shadowRadius);

                clipTop += Math.min(0, shadowDy - shadowRadius);
                clipBottom += Math.max(0, shadowDy + shadowRadius);
            }
        }

        canvas.clipRect(clipLeft, clipTop, clipRight, clipBottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        textViewClip(canvas);

        Layout layout = getLayout();


        int scrollY = getScrollY();
        //only display all the line numbers between the first and last line
        int firstLine = layout.getLineForVertical(scrollY),
                lastLine = layout.getLineForVertical(scrollY + (getHeight() - getExtendedPaddingTop() - getExtendedPaddingBottom()));

        //the y position starts at the baseline of the first line
        int positionY = getBaseline() + (layout.getLineBaseline(firstLine) - layout.getLineBaseline(0));
        drawLineNumber(canvas, layout, positionY, firstLine);
        
        int lineNumber = firstLine + 1;
        for(int i = firstLine + 1; i <= lastLine; i++) {
            //get the next y position using the difference between the current and last baseline
            positionY += layout.getLineBaseline(i) - layout.getLineBaseline(i - 1);
            int lastIndex = layout.getLineEnd(i - 1) - 1;
            String previousLine = this.getText().toString();
            if ( (lastIndex >= previousLine.length()) || (previousLine.charAt(lastIndex) == '\n')) {
                drawLineNumber(canvas, layout, positionY, lineNumber);
                lineNumber++;
            }
        }
        canvas.restore();
    }

    private void drawLineNumber(Canvas canvas, Layout layout, int positionY, int line) {
        if (mController.showLineNumber(line + 1)) {
            int positionX = getLineNumberX(layout, line);
            canvas.drawText(mController.getLineNumberText(mLayoutOnLeft, line + 1), positionX, positionY, mTextPaint);
        }
    }

    //Whenever the padding changes (or is set up for the first time) we need to add enough padding to the correct side to show our line numbers
    private void fixLineNumberPadding() {
        mCachedLineNumberPadding = getLineNumberPadding();
        if(mLayoutOnLeft) {
            super.setPadding(mLeftPadding + mCachedLineNumberPadding, getPaddingTop(), mRightPadding, getPaddingBottom());
        } else {
            super.setPadding(mLeftPadding, getPaddingTop(), mRightPadding + mCachedLineNumberPadding, getPaddingBottom());
        }
        invalidate();
    }

    //the line number padding is calculated as the width of the largest line number text
    private int getLineNumberPadding() {
        Layout layout = getLayout();
        int lineCount = layout != null ? layout.getLineCount() : 1;
        return (int) mTextPaint.measureText(mController.getLineNumberText(mLayoutOnLeft, lineCount));
    }

    //get the x coordinate of a line number
    private int getLineNumberX(Layout layout, int line) {
        //hugging a line means we display as close to the line as possible
        if(mLayoutOnLeft) {
            //padding offset ignores the padding we set - we are displaying content inside that padding
            int leftColumn = getLeftPaddingOffset();
            if(mHugLine) {
                int lineLeft = (int) layout.getLineLeft(line);
                return Math.max(leftColumn, lineLeft - mLeftPadding);
            } else {
                return leftColumn;
            }
        } else {
            //getRightPaddingOffset() returns a - number
            int rightColumn = getWidth() + getRightPaddingOffset() - mCachedLineNumberPadding;
            if(mHugLine) {
                int lineRight = (int) layout.getLineRight(line);
                return Math.min(rightColumn, lineRight + getCompoundPaddingLeft() + mRightPadding);
            } else {
                return rightColumn;
            }
        }
    }
}
