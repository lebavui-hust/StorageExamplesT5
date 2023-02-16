package com.example.storageexamples;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

    static final String FILE_SETTINGS = "my_settings";
    static final String STRING_DATA = "STRING_DATA";
    static final String INT_DATA = "INT_DATA";

    EditText editContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editContent = findViewById(R.id.edit_content);

        findViewById(R.id.button_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editContent.setText("");
            }
        });

        findViewById(R.id.button_load_raw).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    InputStream is = getResources().openRawResource(R.raw.my_text);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null)
                        builder.append(line).append("\n");
                    reader.close();

                    editContent.setText(builder.toString());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        findViewById(R.id.button_load_internal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    InputStream is = openFileInput("internal_data.txt");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null)
                        builder.append(line).append("\n");
                    reader.close();

                    editContent.setText(builder.toString());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        findViewById(R.id.button_save_internal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String content = editContent.getText().toString();
                    OutputStream os = openFileOutput("internal_data.txt", MODE_PRIVATE);
                    OutputStreamWriter writer = new OutputStreamWriter(os);
                    writer.write(content);
                    writer.flush();
                    writer.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        findViewById(R.id.button_delete_internal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String path = getFilesDir().getAbsolutePath() + "/internal_data.txt";
                    Log.v("TAG", path);
                    File file = new File(path);
                    if (file.exists()) {
                        if (file.delete()) {
                            Log.v("TAG", "File deleted");
                        } else {
                            Log.v("TAG", "Failed to delete file");
                        }
                    } else
                        Log.v("TAG", "File not existed");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        findViewById(R.id.button_load_external).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                    Log.v("TAG", "sdPath: " + sdPath);
                    File file = new File(sdPath + "/external_data.txt");
                    InputStream is = new FileInputStream(file);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null)
                        builder.append(line).append("\n");
                    reader.close();

                    editContent.setText(builder.toString());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        findViewById(R.id.button_save_external).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String content = editContent.getText().toString();

                    String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                    Log.v("TAG", "sdPath: " + sdPath);
                    File file = new File(sdPath + "/external_data.txt");
                    OutputStream os = new FileOutputStream(file);

                    OutputStreamWriter writer = new OutputStreamWriter(os);
                    writer.write(content);
                    writer.flush();
                    writer.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        findViewById(R.id.button_copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    InputStream is = getResources().openRawResource(R.raw.my_image);

                    String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                    File file = new File(sdPath + "/external_image.jpg");
                    OutputStream os = new FileOutputStream(file);

                    // OutputStream os = openFileOutput("internal_image.jpg", MODE_PRIVATE);

                    byte[] buf = new byte[2048];
                    int len;
                    while ((len = is.read(buf)) > 0)
                        os.write(buf, 0, len);
                    is.close();
                    os.flush();
                    os.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        // Load prefs
        SharedPreferences prefs = getSharedPreferences(FILE_SETTINGS, MODE_PRIVATE);
        String s = prefs.getString(STRING_DATA, "NO_DATA");
        int i = prefs.getInt(INT_DATA, -1);

        Log.v("TAG", "s = " + s);
        Log.v("TAG", "i = " + i);

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            Log.v("TAG","PERMISSION_DENIED");
            requestPermissions(new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, 123);
        }

        File root = Environment.getExternalStorageDirectory();
        for (File child: root.listFiles()) {
            Log.v("TAG", child.getName() + " --- " + (child.isDirectory()?"Directory":"File"));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            Log.v("TAG","PERMISSION_GRANTED");
        else
            Log.v("TAG","PERMISSION_DENIED");
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences prefs = getSharedPreferences(FILE_SETTINGS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(STRING_DATA, "Hello");
        editor.putInt(INT_DATA, 1234);
        editor.apply();
    }
}