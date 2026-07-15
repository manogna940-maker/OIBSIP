package com.oibsip.unitconverter;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private Spinner categorySpinner, fromSpinner, toSpinner;
    private EditText valueInput;
    private TextView resultText;
    private final String[] categories = {"Length", "Weight", "Temperature"};
    private final String[][] units = {
            {"Metre", "Kilometre", "Centimetre", "Inch", "Foot"},
            {"Kilogram", "Gram", "Pound"},
            {"Celsius", "Fahrenheit", "Kelvin"}
    };

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        categorySpinner = findViewById(R.id.categorySpinner);
        fromSpinner = findViewById(R.id.fromSpinner);
        toSpinner = findViewById(R.id.toSpinner);
        valueInput = findViewById(R.id.valueInput);
        resultText = findViewById(R.id.resultText);
        Button convertButton = findViewById(R.id.convertButton);

        categorySpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories));
        categorySpinner.setOnItemSelectedListener(new SimpleItemSelectedListener(position -> updateUnitSpinners(position)));
        convertButton.setOnClickListener(v -> convert());
        updateUnitSpinners(0);
    }

    private void updateUnitSpinners(int category) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, units[category]);
        fromSpinner.setAdapter(adapter);
        toSpinner.setAdapter(adapter);
        if (units[category].length > 1) toSpinner.setSelection(1);
        resultText.setText("Result will appear here");
    }

    private void convert() {
        String input = valueInput.getText().toString().trim();
        if (input.isEmpty()) { Toast.makeText(this, "Please enter a value", Toast.LENGTH_SHORT).show(); return; }
        try {
            double value = Double.parseDouble(input);
            int category = categorySpinner.getSelectedItemPosition();
            String from = fromSpinner.getSelectedItem().toString();
            String to = toSpinner.getSelectedItem().toString();
            double result = category == 0 ? convertLength(value, from, to)
                    : category == 1 ? convertWeight(value, from, to)
                    : convertTemperature(value, from, to);
            resultText.setText(String.format(Locale.getDefault(), "%.4f %s", result, to));
        } catch (IllegalArgumentException ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private double convertLength(double value, String from, String to) {
        double metres;
        switch (from) {
            case "Kilometre": metres = value * 1000; break;
            case "Centimetre": metres = value / 100; break;
            case "Inch": metres = value * 0.0254; break;
            case "Foot": metres = value * 0.3048; break;
            default: metres = value;
        }
        switch (to) {
            case "Kilometre": return metres / 1000;
            case "Centimetre": return metres * 100;
            case "Inch": return metres / 0.0254;
            case "Foot": return metres / 0.3048;
            default: return metres;
        }
    }

    private double convertWeight(double value, String from, String to) {
        double kilograms = from.equals("Gram") ? value / 1000 : from.equals("Pound") ? value * 0.45359237 : value;
        return to.equals("Gram") ? kilograms * 1000 : to.equals("Pound") ? kilograms / 0.45359237 : kilograms;
    }

    private double convertTemperature(double value, String from, String to) {
        double celsius = from.equals("Fahrenheit") ? (value - 32) * 5 / 9 : from.equals("Kelvin") ? value - 273.15 : value;
        if (celsius < -273.15) throw new IllegalArgumentException("Value is below absolute zero");
        return to.equals("Fahrenheit") ? celsius * 9 / 5 + 32 : to.equals("Kelvin") ? celsius + 273.15 : celsius;
    }
}
