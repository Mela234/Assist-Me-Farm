package com.cropdoc.app.data.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsMessage
import android.util.Log
import com.cropdoc.app.data.db.CropDocDatabase
import com.cropdoc.app.data.model.WeatherData
import com.cropdoc.app.data.repository.WeatherRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class WeatherSmsReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "WeatherSMS"
        const val WEATHER_TYPE = "CROPDOC_WEATHER"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        // ── Concatenate all PDU parts into one full message ───────────────
        val pdus = intent.extras?.get("pdus") as? Array<*> ?: return
        val format = intent.extras?.getString("format")

        val fullMessage = pdus.joinToString("") { pdu ->
            SmsMessage.createFromPdu(pdu as ByteArray, format).messageBody ?: ""
        }

        Log.d(TAG, "Full SMS assembled: $fullMessage")

        if (fullMessage.contains(WEATHER_TYPE)) {
            parseAndSave(context, fullMessage)
        }
    }

    private fun parseAndSave(context: Context, smsBody: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val jsonStart = smsBody.indexOf("{")
                val jsonEnd = smsBody.lastIndexOf("}") + 1
                if (jsonStart == -1 || jsonEnd == 0) return@launch

                val json = JSONObject(smsBody.substring(jsonStart, jsonEnd))

                if (json.optString("type") != WEATHER_TYPE) return@launch

                val weather = WeatherData(
                    temperature = json.getDouble("temperature").toFloat(),
                    humidity    = json.getDouble("humidity").toFloat(),
                    rainfall    = json.getDouble("rainfall").toFloat(),
                    windSpeed   = json.getDouble("windSpeed").toFloat(),
                    forecast    = json.getString("forecast"),
                    location    = json.getString("location")
                )

                val db = CropDocDatabase.getInstance(context)
                WeatherRepository(db.weatherDao()).saveWeather(weather)

                Log.d(TAG, "Weather saved: $weather")

            } catch (e: Exception) {
                Log.e(TAG, "Failed to parse weather SMS", e)
            }
        }
    }
}