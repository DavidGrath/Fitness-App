package com.davidgrath.fitnessapp.framework

import android.app.Application
import android.util.Log
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
import com.davidgrath.fitnessapp.framework.database.AppDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import java.util.Locale

class FitnessApp: Application() {

    lateinit var runningRepository: RunningRepository
    lateinit var walkingRepository: WalkingRepository
    lateinit var cyclingRepository: CyclingRepository
    lateinit var swimmingRepository: SwimmingRepository
//    lateinit var gymRoutineDao: GymRoutineDao
    lateinit var gymRepository: GymRepository

    lateinit var defaultGymRoutineTemplates: List<GymRoutineTemplate>
    lateinit var setIdentifierTitles: Map<String, SimpleIntlString>
    lateinit var gymSetTutorials: Map<String, GymSetTutorial>

    lateinit var yogaRepository: YogaRepository
    lateinit var defaultYogaSessionTemplates: List<YogaSessionTemplate>
    lateinit var asanaIdentifierTitles: Map<String, SimpleIntlString>
    lateinit var yogaAsanaTutorials: Map<String, YogaAsanaTutorial>


    override fun onCreate() {
        super.onCreate()
        val database = AppDatabase.getDatabase(this)
        runningRepository = RunningRepositoryImpl(
            database.runningWorkoutDao(),
            database.runningLocationDataDao()
        )
        walkingRepository = WalkingRepositoryImpl(
            database.walkingWorkoutDao(),
            database.walkingLocationDataDao()
        )
        cyclingRepository = CyclingRepositoryImpl(
            database.cyclingWorkoutDao(),
            database.cyclingLocationDataDao()
        )
        swimmingRepository = SwimmingRepositoryImpl(
            database.swimmingWorkoutDao()
        )
        gymRepository = GymRepositoryImpl(
            database.gymWorkoutDao(),
            database.gymSetDao()
        )
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

        yogaRepository = YogaRepositoryImpl(
            database.yogaWorkoutDao(),
            database.yogaAsanaDao()
        )
        val sessionStream = assets.open("yogaSessionTemplates.json")
        val sessionTypeToken = object: TypeToken<List<YogaSessionTemplate>>(){}.type
        defaultYogaSessionTemplates = gson.fromJson(InputStreamReader(sessionStream), sessionTypeToken)

        val asanaIdentifierStream = assets.open("asanaIdentifierTitles.json")
        val asanaTypeToken = object: TypeToken<Map<String, SimpleIntlString>>(){}.type
        asanaIdentifierTitles = gson.fromJson(InputStreamReader(asanaIdentifierStream), asanaTypeToken)

        val asanaTutorialStream = assets.open("yogaAsanaTutorials.json")
        val asanaTutorialTypeToken = object: TypeToken<Map<String, YogaAsanaTutorial>>(){}.type
        yogaAsanaTutorials = gson.fromJson(InputStreamReader(asanaTutorialStream), asanaTutorialTypeToken)
        val locale = Locale.getDefault()
        Log.d("BROCCOLI", locale.toString())
        Log.d("BROCCOLI", System.getProperty("java.version")?:"null")
    }

    /*override fun createConfigurationContext(overrideConfiguration: Configuration): Context {
        val preferences = getSharedPreferences(Constants.MAIN_PREFERENCES_NAME, MODE_PRIVATE)
        val languageCode = preferences.getString(PreferencesTitles.CHOSEN_LANGUAGE_CODE, "en")!!
        val countryCode = preferences.getString(PreferencesTitles.CHOSEN_COUNTRY_CODE, "CA")
        val locale = if(countryCode != null) {
            Locale(languageCode, countryCode)
        } else {
            Locale(languageCode)
        }
        overrideConfiguration.setLocale(locale)
        return super.createConfigurationContext(overrideConfiguration)
    }*/
}