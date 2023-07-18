package com.shreyd.co2tracker

//import io.karn.notify.Notify
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.android.gms.location.ActivityTransitionEvent
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity
import org.greenrobot.eventbus.EventBus
import java.text.SimpleDateFormat
import java.util.*

class ActivityTransitionReceiver : BroadcastReceiver() {

    var ChannelID = "channel1"

    class OnReceiverEvent(private val data: List<String>) {
        private val event1 = data

        fun getEvents(): List<String> {
            return event1
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (ActivityTransitionResult.hasResult(intent)) {
            val result = ActivityTransitionResult.extractResult(intent)
            println(result?.transitionEvents)
            result?.let {
                result.transitionEvents.forEach { event ->
                    // Info about activity
                    val info = listOf(ActivityTransitionsUtil.toActivityString(event.activityType), ActivityTransitionsUtil.toTransitionType(event.transitionType))
                    val SInfo = "Transition: " + ActivityTransitionsUtil.toActivityString(event.activityType) +
                            " (" + ActivityTransitionsUtil.toTransitionType(event.transitionType) + ")" + "   " +
                            SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())

                    println("This is Shrey ${ActivityTransitionsUtil.toActivityString(event.activityType)}")
                    Toast.makeText(context, SInfo, Toast.LENGTH_LONG).show()

                    if (ActivityTransitionsUtil.toActivityString(event.activityType) == "IN VEHICLE") {
                        var check = 0

                        //print statement is for debugging purposes
                        println(check)
                        EventBus.getDefault().post(OnReceiverEvent(info))
                        println("sent")
                    }
                }
            }
        }

    }

}