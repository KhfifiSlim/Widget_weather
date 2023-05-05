package com.example.tp5;


import static android.content.Context.MODE_PRIVATE;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class NewAppWidget extends AppWidgetProvider {
    private static final String ACTION_SIMPLEAPPWIDGET = "ACTION_BROADCASTWIDGETSAMPLE";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,int appWidgetId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String cityname = sharedPreferences.getString("cityName", "");        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.weatherapi.com/v1/forecast.json?key=8384ce731c8c416892e111040231802&q="+cityname+"&days=1&aqi=yes&alerts=yes").build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {

            }
            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                try {
                    String responseData = response.body().string();
                    JSONObject json = new JSONObject(responseData);
                    String temperature = json.getJSONObject("current").getString("temp_c")+"°c";
                    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
                    views.setTextViewText(R.id.tvWidget, temperature);
                    views.setTextViewText(R.id.cityname, cityname);
                    ComponentName appWidget = new ComponentName(context, NewAppWidget.class);
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    appWidgetManager.updateAppWidget(appWidget, views);
                } catch (JSONException e) {}
            }
        });

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (ACTION_SIMPLEAPPWIDGET.equals(intent.getAction())) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
            views.setTextViewText(R.id.tvWidget, "");
            ComponentName appWidget = new ComponentName(context, NewAppWidget.class);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            appWidgetManager.updateAppWidget(appWidget, views);
        }
    }
}