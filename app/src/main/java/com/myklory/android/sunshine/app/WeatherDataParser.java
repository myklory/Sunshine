package com.myklory.android.sunshine.app;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by myklory on 2016/8/18.
 */
public class WeatherDataParser {



    /**
     * 将timestamp转换为能识别的日期
     * @param time
     * @return
     */
    public static String getReadableDate(long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd");
        return dateFormat.format(time);
    }

    public static String formatTemperature(double max, double min) {
        return Math.round(max) + "/" + Math.round(min);
    }

    /**
     * 通过json解析对应天的最高温度
     * @param weatherJsonStr
     * @param dayIndex
     * @return
     */
    public static double getMaxTemperatureForDay(String weatherJsonStr, int dayIndex){
        try {
            JSONObject jsonObject = new JSONObject(weatherJsonStr);
            JSONArray jsonArray = jsonObject.getJSONArray("list");
            if (jsonArray.length() >= dayIndex){
                JSONObject dayJson = jsonArray.getJSONObject(dayIndex);
                return dayJson.getJSONObject("temp").getDouble("max");
            }
            return -1;
        } catch (JSONException e) {
            Log.e("getMaxTemperatureForDay", e.getMessage(), e);
        }
        return -1;
    }

    /**
     * 从JSON中提取天气数据
     * @param weatherJsonStr
     * @return
     */
    public static String[] getWeatherDataFromJson(String weatherJsonStr){
        String[] weathers = null;
        final String OWM_LIST = "list";
        final String OWM_WEATHER = "weather";
        final String OWM_TEMP = "temp";
        final String OWM_MAIN = "main";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";
        final String OWM_DT = "dt";

        //取今天日期
        Calendar calendar = Calendar.getInstance();

        try {
            JSONObject weatherJson = new JSONObject(weatherJsonStr);
            JSONArray daysWeather = weatherJson.getJSONArray(OWM_LIST);

            weathers = new String[daysWeather.length()];
            for (int i = 0; i < daysWeather.length(); ++i) {
                String day;
                String description;
                String highAndLow;
                JSONObject dayJson = daysWeather.getJSONObject(i);
                //获取日期
                //获取今天日期
                //long dateTime =
                day = getReadableDate(calendar.getTime().getTime());
                calendar.add(calendar.DATE, 1);
                //获取天气情况
                description = dayJson.getJSONArray(OWM_WEATHER).getJSONObject(0).getString(OWM_MAIN);
                //获取最高温度和最低温度
                JSONObject tempJson = dayJson.getJSONObject(OWM_TEMP);
                highAndLow = formatTemperature(tempJson.getDouble(OWM_MAX), tempJson.getDouble(OWM_MIN));
                weathers[i] = day + " - " + description + " - " + highAndLow;
            }

        } catch (JSONException e) {
            Log.e("getWeatherDataFromJson", e.getMessage(), e);
        }
        return weathers;
    }

}
