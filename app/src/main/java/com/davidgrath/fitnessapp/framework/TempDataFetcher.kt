package com.davidgrath.fitnessapp.framework

import android.content.Context
import android.content.res.AssetManager
import android.content.res.AssetManager.AssetInputStream
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import java.io.InputStream

class TempDataFetcher(
    private val context: Context,
    private val model: SimpleAssetString): DataFetcher<InputStream> {

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        callback.onDataReady(context.assets.open(model.string))
    }

    override fun cleanup() {
    }

    override fun cancel() {
    }

    override fun getDataClass(): Class<InputStream> {
        return InputStream::class.java
    }

    override fun getDataSource(): DataSource {
        return DataSource.LOCAL
    }
}

//class TempDataFetcher(
////    private val context: Context,
//    private val model: AssetInputStream): DataFetcher<InputStream> {
//
//    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
////        callback.onDataReady(context.assets.open(model.string))
//        callback.onDataReady(model)
//    }
//
//    override fun cleanup() {
//    }
//
//    override fun cancel() {
//    }
//
//    override fun getDataClass(): Class<InputStream> {
//        return InputStream::class.java
//    }
//
//    override fun getDataSource(): DataSource {
//        return DataSource.LOCAL
//    }
//}