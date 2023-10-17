package com.davidgrath.fitnessapp.framework

import android.app.Application
import com.davidgrath.fitnessapp.data.CyclingRepository
import com.davidgrath.fitnessapp.data.CyclingRepositoryImpl
import com.davidgrath.fitnessapp.data.GymRepository
import com.davidgrath.fitnessapp.data.GymRepositoryImpl
import com.davidgrath.fitnessapp.data.RunningRepository
import com.davidgrath.fitnessapp.data.RunningRepositoryImpl
import com.davidgrath.fitnessapp.data.SwimmingRepository
import com.davidgrath.fitnessapp.data.SwimmingRepositoryImpl
import com.davidgrath.fitnessapp.data.WalkingRepository
import com.davidgrath.fitnessapp.data.WalkingRepositoryImpl
import com.davidgrath.fitnessapp.data.YogaRepository
import com.davidgrath.fitnessapp.data.YogaRepositoryImpl
import com.davidgrath.fitnessapp.data.entities.GymRoutineTemplate
import com.davidgrath.fitnessapp.data.entities.GymSetTutorial
import com.davidgrath.fitnessapp.data.entities.SimpleIntlString
import com.davidgrath.fitnessapp.data.entities.YogaAsanaTutorial
import com.davidgrath.fitnessapp.data.entities.YogaSessionTemplate
import com.davidgrath.fitnessapp.framework.database.CyclingLocationDataDao
import com.davidgrath.fitnessapp.framework.database.CyclingWorkoutDao
import com.davidgrath.fitnessapp.framework.database.GymRoutineDao
import com.davidgrath.fitnessapp.framework.database.GymSetDao
import com.davidgrath.fitnessapp.framework.database.GymWorkoutDao
import com.davidgrath.fitnessapp.framework.database.RunningLocationDataDao
import com.davidgrath.fitnessapp.framework.database.RunningWorkoutDao
import com.davidgrath.fitnessapp.framework.database.SwimmingWorkoutDao
import com.davidgrath.fitnessapp.framework.database.WalkingLocationDataDao
import com.davidgrath.fitnessapp.framework.database.WalkingWorkoutDao
import com.davidgrath.fitnessapp.framework.database.YogaAsanaDao
import com.davidgrath.fitnessapp.framework.database.YogaWorkoutDao
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

class FitnessApp: Application() {

    lateinit var runningRepository: RunningRepository
    lateinit var runningWorkoutDao: RunningWorkoutDao
    lateinit var runningLocationDataDao: RunningLocationDataDao

    lateinit var walkingRepository: WalkingRepository
    lateinit var walkingWorkoutDao: WalkingWorkoutDao
    lateinit var walkingLocationDataDao: WalkingLocationDataDao

    lateinit var cyclingRepository: CyclingRepository
    lateinit var cyclingWorkoutDao: CyclingWorkoutDao
    lateinit var cyclingLocationDataDao: CyclingLocationDataDao

    lateinit var swimmingRepository: SwimmingRepository
    lateinit var swimmingWorkoutDao: SwimmingWorkoutDao

    lateinit var gymWorkoutDao: GymWorkoutDao
    lateinit var gymRoutineDao: GymRoutineDao
    lateinit var gymSetDao: GymSetDao
    lateinit var gymRepository: GymRepository
    lateinit var defaultGymRoutineTemplates: List<GymRoutineTemplate>
    lateinit var setIdentifierTitles: Map<String, SimpleIntlString>
    lateinit var gymSetTutorials: Map<String, GymSetTutorial>

    lateinit var yogaWorkoutDao: YogaWorkoutDao
    lateinit var yogaAsanaDao: YogaAsanaDao
    lateinit var yogaRepository: YogaRepository
    lateinit var defaultYogaSessionTemplates: List<YogaSessionTemplate>
    lateinit var asanaIdentifierTitles: Map<String, SimpleIntlString>
    lateinit var yogaAsanaTutorials: Map<String, YogaAsanaTutorial>


    override fun onCreate() {
        super.onCreate()
        runningWorkoutDao = RunningWorkoutDao()
        runningLocationDataDao = RunningLocationDataDao()
        runningRepository = RunningRepositoryImpl(
            runningWorkoutDao, runningLocationDataDao
        )
        walkingWorkoutDao = WalkingWorkoutDao()
        walkingLocationDataDao = WalkingLocationDataDao()
        walkingRepository = WalkingRepositoryImpl(
            walkingWorkoutDao, walkingLocationDataDao
        )
        cyclingWorkoutDao = CyclingWorkoutDao()
        cyclingLocationDataDao = CyclingLocationDataDao()
        cyclingRepository = CyclingRepositoryImpl(
            cyclingWorkoutDao, cyclingLocationDataDao
        )
        swimmingWorkoutDao = SwimmingWorkoutDao()
        swimmingRepository = SwimmingRepositoryImpl(
            swimmingWorkoutDao
        )
        gymWorkoutDao = GymWorkoutDao()
        gymRoutineDao = GymRoutineDao()
        gymSetDao = GymSetDao()
        gymRepository = GymRepositoryImpl(gymWorkoutDao, gymRoutineDao, gymSetDao)
        val stream = assets.open("gymRoutineTemplates.json")
        val gson = Gson()
        val routineTypeToken = object: TypeToken<List<GymRoutineTemplate>>(){}.type
        defaultGymRoutineTemplates = gson.fromJson(InputStreamReader(stream), routineTypeToken)

        val identifierStream = assets.open("setIdentifierTitles.json")
        val mapTypeToken = object: TypeToken<Map<String, SimpleIntlString>>(){}.type
        setIdentifierTitles = gson.fromJson(InputStreamReader(identifierStream), mapTypeToken)

        val tutorialStream = assets.open("gymSetTutorials.json")
        val tutorialTypeToken = object: TypeToken<Map<String, GymSetTutorial>>(){}.type
        gymSetTutorials = gson.fromJson(InputStreamReader(tutorialStream), tutorialTypeToken)

        yogaWorkoutDao = YogaWorkoutDao()
        yogaAsanaDao = YogaAsanaDao()
        yogaRepository = YogaRepositoryImpl(yogaWorkoutDao, yogaAsanaDao)
        val sessionStream = assets.open("yogaSessionTemplates.json")
        val sessionTypeToken = object: TypeToken<List<YogaSessionTemplate>>(){}.type
        defaultYogaSessionTemplates = gson.fromJson(InputStreamReader(sessionStream), sessionTypeToken)

        val asanaIdentifierStream = assets.open("asanaIdentifierTitles.json")
        val asanaTypeToken = object: TypeToken<Map<String, SimpleIntlString>>(){}.type
        asanaIdentifierTitles = gson.fromJson(InputStreamReader(asanaIdentifierStream), asanaTypeToken)

        val asanaTutorialStream = assets.open("yogaAsanaTutorials.json")
        val asanaTutorialTypeToken = object: TypeToken<Map<String, YogaAsanaTutorial>>(){}.type
        yogaAsanaTutorials = gson.fromJson(InputStreamReader(asanaTutorialStream), asanaTutorialTypeToken)
    }
}