package com.jackandphantom.mytodo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
//Entry point for the application
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.task_activity_main)
    }
}