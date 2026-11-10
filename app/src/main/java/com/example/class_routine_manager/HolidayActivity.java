package com.example.class_routine_manager;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.class_routine_manager.databinding.ActivityHolidayBinding;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HolidayActivity extends AppCompatActivity {

    private ActivityHolidayBinding binding;
    private String lastHolidayData = "No data fetched yet.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHolidayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        // ১. HTTP Request & JSON Parsing (Lab 11)
        fetchHolidays();

        // ২. Implicit Intent (Share Routine)
        binding.btnShareRoutine.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My Holiday Routine");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out my upcoming holidays:\n\n" + lastHolidayData);
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        });

        // ৩. XML Parsing (Lab 11)
        binding.btnParseXml.setOnClickListener(v -> parseXmlData());
    }

    private void fetchHolidays() {
        new Thread(() -> {
            try {
                URL url = new URL("https://date.nager.at/api/v3/NextPublicHolidays/BD");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONArray jsonArray = new JSONArray(response.toString());
                StringBuilder resultText = new StringBuilder();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject holiday = jsonArray.getJSONObject(i);
                    resultText.append("📅 ").append(holiday.getString("date"))
                            .append("\n🏷️ ").append(holiday.getString("name")).append("\n\n");
                }

                lastHolidayData = resultText.toString();

                runOnUiThread(() -> {
                    binding.tvLoading.setVisibility(View.GONE);
                    binding.tvHolidayResult.setText(lastHolidayData);
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    binding.tvLoading.setText("Error fetching data.");
                    Toast.makeText(HolidayActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    // XML Parsing Demo
    private void parseXmlData() {
        String xmlData = "<routine><class><subject>Math</subject><teacher>Jamali Sir</teacher></class><class><subject>Algorithm</subject><teacher>Nihal Sir</teacher></class></routine>";
        StringBuilder parsedResult = new StringBuilder("📌 XML Parsed Data:\n\n");

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xmlData));

            int eventType = parser.getEventType();
            String currentTag = "";

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    currentTag = parser.getName();
                } else if (eventType == XmlPullParser.TEXT) {
                    if (!parser.getText().trim().isEmpty()) {
                        parsedResult.append(currentTag).append(": ").append(parser.getText().trim()).append("\n");
                    }
                }
                eventType = parser.next();
            }

            binding.tvHolidayResult.setText(parsedResult.toString());
            Toast.makeText(this, "XML Parsed Successfully!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, "XML Parse Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}