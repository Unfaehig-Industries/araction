package tech.unfaehig_industries.tooz.araction.tile_views

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.view.View
import androidx.core.graphics.ColorUtils
import kotlinx.coroutines.*
import timber.log.Timber
import java.time.Instant

class TileButton : View {

    private var rect: RectF = RectF(0f,0f,10f,10f)
    private val rectInsetHighlight: Float = 5f
    var label: String = ""
    private var labelCoordinates: Pair<Float, Float> = Pair(0f, 0f)
    private val labelSize: Float =25f
    var children: ArrayList<TileButton> = ArrayList()
    private val fillPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val labelPaint: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)

    private lateinit var hoverJob: Job

    constructor(context: Context) : super(context)

    constructor(context: Context, rect: RectF, label: String, children: ArrayList<TileButton>, fillColor: Int, labelColor: Int) : super(context) {
        this.rect = RectF(rect)
        this.left = rect.left.toInt()
        this.right = rect.right.toInt()
        this.top = rect.top.toInt()
        this.bottom = rect.bottom.toInt()

        this.label = label

        this.children = children

        this.fillPaint.apply { color= fillColor; style= Paint.Style.FILL }
        this.labelCoordinates = Pair(rect.left+10f,rect.centerY()+(labelSize/2))
        val labelTypeface: Typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        this.labelPaint.apply { color= labelColor; typeface= labelTypeface; textSize= labelSize }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.run {
            this.drawRect(rect, fillPaint)
            this.drawText(label, labelCoordinates.first, labelCoordinates.second, labelPaint)
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

    private fun animateHover(durationInSeconds: Long = 3L) {
        rect.inset(-rectInsetHighlight, -rectInsetHighlight)

        @OptIn(DelicateCoroutinesApi::class)
        hoverJob = GlobalScope.launch {
            val startTime = Instant.now().plusSeconds(durationInSeconds)
            val delay: Long = 100L
            var percent: Float = 0f
            val step: Float = (1f / durationInSeconds) / (1000 / delay)

            while (Instant.now().isBefore(startTime)) {
                fillPaint.shader = RadialGradient(rect.centerX(), rect.centerY(), ( rect.width() / 2 ), intArrayOf(ColorUtils.blendARGB(fillPaint.color, Color.BLACK, 0.6f), fillPaint.color), floatArrayOf(percent, 1f), Shader.TileMode.CLAMP)
                invalidate()
                percent += step
                delay(delay)
            }
        }
    }

    private fun cancelHover() {
        hoverJob.cancel("hover leave")

        rect.inset(rectInsetHighlight, rectInsetHighlight)
        fillPaint.shader = null
        invalidate()
    }

    fun isOnButton(menu: View, screen: RectF, buttonRect: RectF): Boolean {
        Timber.d("menu x: ${menu.translationX}")
        Timber.d("menu y: ${menu.translationY}")
        Timber.d("screen -x: ${screen.centerX() - ( buttonRect.width() / 2 )}")
        Timber.d("screen +x: ${screen.centerX() + ( buttonRect.width() / 2 )}")
        Timber.d("screen -y: ${screen.centerY() - ( buttonRect.height() / 2 )}")
        Timber.d("screen +y: ${screen.centerY() + ( buttonRect.height() / 2 )}")

        return menu.translationX + this.left >= screen.centerX() - ( buttonRect.width() / 2 ) &&
                menu.translationX + this.right <= screen.centerX() + ( buttonRect.width() / 2 ) &&
                menu.translationY + this.top >= screen.centerY() - ( buttonRect.height() / 2 ) &&
                menu.translationY + this.bottom <= screen.centerY() + ( buttonRect.height() / 2 )
    }
}