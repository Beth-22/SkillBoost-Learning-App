package com.example.skillboost

import android.app.Application
import com.example.skillboost.data.CourseRepository
import com.example.skillboost.data.UserRepository

class SkillBoostApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        UserRepository.initialize(applicationContext)
    }
}