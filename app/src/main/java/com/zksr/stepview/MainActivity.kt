package com.zksr.stepview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val stepView = findViewById<StepView>(R.id.stepView)
        stepView.setStep(StepView.Step.FOUR)
    }
}