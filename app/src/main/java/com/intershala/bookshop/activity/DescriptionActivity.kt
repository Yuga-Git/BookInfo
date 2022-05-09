package com.intershala.bookshop.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.intershala.bookshop.R
import com.intershala.bookshop.database.BookDatabase
import com.intershala.bookshop.database.BookEntity
import com.intershala.bookshop.util.ConnectionManager
import com.squareup.picasso.Picasso
import org.json.JSONObject

class DescriptionActivity : AppCompatActivity() {

    lateinit var toolBar : Toolbar
    lateinit var imgBookImage : ImageView
    lateinit var txtBookName : TextView
    lateinit var txtBookAuthor : TextView
    lateinit var txtprice : TextView
    lateinit var txtrating : TextView
    lateinit var txtBookDesc : TextView
    lateinit var progressBar: ProgressBar
    lateinit var progressbarlayout : RelativeLayout
    lateinit var btnAddToFavourites : Button

    var bookId : String? = "100"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)


        imgBookImage = findViewById(R.id.imgDecBookImage)
        txtBookName = findViewById(R.id.txtDecBookName)
        txtBookAuthor = findViewById(R.id.txtDecBookAuthor)
        txtrating = findViewById(R.id.txtDecRating)
        txtprice    = findViewById(R.id.txtDecBookPrice)
        txtBookDesc = findViewById(R.id.txtBookDesc)
        btnAddToFavourites = findViewById(R.id.btnDecAddToFavourites)
        progressBar = findViewById(R.id.decProgressBar)
        progressbarlayout = findViewById(R.id.decProgressBarLayout)

        progressbarlayout.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE

        toolBar = findViewById(R.id.dectoolbar)
        setSupportActionBar(toolBar)
        supportActionBar?.title = "Book Details"


        if (intent != null) {
            bookId = intent.getStringExtra("book_Id")
        } else {
            finish()
            Toast.makeText(
                this@DescriptionActivity,
                "Some unexpected error occurred",
                Toast.LENGTH_SHORT
            ).show()

        }
        if (bookId == "100") {
            finish()
            Toast.makeText(
                this@DescriptionActivity,
                "Some unexpected error occurred",
                Toast.LENGTH_SHORT
            ).show()
        }

        val queue = Volley.newRequestQueue(this@DescriptionActivity)
        val url = "http://13.235.250.119/v1/book/get_book/"

        val jsonParams = JSONObject()
        jsonParams.put("book_id", bookId)

        if (ConnectionManager().checkConnectivity(this@DescriptionActivity)) {

            val jsonRequest = object : JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {

                    try {
                        val success = it.getBoolean("success")
                        if (success) {

                            val bookJsonObject = it.getJSONObject("book_data")

                            progressbarlayout.visibility = View.GONE

                            val bookImageURL = bookJsonObject.getString("image")


                            Picasso.get().load(bookJsonObject.getString("image"))
                                .error(R.drawable.default_book_cover).into(imgBookImage)

                            txtBookName.text = bookJsonObject.getString("name")
                            txtBookAuthor.text = bookJsonObject.getString("author")
                            txtrating.text = bookJsonObject.getString("rating")
                            txtprice.text = bookJsonObject.getString("price")
                            txtBookDesc.text = bookJsonObject.getString("description")

                            val bookEntity = BookEntity(
                            bookId ?.toInt() as Int,
                            txtBookName.text.toString(),
                            txtBookAuthor.text.toString(),
                            txtprice.text.toString(),
                            txtrating.text.toString(),
                            txtBookDesc.text.toString(),
                            bookImageURL
                            )

                            val checkFav = DBAAsyncTask(applicationContext, bookEntity, 1).execute()
                            val isFav = checkFav.get()
                            if(isFav) {
                                btnAddToFavourites.text = "Remove From Favourites"
                                val favColor = ContextCompat.getColor(applicationContext, R.color.color_favourites)
                                btnAddToFavourites.setBackgroundColor(favColor)
                            }else{
                                btnAddToFavourites.text = "Add to Favourites"
                                val noFavColor = ContextCompat.getColor(applicationContext, R.color.purple_500)
                                btnAddToFavourites.setBackgroundColor(noFavColor)
                            }


                            btnAddToFavourites.setOnClickListener {
                                if (!DBAAsyncTask(applicationContext, bookEntity, 1).execute()
                                        .get()) {
                                            val async = DBAAsyncTask(applicationContext, bookEntity,2).execute()
                                            val result = async.get()
                                            if(result){
                                                Toast.makeText(this@DescriptionActivity, "Book added to favourites", Toast.LENGTH_SHORT).show()
                                                btnAddToFavourites.text = "Book Remove From Favourites"
                                                val favColor = ContextCompat.getColor(applicationContext, R.color.color_favourites)
                                                btnAddToFavourites.setBackgroundColor(favColor)
                                            }else{
                                                Toast.makeText(this@DescriptionActivity, "Some error occurs!!", Toast.LENGTH_SHORT).show()
                                            }
                                }else{
                                            val async = DBAAsyncTask(applicationContext, bookEntity,3).execute()
                                            val result = async.get()
                                            if(result){
                                                Toast.makeText(this@DescriptionActivity, "Book removed to favourites", Toast.LENGTH_SHORT).show()
                                                btnAddToFavourites.text = "Book add to Favourites"
                                                val nofavColor = ContextCompat.getColor(applicationContext, R.color.purple_500)
                                                btnAddToFavourites.setBackgroundColor(nofavColor)
                                            }else{
                                                Toast.makeText(this@DescriptionActivity, "Some error occurs!!", Toast.LENGTH_SHORT).show()
                                            }

                                }
                            }

                            }else {
                            Toast.makeText(
                                this@DescriptionActivity,
                                "Some error occurred!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@DescriptionActivity,
                            "Some error occurred!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }, Response.ErrorListener {

                    Toast.makeText(
                        this@DescriptionActivity,
                        "Valley error occurred!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }) {

                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-Type"] = "application/json"
                    headers["token"] = "8cb9fb7dbfe3ff"
                    return headers
                }
            }
            queue.add(jsonRequest)
        }
        else {
            val dialog = AlertDialog.Builder(this@DescriptionActivity)
            dialog.setTitle("Success")
            dialog.setMessage("Internet Connection not Found")
            dialog.setPositiveButton("Open Settings") { text, listner ->
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                finish()
            }
            dialog.setNegativeButton("Exit") { text, listner ->
                ActivityCompat.finishAffinity(this@DescriptionActivity)
            }
            dialog.create()
            dialog.show()
        }
    }
    class DBAAsyncTask(val context : Context, val bookEntity: BookEntity, val mode : Int) : AsyncTask<Void, Void, Boolean>(){

        /* Mode -> 1. Check db if book is favourites or not
          Mode -> 2. Save the book in db as favourites
          Mode -> 3. Remove the book from favourites
         */

        val db= Room.databaseBuilder(context, BookDatabase :: class.java, "books-db").build()
        override fun doInBackground(vararg p0: Void?): Boolean {
            when (mode){
                1->{
                    val book : BookEntity? = db.bookDao().getBookById(bookEntity.book_id.toString())
                    db.close()
                    return book != null
                }

                2->{
                    db.bookDao().insertBook(bookEntity)
                    db.close()
                    return true
                }

                3->{
                    db.bookDao().deleteBook(bookEntity)
                    db.close()
                    return true
                }
            }
            return false
        }
    }
}