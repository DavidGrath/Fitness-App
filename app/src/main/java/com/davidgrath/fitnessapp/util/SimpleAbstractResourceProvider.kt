package com.davidgrath.fitnessapp.util

import com.davidgrath.fitnessapp.framework.SimpleAssetString

interface SimpleAbstractResourceProvider {
    fun provideWorkoutBanner(key: String): Any
    fun provideGymRoutineImage(key: String): Any
}

class ResourceProviderUrls: SimpleAbstractResourceProvider {

    override fun provideWorkoutBanner(key: String): Any {
        return workoutNameToUrlMap[key]!!
    }

    override fun provideGymRoutineImage(key: String): Any {
        return routineNameToUrlMap[key]!!
    }
}

class ResourceProviderAssetPaths: SimpleAbstractResourceProvider {

    override fun provideWorkoutBanner(key: String): Any {
        return SimpleAssetString(workoutNameToAssetMap[key]!!)
    }

    override fun provideGymRoutineImage(key: String): Any {
        return SimpleAssetString(routineNameToAssetMap[key]!!)
    }
}