package com.example.wuyd2.myapplication;
/**
 * author:wuyd
 * date:2019/1/18
 * 自定义环形进度条
 */
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

public class DoughnutProgress extends View {
    private static final int DEFAULT_MIN_WIDTH = 150; //View默认最小宽度
    private static final int RED = 35, GREEN = 85, BLUE = 230; //基础颜色，这里是橙红色
    private static final int MIN_ALPHA = 30; //最小不透明度
    private static final int MAX_ALPHA = 255; //最大不透明度
    private static final float doughnutRaduisPercent = 0.65f; //圆环外圆半径占View最大半径的百分比
    private static final float doughnutWidthPercent = 0.12f; //圆环宽度占View最大半径的百分比
    private static final float MIDDLE_WAVE_RADUIS_PERCENT = 0.9f; //第二个圆出现时，第一个圆的半径百分比
    private static final float WAVE_WIDTH = 5f; //波纹圆环宽度

    //圆环颜色
    private static int[] doughnutColors = new int[]{
            Color.argb(MAX_ALPHA, RED, GREEN, BLUE),
            Color.argb(MIN_ALPHA, RED, GREEN, BLUE),
            Color.argb(MIN_ALPHA, RED, GREEN, BLUE)};

    private Paint paint = new Paint(); //画笔
    private float width; //自定义view的宽度
    private float height; //自定义view的高度
    private float currentAngle = 0f; //当前旋转角度
    private float raduis; //自定义view的最大半径
    private float firstWaveRaduis;
    private float secondWaveRaduis;

    private Thread thread = new Thread(){
        @Override
        public void run() {
            while(true){
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                postInvalidate();
            }
        }
    };

    public DoughnutProgress(Context context) {
        super(context);
        init();
    }

    public DoughnutProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DoughnutProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        thread.start();
    }

    private void resetParams() {
        width = getWidth();
        height = getHeight();
        raduis = Math.min(width, height)/2;
    }

    private void initPaint() {
        paint.reset();
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        resetParams();

        //将画布中心设为原点(0,0), 方便后面计算坐标
        canvas.translate(width / 2, height / 2);

        //转起来
        canvas.rotate(-currentAngle, 0, 0);
        if (currentAngle >= 360f){
            currentAngle = currentAngle - 360f;
        } else{
            currentAngle = currentAngle + 2f;
        }

        //画渐变圆环
        float doughnutWidth = raduis * doughnutWidthPercent;//圆环宽度
        //圆环外接矩形
        RectF rectF = new RectF(-raduis * doughnutRaduisPercent, -raduis * doughnutRaduisPercent, raduis * doughnutRaduisPercent, raduis * doughnutRaduisPercent);
        initPaint();
        paint.setStrokeWidth(doughnutWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setShader(new SweepGradient(0, 0, doughnutColors, null));
        canvas.drawArc(rectF, 0, 360, false, paint);

        //画旋转头部圆
        initPaint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.argb(MAX_ALPHA, RED, GREEN, BLUE));
        canvas.drawCircle(raduis * doughnutRaduisPercent, 0, doughnutWidth / 2, paint);

        //实现类似水波涟漪效果
        initPaint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(WAVE_WIDTH);
        secondWaveRaduis = calculateWaveRaduis(secondWaveRaduis);
        firstWaveRaduis = calculateWaveRaduis(secondWaveRaduis + raduis*(MIDDLE_WAVE_RADUIS_PERCENT - doughnutRaduisPercent) - raduis*doughnutWidthPercent/2);
        paint.setColor(Color.argb(calculateWaveAlpha(secondWaveRaduis), RED, GREEN, BLUE));
        canvas.drawCircle(0, 0, secondWaveRaduis, paint); //画第二个圆（初始半径较小的）

        initPaint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(WAVE_WIDTH);
        paint.setColor(Color.argb(calculateWaveAlpha(firstWaveRaduis), RED, GREEN, BLUE));
        canvas.drawCircle(0, 0, firstWaveRaduis, paint); //画第一个圆（初始半径较大的）
    }

    /**
     * 计算波纹圆的半径
     * @param waveRaduis
     * @return
     */
    private float calculateWaveRaduis(float waveRaduis){
        if(waveRaduis < raduis*doughnutRaduisPercent + raduis*doughnutWidthPercent/2){
            waveRaduis = raduis*doughnutRaduisPercent + raduis*doughnutWidthPercent/2;
        }
        if(waveRaduis > raduis*MIDDLE_WAVE_RADUIS_PERCENT + raduis*(MIDDLE_WAVE_RADUIS_PERCENT - doughnutRaduisPercent) - raduis*doughnutWidthPercent/2){
            waveRaduis = waveRaduis - (raduis*MIDDLE_WAVE_RADUIS_PERCENT + raduis*(MIDDLE_WAVE_RADUIS_PERCENT - doughnutRaduisPercent) - raduis*doughnutWidthPercent/2) + raduis*doughnutWidthPercent/2 + raduis*doughnutRaduisPercent;
        }
        waveRaduis += 0.6f;
        return waveRaduis;
    }

    /**
     * 根据波纹圆的半径计算不透明度
     * @param waveRaduis
     * @return
     */
    private int calculateWaveAlpha(float waveRaduis){
        float percent = (waveRaduis-raduis*doughnutRaduisPercent-raduis*doughnutWidthPercent/2)/(raduis-raduis*doughnutRaduisPercent-raduis*doughnutWidthPercent/2);
        if(percent >= 1f){
            return 0;
        }else{
            return (int) (MIN_ALPHA*(1f-percent));
        }
    }

    /**
     * 当布局为wrap_content时设置默认长宽
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec), measure(heightMeasureSpec));
    }

    private int measure(int origin) {
        int result = DEFAULT_MIN_WIDTH;
        int specMode = MeasureSpec.getMode(origin);
        int specSize = MeasureSpec.getSize(origin);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }
}