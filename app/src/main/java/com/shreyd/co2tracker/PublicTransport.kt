package com.shreyd.co2tracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.Gson
import com.google.maps.android.PolyUtil
import com.shreyd.co2tracker.model.GeocoderResponse
import com.shreyd.co2tracker.model.RoutesResponse
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import java.net.URL




class PublicTransport : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var routeResult: RoutesResponse
    private lateinit var polyline: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_public_transport)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapRoute) as SupportMapFragment?
        mapFragment?.getMapAsync(this)


        val lat = "38.9081476"
        val lon = "-77.2240058"
        val sUrl = "https://maps.googleapis.com/maps/api/geocode/json?latlng=$lat,$lon&result_type=route&key=${Constants.GMAPKEY}"

//
//        val url = URL(sUrl)
//
//        val request = Request.Builder().url(url).build()
//
//        println(request.toString())
////

//        okHttpClient.newCall(request).enqueue(object: Callback {
//            override fun onResponse(call: Call, response: Response) {
//
//                result = response.body?.string()
//                println(result)
//                val geocoderResp = gson.fromJson(result, GeocoderResponse::class.java)
//
//            }
//
//            override fun onFailure(call: Call, e: IOException) {
//                e.printStackTrace()
//            }
//
//        })



    }

    override fun onMapReady(map: GoogleMap) {
        println("--------MAP READY--------")

//        var okHttpClient: OkHttpClient = OkHttpClient()
//
//        var result: String? = null
//
        val gson = Gson()
//
//        val sUrl2 = "https://routes.googleapis.com/directions/v2:computeRoutes"
//
//        val json = "{\"origin\":{\"address\":\"1944 Horse Shoe Drive, Vienna, VA\"},\"destination\":{\"address\":\"7500 GEOINT Drive, Springfield, VA\"},\"travelMode\":\"TRANSIT\"}"
//
//        val body: RequestBody = json.toRequestBody("/application/json".toMediaTypeOrNull())
//        val url2 = URL(sUrl2)
//
//        val request2 = Request.Builder().post(body).url(url2).addHeader("Content-Type", "application/json").addHeader("X-Goog-Api-Key", Constants.GMAPKEY).addHeader("X-Goog-FieldMask", "routes.legs.polyline,routes.legs.steps.navigationInstruction,routes.legs.steps.transitDetails").build()


        CoroutineScope(Dispatchers.Main).launch {
//            okHttpClient.newCall(request2).enqueue(object: Callback {
//                override fun onResponse(call: Call, response: Response) {
//                    result = response.body?.string()
//                    routeResult = gson.fromJson(result, RoutesResponse::class.java)
//                    polyline = routeResult.routes[0].legs[0].polyline.encodedPolyline
//                    println(result)
//
//                }
//
//                override fun onFailure(call: Call, e: IOException) {
//                    e.printStackTrace()
//                }
//
//            })
            val response = " {\n" +
                    "   \"routes\": [\n" +
                    "     {\n" +
                    "       \"legs\": [\n" +
                    "         {\n" +
                    "           \"polyline\": {\n" +
                    "             \"encodedPolyline\": \"w`olFhvlvMmEiDe@OWCo@FaCz@aFzBSJx@rCA@EBoAsEu@wCe@oBOXgBnBQ?Ua@h@c@FLGMi@b@T`@g@b@iAf@}@Ru@D?S?R_FNgGVeAEq@OmC{@}CmAa@UcA}@c@m@w@gBi@oAcAqBmAcDmAiEgAsCkC_Ge@y@Gu@DeAhAoAr@cAv@cBh@wABw@j@kCTmAkF_Cy@w@c@s@]gAQaAK}A?{@FgAPoAZiAd@cAl@{@`BgBv@o@nAy@|Ao@tA[@LAMnBSzGYhA?zB`@f@S^EX@JFH\\\\R`GNxBR`DRjDETIPc@Vc@Fs@A_AMcA@mBR}BHaBHoARw@\\\\_@TGQFPgAz@c@h@s@pAe@tAw@zD_A`D}@jB_AnAoAjAUNQPfGxLZr@IHBEw@_BeB`BEKDJuIpI[b@o@jAm@r@_@VqAh@PpELXLnGGZDpDFbARjAHPaEvDyBxBsD}G_@w@W}@KeAAe@@kLBqDHmDTcDZcDg@U_@a@Yg@Ws@MqAIaCOwIMu@OcJ@_DHyDt@cHr@iGNcCDaCSgSEyDDoAJw@Lk@f@sAf@u@t@q@ZSl@Dt@WvGe@VQnAGl@Fv@Z\\\\VvKrKl@j@X_@tA{B@@MMf@w@\\\\[d@Kf@@d@PNLf@r@DV`ArA`@v@JTFDXQVIr@G`HInBEfA_KJCbNhBnFv@hHf@vMl@jGF|EIlFUvEc@dFk@tFaAdFkAdRyFrCo@xCc@zCUxCGlDH`EVnCh@`EdA~Bz@nJlE`@ZlAb@jCbBnBbBdAdArHbJrBhBjAx@`B|@fBt@|Af@jAVnBX`EPdCA|COnEm@xJyAhTcDzGaAjDU~NGj_@IbFAh\\\\G~PFzUEvJG~GEh_@CfFMlDOxE]pGs@|HoA|SqD~BWvBGpC?zDLnE`@pDh@`Ez@dEpApEfBvRhIzBv@vA`@jCn@nDb@|DP`D@nDMxD_@lCc@dDo@xCu@rCcAvBaA|HcFrFuD|DuC|LaJfa@sXbCmB|BaCdAqA|BuDbBsDhAgDt@iCTcAzL{z@|Fia@`B_KzFkUlBsI~EeNtEiLnDsHfB}Bb@_@l@_@z@WdAMlA?j@FnNdD`KlCdE~@~GtA|HhBl^~FzGbAvDZjGt@jFd@~ADf@GP_@?SGWy@_Bs@oB[oBS}BAiCN_Ct@}FdBmLxCeOt@}DTYPE\\\\A~@PvC|@\\\\@RC^SX]Ls@@q@a@uJ[aGEiAcAkFKO?TqC`RFtATrDO`@_@b@QFOBwEeAWxBYvF_@nHWpCDt@uBvP]tCIlB@pADrAXlCH\\\\Lt@n@xBzAtDvBfEr@bAbB`BtNpLrAvAr@nAb@`Aj@rBTvALhADlA@nBKt^?vAY?X?Oxb@?dE@~@JpB^`Dr@~Cj@~At@`BjAlB`AhAzDbEt@|@vAvCrAjEh@zBd@pCbAtJEX@ZNlBAbAC`@Ox@]dAy@~A_AlBO^Q|@A`ABb@Hl@Rj@tA`CfAtA`BmBpF}Gv@{@bAu@tAu@pBe@l@G`B?\\\\LvIl@xAIxAYl@WzA_AZWp@w@rB_DvDcFp@{@TEh@_@j@g@l@]d@QfAQhAK~EWxCWd@Kf@U`@WnAmAfCqCgBcEIHj@hAHEJ^p@tAj@u@LAJGFUFo@`@kJHwACsEKwFKqCM[}@}]Fs@CgBJmBLy@R{@d@cAz@cAbAi@v@[HKlC_BZYnEgGpB|C`@x@Nh@ZtBf@tCBfACdAMjATHv@f@t@z@NROTNUtD`FKJJK^v@N^XjAH`@CVIVOHoALUCMQMKQ?IJATLPZD`C]NQDUM_AYgAg@iAHKIJ{EuGg@i@w@g@LwABqBAc@y@mFWaAYq@qBaDMQpGoIPWXQSMGA_B_As@q@gBwBa@[gB_AkAe@cAm@aAeAm@gAcAwBs@iAk@u@s@q@yBqAmCgA}JaD}NwD_Cc@}E{@_KyA}D]sF]YzFa@dHW|AkC|Ie@fBW`BMxB?bATlD`BdKbC~NBPf@Mv@Wl@_@nA{AAH?AACj@o@NMHWAQIK\"\n" +
                    "           },\n" +
                    "           \"steps\": [\n" +
                    "             {\n" +
                    "               \"navigationInstruction\": {\n" +
                    "                 \"maneuver\": \"DEPART\",\n" +
                    "                 \"instructions\": \"Head northeast on Horse Shoe Dr toward Capo Ct\"\n" +
                    "               }\n" +
                    "             },\n" +
                    "             {\n" +
                    "               \"navigationInstruction\": {\n" +
                    "                 \"maneuver\": \"TURN_LEFT\",\n" +
                    "                 \"instructions\": \"Turn left onto Chain Bridge Rd\"\n" +
                    "               }\n" +
                    "             },\n" +
                    "             {\n" +
                    "               \"navigationInstruction\": {\n" +
                    "                 \"instructions\": \"Bus towards Tysons Cnr - Maple Ave\"\n" +
                    "               },\n" +
                    "               \"transitDetails\": {\n" +
                    "                 \"stopDetails\": {\n" +
                    "                   \"arrivalStop\": {\n" +
                    "                     \"name\": \"Westpark Dr & Greensboro Dr\",\n" +
                    "                     \"location\": {\n" +
                    "                       \"latLng\": {\n" +
                    "                         \"latitude\": 38.925343999999996,\n" +
                    "                         \"longitude\": -77.233735\n" +
                    "                       }\n" +
                    "                     }\n" +
                    "                   },\n" +
                    "                   \"arrivalTime\": \"2023-10-11T20:41:18Z\",\n" +
                    "                   \"departureStop\": {\n" +
                    "                     \"name\": \"Chain Bridge Rd & Horse Shoe D\",\n" +
                    "                     \"location\": {\n" +
                    "                       \"latLng\": {\n" +
                    "                         \"latitude\": 38.915459,\n" +
                    "                         \"longitude\": -77.239746\n" +
                    "                       }\n" +
                    "                     }\n" +
                    "                   },\n" +
                    "                   \"departureTime\": \"2023-10-11T20:20:08Z\"\n" +
                    "                 },\n" +
                    "                 \"localizedValues\": {\n" +
                    "                   \"arrivalTime\": {\n" +
                    "                     \"time\": {\n" +
                    "                       \"text\": \"4:41 PM\"\n" +
                    "                     },\n" +
                    "                     \"timeZone\": \"America/New_York\"\n" +
                    "                   },\n" +
                    "                   \"departureTime\": {\n" +
                    "                     \"time\": {\n" +
                    "                       \"text\": \"4:20 PM\"\n" +
                    "                     },\n" +
                    "                     \"timeZone\": \"America/New_York\"\n" +
                    "                   }\n" +
                    "                 },\n" +
                    "                 \"headsign\": \"Tysons Cnr - Maple Ave\",\n" +
                    "                 \"transitLine\": {\n" +
                    "                   \"agencies\": [\n" +
                    "                     {\n" +
                    "                       \"name\": \"Fairfax Connector\",\n" +
                    "                       \"phoneNumber\": \"+1 703-339-7200\",\n" +
                    "                       \"uri\": \"http://www.fairfaxcounty.gov/connector/routes/\"\n" +
                    "                     }\n" +
                    "                   ],\n" +
                    "                   \"name\": \"Maple Avenue - Tysons\",\n" +
                    "                   \"color\": \"#d2a3cb\",\n" +
                    "                   \"nameShort\": \"463\",\n" +
                    "                   \"textColor\": \"#ffffff\",\n" +
                    "                   \"vehicle\": {\n" +
                    "                     \"name\": {\n" +
                    "                       \"text\": \"Bus\"\n" +
                    "                     },\n" +
                    "                     \"type\": \"BUS\",\n" +
                    "                     \"iconUri\": \"//maps.gstatic.com/mapfiles/transit/iw2/6/bus2.png\"\n" +
                    "                   }\n" +
                    "                 },\n" +
                    "                 \"stopCount\": 13\n" +
                    "               }\n" +
                    "             },\n" +
                    "             {\n" +
                    "               \"navigationInstruction\": {\n" +
                    "                 \"maneuver\": \"DEPART\",\n" +
                    "                 \"instructions\": \"Head northeast on Westpark Dr toward Greensboro Dr\"\n" +
                    "               }\n" +
                    "             },\n" +
                    "             {\n" +
                    "               \"navigationInstruction\": {\n" +
                    "                 \"maneuver\": \"TURN_LEFT\",\n" +
                    "                 \"instructions\": \"Turn left onto Greensboro Dr\\nDestination will be on the right\"\n" +
                    "               }\n" +
                    "             },\n" +
                    "             {\n" +
                    "               \"navigationInstruction\": {\n" +
                    "                 \"instructions\": \"Bus towards Lorton VRE - Springfield\"\n" +
                    "               },\n" +
                    "               \"transitDetails\": {\n" +
                    "                 \"stopDetails\": {\n" +
                    "                   \"arrivalStop\": {\n" +
                    "                     \"name\": \"Saratoga Park & Ride\",\n" +
                    "                     \"location\": {\n" +
                    "                       \"latLng\": {\n" +
                    "                         \"latitude\": 38.746009,\n" +
                    "                         \"longitude\": -77.209373\n" +
                    "                       }\n" +
                    "                     }\n" +
                    "                   },\n" +
                    "                   \"arrivalTime\": \"2023-10-11T21:34:00Z\",\n" +
                    "                   \"departureStop\": {\n" +
                    "                     \"name\": \"Greensboro Dr & The Rotonda\",\n" +
                    "                     \"location\": {\n" +
                    "                       \"latLng\": {\n" +
                    "                         \"latitude\": 38.926144,\n" +
                    "                         \"longitude\": -77.233665\n" +
                    "                       }\n" +
                    "                     }\n" +
                    "                   },\n" +
                    "                   \"departureTime\": \"2023-10-11T20:50:56Z\"\n" +
                    "                 },\n" +
                    "                 \"localizedValues\": {\n" +
                    "                   \"arrivalTime\": {\n" +
                    "                     \"time\": {\n" +
                    "                       \"text\": \"5:34 PM\"\n" +
                    "                     },\n" +
                    "                     \"timeZone\": \"America/New_York\"\n" +
                    "                   },\n" +
                    "                   \"departureTime\": {\n" +
                    "                     \"time\": {\n" +
                    "                       \"text\": \"4:50 PM\"\n" +
                    "                     },\n" +
                    "                     \"timeZone\": \"America/New_York\"\n" +
                    "                   }\n" +
                    "                 },\n" +
                    "                 \"headsign\": \"Lorton VRE - Springfield\",\n" +
                    "                 \"transitLine\": {\n" +
                    "                   \"agencies\": [\n" +
                    "                     {\n" +
                    "                       \"name\": \"Fairfax Connector\",\n" +
                    "                       \"phoneNumber\": \"+1 703-339-7200\",\n" +
                    "                       \"uri\": \"http://www.fairfaxcounty.gov/connector/routes/\"\n" +
                    "                     }\n" +
                    "                   ],\n" +
                    "                   \"name\": \"Lorton - Springfield - Tysons\",\n" +
                    "                   \"color\": \"#999dce\",\n" +
                    "                   \"nameShort\": \"494\",\n" +
                    "                   \"textColor\": \"#ffffff\",\n" +
                    "                   \"vehicle\": {\n" +
                    "                     \"name\": {\n" +
                    "                       \"text\": \"Bus\"\n" +
                    "                     },\n" +
                    "                     \"type\": \"BUS\",\n" +
                    "                     \"iconUri\": \"//maps.gstatic.com/mapfiles/transit/iw2/6/bus2.png\"\n" +
                    "                   }\n" +
                    "                 },\n" +
                    "                 \"stopCount\": 18\n" +
                    "               }\n" +
                    "             },\n" +
                    "             {},\n" +
                    "             {\n" +
                    "               \"navigationInstruction\": {\n" +
                    "                 \"instructions\": \"Bus towards Franconia-Springfield\"\n" +
                    "               },\n" +
                    "               \"transitDetails\": {\n" +
                    "                 \"stopDetails\": {\n" +
                    "                   \"arrivalStop\": {\n" +
                    "                     \"name\": \"Fort Belvoir North Area\",\n" +
                    "                     \"location\": {\n" +
                    "                       \"latLng\": {\n" +
                    "                         \"latitude\": 38.755545999999995,\n" +
                    "                         \"longitude\": -77.195934999999992\n" +
                    "                       }\n" +
                    "                     }\n" +
                    "                   },\n" +
                    "                   \"arrivalTime\": \"2023-10-11T22:06:00Z\",\n" +
                    "                   \"departureStop\": {\n" +
                    "                     \"name\": \"Saratoga P&R\",\n" +
                    "                     \"location\": {\n" +
                    "                       \"latLng\": {\n" +
                    "                         \"latitude\": 38.745788,\n" +
                    "                         \"longitude\": -77.209763\n" +
                    "                       }\n" +
                    "                     }\n" +
                    "                   },\n" +
                    "                   \"departureTime\": \"2023-10-11T21:49:00Z\"\n" +
                    "                 },\n" +
                    "                 \"localizedValues\": {\n" +
                    "                   \"arrivalTime\": {\n" +
                    "                     \"time\": {\n" +
                    "                       \"text\": \"6:06 PM\"\n" +
                    "                     },\n" +
                    "                     \"timeZone\": \"America/New_York\"\n" +
                    "                   },\n" +
                    "                   \"departureTime\": {\n" +
                    "                     \"time\": {\n" +
                    "                       \"text\": \"5:49 PM\"\n" +
                    "                     },\n" +
                    "                     \"timeZone\": \"America/New_York\"\n" +
                    "                   }\n" +
                    "                 },\n" +
                    "                 \"headsign\": \"Franconia-Springfield\",\n" +
                    "                 \"transitLine\": {\n" +
                    "                   \"agencies\": [\n" +
                    "                     {\n" +
                    "                       \"name\": \"Fairfax Connector\",\n" +
                    "                       \"phoneNumber\": \"+1 703-339-7200\",\n" +
                    "                       \"uri\": \"http://www.fairfaxcounty.gov/connector/routes/\"\n" +
                    "                     }\n" +
                    "                   ],\n" +
                    "                   \"name\": \"Boston Blvd - Saratoga\",\n" +
                    "                   \"color\": \"#009f4f\",\n" +
                    "                   \"nameShort\": \"341\",\n" +
                    "                   \"textColor\": \"#ffffff\",\n" +
                    "                   \"vehicle\": {\n" +
                    "                     \"name\": {\n" +
                    "                       \"text\": \"Bus\"\n" +
                    "                     },\n" +
                    "                     \"type\": \"BUS\",\n" +
                    "                     \"iconUri\": \"//maps.gstatic.com/mapfiles/transit/iw2/6/bus2.png\"\n" +
                    "                   }\n" +
                    "                 },\n" +
                    "                 \"stopCount\": 6\n" +
                    "               }\n" +
                    "             },\n" +
                    "             {\n" +
                    "               \"navigationInstruction\": {\n" +
                    "                 \"maneuver\": \"DEPART\",\n" +
                    "                 \"instructions\": \"Head southeast\\nRestricted usage road\\nDestination will be on the right\"\n" +
                    "               }\n" +
                    "             }\n" +
                    "           ]\n" +
                    "         }\n" +
                    "       ]\n" +
                    "     }\n" +
                    "   ]\n" +
                    " }\n" +
                    "\n"

            routeResult = gson.fromJson(response, RoutesResponse::class.java)
            polyline = routeResult.routes[0].legs[0].polyline.encodedPolyline

            delay(4000)

            val resp = PolyUtil.decode(polyline)
            println(resp)
            val polyO = PolylineOptions()
            resp.forEach {
                polyO.add(it)
            }
            map.addPolyline(polyO)

            val markers = routeResult.routes[0].legs[0].steps
            println(markers.size)
            val markerO = MarkerOptions()
            markers.forEach {
                if(it.transitDetails != null) {
                    map.addMarker(markerO.position(LatLng(
                        it.transitDetails.stopDetails.arrivalStop.location.latLng.latitude,
                        it.transitDetails.stopDetails.arrivalStop.location.latLng.longitude)).title("Marker!"))
                }
            }

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(resp[0], 11f))

            val stops: TextView = findViewById(R.id.stops)
            //Transit Type + Depart Dest + Arrival Dest
            markers.forEach {
                if(it.transitDetails != null) {
                    val someString = "${it.transitDetails.transitLine.vehicle.name.text}       ${it.transitDetails.stopDetails.departureStop.name} -> ${it.transitDetails.stopDetails.arrivalStop.name}\n${it.transitDetails.transitLine.name}\n${it.transitDetails.transitLine.nameShort}\n\n"
                    stops.text = stops.text as String + someString
                }

            }
        }




    }
    //routes.legs.steps.transitDetails
    //routes.legs.steps.polyline,routes.legs.steps.navigationInstruction,routes.legs.steps.transitDetails
}