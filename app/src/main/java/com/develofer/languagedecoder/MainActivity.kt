package com.develofer.languagedecoder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.develofer.languagedecoder.model.API.retrofitService
import com.develofer.languagedecoder.databinding.ActivityMainBinding
import com.develofer.languagedecoder.model.DetectionResponse
import com.develofer.languagedecoder.model.Language
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import android.R
import android.annotation.SuppressLint

import android.graphics.drawable.Drawable
import android.view.ViewGroup

import androidx.coordinatorlayout.widget.CoordinatorLayout

import com.google.android.material.appbar.AppBarLayout
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity() {

    private lateinit var decodeButton: Button
    private lateinit var languageResults: TextView
    private lateinit var textInput: EditText

    private lateinit var binding: ActivityMainBinding

    var allLanguages = emptyList<Language>()

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setAppBarHeight()
        initListener()
        getLanguages()
    }


    private fun initListener() {
        binding.decodeButton.setOnClickListener {
            val text = binding.textInput.text.toString()
            if (text.isNotEmpty()) {
                getTextLanguage(text)
            }
        }
    }

    private fun getTextLanguage(text: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = retrofitService.getTextLanguage(text)
            if (result.isSuccessful) {
                checkResult(result.body())
            } else {
                showError()
            }
        }

    }

    private fun checkResult(detectionResponse: DetectionResponse?) {
        if (detectionResponse != null && !detectionResponse.data.detections.isNullOrEmpty()) {
            val suspiciousLanguages = detectionResponse.data.detections.filter { it.isReliable }
            if (suspiciousLanguages.isNotEmpty()) {

                val languageCompleteName =
                    allLanguages.find { it.code == suspiciousLanguages.first().language }

                if (languageCompleteName != null) {
                    runOnUiThread {
                        binding.suspiciousLanguage.text = languageCompleteName.name
                    }
                }

            }
        }
    }

    private fun getLanguages() {
        CoroutineScope(Dispatchers.IO).launch {
            val languages: Response<List<Language>> = retrofitService.getLanguages()
            if (languages.isSuccessful) {
                allLanguages = languages.body() ?: emptyList()
                showSuccess()
            } else {
                showError()
            }
        }
    }

    private fun showSuccess() {
        runOnUiThread {
            Toast.makeText(this, "Petition success", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showError() {
        runOnUiThread {
            Toast.makeText(this, "Error making service call", Toast.LENGTH_SHORT).show()
        }
    }
    private fun setAppBarHeight() {
        val appBarLayout = binding.appbar
        appBarLayout.layoutParams =
            CoordinatorLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                getStatusBarHeight() + dpToPx(56)
            )
    }

    private fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    private fun dpToPx(dp: Int): Int {
        val density = resources
            .displayMetrics.density
        return (dp.toFloat() * density).roundToInt()
    }

}