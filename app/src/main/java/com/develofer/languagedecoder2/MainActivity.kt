package com.develofer.languagedecoder2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.develofer.languagedecoder2.model.API.retrofitService
import com.develofer.languagedecoder2.databinding.ActivityMainBinding
import com.develofer.languagedecoder2.model.Language
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var decodeButton: Button
    private lateinit var languageResults: TextView
    private lateinit var textInput: EditText

    private lateinit var binding: ActivityMainBinding

    var languages = emptyList<Language>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        getLanguages()
    }

    private fun getLanguages() {
        val languages: Response<List<Language>> = retrofitService.getLanguages()
        if (languages.isSuccessful){
            this.languages = languages.body() ?: emptyList()
        } else {
            showError()
        }
    }

    private fun showError() {
        Toast.makeText(this, "Error making service call", Toast.LENGTH_SHORT).show()
    }

    private fun initView() {
        binding.decodeButton
    }
}