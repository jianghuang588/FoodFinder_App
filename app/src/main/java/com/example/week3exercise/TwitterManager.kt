package com.example.week3exercise


import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit


class TwitterManager {


    //apiKey
    //hsIn8cW5PYaIvV-K06DIuWhFM3FEe-f3HO49CKRGL7sGxkyi1pZbAqFvJYdTf7Wmi5bzzQuNiijc8c6kdmCH2WsI2yB2T4cvF7sq_bWErOgwmBzwzvrZzQbWEsQuZXYx
   fun retrieveTweets(apiKey: String, latitude: Double, longitude: Double): MutableCollection<Tweet> {

       val searchTerm = "the"
       val radius = "30mi"

       val request = Request.Builder()
           .url("https://api.yelp.com/v3/businesses/search?location=washington&$longitude&$latitude&$radius")
           .header("Authorization", "Bearer $apiKey")
           .build()
       val response = ApiUtils.okHttpClient.newCall(request).execute()
       val tweets: MutableCollection<Tweet> = mutableListOf()
       val responseString: String? = response.body?.string()


       if (!responseString.isNullOrEmpty() && response.isSuccessful) {
           val json: JSONObject = JSONObject(responseString)

           val statuses: JSONArray = json.getJSONArray("businesses")

           for (i in 0 until statuses.length()) {
               val curr = statuses.getJSONObject(i)


               val name = curr.getString("name")
               val category = curr.getJSONArray("categories")
               val currentCategory = category.getJSONObject(0)
               val handle = currentCategory.getString("title")
               val content = curr.getString("url")
               val profilePictureUrl = curr.getString("image_url")
               
               val tweet = Tweet(
                   username = name,
                   content = content,
                   handle = handle,
                   iconUrl = profilePictureUrl
               )
               tweets.add(tweet)
           }
       }
       return tweets
   }
}

