package com.develofer.languagedecoder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.develofer.languagedecoder.databinding.ActivityMainBinding

import androidx.activity.viewModels

import com.develofer.languagedecoder.viewmodel.MainViewModel


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel>()
    private lateinit var textInput: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initListener()
        viewModel.getLanguagesNames()

        viewModel.textSuspicious.observe(this, { suspicious ->
            binding.suspiciousLanguage.text = suspicious
        })
        viewModel.textSuspiciousProb.observe(this, { suspiciousProb ->
            binding.suspiciousLanguageProbability.text = suspiciousProb
        })
        viewModel.textOtherSuspicious.observe(this, { otherSuspicious ->
            binding.otherSuspicious.text = otherSuspicious
        })
        viewModel.textOtherSuspiciousProb.observe(this, { otherSuspiciousProb ->
            binding.otherSuspiciousProbabilityText.text = otherSuspiciousProb
        })
    }

    private fun initListener() {
        binding.decodeButton.setOnClickListener {
            textInput = binding.textInput.text.toString()
            viewModel.getTextLanguage(textInput)
        }
    }


    /*private fun setAppBarHeight() {
        val appBarLayout = binding.appbar
        appBarLayout.layoutParams =
            CoordinatorLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                getStatusBarHeight() + dpToPx()
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

    private fun dpToPx(): Int {
        val density = resources
            .displayMetrics.density
        return (56.toFloat() * density).roundToInt()
    }*/

}