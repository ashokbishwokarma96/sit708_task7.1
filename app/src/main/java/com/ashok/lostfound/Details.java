package com.ashok.lostfound;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ashok.lostfound.database.DatabaseHelper;
import com.ashok.lostfound.model.LostFound;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Details extends AppCompatActivity {
    private RadioButton radioLost, radioFound;
    private Button  btnSave;
    private Calendar calendar;
    private EditText User_name, User_phone, User_desc,btnDatePicker,User_location;
    private RadioGroup radioGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        radioGroup = findViewById(R.id.radioGroup);
        radioLost = findViewById(R.id.radioLost);
        radioFound = findViewById(R.id.radioFound);
        btnDatePicker = findViewById(R.id.btnDatePicker);
        btnSave = findViewById(R.id.btnSave);
        calendar = Calendar.getInstance();
        User_name = findViewById(R.id.etName);
        User_phone = findViewById(R.id.etPhone);
        User_location=findViewById(R.id.etLocation);
        User_desc = findViewById(R.id.etDescription);

        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                showDatePickerDialog();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDetails();
            }
        });
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioLost) {
                radioLost.setChecked(true);
                radioFound.setChecked(false);
            } else if (checkedId == R.id.radioFound) {
                radioLost.setChecked(false);
                radioFound.setChecked(true);
            }
        });
    }
    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        if (calendar.after(Calendar.getInstance())) {
                            Toast.makeText(getApplicationContext(), "User cant select FUTURE date", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd", Locale.getDefault());
                        String selectedDate = dateFormat.format(calendar.getTime());
                        btnDatePicker.setText(selectedDate);

                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }
    private void saveDetails() {
        String postType = radioLost.isChecked() ? "LOST" : "FOUND";
        String name =User_name.getText().toString();
        String phone=User_phone.getText().toString();
        String desc = User_desc.getText().toString();
        String date = btnDatePicker.getText().toString();
        String location=User_location.getText().toString();
        // Implementing database
        if (postType.isEmpty()||date.isEmpty()){
            Toast.makeText(this, "Name and Date must not be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        Long rowID= new DatabaseHelper(Details.this).insertData(new LostFound(postType,name,  phone,  desc,  date,  location ));
        if (rowID != -1) {
            Toast.makeText(Details.this, "Details saved successfully", Toast.LENGTH_SHORT).show();
            clear();
            Intent intent = new Intent(Details.this, MainActivity.class);
            // Pass any necessary data using intent.putExtra() if needed
            startActivity(intent);
        } else {
            Toast.makeText(Details.this, "Failed to save details", Toast.LENGTH_SHORT).show();
        }
    }
    private void clear(){
        radioLost.setChecked(false);
        radioFound.setChecked(false);
        btnSave.setText("");
        User_name.setText("");
        User_phone.setText("");
        User_location.setText("");
        User_desc.setText("");
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        View focusedView = getCurrentFocus();
        if (focusedView != null) {
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        }
    }
}