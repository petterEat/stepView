package com.zksr.stepview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val stepView = findViewById<com.zksr.step.StepView>(R.id.stepView)
        stepView.setStep(com.zksr.step.StepView.Step.FOUR)
    }
}