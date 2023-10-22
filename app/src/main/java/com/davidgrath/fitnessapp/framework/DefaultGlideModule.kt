package com.davidgrath.fitnessapp.framework

import android.content.Context
import android.content.res.AssetManager
import android.content.res.AssetManager.AssetInputStream
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.signature.ObjectKey
import java.io.InputStream

/**
 * Built based on this tutorial:
 * https://bumptech.github.io/glide/tut/custom-modelloader.html
 * For some reason, AssetInputStream doesn't work directly with Glide, so I'm using this instead
 */
@GlideModule
class DefaultGlideModule: AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.prepend(SimpleAssetString::class.java, InputStream::class.java,
            TempModelLoaderFactory(context)
        )
    }

    class TempStreamLoader(private val context: Context) : ModelLoader<SimpleAssetString, InputStream> {
        override fun buildLoadData(model: SimpleAssetString, width: Int, height: Int, options: Options): ModelLoader.LoadData<InputStream>? {
            return ModelLoader.LoadData(ObjectKey(model.string), TempDataFetcher(context, model))
        }

        override fun handles(model: SimpleAssetString): Boolean {
            return true
        }
    }


}


/*
@GlideModule
class DefaultGlideModule: AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.prepend(AssetInputStream::class.java, InputStream::class.java,
            TempModelLoaderFactory()
        )
    }

    class TempStreamLoader(*/
/*private val context: Context*//*
) : ModelLoader<AssetInputStream, InputStream> {
        override fun buildLoadData(model: AssetInputStream, width: Int, height: Int, options: Options): ModelLoader.LoadData<InputStream>? {
            return ModelLoader.LoadData(ObjectKey(model), TempDataFetcher(model))
        }

        override fun handles(model: AssetInputStream): Boolean {
            return true
        }
    }


}
*/
