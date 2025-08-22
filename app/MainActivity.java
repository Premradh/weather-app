package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    EditText cityInput;
    Button getWeatherBtn;
    TextView weatherResult;

    String API_KEY = "YOUR_OPENWEATHERMAP_API_KEY"; // Replace with your API key

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityInput = findViewById(R.id.cityInput);
        getWeatherBtn = findViewById(R.id.getWeatherBtn);
        weatherResult = findViewById(R.id.weatherResult);

        getWeatherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = cityInput.getText().toString().trim();
                if (!city.isEmpty()) {
                    fetchWeather(city);
                } else {
                    weatherResult.setText("Please enter a city name!");
                }
            }
        });
    }

    private void fetchWeather(String city) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city +
                     "&appid=" + API_KEY + "&units=metric";

        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> weatherResult.setText("Failed to fetch weather data"));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONObject json = new JSONObject(responseData);
                        String temp = json.getJSONObject("main").getString("temp");
                        String desc = json.getJSONArray("weather").getJSONObject(0).getString("description");

                        runOnUiThread(() -> weatherResult.setText(
                                "Temperature: " + temp + "°C\n" +
                                "Condition: " + desc
                        ));

                    } catch (Exception e) {
                        runOnUiThread(() -> weatherResult.setText("Error parsing weather data"));
                    }
                }
            }
        });
    }
}
