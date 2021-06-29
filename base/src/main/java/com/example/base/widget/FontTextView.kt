package com.example.base.widget

import android.content.Context
import android.text.Spanned
import android.util.AttributeSet
import com.example.base.R


/**
 * Created by LiuG on 2020-04-22.
 *
 * 自定义字体TextView
 */
open class FontTextView : androidx.appcompat.widget.AppCompatTextView {

    private var fontAssetsPath: String = defaultFontAssetsPath
    private var maxTextNum: Int
    private var needSubString: Boolean

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val array = context.obtainStyledAttributes(attrs, R.styleable.FontTextView)
        val roundFont=array.getBoolean(R.styleable.FontTextView_isRoundFont,false)
        if(roundFont){
            fontAssetsPath=roundFontAssetsPath
        }
        typeface = TypefaceHelper[context, fontAssetsPath]
        //typeface = Typeface.createFromAsset(context.assets, fontAssetsPath)
        maxTextNum = array.getInt(R.styleable.FontTextView_maxTextNum, 0)
        needSubString = array.getBoolean(R.styleable.FontTextView_needSubString, true)
        array.recycle()
    }

    /**
     * 需要设置...类型麻烦在代码中去设置，如果在此重写该方法，会导致富文本无法生效
     */
//    override fun setText(text: CharSequence?, type: BufferType?) {
//        if (needSubString) {
//            var result = text.toString()
//            if (maxTextNum > 0 && result.length > maxTextNum) {
//                result = result.substring(0, maxTextNum - 1) + "..."
//            }
//            super.setText(result, type)
//        } else {
//            super.setText(text, type)
//        }
//
//    }

    companion object {
        private const val defaultFontAssetsPath = "font/round.TTF"
        private const val roundFontAssetsPath="font/normal_round.TTF"
    }

}