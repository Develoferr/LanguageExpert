package com.develofer.languagedecoder.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.develofer.languagedecoder.model.API
import com.develofer.languagedecoder.model.DetectionResponse
import com.develofer.languagedecoder.model.Language
import com.develofer.languagedecoder.model.PointedLanguage
import kotlinx.coroutines.launch
import retrofit2.Response

class MainViewModel : ViewModel() {

    private var allLanguagesNames = emptyList<Language>()
    private var principalSuspiciousProb: String? = null

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
            val languages: Response<List<Language>> = API.retrofitService.getLanguages()
            if (languages.isSuccessful) {
                allLanguagesNames = languages.body() ?: emptyList()
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
                    checkResult(result.body())
                } else {
                    showError()
                }
                cleanText()
            }
        }

    }

    private fun cleanText() {
        //binding.textInput.setText("")
    }

    private fun checkResult(detectionResponse: DetectionResponse?) {
        if (detectionResponse != null && !detectionResponse.data.detections.isNullOrEmpty()) {

            val suspiciousDetection = detectionResponse.data.detections.filter { it.isReliable }
            val otherSuspiciousDetections =
                detectionResponse.data.detections.filter { !it.isReliable }

            if (suspiciousDetection.isNotEmpty()) {
                val languageCompleteName =
                    allLanguagesNames.find { it.code == suspiciousDetection.first().language }
                    principalSuspiciousProb = suspiciousDetection.first().confidence.toString()
                if (languageCompleteName != null) {
                    _textSuspicious.value = languageCompleteName.name
                    _textSuspiciousProb.value = principalSuspiciousProb
                }
            }
            val traducedLanguages: MutableList<PointedLanguage>? = null
            if (otherSuspiciousDetections.isNotEmpty()) {
                var languages: String? = null
                var confidences: String? = null

                otherSuspiciousDetections.forEachIndexed { i, Detection ->
                    val completeName = allLanguagesNames.find { it.code == Detection.language }
                    if (completeName != null) {
                        val pointedLanguage =
                            PointedLanguage(completeName.name, Detection.confidence.toString())
                        traducedLanguages?.add(pointedLanguage)
                        if (i == 0) {
                            languages = pointedLanguage.language + " \n "
                            if (principalSuspiciousProb != pointedLanguage.probability) {
                                confidences = pointedLanguage.probability + " \n "
                            }
                        } else {
                            languages += pointedLanguage.language
                            languages += " \n "
                            if (principalSuspiciousProb != pointedLanguage.probability) {
                                confidences += pointedLanguage.probability
                                confidences += " \n "
                            }
                        }
                    }
                }
                _textOtherSuspicious.value = languages
                _textOtherSuspiciousProb.value = confidences

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

