package com.davidgrath.fitnessapp.framework

import android.content.Context
import android.content.res.AssetManager
import android.content.res.AssetManager.AssetInputStream
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import java.io.InputStream

class TempModelLoaderFactory(private val context: Context): ModelLoaderFactory<SimpleAssetString, InputStream> {
    override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<SimpleAssetString, InputStream> {
        return DefaultGlideModule.TempStreamLoader(context)
    }

    override fun teardown() {

    }
}

/*
class TempModelLoaderFactory(*/
/*private val context: Context*//*
): ModelLoaderFactory<AssetInputStream, InputStream> {
    override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<AssetInputStream, InputStream> {
        return DefaultGlideModule.TempStreamLoader()
    }

    override fun teardown() {

    }
}*/
