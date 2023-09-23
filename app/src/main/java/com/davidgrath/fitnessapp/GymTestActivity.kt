package com.davidgrath.fitnessapp

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.davidgrath.fitnessapp.data.entities.GymRoutineTemplate
import com.davidgrath.fitnessapp.databinding.ActivityGymTestBinding
import com.davidgrath.fitnessapp.framework.FitnessApp
import com.davidgrath.fitnessapp.framework.FitnessService

class GymTestActivity: AppCompatActivity(), GymRoutinesFragment.GymRoutinesListener, GymRoutineSetsFragment.GymRoutineSetsListener, GymSetFragment.GymSetListener {

    private lateinit var viewModel: GymTestViewModel
    private lateinit var binding: ActivityGymTestBinding
    private lateinit var fragment: GymRoutinesFragment
    private var binder: FitnessService.FitnessBinder? = null

    private val servConn = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            binder = service as FitnessService.FitnessBinder?
            viewModel.fitnessBinder = binder
        }

        override fun onServiceDisconnected(name: ComponentName?) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGymTestBinding.inflate(layoutInflater)
//        val gymRepository = (application as FitnessApp).gymRepository
//        viewModel = ViewModelProvider(this, GymTestViewModelFactory(gymRepository)).get(GymTestViewModel::class.java)

        val serviceIntent = Intent(this, FitnessService::class.java)
        startService(serviceIntent)
        bindService(serviceIntent, servConn, BIND_AUTO_CREATE)
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GymTestViewModel::class.java)
        if(savedInstanceState == null) {
            fragment = GymRoutinesFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .add(R.id.frame_gym_test, fragment, TAG_ROUTINES)
                .commit()
        } else {
            fragment = supportFragmentManager.findFragmentByTag(TAG_ROUTINES) as GymRoutinesFragment
        }
        setContentView(binding.root)
    }

    override fun onRoutineClicked(position: Int, template: GymRoutineTemplate) {
        val routineSetsFragment = GymRoutineSetsFragment.newInstance(position)
        supportFragmentManager.beginTransaction()
            .hide(fragment)
            .add(R.id.frame_gym_test, routineSetsFragment, TAG_ROUTINE_SETS)
            .addToBackStack(null)
            .commit()
    }

    override fun onStartRoutine(routineTemplatesIndex: Int) {
        val routineSetsFragment = supportFragmentManager.findFragmentByTag(TAG_ROUTINE_SETS) as GymRoutineSetsFragment
        val setFragment = GymSetFragment.newInstance(routineTemplatesIndex, 0)
        supportFragmentManager.beginTransaction()
            .hide(routineSetsFragment)
            .add(R.id.frame_gym_test, setFragment, TAG_SET)
            .addToBackStack(null)
            .commit()
    }

    override fun onNextSet(routineTemplatesIndex: Int, currentTemplateSetIndex: Int) {
        val currentRoutineTemplate = (application as FitnessApp).defaultGymRoutineTemplates[routineTemplatesIndex]
        if(currentTemplateSetIndex + 1 < currentRoutineTemplate.sets.size) {
            val currentSetFragment =
                supportFragmentManager.findFragmentByTag(TAG_SET) as GymSetFragment
            val setFragment = GymSetFragment.newInstance(routineTemplatesIndex, currentTemplateSetIndex + 1)
            supportFragmentManager.beginTransaction()
                .remove(currentSetFragment)
                .add(R.id.frame_gym_test, setFragment, TAG_SET)
                .addToBackStack(null)
                .commit()
        } else {

        }
    }

    override fun onRoutineFinished() {
        val currentSetFragment = supportFragmentManager.findFragmentByTag(TAG_SET) as GymSetFragment
        val routineSetsFragment = supportFragmentManager.findFragmentByTag(TAG_ROUTINE_SETS) as GymRoutineSetsFragment
        val routinesFragment = supportFragmentManager.findFragmentByTag(TAG_ROUTINES) as GymRoutinesFragment
        supportFragmentManager.beginTransaction()
            .remove(currentSetFragment)
            .remove(routineSetsFragment)
            .show(routinesFragment)
            .addToBackStack(null)
            .commit()
    }

    companion object {
        const val TAG_ROUTINES = "routines"
        const val TAG_ROUTINE_SETS = "routineSets"
        const val TAG_SET = "set"
    }
}
