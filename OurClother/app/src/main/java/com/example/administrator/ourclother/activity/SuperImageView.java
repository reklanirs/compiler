package com.example.administrator.ourclother.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.widget.ImageView;

public class SuperImageView extends ImageView {

    PointF Center_postion= new PointF();
    PointF LeftTop_postion= new PointF();
	static final float MAX_SCALE = 2.0f;


	float imageW;//图片宽高
	float imageH;
	float rotatedImageW;
	float rotatedImageH;
	float viewW;//显示宽高
	float viewH;
	Matrix matrix = new Matrix();//矩阵

    Matrix matrixchange = new Matrix();
    Matrix savedmatrixchange = new Matrix();

	Matrix savedMatrix = new Matrix();
	static final int NONE = 0;// 初始状态
	static final int DRAG = 1;// 拖动
	static final int ZOOM = 2;// 缩放
	static final int ROTATE = 3;// 旋转
	static final int ZOOM_OR_ROTATE = 4; // 缩放或旋转
	int mode = NONE;

	PointF pA = new PointF();//坐标A
	PointF pB = new PointF();//坐标B
	PointF mid = new PointF();//中心坐标是两指的中心位置（注意）
	PointF lastClickPos = new PointF();//最后点击的位置
	long lastClickTime = 0;//最后点击时间？？
	double rotation = 0.0;//旋转角度？？
	float dist = 1f;//两点间距

	public SuperImageView(Context context) {
		super(context);
		init();
	}
	public SuperImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public SuperImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

    //设置为矩阵变换
	private void init() {
		setScaleType(ScaleType.MATRIX);
	}

	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
        setImageWidthHeight();
	}

	public void setImageDrawable(Drawable drawable) {
		super.setImageDrawable(drawable);
        setImageWidthHeight();
	}

	public void setImageResource(int resId) {
		super.setImageResource(resId);
		setImageWidthHeight();
	}
    //设置图片宽高
	private void setImageWidthHeight() {
		Drawable d = getDrawable();
		if (d == null) {
			return;
		}
		imageW = rotatedImageW = d.getIntrinsicWidth();
		imageH = rotatedImageH = d.getIntrinsicHeight();
		initImage();
	}
    //  尺寸改变---------------------------------------------------------------！！！
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		viewW = w;
		viewH = h;
		if (oldw == 0) {
            initImage();
        }
	}
    //图片初始化
	private void initImage() {
        //如果图片载入无效，退出
		if (viewW <= 0 || viewH <= 0 || imageW <= 0 || imageH <= 0) {
			return;
		}
		mode = NONE;
		matrix.setScale(0, 0);//不进行缩放
 //       matrixchange.set(matrix);//获取矩阵
        fixScale();
		fixTranslation();
		setImageMatrix(matrix);

	}
//------------------------------------------------------------------------------------------------//
    //固定尺寸
	private void fixScale() {
		float p[] = new float[9];//9个浮点数
		matrix.getValues(p);//获取图片九个点的值

        float curScale = Math.abs(p[0]) + Math.abs(p[1]);//图片刚加载时值恒为0
		float minScale = Math.min((float) viewW / (float) rotatedImageW,
				(float) viewH / (float) rotatedImageH);
    	matrix.setScale(minScale, minScale);
    }
    //固定:调用它的话，缩小图片时无法进行移动
	private void fixTranslation() {
        //从（0，0）到 (imageW, imageH)的矩形
        RectF rect = new RectF(0,0, imageW, imageH);
        //获取图片左上角的位置
        matrix.mapRect(rect);

        //获取图片的长宽
        float height = rect.height();
        float width = rect.width();

        float deltaX = 0, deltaY = 0;

        if (width < viewW) {
            deltaX = (viewW - width) / 2 - rect.left;
        } else if (rect.left > 0) {
            deltaX = -rect.left;
        } else if (rect.right < viewW) {
            deltaX = viewW - rect.right;
        }

        if (height < viewH) {
            deltaY = (viewH - height) / 2 - rect.top;
        } else if (rect.top > 0) {
            deltaY = -rect.top;
        } else if (rect.bottom < viewH) {
            deltaY = viewH - rect.bottom;
        }
        matrix.postTranslate(deltaX, deltaY);//图片向后移动(deltaX, deltaY)
        setPos();
    }
    //设置中心点
    private void setPos() {

        //从（0，0）到 (imageW, imageH)的矩形
        RectF rect = new RectF(0,0, imageW, imageH);
        //获取图片左上角的位置
        matrix.mapRect(rect);

        float p[] = new float[9];//9个浮点数
        matrix.getValues(p);//获取图片九个点的值
        //获取图片的中心位置
        float x = (rect.left+rect.right)/2;
        float y = (rect.top+rect.bottom)/2;
        Center_postion.set(x,y);
        LeftTop_postion.set(rect.left,rect.top);


    }

    //事件响应
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		// 主点按下
		case MotionEvent.ACTION_DOWN:
			savedMatrix.set(matrix);//按键响应首先保存当前矩阵
            savedmatrixchange.set(matrixchange);//按键响应首先保存当前矩阵
            //获取按下位置
			pA.set(event.getX(), event.getY());
			pB.set(event.getX(), event.getY());
            //模式：拖动
			mode = DRAG;
			break;
		// 副点按下
		case MotionEvent.ACTION_POINTER_DOWN:
			if (event.getActionIndex() > 1)//Returns true if this motion event is a touch event.
				break;//一般不会>1
            //获取两点距离
			dist = spacing(event.getX(0), event.getY(0), event.getX(1),
					event.getY(1));
			// 如果两点距离大于10，则判定为多点模式
			if (dist > 10f) {
				savedMatrix.set(matrix);//如果距离大于10，保存当前矩阵
                savedmatrixchange.set(matrixchange);//按键响应首先保存当前矩阵
				pA.set(event.getX(0), event.getY(0));
				pB.set(event.getX(1), event.getY(1));
				mid.set((event.getX(0) + event.getX(1)) / 2,
						(event.getY(0) + event.getY(1)) / 2);
				mode = ZOOM_OR_ROTATE;
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			if (mode == DRAG)
            {
                //如果为拖动模式
				if (spacing(pA.x, pA.y, pB.x, pB.y) < 50) {
					long now = System.currentTimeMillis();//获取系统时间？
					if (now - lastClickTime < 500
							&& spacing(pA.x, pA.y, lastClickPos.x,
									lastClickPos.y) < 50) {
						doubleClick(pA.x, pA.y);//如果在短时间内点击，执行双击命令
						now = 0;
					}
					lastClickPos.set(pA);
					lastClickTime = now;
				}
			}
			mode = NONE;
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == ZOOM_OR_ROTATE)
            {
                //如果为缩放或旋转模式
				PointF pC = new PointF(event.getX(1) - event.getX(0) + pA.x,
						event.getY(1) - event.getY(0) + pA.y);
				double a = spacing(pB.x, pB.y, pC.x, pC.y);//BC间距
				double b = spacing(pA.x, pA.y, pC.x, pC.y);//AC间距
				double c = spacing(pA.x, pA.y, pB.x, pB.y);//AB间距
				if (a >= 10) {//如果BC间距>10
					double cosB = (a * a + c * c - b * b) / (2 * a * c);
					double angleB = Math.acos(cosB);//求出角度
					double PID4 = Math.PI / 4;
					if (angleB > PID4 && angleB < 3 * PID4) {//不懂
						mode = ROTATE;
						rotation = 0;
					} else {
						mode = ZOOM;
					}
				}
			}
			if (mode == DRAG) {
                //如果为拖动模式
				matrix.set(savedMatrix);//矩阵还原为缩放、转动前？？？？？？？？？？？？？？？？？
                matrixchange.set(savedmatrixchange);
				pB.set(event.getX(), event.getY());//获取点B位置
				matrix.postTranslate(event.getX() - pA.x, event.getY() - pA.y);//进行变化
                matrixchange.postTranslate(event.getX() - pA.x, event.getY() - pA.y);//进行变化
                setImageMatrix(matrix);
                setPos();
            } else if (mode == ZOOM) {
                //如果为缩放模式
				float newDist = spacing(event.getX(0), event.getY(0),
						event.getX(1), event.getY(1));//获取新距离
				if (newDist > 10f) {
					matrix.set(savedMatrix);//？？？？？？？？？？
                    matrixchange.set(savedmatrixchange);
					float tScale = newDist / dist;//确定缩放倍数
                    matrix.postScale(tScale, tScale, Center_postion.x,Center_postion.y);
                    matrixchange.postScale(tScale, tScale, Center_postion.x,Center_postion.y);

                    setImageMatrix(matrix);
                    setPos();
				}
			} else if (mode == ROTATE) {
                //如果为旋转模式
				PointF pC = new PointF(event.getX(1) - event.getX(0) + pA.x,
						event.getY(1) - event.getY(0) + pA.y);
				double a = spacing(pB.x, pB.y, pC.x, pC.y);
				double b = spacing(pA.x, pA.y, pC.x, pC.y);
				double c = spacing(pA.x, pA.y, pB.x, pB.y);
				if (b > 10) {
					double cosA = (b * b + c * c - a * a) / (2 * b * c);
					double angleA = Math.acos(cosA);
					double ta = pB.y - pA.y;
					double tb = pA.x - pB.x;
					double tc = pB.x * pA.y - pA.x * pB.y;
					double td = ta * pC.x + tb * pC.y + tc;
					if (td > 0) {
						angleA = 2 * Math.PI - angleA;
					}
					rotation = angleA;
					matrix.set(savedMatrix);
                    matrixchange.set(savedmatrixchange);//按键响应首先保存当前矩阵
					matrix.postRotate((float) (rotation * 180 / Math.PI),
                            Center_postion.x,Center_postion.y);
                    matrixchange.postRotate((float) (rotation * 180 / Math.PI),
                            Center_postion.x,Center_postion.y);
					setImageMatrix(matrix);
                    setPos();
                }

			}
			break;
		}
		return true;
	}

	/**
	 * 两点的距离
	 */
	private float spacing(float x1, float y1, float x2, float y2) {
		float x = x1 - x2;
		float y = y1 - y2;
		return FloatMath.sqrt(x * x + y * y);
	}

	private void doubleClick(float x, float y) {
		float p[] = new float[9];
		matrix.getValues(p);
		float curScale = Math.abs(p[0]) + Math.abs(p[1]);

		float minScale = Math.min((float) viewW / (float) rotatedImageW,
				(float) viewH / (float) rotatedImageH);
		if (curScale <= minScale + 0.01) { // 放大
			float toScale = Math.max(minScale, MAX_SCALE) / curScale;
            matrix.postScale(toScale, toScale, x, y);
            matrixchange.postScale(toScale, toScale, x, y);

        } else { // 缩小
			float toScale = minScale / curScale;
            matrix.postScale(toScale, toScale, x, y);
            matrixchange.postScale(toScale, toScale, x, y);
		}
		setImageMatrix(matrix);
	}

}
