package tech.unfaehig_industries.tooz.tile_menu.tile_menu

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.view.View
import androidx.core.graphics.ColorUtils
import kotlinx.coroutines.*
import java.time.Instant

open class TileButton : View {

    lateinit var positionRect: RectF
    val baseX: Float get() = positionRect.left
    val baseY: Float get() = positionRect.top
    private lateinit var boundingRect: RectF

    private var label: String = ""
    private var labelCoordinates: Pair<Float, Float> = Pair(0f, 0f)
    private val labelSize: Float =25f

    private val fillPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val labelPaint: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)

    private lateinit var hoverJob: Job

    var actionable: Boolean = true
    private var callback: (() -> Unit)? = null
    private var submenu: Array<TileButtonData>? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, data: TileButtonData, _positionRect: RectF, _boundingRect: RectF, labelColor: Int, _actionable: Boolean) : super(context) {
        boundingRect = RectF(_boundingRect)
        left = boundingRect.left.toInt()
        right = boundingRect.right.toInt()
        top = boundingRect.top.toInt()
        bottom = boundingRect.bottom.toInt()

        positionRect = RectF(_positionRect)
        translationX = baseX
        translationY = baseY

        label = data.label
        this.labelCoordinates = Pair(boundingRect.left+10f, boundingRect.centerY()+(labelSize/2))
        val labelTypeface: Typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        labelPaint.apply { color= labelColor; typeface= labelTypeface; textSize= labelSize; strokeWidth= 3f }

        fillPaint.apply { color= data.tileColor; style= Paint.Style.FILL }

        actionable = _actionable
        setAction(data)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.let {
            canvas.drawRect(boundingRect, fillPaint)
            canvas.drawText(label, labelCoordinates.first, labelCoordinates.second, labelPaint)

            if(isHovered) {
                canvas.drawLine(0f, 0f, boundingRect.width() - 1f, 0f, labelPaint) // Top
                canvas.drawLine(0f, 0f, 0f, boundingRect.height() - 1f, labelPaint) // Left
                canvas.drawLine(boundingRect.width() - 1f, 0f, boundingRect.width() - 1f, boundingRect.height() - 1f, labelPaint) // Right
                canvas.drawLine(0f, boundingRect.height() - 1f, boundingRect.width() - 1f, boundingRect.height() - 1f, labelPaint) // Bottom
            }
        }
    }

    override fun onHoverChanged(hovered: Boolean) {
        super.onHoverChanged(hovered)

        if (hovered) {
            animateHover()
        }
        else {
            cancelHover()
        }
    }

    private fun animateHover(durationInSeconds: Long = 2L) {
        if (!actionable) {
            invalidate()
            return
        }

        @OptIn(DelicateCoroutinesApi::class)
        hoverJob = GlobalScope.launch {
            val endTime = Instant.now().plusSeconds(durationInSeconds)
            val delay = 100L
            var percent = 0f
            // Calculate steps from 60%, because the full width of the boundingRect is used, but the RadialGradient fills the button at about 60% already
            // This ensures that the button is full, at about the same time as the while loop has ended
            val step: Float = 0.6f / ((durationInSeconds * 1000) / delay)

            while (Instant.now().isBefore(endTime)) {
                fillPaint.shader = RadialGradient(boundingRect.centerX(), boundingRect.centerY(), boundingRect.width(), intArrayOf(ColorUtils.blendARGB(fillPaint.color, Color.BLACK, 0.6f), fillPaint.color), floatArrayOf((percent+step).coerceAtLeast(0f), percent), Shader.TileMode.CLAMP)
                invalidate()
                percent += step
                delay(delay)
            }

            if(isHovered) {
                takeAction()
            }
        }
    }

    fun cancelHover() {
        if(this::hoverJob.isInitialized) {
            hoverJob.cancel("hover leave")
        }

        fillPaint.shader = null
        invalidate()
    }

    fun isInCenter(screen: RectF): Boolean {
        return this.translationX <= screen.centerX() &&
                this.translationX + this.boundingRect.width() >= screen.centerX() &&
                this.translationY <= screen.centerY() &&
                this.translationY + this.boundingRect.height() >= screen.centerY()
    }

    private fun setAction(data: TileButtonData) {
        if(data is TileActionButtonData) {
            callback = data.callback
            submenu = null
        }
        if(data is TileSubmenuButtonData) {
            callback = null
            submenu = data.submenu
        }
    }

    private fun takeAction() {
        callback?.let { it() }

        submenu?.let {
            if (parent is TileMenu) {
                (parent as TileMenu).loadNewMenu(this, it)
            }
        }
    }
}