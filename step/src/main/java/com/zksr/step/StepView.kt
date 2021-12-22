package com.zksr.step

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.IllegalArgumentException

/**
 * 自定义进度view
 */
class StepView : View {

    private val tag = this.javaClass.simpleName

    private var mWith                     : Int = 0
    private var mHeight                   : Int = 0
    private var perLineLength             : Int = 0
    private var passWh                    = mutableListOf<Int>()
    private var normalWh                  = mutableListOf<Int>()
    private var targetWh                  = mutableListOf<Int>()
    private var defaultLineColorForNormal : Int = 0
    private var defaultLineColorForPassed : Int = 0
    private var doCount                   : Int = 0
    private var lineLength                : Int = 0
    private var stepNum                   : Int = 0
    private var maxDotCount               : Int = 0
    private var defaultLinesStrokeWith    : Float = 0.0f
    private var defaultTextSize           : Float = 0.0f
    private var defaultText2DotMargin     : Float = 0.0f
    private var defaultMargin             : Float = 0.0f
    private var defaultLine2TopMargin     : Float = 0.0f
    private var defaultText2Bottom        : Float = 10.0f
    private var normalPic                 : Bitmap = BitmapFactory.decodeResource(resources,R.mipmap.lingxing)
    private var targetPic                 : Bitmap = BitmapFactory.decodeResource(resources,R.mipmap.lingxing_target)
    private var passedPic                 : Bitmap = BitmapFactory.decodeResource(resources,R.mipmap.lingxing_passed)
    private var textLocation              : Int = 0
    private var isTextBelowLine           : Boolean = true
    private var normalLineColor           : Int = 0
    private var passedLineColor           : Int = 0
    private var linesStrokeWith           : Float = 0.0f
    private var textColor                 : Int = 0
    private var textSize                  : Float = 0.0f
    private var text2DotMargin            : Float = 0.0f
    private var margin                    : Float = 0.0f
    private var line2TopMargin            : Float = 0.0f
    private var line2BottomMargin         : Float = 0.0f
    private var text2Bottom               : Float = 0.0f
    private var text2Top                  : Float = 0.0f
    private var viewClickable             : Boolean = false
    private lateinit var linePaint        : Paint
    private lateinit var textPaint        : Paint
    private lateinit var bounds           : Rect
    private var texts                     = mutableListOf("基本信息","详情信息","环境照片","实名验证","提交单据")

    constructor(context: Context):super(context){
        init(context,null,0)
    }

    constructor(context: Context, attests: AttributeSet):super(context,attests){
        init(context,attests,0)
    }

    constructor(context: Context, attests: AttributeSet,defStyleInt: Int):super(context,attests,defStyleInt){
        init(context,attests,defStyleInt)
    }

    private fun init(context: Context,attests: AttributeSet?,defStyleInt: Int){
        defaultLineColorForNormal = Color.parseColor("#545454")
        defaultLineColorForPassed = Color.parseColor("#545454")
        defaultLinesStrokeWith    = dp2px(context,1).toFloat()
        defaultTextSize           = sp2px(context,80).toFloat()
        defaultText2DotMargin     = dp2px(context,15).toFloat()
        defaultMargin             = dp2px(context,100).toFloat()
        defaultLine2TopMargin     = dp2px(context,30).toFloat()
        defaultText2Bottom        = dp2px(context,20).toFloat()

        attests?.let {
            val typedArray = context.obtainStyledAttributes(it,R.styleable.StepView,defStyleInt,0)
            doCount     = typedArray.getInteger(R.styleable.StepView_count,doCount)
            if (doCount < 2){
                throw IllegalArgumentException("节点不能少于两个")
            }
            lineLength  = typedArray.getInteger(R.styleable.StepView_line_length,lineLength)
            stepNum     = typedArray.getInteger(R.styleable.StepView_step,stepNum)
            maxDotCount = typedArray.getInteger(R.styleable.StepView_max_dot_count,maxDotCount)
            if (maxDotCount < doCount){
                lineLength = 0
            }
            textLocation = typedArray.getInt(R.styleable.StepView_text_location,textLocation)
            isTextBelowLine = textLocation == 0
            normalLineColor = typedArray.getColor(R.styleable.StepView_line_normal_color,defaultLineColorForNormal)
            passedLineColor = typedArray.getColor(R.styleable.StepView_line_passed_color,defaultLineColorForPassed)
            linesStrokeWith = typedArray.getFloat(R.styleable.StepView_line_stroke_with,defaultLinesStrokeWith)
            textSize        = typedArray.getDimension(R.styleable.StepView_text_size,defaultTextSize)
            textColor       = typedArray.getColor(R.styleable.StepView_text_color,defaultLineColorForPassed)
            text2DotMargin  = typedArray.getDimension(R.styleable.StepView_text_to_line_margin,defaultText2DotMargin)
            margin          = typedArray.getDimension(R.styleable.StepView_margin,defaultMargin)
            line2TopMargin  = typedArray.getDimension(R.styleable.StepView_line_to_top_margin,defaultLine2TopMargin)
            text2Bottom     = typedArray.getDimension(R.styleable.StepView_text_to_bottom_margin,defaultText2Bottom)
            viewClickable   = typedArray.getBoolean(R.styleable.StepView_view_clickable,false)
            typedArray.recycle()

            if (!isTextBelowLine){
                line2BottomMargin = line2TopMargin
                text2Top = text2Bottom
            }

            linePaint = Paint()
            linePaint.isAntiAlias = true
            linePaint.strokeWidth = linesStrokeWith

            textPaint = Paint()
            textPaint.isAntiAlias = true
            textPaint.color = textColor
            textPaint.textSize = textSize

            bounds = Rect()
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (viewClickable){
            event?.let {
                when(it.action){
                    MotionEvent.ACTION_DOWN ->{
                        val point = Point(it.x.toInt(), it.y.toInt())
                        val stepDotIndex = getStepDotIndex(point)
                        if (stepDotIndex > -1){
                            stepNum = stepDotIndex
                            invalidate()
                        }
                    }
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun getStepDotIndex(point: Point): Int {
        val stepSquareRect = getStepSquareRect()
        for (index in 0 until doCount){
            val rect = stepSquareRect[index]
            if (rect.contains(point.x,point.y)){
                return index
            }
        }
        return -1
    }

    /**
     * 获取所有步骤点的矩阵范围
     */
    private fun getStepSquareRect(): MutableList<Rect> {
        val rectList = mutableListOf<Rect>()
        var mLeft   : Float
        var mTop    : Float
        var mRight  : Float
        var mBottom : Float
        for (index in 0 until doCount){
            val rect = Rect()
            mLeft   = margin + perLineLength * index - targetWh[0] / 2
            mRight  = margin + perLineLength * index + targetWh[0] / 2
            mTop = 0.0f
            mBottom = mHeight.toFloat()
            rect.set(mLeft.toInt(),mTop.toInt(),mRight.toInt(),mBottom.toInt())
            rectList.add(rect)
        }
        return rectList
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        mWith = (w - margin * 2).toInt()
        mHeight = h
        //当线条长度可变时
        perLineLength = if (lineLength == 0){
            mWith / (doCount - 1)
        //当线条长度固定时候
        }else{
            mWith / (maxDotCount - 1)
        }
        normalWh    = calculateWithAndHeight(normalPic)
        passWh      = calculateWithAndHeight(passedPic)
        targetWh    = calculateWithAndHeight(targetPic)
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            drawConnectLine(it, stepNum)
            drawNormalSquare(it, stepNum)
            drawTargetSquare(it, stepNum)
            drawText(it)
        }
    }

    /**
     * 画各连接点之间的线条
     */
    private fun drawConnectLine(canvas: Canvas,stepNum:Int){
        var startX  : Float
        var stopX   : Float
        for (index in 0 until doCount - 1){
            startX = if (index == stepNum){
                (margin + perLineLength * index + targetWh[0] / 2)
            }else if (index > stepNum){
                (margin + perLineLength * index + normalWh[0] / 2)
            }else{
                (margin + perLineLength * index + passWh[0] / 2)
            }
            stopX = if (stepNum == index +1){
                (margin + perLineLength * (index + 1) - targetWh[0] / 2)
            }else if (index + 1 < stepNum){
                (margin + perLineLength * (index + 1) - passWh[0] / 2)
            }else{
                (margin + perLineLength * (index + 1) - normalWh[0] / 2)
            }
            /**
             * 当步数超过目标步数时 设置为已过颜色，没有超过时，设置为普通颜色
             */
            if (stepNum > index){
                linePaint.color = passedLineColor
            }else{
                linePaint.color = normalLineColor
            }
            /**
             * 绘制
             */
            if (isTextBelowLine){
                //文字下线条下方时，设置线条y轴的位置并且绘制
                canvas.drawLine(startX,line2TopMargin,stopX,line2TopMargin,linePaint)
            }else{
                //当文字下线条上方时，设置线条y轴的位置并且绘制
                canvas.drawLine(startX,mHeight - line2BottomMargin,stopX,mHeight - line2BottomMargin,linePaint)
            }

        }
    }

    /**
     * 绘制普通连接点图标(通过 未通过)
     */
    private fun drawNormalSquare(canvas: Canvas,stepNum: Int){
        for (index in 0 until doCount){
            //在目标点及目标点以前都不绘制
            if (index == stepNum){
                continue
            }else if (index > stepNum){
                val mLeft   = margin + (perLineLength * index) - normalWh[0] / 2
                val mTop    : Float = if (isTextBelowLine){
                    line2TopMargin - normalWh[1] / 2
                }else{
                    mHeight - line2BottomMargin - normalWh[1] / 2
                }
                canvas.drawBitmap(normalPic,mLeft,mTop,null)
            }else{
                val mLeft   = margin + (perLineLength * index) - passWh[0] / 2
                val mTop    : Float = if (isTextBelowLine){
                    line2TopMargin - passWh[1] / 2
                }else{
                    mHeight - line2BottomMargin - passWh[0] / 2
                }
                canvas.drawBitmap(passedPic,mLeft,mTop,null)
            }
        }
    }

    /**
     * 绘制目标点图标
     */
    private fun drawTargetSquare(canvas: Canvas,stepNum: Int){
        val mLeft = margin + (perLineLength * stepNum) - targetWh[0] / 2
        val mTop : Float = if (isTextBelowLine){
            line2TopMargin - targetWh[1] / 2
        }else{
            mHeight - line2BottomMargin - targetWh[1] / 2
        }
        canvas.drawBitmap(targetPic,mLeft,mTop,null)
    }

    /**
     * 绘制步骤文字
     */
    private fun drawText(canvas: Canvas){
        for (index in 0 until doCount ){
            val text = texts[index]
            textPaint.getTextBounds(text,0,text.length,bounds)
            val textWith    = bounds.width()
            val textHeight  = bounds.height()
            val mX = margin + perLineLength * index - textWith / 2
            val mY : Float = if (isTextBelowLine){
                mHeight - text2Bottom
            }else{
                (text2DotMargin + textHeight)
            }
            canvas.drawText(text,mX,mY,textPaint)
        }
    }

    private fun calculateWithAndHeight(bitmap: Bitmap): MutableList<Int> {
        return mutableListOf(bitmap.width, bitmap.height)
    }

    private fun dp2px(context: Context,value : Int) : Int{
        val density = context.resources.displayMetrics.density
        return (density * value + 0.5f).toInt()
    }

    private fun sp2px(context: Context,value : Int) : Int{
        val scaleDensity = context.resources.displayMetrics.scaledDensity
        return (value / scaleDensity + 0.5f).toInt()
    }

    public fun setDotCount(count:Int){
        if (count < 2){
            throw IllegalArgumentException("节点至少三个")
        }
        doCount = count
    }

    public fun setDescTexts(descs:MutableList<String>){
        if (descs.isEmpty() || descs.size < 2){
            throw IllegalArgumentException("文字备注至少三个")
        }
        texts.clear()
        texts.addAll(descs)
    }

    public fun setClickableView(canClick:Boolean){
        this.viewClickable = canClick
    }

    public fun setStep(stepNum: Step){
        when(stepNum){
            Step.ONE ->{
                this.stepNum = 0
            }
            Step.TWO ->{
                this.stepNum = 1
            }
            Step.THREE ->{
                this.stepNum = 2
            }
            Step.FOUR ->{
                this.stepNum = 3
            }
            Step.FIVE ->{
                this.stepNum = 4
            }
        }
    }

    public enum class Step{
        ONE,TWO,THREE,FOUR,FIVE
    }
}