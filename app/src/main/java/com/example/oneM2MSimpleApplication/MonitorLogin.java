package com.example.oneM2MSimpleApplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class MonitorLogin extends AppCompatActivity {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private CheckBox checkBox;

    private EditText usernameText;
    private EditText passwordText;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_login);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        checkBox = findViewById(R.id.checkBox);
        usernameText = findViewById(R.id.editText_username);
        passwordText = findViewById(R.id.editText_Password);
        login = findViewById(R.id.button_login);

        boolean isRemember = preferences.getBoolean("remember_password", false);
        if (isRemember) {
            String username = preferences.getString("username", "");
            String password = preferences.getString("password", "");
            usernameText.setText(username);
            passwordText.setText(password);
            checkBox.setChecked(true);
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameText.getText().toString();
                String password = passwordText.getText().toString();
                if (username.equals("admin") && password.equals("admin")) {
                    editor = preferences.edit();
                    if (checkBox.isChecked()) {
                        editor.putBoolean("remember_password", true);
                        editor.putString("username", username);
                        editor.putString("password", password);
                    } else {
                        editor.clear();
                    }
                    editor.commit();
                    startActivity(new Intent(MonitorLogin.this,MonitorMain.class));
                } else {
                    Toast.makeText(MonitorLogin.this, "account or password is invalid", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}