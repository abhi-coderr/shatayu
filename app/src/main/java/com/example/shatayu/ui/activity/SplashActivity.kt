package com.example.shatayu.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.shatayu.R
import com.example.shatayu.databinding.ActivitySplashBinding


@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var activitySplashBinding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySplashBinding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(activitySplashBinding.root)
        animationLogo()
        navigationDelay()
    }

    private fun navigationDelay() {
        Handler().postDelayed({
            // Intent is used to switch from one activity to another.
            val i = Intent(this, UploadVideoActivity::class.java)
            startActivity(i) // invoke the SecondActivity.
            finish() // the current activity will get finished.
        }, 3000)
    }

    private fun animationLogo() {
        val slideAnimation = AnimationUtils.loadAnimation(this, R.anim.logo_anim)
        activitySplashBinding.appLogoImgView.startAnimation(slideAnimation)
    }

}