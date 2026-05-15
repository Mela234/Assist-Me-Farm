package com.cropdoc.app.data.sms;

/**
 * Listens for incoming SMS from your backend.
 * Expected SMS format (JSON):
 * {
 *  "type": "CROPDOC_WEATHER",
 *  "temperature": 24.5,
 *  "humidity": 68.0,
 *  "rainfall": 2.5,
 *  "windSpeed": 12.0,
 *  "forecast": "Partly cloudy with light showers expected in the afternoon",
 *  "location": "Harare"
 * }
 */
@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\u0018\u0000 \r2\u00020\u0001:\u0001\rB\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0018\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0016J\u0018\u0010\n\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u000b\u001a\u00020\fH\u0002\u00a8\u0006\u000e"}, d2 = {"Lcom/cropdoc/app/data/sms/WeatherSmsReceiver;", "Landroid/content/BroadcastReceiver;", "<init>", "()V", "onReceive", "", "context", "Landroid/content/Context;", "intent", "Landroid/content/Intent;", "parseAndSave", "smsBody", "", "Companion", "app_debug"})
public final class WeatherSmsReceiver extends android.content.BroadcastReceiver {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "WeatherSMS";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String WEATHER_TYPE = "CROPDOC_WEATHER";
    @org.jetbrains.annotations.NotNull()
    public static final com.cropdoc.app.data.sms.WeatherSmsReceiver.Companion Companion = null;
    
    public WeatherSmsReceiver() {
        super();
    }
    
    @java.lang.Override()
    public void onReceive(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    android.content.Intent intent) {
    }
    
    private final void parseAndSave(android.content.Context context, java.lang.String smsBody) {
    }
    
    @kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0007"}, d2 = {"Lcom/cropdoc/app/data/sms/WeatherSmsReceiver$Companion;", "", "<init>", "()V", "TAG", "", "WEATHER_TYPE", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}