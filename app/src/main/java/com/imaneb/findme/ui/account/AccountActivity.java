package com.imaneb.findme.ui.account;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.RequestManager;
import com.imaneb.findme.HomeActivity;
import com.imaneb.findme.R;
import com.imaneb.findme.data.model.User;
import com.imaneb.findme.utils.Constants;
import com.imaneb.findme.utils.DataConverter;
import com.imaneb.findme.utils.InputDialog;
import com.imaneb.findme.utils.LoadingDialog;
import com.imaneb.findme.utils.StateResource;
import com.imaneb.findme.viewModels.ViewModelProviderFactory;
import com.google.android.material.snackbar.Snackbar;
import com.vanniktech.rxpermission.RealRxPermission;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import me.mutasem.booleanselection.BooleanSelectionView;

public class AccountActivity extends DaggerAppCompatActivity implements View.OnClickListener, InputDialog.ChangeStatusListener {

    private static final String TAG = "AccountActivity_Tag";
    private static final int REQUEST_CODE = 1;
    private AccountViewModel accountViewModel;
    @Inject
    ViewModelProviderFactory providerFactory;
    @Inject
    InputDialog inputDialog;
    @Inject
    LoadingDialog loadingDialog;
    @Inject
    RequestManager requestManager;

    private CircleImageView profileImage;
    private TextView displayName;
    private TextView status, ageView;
    private Button changeStatus;
    private Button changeImage;
    private Button logoutBtn;
    private Button changeConstraints;

    private SeekBar sBar;
    BooleanSelectionView gender;
    private String genderselected = "All";
    private int min_Age = 18;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        intToolbar();
        intView();

        accountViewModel = new ViewModelProvider(getViewModelStore(), providerFactory).get(AccountViewModel.class);
        observerInfoChange();
        observeStatusUpdate();
        observeDisplayImageUpdate();
    }

    private void observeDisplayImageUpdate() {
        accountViewModel.observeDisplayImageChange().observe(this, new Observer<StateResource>() {
            @Override
            public void onChanged(StateResource stateResource) {
                if (stateResource != null) {
                    switch (stateResource.status) {
                        case LOADING:
                            loadingDialog.show(getSupportFragmentManager(), "loading_dialog");
                            break;
                        case SUCCESS:
                            loadingDialog.dismiss();
                            showSnackBar("Display Image Updated Successfully");
                            break;
                        case ERROR:
                            loadingDialog.dismiss();
                            showSnackBar(stateResource.message);
                            break;
                    }
                }
            }
        });
    }

    private void observeStatusUpdate() {
        accountViewModel.observerStatusChange().observe(this, new Observer<StateResource>() {
            @Override
            public void onChanged(StateResource stateResource) {
                if (stateResource != null) {
                    switch (stateResource.status) {
                        case LOADING:
                            inputDialog.dismiss();
                            loadingDialog.show(getSupportFragmentManager(), "loading_dialog");
                            break;
                        case SUCCESS:
                            loadingDialog.dismiss();
                            showSnackBar("Status Updated Successfully");
                            break;
                        case ERROR:
                            loadingDialog.dismiss();
                            showSnackBar(stateResource.message);
                            break;
                    }
                }
            }
        });
    }

    private void observerInfoChange() {
        accountViewModel.getUserInfo().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (!user.getImage().equals("default")) {
                    requestManager.load(user.getImage()).into(profileImage);
                }
                if (!user.getStatus().equals("default")) {
                    status.setText(user.getStatus());
                }
                displayName.setText(user.getDisplayName());

                System.out.println("geting constraints "+ (String.valueOf(user.getConstraints().get("constraint_minAge"))));
                sBar.setProgress(Integer.parseInt(String.valueOf(user.getConstraints().get("constraint_minAge"))));
                ageView.setText((String.valueOf(user.getConstraints().get("constraint_minAge"))));
            }
        });
    }

    private void intView() {
        profileImage = findViewById(R.id.profile_image);
        displayName = findViewById(R.id.display_name);

        sBar = (SeekBar) findViewById(R.id.seekBar);
        ageView = findViewById(R.id.ageView);
        ageView.setText(min_Age+"");
        sBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int pval = 18;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pval = progress;
                ageView.setText(pval+"");
                min_Age = pval;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //write custom code to on start progress
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ageView.setText(pval+"");
                min_Age = pval;
            }
        });

        status = findViewById(R.id.status_view);
        changeImage = findViewById(R.id.change_image_btn);
        changeStatus = findViewById(R.id.change_status_btn);
        logoutBtn = findViewById(R.id.logout_btn);
        changeConstraints = findViewById(R.id.change_constraint_btn);

        changeImage.setOnClickListener(this);
        changeStatus.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
        changeConstraints.setOnClickListener(this);


        gender = findViewById(R.id.gender);
        gender.setSelection(BooleanSelectionView.Selection.End);
        gender.setSelectionListener(new BooleanSelectionView.SelectionListener() {
            @Override
            public void onSelectionChanged(int selection, String selectedText) {
                genderselected = selectedText;
            }
        });



    }

    private void intToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextAppearance(this, R.style.ToolbarTextAppearance);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Account");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.change_status_btn:
                showInputDialog();
                break;
            case R.id.change_image_btn:
                showGallery();
                break;
            case R.id.logout_btn:
                performLogout();
                break;
            case R.id.change_constraint_btn:
                System.out.println("####### Constraints######"+ min_Age+" "+genderselected);
                updateConstraint(min_Age,genderselected);
                break;
        }
    }

    private void updateConstraint(int min_age, String genderselected) {

        accountViewModel.updateConstraint(min_age,genderselected);
        Constants.setConstraint_gender(genderselected);
        Constants.setConstraint_age(min_age);
    }

    private void performLogout() {
        accountViewModel.logOut();
        moveToHomeActivity();
    }

    private void moveToHomeActivity() {
        Intent intent = new Intent(AccountActivity.this, com.imaneb.findme.HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void showGallery() {
        //Storage permission
        RealRxPermission.getInstance(getApplicationContext())
                .request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe();
        //Open gallery Intent
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE){
            if (resultCode==RESULT_OK){
                Uri selectedImage = data.getData();
                if(selectedImage!=null){
                    convertToByte(selectedImage);
                }
            }
        }
    }

    private void convertToByte(Uri selectedImage) {
        File imageFile = new File(DataConverter.getRealPathFromURI(selectedImage,this));
        try {
            Bitmap bitmap = new Compressor(getApplication()).compressToBitmap(imageFile);
            //save to st
            accountViewModel.updateDisplayImage(bitmap);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showInputDialog() {
        inputDialog.show(getSupportFragmentManager(), "inputDialog");
    }

    private void showSnackBar(String msg) {
        View contextView = findViewById(android.R.id.content);
        Snackbar.make(contextView, msg, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void saveNewStatus(String status) {
        accountViewModel.updateStatus(status);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if ( id == android.R.id.home ) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
