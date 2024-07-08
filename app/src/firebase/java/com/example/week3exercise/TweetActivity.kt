package com.example.week3exercise

import android.location.Address
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener



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
            getTweetsFromFirebase(currenAddress)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("Tweets", ArrayList(currentTweets))
    }


    fun getTweetsFromFirebase(currenAddress: Address) {
        val state = currenAddress.adminArea ?: "unknown"
        val reference = firebaseDatabase.getReference("tweets/$state")

        addTweet.setOnClickListener{
            val newTweetReference = reference.push()
            val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
            val email: String = firebaseAuth.currentUser!!.email!!
            val inputedContent: String = tweetContent.text.toString()

            val tweet = Tweet(
                username = email,
                handle = email,
                content = inputedContent,
                iconUrl = ""
            )
            newTweetReference.setValue(tweet)
        }
        reference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val tweets= mutableListOf<Tweet>()

                snapshot.children.forEach{data: DataSnapshot ->
                    val tweet = data.getValue(Tweet::class.java)
                    if (tweet != null) {
                        tweets.add(tweet)
                    }
                }
                val adapter = TweetAdapter(tweets)
                recyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@TweetActivity,
                    "Network error with database: ${error.message}",
                    Toast.LENGTH_LONG
                )
            }
        })
    }
}


