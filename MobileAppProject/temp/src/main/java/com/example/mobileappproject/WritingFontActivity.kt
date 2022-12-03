package com.example.mobileappproject

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.mobileappproject.databinding.ActivityWritingFontBinding

class WritingFontActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWritingFontBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWritingFontBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

}