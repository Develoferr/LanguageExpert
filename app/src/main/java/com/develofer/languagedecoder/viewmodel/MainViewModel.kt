package com.develofer.languagedecoder.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.develofer.languagedecoder.repository.API
import com.develofer.languagedecoder.model.DetectionResponse
import com.develofer.languagedecoder.model.Language
import com.develofer.languagedecoder.model.PointedLanguage
import kotlinx.coroutines.launch
import retrofit2.Response

class MainViewModel : ViewModel() {

    private var allLanguagesNames = emptyList<Language>()
    private var principalSuspiciousProb: String? = null

    private val _textInputView = MutableLiveData<String>()
    val textInputView: LiveData<String> get() = _textInputView

    private val _startCleaning = MutableLiveData<Boolean>()
    val startCleaning: LiveData<Boolean> get() = _startCleaning

    private val _textSuspicious = MutableLiveData<String>()
    val textSuspicious: LiveData<String> get() = _textSuspicious

    private val _textSuspiciousProb = MutableLiveData<String>()
    val textSuspiciousProb: LiveData<String> get() = _textSuspiciousProb

    private val _textOtherSuspicious = MutableLiveData<String>()
    val textOtherSuspicious: LiveData<String> get() = _textOtherSuspicious

    private val _textOtherSuspiciousProb = MutableLiveData<String>()
    val textOtherSuspiciousProb: LiveData<String> get() = _textOtherSuspiciousProb


    fun getLanguagesNames() {
        viewModelScope.launch {
            val languageNames: Response<List<Language>> = API.retrofitService.getLanguages()
            if (languageNames.isSuccessful) {
                allLanguagesNames = languageNames.body() ?: emptyList()
                showSuccess()
            } else {
                showError()
            }
        }
    }

    fun getTextLanguage(text: String) {
        if (text.isNotEmpty()) {
            viewModelScope.launch {
                val result = API.retrofitService.getTextLanguage(text)
                if (result.isSuccessful) {
                    transformResult(result.body())
                } else {
                    showError()
                }
                cleanText()
                setText(text)
            }
        }
    }

    private fun setText(text: String) {
        _textInputView.value = text
    }

    private fun cleanText() {
        _startCleaning.value = true
    }

    fun resetCleaner() {
        _startCleaning.value = false
    }

    private fun transformResult(detectionResponse: DetectionResponse?) {
        if (detectionResponse != null && !detectionResponse.data.detections.isNullOrEmpty()) {

            val suspiciousDetection = detectionResponse.data.detections.filter { it.isReliable }
            val otherSuspicious = detectionResponse.data.detections.filter { !it.isReliable }

            if (suspiciousDetection.isNotEmpty()) {
                val languageCompleteName =
                    allLanguagesNames.find { it.code == suspiciousDetection.first().language }
                principalSuspiciousProb = suspiciousDetection.first().confidence.toString()
                if (languageCompleteName != null && principalSuspiciousProb != null) {
                    _textSuspicious.value = languageCompleteName.name
                    _textSuspiciousProb.value = principalSuspiciousProb!!
                }
            }
            val traducedLanguages: MutableList<PointedLanguage>? = null
            if (otherSuspicious.isNotEmpty()) {
                var otherSuspiciousLanguages: String? = null
                var otherSuspiciousLanguagesProb: String? = null

                otherSuspicious.forEachIndexed { i, Detection ->
                    val completeName = allLanguagesNames.find { it.code == Detection.language }
                    if (completeName != null) {
                        val pointedLanguage =
                            PointedLanguage(completeName.name, Detection.confidence.toString())
                        traducedLanguages?.add(pointedLanguage)
                        if (i == 0) {
                            otherSuspiciousLanguages = pointedLanguage.language + " \n \n "
                            if (principalSuspiciousProb != pointedLanguage.probability) {
                                otherSuspiciousLanguagesProb = pointedLanguage.probability + " \n \n "
                            } else { otherSuspiciousLanguagesProb = " \n \n "}
                        } else {
                            otherSuspiciousLanguages += "${pointedLanguage.language} \n \n "
                            if (principalSuspiciousProb != pointedLanguage.probability
                                && otherSuspiciousLanguagesProb != null) {
                                otherSuspiciousLanguagesProb += "${pointedLanguage.probability} \n \n"
                            }
                        }
                    }
                }
                if (!otherSuspiciousLanguages.isNullOrEmpty()) {
                    _textOtherSuspicious.value = otherSuspiciousLanguages!!
                }
                if (!otherSuspiciousLanguagesProb.isNullOrEmpty()) {
                    _textOtherSuspiciousProb.value = otherSuspiciousLanguagesProb!!

                }
            }

        }
    }
}


private fun showSuccess() {
    /*runOnUiThread {
        Toast.makeText(this, "Petition success", Toast.LENGTH_SHORT).show()
    }*/
}

private fun showError() {
    /*runOnUiThread {
        Toast.makeText(this, "Error making service call", Toast.LENGTH_SHORT).show()
    }*/
}

