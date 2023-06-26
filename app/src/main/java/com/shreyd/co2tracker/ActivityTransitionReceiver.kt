package com.shreyd.co2tracker

//import io.karn.notify.Notify
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity
import org.greenrobot.eventbus.EventBus
import java.text.SimpleDateFormat
import java.util.*

class ActivityTransitionReceiver : BroadcastReceiver() {

    var ChannelID = "channel1"

    class OnReceiverEvent(val data: String) {
        val eventos = data

        fun getEvents(): String {
            return eventos
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (ActivityTransitionResult.hasResult(intent)) {
            val result = ActivityTransitionResult.extractResult(intent)
            println(result?.transitionEvents)
            result?.let {
                result.transitionEvents.forEach { event ->
                    // Info about activity
                    val info =
                        "Transition: " + ActivityTransitionsUtil.toActivityString(event.activityType) +
                                " (" + ActivityTransitionsUtil.toTransitionType(event.transitionType) + ")" + "   " +
                                SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())
                    // notification details
//                    Notify
//                        .with(context)
//                        .content {
//                            title = "Activity Detected"
//                            text = "I can see you are in ${
//                                ActivityTransitionsUtil.toActivityString(
//                                    event.activityType
//                                )
//                            } state"
//                        }
//                        .show(id = Constants.ACTIVITY_TRANSITION_NOTIFICATION_ID)
                    println("This is Shrey ${ActivityTransitionsUtil.toActivityString(event.activityType)}")
                    Toast.makeText(context, info, Toast.LENGTH_LONG).show()

                    var check = 0

                    println(check)
                    EventBus.getDefault().post(OnReceiverEvent(info))
                    println("sent")


                }
            }
        }

    }

}