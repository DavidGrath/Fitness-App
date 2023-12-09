package com.davidgrath.fitnessapp.ui.profile

import com.davidgrath.fitnessapp.data.UserDataRepository
import com.davidgrath.fitnessapp.data.UserDataRepositoryTestImpl
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class ProfileViewModelTest {

    private lateinit var userDataRepository: UserDataRepository
    private lateinit var profileViewModel: ProfileViewModel

    @Before
    fun setUp() {
        userDataRepository = UserDataRepositoryTestImpl()
        profileViewModel = ProfileViewModel(userDataRepository)
    }

    // GIVEN Class is initialized
    // WHEN Avatar type is set
    // AND Avatar type is not equal to the current value
    // THEN LiveData should emit new value of Avatar type
    @Test
    fun testEmitAvatarType() {
        profileViewModel.submitAvatarType("none")
        profileViewModel.profileScreenStateLiveData.observeForever {  }
        assertEquals("", profileViewModel.profileScreenStateLiveData.value!!.userAvatarType)
        assertEquals("none", profileViewModel.profileScreenStateLiveData.value!!.userAvatarType)
    }
}