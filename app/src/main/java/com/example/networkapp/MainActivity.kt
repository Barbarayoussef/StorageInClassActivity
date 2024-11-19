package com.example.networkapp

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONObject
// TODO (1: Fix any bugs)
// TODO (2: Add function saveComic(...) to save comic info when downloaded
// TODO (3: Automatically load previously saved comic when app starts)

class MainActivity : AppCompatActivity() {

    private lateinit var requestQueue: RequestQueue
    private lateinit var titleTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var numberEditText: EditText
    private lateinit var showButton: Button
    private lateinit var comicImageView: ImageView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestQueue = Volley.newRequestQueue(this)
        sharedPreferences = getSharedPreferences("ComicApp", Context.MODE_PRIVATE)

        titleTextView = findViewById(R.id.comicTitleTextView)
        descriptionTextView = findViewById(R.id.comicDescriptionTextView)
        numberEditText = findViewById(R.id.comicNumberEditText)
        showButton = findViewById(R.id.showComicButton)
        comicImageView = findViewById(R.id.comicImageView)

        showButton.setOnClickListener {
            val comicId = numberEditText.text.toString()
            if (comicId.isNotEmpty()) {
                downloadComic(comicId)
            } else {
                Toast.makeText(this, "Please enter a comic number", Toast.LENGTH_SHORT).show()
            }
        }

        // Automatically load previously saved comic
        loadSavedComic()
    }

    // Fetches comic from web as JSONObject
    private fun downloadComic(comicId: String) {
        val url = "https://xkcd.com/$comicId/info.0.json"
        requestQueue.add(
            JsonObjectRequest(url,
                { comicObject ->
                    showComic(comicObject)
                    saveComic(comicObject) // Save the comic after it is successfully fetched
                },
                {
                    Toast.makeText(this, "Failed to fetch comic", Toast.LENGTH_SHORT).show()
                }
            )
        )
    }

    // Display a comic for a given comic JSON object
    private fun showComic(comicObject: JSONObject) {
        titleTextView.text = comicObject.getString("title")
        descriptionTextView.text = comicObject.getString("alt")
        Picasso.get().load(comicObject.getString("img")).into(comicImageView)
    }

    // Save comic info to SharedPreferences
    private fun saveComic(comicObject: JSONObject) {
        with(sharedPreferences.edit()) {
            putString("comicTitle", comicObject.getString("title"))
            putString("comicAlt", comicObject.getString("alt"))
            putString("comicImageUrl", comicObject.getString("img"))
            apply()
        }
        Toast.makeText(this, "Comic saved", Toast.LENGTH_SHORT).show()
    }

    // Load previously saved comic and display it
    private fun loadSavedComic() {
        val comicTitle = sharedPreferences.getString("comicTitle", null)
        val comicAlt = sharedPreferences.getString("comicAlt", null)
        val comicImageUrl = sharedPreferences.getString("comicImageUrl", null)

        if (comicTitle != null && comicAlt != null && comicImageUrl != null) {
            titleTextView.text = comicTitle
            descriptionTextView.text = comicAlt
            Picasso.get().load(comicImageUrl).into(comicImageView)
        } else {
            Toast.makeText(this, "No saved comic found", Toast.LENGTH_SHORT).show()
        }
    }
}
