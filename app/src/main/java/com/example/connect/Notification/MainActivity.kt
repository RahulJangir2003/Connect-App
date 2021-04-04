package com.example.connect.Notification

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.connect.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.onesignal.OneSignal
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import kotlin.random.Random

private const val CHANNEL_ID = "my_channel"

class MainActivity : AppCompatActivity() {
    lateinit var notificationManager: NotificationManagerCompat
    lateinit var uid:String;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        uid = FirebaseAuth.getInstance().currentUser!!.uid
        //f394637a-b77c-4744-9147-c3391fce7f06
        OneSignal.startInit(this).init()
        OneSignal.setSubscription(true)
        OneSignal.idsAvailable(OneSignal.IdsAvailableHandler { userId, registrationId ->
            FirebaseDatabase.getInstance().reference.child("user").child(uid).child("notificationKey").setValue(userId);
        })
        OneSignal.setInFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)


        btnSend.setOnClickListener {
            print("herer ")
            val ref:DatabaseReference = FirebaseDatabase.getInstance().reference.child("user").child(uid).child("notificationKey");
           ref.addValueEventListener(
             object : ValueEventListener{
                 override fun onCancelled(error: DatabaseError) {
                     Log.d("DataBaseError",error.toString())
                 }

                 override fun onDataChange(snapshot: DataSnapshot) {
                    sendNotification("annsdfsdf","asdfasf",snapshot.value.toString())
                 }

             }
           )
        }
    }
    private fun sendNotification(massage:String, heading:String ,notificationKey:String ){
        Log.d("notificationKeyShow",notificationKey)
        try {
            val notificationContent = JSONObject(
                "{'contents':{'en':'" + massage + "'},"+
                        "'include_player_ids':['" + notificationKey + "']," +
                        "'headings':{'en': '" + heading + "'}}")
            Log.d("jsonError","no error")
            OneSignal.postNotification(notificationContent,null)

        }catch (e:JSONException){
            Log.d("jsonError","$e")
            print("error ocuurrerd $e")
        }
    }

}