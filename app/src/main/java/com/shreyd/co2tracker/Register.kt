package com.shreyd.co2tracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.content.res.ResourcesCompat

class Register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val cars = resources.getStringArray(R.array.programming_languages)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, cars)
        val autocompleteTV = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        autocompleteTV.setAdapter(arrayAdapter)
        autocompleteTV.setDropDownBackgroundDrawable(ResourcesCompat.getDrawable(resources, R.drawable.dropdown_bg, null))
    }
}