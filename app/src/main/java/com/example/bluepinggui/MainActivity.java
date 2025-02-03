package com.example.bluepinggui;

import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.bluepinggui.target.profile.TargetProfile;
import com.example.bluepinggui.target.profile.TargetProfileBuilder;
import com.example.bluepinggui.service.factory.BlueServiceType;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_PERMISSIONS = 101;

    private EditText etBluetoothAddress1;
    private EditText etThreadCount;
    private TextView tvStatus;

    private PowerManager.WakeLock wakeLock;
    private TargetProfile targetProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!checkPermissions()) {
            requestPermissions();
            return;
        }

        tvStatus = findViewById(R.id.tv_status);
        etBluetoothAddress1 = findViewById(R.id.et_bluetooth_address);
        etThreadCount = findViewById(R.id.et_thread_count);

        Button btnStartPinging = findViewById(R.id.btn_start_pinging);
        Button btnStopPinging = findViewById(R.id.btn_stop_pinging);

        btnStartPinging.setOnClickListener(v -> {
            String address = etBluetoothAddress1.getText().toString().trim();
            String threadCountInput = etThreadCount.getText().toString().trim();

            if (address.isEmpty() || threadCountInput.isEmpty()) {
                tvStatus.setText("Status: Please fill in all fields.");
                return;
            }

            int threadCount;
            try {
                threadCount = Integer.parseInt(threadCountInput);
            } catch (NumberFormatException e) {
                tvStatus.setText("Status: Invalid thread count.");
                return;
            }

            setStatus("Pinging and pairing ...");

            PowerManager powerManager = (PowerManager)getSystemService(Context.POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "BluetoothPingerService::WakeLock");
            wakeLock.acquire();

            targetProfile = new TargetProfileBuilder()
                    .bluetoothAddress(address)
                    .services(new BlueServiceType[]{ BlueServiceType.PING, BlueServiceType.PAIR, BlueServiceType.SCAN })
                    .threadCount(threadCount)
                    .build();

            targetProfile.launch(this);
        });

        btnStopPinging.setOnClickListener(v -> {
            targetProfile.stop();
            setStatus("Stopped");
            if (wakeLock != null && wakeLock.isHeld()) {
                wakeLock.release();
            }
        });
    }

    private void setStatus(String txtStatus) {
        tvStatus.setText("Status: " + txtStatus);
    }

    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.ACCESS_FINE_LOCATION
                },
                REQUEST_CODE_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with Bluetooth functionality
                Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH_CONNECT)) {
                    // Show rationale and request again
                    Toast.makeText(this, "Bluetooth permissions are required to scan devices. Please grant the permissions.", Toast.LENGTH_LONG).show();
                    requestPermissions();
                } else {
                    // Show a message explaining the importance of the permission and direct them to settings
                    Toast.makeText(this, "Bluetooth permissions denied. You can enable them in the app settings.", Toast.LENGTH_LONG).show();

                    // Optionally, direct user to app settings
                    new AlertDialog.Builder(this)
                            .setTitle("Permission Required")
                            .setMessage("Bluetooth permissions are required for this app to function. You can enable them in the app settings.")
                            .setPositiveButton("Go to Settings", (dialog, which) -> {
                                // Open the app settings to allow user to manually enable permissions
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivityForResult(intent, 1001);  // Request code for app settings
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                }
            }
        }
    }
}