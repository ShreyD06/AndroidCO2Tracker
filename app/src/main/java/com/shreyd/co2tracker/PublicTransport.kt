package com.shreyd.co2tracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.Gson
import com.shreyd.co2tracker.model.GeocoderResponse
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import java.net.URL




class PublicTransport : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_public_transport)

        val lat = "38.9081476"
        val lon = "-77.2240058"
        val sUrl = "https://maps.googleapis.com/maps/api/geocode/json?latlng=$lat,$lon&result_type=route&key=${Constants.GMAPKEY}"
        var okHttpClient: OkHttpClient = OkHttpClient()




        val url = URL(sUrl)

        val request = Request.Builder().url(url).build()

        println(request.toString())
//
        var result: String? = null
//
        val gson = Gson()
        okHttpClient.newCall(request).enqueue(object: Callback {
            override fun onResponse(call: Call, response: Response) {

                result = response.body?.string()
                println(result)
                val geocoderResp = gson.fromJson(result, GeocoderResponse::class.java)

            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

        })



        val sUrl2 = "https://routes.googleapis.com/directions/v2:computeRoutes"

        val json = "{\"origin\":{\"address\":\"1944 Horse Shoe Drive, Vienna, VA\"},\"destination\":{\"address\":\"7500 GEOINT Drive, Springfield, VA\"},\"travelMode\":\"TRANSIT\"}"

        val body: RequestBody = json.toRequestBody("/application/json".toMediaTypeOrNull())
        val url2 = URL(sUrl2)

        val request2 = Request.Builder().post(body).url(url2).addHeader("Content-Type", "application/json").addHeader("X-Goog-Api-Key", Constants.GMAPKEY).addHeader("X-Goog-FieldMask", "routes.legs.steps.transitDetails").build()

        okHttpClient.newCall(request2).enqueue(object: Callback {
            override fun onResponse(call: Call, response: Response) {
                result = response.body?.string()
                println(result)
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

        })

    }
}