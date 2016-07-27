package com.example.ahmed.flashlight;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
import android.hardware.Camera.Parameters;

public class MainActivity extends AppCompatActivity {
    private Camera camera;
    private boolean isFlashOn, hasFlash;
    Parameters param;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if device is supporting flash light or not
        hasFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (!hasFlash) {
            // Device doesn't support flash
            AlertDialog alert = new AlertDialog.Builder(this).create();
            alert.setTitle("ERROR");
            alert.setMessage("This device doesn't support flash light!");
            alert.setButton(DialogInterface.BUTTON_POSITIVE,
                    "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
            /**
             * or in other way:
             AlertDialog.Builder builder = new AlertDialog.Builder(this);
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            AlertDialog alertDialog = alert.create();
             **/
            alert.show();
        }
        getCamera();

        ToggleButton flashSwitch = (ToggleButton) findViewById(R.id.flash_switch);
        flashSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    turnOnFlash();
                }
                else {
                    turnOffFlash();
                }
            }
        });
    }

    private void getCamera() {
        if (camera == null)
            try {
                camera = Camera.open();
                param = camera.getParameters();
            } catch (RuntimeException e) {
                Log.e("Camera Error. Failed to  Open. Error: ", e.getMessage());
            }
    }

    // Turning on flash
    private void turnOnFlash() {
        if (!isFlashOn) {
            if (camera == null || param == null)
                return;
            param = camera.getParameters();
            param.setFlashMode(Parameters.FLASH_MODE_TORCH);
            camera.setParameters(param);
            camera.startPreview();
            isFlashOn = true;

            Log.v("FlashLightState", "Flash has been turned on...");
        }
    }

    // Turning off flash
    private void turnOffFlash() {
        if (isFlashOn) {
            if (camera == null || param == null)
                return;
            param = camera.getParameters();
            param.setFlashMode(Parameters.ANTIBANDING_OFF);
            camera.setParameters(param);
            camera.stopPreview();
            isFlashOn = false;

            Log.v("FlashLightState", "Flash has been turned off...");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        turnOffFlash();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }
}
