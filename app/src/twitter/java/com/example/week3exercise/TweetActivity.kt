package com.example.week3exercise

import android.location.Address
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
class TweetActivity : AppCompatActivity(){

    private lateinit var recyclerView: RecyclerView

    private lateinit var tweetContent: EditText

    private lateinit var addTweet: FloatingActionButton

    private lateinit var firebaseDatabase: FirebaseDatabase

    private var currentTweets: MutableList<Tweet> = mutableListOf()

    private lateinit var  apiKey: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tweets)

        firebaseDatabase = FirebaseDatabase.getInstance()

        tweetContent = findViewById(R.id.tweet_content)
        addTweet = findViewById(R.id.add_tweet)

        //
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val currenAddress: Address = intent.getParcelableExtra(MapsActivity.INTENT_KEY_ADDRESS)!!
        val state = currenAddress.adminArea ?: "unknown"
        if (currenAddress != null) {
            title = getString(R.string.tweet_title, currenAddress.getAddressLine(0))
        }
        recyclerView = findViewById(R.id.recycleViews)
        recyclerView.layoutManager = LinearLayoutManager(this)



        if (savedInstanceState != null) {
            val saveTweet: List<Tweet> = savedInstanceState.getSerializable("Tweets") as List<Tweet>
            currentTweets.addAll(saveTweet)
            val adapter = TweetAdapter(currentTweets)
            recyclerView.adapter = adapter
        } else {
            Log.d("TweetsActivity", "First time - getting tweets from Twitter")
            getTweetsFromTwitter(currenAddress)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("Tweets", ArrayList(currentTweets))
    }


    fun getTweetsFromTwitter(currenAddress: Address) {
        addTweet.hide()
        tweetContent.isGone = true
        val twitterManager = TwitterManager()
        // https://stackoverflow.com/questions/65008486/globalscope-vs-coroutinescope-vs-lifecyclescope
        // GlobalScope.launch(Dispatchers.IO) this is come from the above website
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val tweets = twitterManager.retrieveTweets(

                    latitude = currenAddress.latitude,
                    longitude = currenAddress.longitude,
                    apiKey = getString(R.string.api_key)
                )
                currentTweets.clear()
                currentTweets.addAll(tweets)

                val tweet = tweets.toList()
                runOnUiThread {
                    val adapter = TweetAdapter(tweet)
                    recyclerView.adapter = adapter
                }
            } catch (exception: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@TweetActivity,
                        "fail to retrieve tweet",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}


