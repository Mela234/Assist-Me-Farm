package com.cropdoc.app.data.agent

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.CalendarContract
import android.provider.Telephony
import android.util.Log
import com.google.ai.edge.litertlm.Tool
import com.google.ai.edge.litertlm.ToolParam
import com.google.ai.edge.litertlm.ToolSet
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class CropDocToolSet(private val context: Context) : ToolSet {

    companion object {
        private const val TAG = "CropDocTools"
        const val NOTIFICATION_CHANNEL_ID = "cropdoc_reminders"
    }

    // ── Save Note ─────────────────────────────────────────────────────────────

    @Tool(description = "Save a note to the phone's notes app. Use this when the farmer wants to record an observation, log an activity, or save information about their farm.")
    fun saveNote(
        @ToolParam(description = "Short title for the note e.g. 'Applied lime to North Field'")
        title: String,
        @ToolParam(description = "Full content of the note")
        content: String
    ): Map<String, Any> {
        return try {
            // Try Google Keep first, fall back to generic ACTION_INSERT
            val intent = Intent(Intent.ACTION_INSERT).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, title)
                putExtra(Intent.EXTRA_TEXT, content)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            Log.d(TAG, "Note opened: $title")
            mapOf("status" to "success", "message" to "Note opened in your notes app")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open notes app", e)
            mapOf("status" to "error", "message" to "Could not open notes app: ${e.message}")
        }
    }

    // ── Set Reminder ──────────────────────────────────────────────────────────

    @Tool(description = "Set a reminder notification that will alert the farmer at a specific time. Use this for irrigation reminders, fertilizer application, harvest checks, etc.")
    fun setReminder(
        @ToolParam(description = "ISO 8601 datetime string e.g. '2024-01-15T06:00:00'. If only a time is mentioned (e.g. tomorrow 6am), construct the full datetime.")
        time: String,
        @ToolParam(description = "The reminder message the farmer will see in the notification")
        message: String
    ): Map<String, Any> {
        return try {
            ensureNotificationChannel()

            val triggerTime = parseDateTime(time)
            if (triggerTime <= System.currentTimeMillis()) {
                return mapOf("status" to "error", "message" to "Reminder time is in the past")
            }

            val notificationIntent = Intent(context, ReminderReceiver::class.java).apply {
                putExtra("message", message)
                putExtra("title", "CropDoc Reminder")
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                System.currentTimeMillis().toInt(),
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                // Fall back to inexact alarm
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            }

            Log.d(TAG, "Reminder set for $time: $message")
            mapOf("status" to "success", "message" to "Reminder set for $time")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set reminder", e)
            mapOf("status" to "error", "message" to "Could not set reminder: ${e.message}")
        }
    }

    // ── Create Calendar Event ─────────────────────────────────────────────────

    @Tool(description = "Create a calendar event in the farmer's native calendar app. Use this for planting dates, harvest days, market days, field inspections, etc.")
    fun createCalendarEvent(
        @ToolParam(description = "Title of the event e.g. 'Harvest Zone A Maize'")
        title: String,
        @ToolParam(description = "ISO 8601 date string e.g. '2024-01-15'")
        date: String,
        @ToolParam(description = "Time of event in HH:mm format e.g. '06:00'. Default: 08:00")
        time: String = "08:00",
        @ToolParam(description = "Optional notes or description for the event")
        notes: String? = null
    ): Map<String, Any> {
        return try {
            val startMillis = parseDateTime("${date}T${time}:00")

            val intent = Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(CalendarContract.Events.TITLE, title)
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
                putExtra(CalendarContract.EXTRA_EVENT_END_TIME, startMillis + 60 * 60 * 1000) // 1 hour
                if (!notes.isNullOrBlank()) {
                    putExtra(CalendarContract.Events.DESCRIPTION, notes)
                }
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            Log.d(TAG, "Calendar event opened: $title on $date")
            mapOf("status" to "success", "message" to "Calendar event opened for $title on $date")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create calendar event", e)
            mapOf("status" to "error", "message" to "Could not create calendar event: ${e.message}")
        }
    }

    // ── Send SMS ──────────────────────────────────────────────────────────────

    @Tool(description = "Open the SMS app pre-filled with a message for the farmer to review and send. Use this when the farmer wants to contact a grain buyer, agricultural supplier, or specialist.")
    fun sendSms(
        @ToolParam(description = "Phone number to send to e.g. '+263771234567'")
        phoneNumber: String,
        @ToolParam(description = "The message text to pre-fill")
        message: String
    ): Map<String, Any> {
        return try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:${phoneNumber}")
                putExtra("sms_body", message)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            Log.d(TAG, "SMS app opened for $phoneNumber")
            mapOf("status" to "success", "message" to "SMS app opened — please review and send")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open SMS app", e)
            mapOf("status" to "error", "message" to "Could not open SMS app: ${e.message}")
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun parseDateTime(isoString: String): Long {
        return try {
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
            val dateTime = LocalDateTime.parse(isoString, formatter)
            dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        } catch (e: Exception) {
            // fallback: 1 hour from now
            System.currentTimeMillis() + 3600 * 1000
        }
    }

    private fun ensureNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "CropDoc Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Farm reminders set by CropDoc"
            }
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }
}