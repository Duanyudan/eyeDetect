package com.example.base.widget

import android.content.Context
import android.graphics.Typeface
import androidx.collection.SimpleArrayMap


/**
 * <pre>
 *     author : yangliu
 *     e-mail : yangliu@codemao.cn
 *     time   : 2020/12/01
 *     desc   :
 * </pre>
 */
object TypefaceHelper {
    private const val TAG = "TypefaceHelper"

    private val TYPEFACE_CACHE = SimpleArrayMap<String, Typeface>()

    operator fun get(context: Context, name: String): Typeface? {
        synchronized(TYPEFACE_CACHE) {
            if (!TYPEFACE_CACHE.containsKey(name)) {
                try {
                    val t = Typeface.createFromAsset(context.assets, name)
                    TYPEFACE_CACHE.put(name, t)
                } catch (e: Exception) {
                    return null
                }
            }
            return TYPEFACE_CACHE[name]
        }
    }

}