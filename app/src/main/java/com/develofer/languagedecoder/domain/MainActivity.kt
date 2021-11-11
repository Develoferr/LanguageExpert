package com.develofer.languagedecoder.domain

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
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

        viewModel.getLanguagesNames()
        initListener()
        initObservers()

    }

    private fun initObservers() {

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
        viewModel.textInputView.observe( this, { textInput ->
            binding.textInputView.text = textInput
        })

        viewModel.startCleaning.observe(this, { starter ->
            if (starter == true) {
                cleanText()
            }
        })
    }

    private fun cleanText() {
        binding.textInput.text = null
        viewModel.resetCleaner()
    }

    private fun initListener() {
        binding.decodeButton.setOnClickListener {
            textInput = binding.textInput.text.toString()
            viewModel.getTextLanguage(textInput)
            hideKeyboard()
        }
    }

    private fun hideKeyboard() {
        val view = currentFocus ?: View(this)
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

}