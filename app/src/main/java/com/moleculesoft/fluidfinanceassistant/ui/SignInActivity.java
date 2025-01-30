package com.moleculesoft.fluidfinanceassistant.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.moleculesoft.fluidfinanceassistant.R;
import com.moleculesoft.fluidfinanceassistant.data.model.User;
import com.moleculesoft.fluidfinanceassistant.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class SignInActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private ProgressBar progressBar;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "user_prefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        progressBar = findViewById(R.id.progress_bar);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            if (!username.isEmpty() && !password.isEmpty()) {
                loginUser(username, password);
            } else {
                Toast.makeText(SignInActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginUser(String username, String password) {
        progressBar.setVisibility(View.VISIBLE);

        String url = getString(R.string.base_url) + "/login_user";

        // Encode username and password to Base64 for Basic Authentication
        String credentials = username + ":" + password;
        String encodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest loginRequestObject = new JsonObjectRequest(Request.Method.POST, url, null,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        Util.saveAppUser(
                                this,
                                new User(
                                        response.getInt("UserID"),
                                        response.getString("FullName"),
                                        response.getString("Email")
                                )
                        );
                        navigateToHome();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(SignInActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {

                    String responseBody = null;
                    try {
                        responseBody = new String(error.networkResponse.data, "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    JSONObject errorJson = null;
                    try {
                        errorJson = new JSONObject(responseBody);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        Toast.makeText(SignInActivity.this, errorJson.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    progressBar.setVisibility(View.GONE);
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // Set the Authorization header for Basic Authentication
                Map<String, String> headers = new HashMap<>();
                headers.put("X-User-Credentials", "Basic " + encodedCredentials); // "Basic " + Base64 encoded username:password
                return headers;
            }
        };

        queue.add(loginRequestObject);
    }

    private void navigateToHome() {
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
