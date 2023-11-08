package com.davidgrath.fitnessapp

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.davidgrath.fitnessapp.data.entities.YogaSessionTemplate
import com.davidgrath.fitnessapp.databinding.ActivityYogaTestBinding
import com.davidgrath.fitnessapp.framework.FitnessApp
import com.davidgrath.fitnessapp.framework.FitnessService

class YogaTestActivity: AppCompatActivity(), YogaSessionsFragment.YogaSessionsListener, YogaSessionAsanasFragment.YogaSessionAsanasListener, YogaAsanaFragment.YogaAsanaListener {

    private lateinit var viewModel: YogaTestViewModel
    private lateinit var binding: ActivityYogaTestBinding
    private lateinit var fragment: YogaSessionsFragment
    private var binder: FitnessService.FitnessBinder? = null

    private val servConn = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            binder = service as FitnessService.FitnessBinder?
            viewModel.fitnessService = binder
        }

        override fun onServiceDisconnected(name: ComponentName?) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityYogaTestBinding.inflate(layoutInflater)
        val serviceIntent = Intent(this, FitnessService::class.java)
        startService(serviceIntent)
        bindService(serviceIntent, servConn, BIND_AUTO_CREATE)
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(YogaTestViewModel::class.java)
        if(savedInstanceState == null) {
            fragment = YogaSessionsFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .add(R.id.frame_yoga_test, fragment, TAG_SESSIONS)
                .commit()
        } else {
            fragment = supportFragmentManager.findFragmentByTag(TAG_SESSIONS) as YogaSessionsFragment
        }
        setContentView(binding.root)
    }

    override fun onSessionClicked(position: Int, template: YogaSessionTemplate) {
        val sessionAsanasFragment = YogaSessionAsanasFragment.newInstance(position)
        supportFragmentManager.beginTransaction()
            .hide(fragment)
            .add(R.id.frame_yoga_test, sessionAsanasFragment, TAG_SESSION_ASANAS)
            .addToBackStack(null)
            .commit()
    }

    override fun onStartSession(sessionTemplatesIndex: Int) {
        val sessionAsanasFragment = supportFragmentManager.findFragmentByTag(TAG_SESSION_ASANAS) as YogaSessionAsanasFragment
        val asanaFragment = YogaAsanaFragment.newInstance(sessionTemplatesIndex, 0)
        supportFragmentManager.beginTransaction()
            .hide(sessionAsanasFragment)
            .add(R.id.frame_yoga_test, asanaFragment, TAG_ASANA)
            .addToBackStack(null)
            .commit()
    }

    override fun onNextAsana(sessionTemplatesIndex: Int, currentTemplateAsanaIndex: Int) {
        val currentSessionTemplate = (application as FitnessApp).defaultYogaSessionTemplates[sessionTemplatesIndex]
        if(currentTemplateAsanaIndex + 1 < currentSessionTemplate.asanas.size) {
            val currentSetFragment =
                supportFragmentManager.findFragmentByTag(TAG_ASANA) as YogaAsanaFragment
            val setFragment = YogaAsanaFragment.newInstance(sessionTemplatesIndex, currentTemplateAsanaIndex + 1)
            supportFragmentManager.beginTransaction()
                .remove(currentSetFragment)
                .add(R.id.frame_yoga_test, setFragment, TAG_ASANA)
                .addToBackStack(null)
                .commit()
        } else {

        }
    }

    override fun onSessionFinished() {
        val currentAsanaFragment = supportFragmentManager.findFragmentByTag(TAG_ASANA) as YogaAsanaFragment
        val sessionAsanasFragment = supportFragmentManager.findFragmentByTag(TAG_SESSION_ASANAS) as YogaSessionAsanasFragment
        val sessionsFragment = supportFragmentManager.findFragmentByTag(TAG_SESSIONS) as YogaSessionsFragment
        supportFragmentManager.beginTransaction()
            .remove(currentAsanaFragment)
            .remove(sessionAsanasFragment)
            .show(sessionsFragment)
            .addToBackStack(null)
            .commit()
    }

    companion object {
        const val TAG_SESSIONS = "sessions"
        const val TAG_SESSION_ASANAS = "session_asanas"
        const val TAG_ASANA = "asana"
    }
}