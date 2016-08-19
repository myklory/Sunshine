package com.myklory.android.sunshine.app;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    //listview适配器
    ArrayAdapter<String> foreCastAdapter;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //告诉fragment需要一个菜单。
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //从菜单资源加载菜单
        inflater.inflate(R.menu.forecastfragment, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //判断id是不是我们点击的菜单id
        if (id == R.id.action_refresh){
            new FetchWeatherTask().execute("Chengdu");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        /*ArrayList<String> foreCast = new ArrayList<String>();
        foreCast.add("today-sunny-88/67");
        foreCast.add("tomorrow-cloud-86/63");*/
        new FetchWeatherTask().execute("Chengdu");
        String[] weathers = {
                "today-sunny-88/67",
                "tomorrow-cloud-86/63",
                "San-froggy-83/60",
                "Sun-sunny-89/68"

        };
        ArrayList<String> foreCastList = new ArrayList<String>(Arrays.asList(weathers));

        foreCastAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview,
                foreCastList);

        ListView listView = (ListView)rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(foreCastAdapter);

        //设置Listview点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * 鼠标点击事件
             * @param adapterView 事件点击的parentview
             * @param view 点击的view，这里是每个listview中的item，即textView
             * @param i 在adpter中的位置，即foreCastAdapter中的位置
             * @param l
             */
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast toast = Toast.makeText(adapterView.getContext(), ((TextView)view).getText(), Toast.LENGTH_SHORT);
                toast.show();
                //新建一个Intent，使用新的Activity来初始化他
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                //传递数据给DetailActivity
                intent.putExtra(Intent.EXTRA_INTENT, foreCastAdapter.getItem(i));
                //启动Activity
                startActivity(intent);
            }
        });
        return rootView;
    }



    private class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        /**
         * 执行查询天气
         * @param strings
         * @return
         */
        @Override
        protected String[] doInBackground(String... strings) {
            String foreCastJson = getForeCast(strings[0]);
            String[] weathers = WeatherDataParser.getWeatherDataFromJson(foreCastJson);
            Log.d("getForeCast", weathers.toString());
            //parseWeather(foreCastJson);
            return weathers;
        }

        /**
         * 得到天气结果后更新界面
         * @param strings
         */
        @Override
        protected void onPostExecute(String[] strings) {
        //    super.onPostExecute(strings);
            foreCastAdapter.clear();
            foreCastAdapter.addAll(strings);
        }

        private void parseWeather(String foreCastJson){
            double maxTemp = WeatherDataParser.getMaxTemperatureForDay(foreCastJson, 0);
            Log.d("parseWeather", Double.toString(maxTemp));
        }

        private String getForeCast(String city) {
            String forecastJsonStr = null;
            BufferedReader reader = null;
            HttpURLConnection conn = null;
            InputStream is = null;

            //设置Uri变量
            final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily";
            final String QUERY_PARAM = "q";
            final String FORMAT_PARAM = "mode";
            final String UNITS_PARAM = "units";
            final String DAYS_PARAM = "cnt";
            final String APP_ID_PARAM = "appid";

            //设置Uri参数
            String mode = "json";
            String units = "metric";
            Integer numDays = 7;
            String appid = "540472accf47d8ee6ee6d8955d638205";

            //通过变量和参数构造Uri
            Uri uriBuilder = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, city)
                    .appendQueryParameter(FORMAT_PARAM, mode)
                    .appendQueryParameter(UNITS_PARAM, units)
                    .appendQueryParameter(DAYS_PARAM, numDays.toString())
                    .appendQueryParameter(APP_ID_PARAM, appid).build();

            try{
                URL url = new URL(uriBuilder.toString());
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();
                is = conn.getInputStream();

                StringBuffer buffer = new StringBuffer();
                reader = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = reader.readLine()) != null){
                    buffer.append(line + "\n");
                }
                if (buffer.length() != 0){
                    forecastJsonStr = buffer.toString();
                }
            } catch (MalformedURLException e){
                Log.e("MainActivityFragment", "getForeCast", e);
            } catch (ProtocolException e){
                Log.e("MainActivityFragment", "getForeCast", e);
            } catch (IOException e) {
                Log.e("MainActivityFragment", "getForeCast", e);
            } catch (Exception e) {
                Log.e("MainActivityFragment", "getForeCast", e);
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e("MainActivityFragment", "getForeCast", e);
                    }
                }
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        Log.e("MainActivityFragment", "getForeCast", e);
                    }
                }
                if (conn != null) {
                    conn.disconnect();
                }
            }

            return forecastJsonStr;
        }
    }
}
