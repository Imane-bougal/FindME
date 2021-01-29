package com.imaneb.findme.ui.register;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.imaneb.findme.R;
import com.imaneb.findme.ui.main.MainActivity;
import com.imaneb.findme.utils.LoadingDialog;
import com.imaneb.findme.utils.RxBindingHelper;
import com.imaneb.findme.utils.StateResource;
import com.imaneb.findme.viewModels.ViewModelProviderFactory;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.functions.Function3;
import io.reactivex.observers.DisposableObserver;
import me.mutasem.booleanselection.BooleanSelectionView;

public class RegisterActivity extends DaggerAppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RegisterActivity_Tag";
    private RegisterViewModel registerViewModel;

    RequestQueue requestQueue;
    String insertUrl = "http://192.168.42.6:8080/users/save";

    private EditText displayNameInput;
    private EditText emailInput;
    private EditText passwordInput;
    DatePicker picker;
    BooleanSelectionView gender;
    String genderselected = "Female";
    TelephonyManager telephonyManager;
    private Button createAccountBtn;


    Observable<Boolean> formObservable;

    @Inject
    LoadingDialog loadingDialog;

    @Inject
    ViewModelProviderFactory providerFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        intToolbar();
        intView();
        formValidation();

        registerViewModel = new ViewModelProvider(getViewModelStore(), providerFactory).get(RegisterViewModel.class);

        subscribeObservers();

    }

    private void subscribeObservers() {
        registerViewModel.observeRegister().observe(this, new Observer<StateResource>() {
            @Override
            public void onChanged(StateResource stateResource) {
                if (stateResource != null) {
                    switch (stateResource.status) {
                        case LOADING:
                            Log.d(TAG, "onChanged: Loading");
                            loadingDialog.show(getSupportFragmentManager(), "loadingDialog");
                            break;
                        case SUCCESS:
                            Log.d(TAG, "onChanged: Success");
                            loadingDialog.dismiss();
                            moveToHomeActivity();
                            break;
                        case ERROR:
                            Log.d(TAG, "onChanged: Error" + stateResource.message);
                            loadingDialog.dismiss();
                            showSnackBar(stateResource.message);
                            break;
                    }
                }

            }
        });
    }

    private void moveToHomeActivity() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

    }


    private void formValidation() {
        Observable<String> display_name_observable = RxBindingHelper.getObservableFrom(displayNameInput);
        Observable<String> email_observable = RxBindingHelper.getObservableFrom(emailInput);
        Observable<String> password_observable = RxBindingHelper.getObservableFrom(passwordInput);


        formObservable = Observable.combineLatest(email_observable, password_observable, display_name_observable, new Function3<String, String, String, Boolean>() {
            @Override
            public Boolean apply(String email, String password, String displayName) throws Exception {
                return isValidForm(email.trim(), password.trim(), displayName.trim());
            }
        });

        formObservable.subscribe(new DisposableObserver<Boolean>() {
            @Override
            public void onNext(Boolean aBoolean) {
                createAccountBtn.setEnabled(aBoolean);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private Boolean isValidForm(String email, String password, String displayName) {

        boolean isDisplayName = !displayName.isEmpty();
        if (!isDisplayName) {
            displayNameInput.setError("Please enter valid name");
        }

        boolean isEmail = Patterns.EMAIL_ADDRESS.matcher(email).matches() && !email.isEmpty();
        if (!isEmail) {
            emailInput.setError("Please enter valid email");
        }

        boolean isPassword = password.length() > 6 && !password.isEmpty();
        if (!isPassword) {
            passwordInput.setError("Password must be greater then 6 digit");
        }

        return isDisplayName && isEmail && isPassword;
    }

    private void intView() {
        displayNameInput = findViewById(R.id.status_input);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        picker = findViewById(R.id.datePicker1);
        gender = findViewById(R.id.gender);
        gender.setSelection(BooleanSelectionView.Selection.End);
        gender.setSelectionListener(new BooleanSelectionView.SelectionListener() {
            @Override
            public void onSelectionChanged(int selection, String selectedText) {
                genderselected = selectedText;
            }
        });
        createAccountBtn = findViewById(R.id.create_account_btn);
        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                perform_register();
            }
        });
    }

    private void intToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextAppearance(this, R.style.ToolbarTextAppearance);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Create Account");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create_account_btn:
                perform_register();
                break;
        }
    }

    private void perform_register() {
        String name = displayNameInput.getText().toString();
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        String imei = telephonyManager.getDeviceId();
        String birthday = "" + picker.getYear() + "-" +
                (picker.getMonth() + 1) + "-" + picker.getDayOfMonth();
        registerViewModel.register(email, password, name, imei, birthday, genderselected);
        setinscrition(email, password, name, imei, birthday, genderselected);
        System.out.println("register preformed");
    }

    private void setinscrition(String email, String password, String name, String imei, String birthday, String gender) {
        // Post params to be sent to the server
        HashMap<String, String> map = new HashMap<>();
        map.put("email", email);
        map.put("password", password);
        map.put("displayName", name);
        map.put("image", "default");
        map.put("status", "default");
        map.put("gender", gender);
        map.put("imei", imei);
        map.put("birthday", birthday);
        map.put("phone", "default");
        map.put("constraint_gender","All");
        map.put("constraint_Age","18");
        JsonObjectRequest req = new JsonObjectRequest(insertUrl, new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("*****working");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                System.out.println("****not working ****: "+ error);
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        JSONObject obj = new JSONObject(res);
                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    } catch (JSONException e2) {
                        // returned data is not JSONObject?
                        e2.printStackTrace();
                    }
                }
            }
        }
        );
        // add the request object to the queue to be executed
        requestQueue.add(req);
    }

    private void showSnackBar(String msg) {
        View contextView = findViewById(android.R.id.content);
        Snackbar.make(contextView, msg, Snackbar.LENGTH_LONG).show();
    }
}
