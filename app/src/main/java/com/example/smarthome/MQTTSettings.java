package com.example.smarthome;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MQTTSettings extends Activity {

    DBHelper dbHelper;
    SQLiteDatabase db;

    EditText mqtt_server;
    EditText port;
    EditText user;
    EditText password;
    Button apply_mqtt;

    EditText switch_name;
    EditText topic;
    Button apply_switch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mqtt_settings);

        dbHelper = new DBHelper(this);

        mqtt_server = (EditText) findViewById(R.id.mqtt_server_ed);
        port = (EditText) findViewById(R.id.port_ed);
        user = (EditText) findViewById(R.id.username_ed);
        password = (EditText) findViewById(R.id.password_ed);
        apply_mqtt = (Button) findViewById(R.id.applyMqttt);

        switch_name = (EditText) findViewById(R.id.switch_name_ed);
        topic = (EditText) findViewById(R.id.switch_topic_ed);
        apply_switch = (Button) findViewById(R.id.applySwitch);

        apply_mqtt.setOnClickListener(view -> showInputDialog());
        apply_switch.setOnClickListener(view -> showInputDialog());

        mqtt_server.setText(new Preferences(this).getMQTTServer());
        port.setText(new Preferences(this).getPort());
        user.setText(new Preferences(this).getUsername());
        password.setText(new Preferences(this).getPassword());
    }

    public void showInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MQTTSettings.this);
        LinearLayout linearLayout = new LinearLayout(MQTTSettings.this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        final EditText user = new EditText(this);
        final EditText password = new EditText(this);

        user.setInputType(InputType.TYPE_CLASS_TEXT );
        user.setHint("Логин");
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        password.setHint("Пароль");

        linearLayout.addView(user);
        linearLayout.addView(password);
        builder.setView(linearLayout);

        builder.setMessage("Введите  логин и пароль: ")
                .setNegativeButton("Отменить", (dialog, id) -> dialog.cancel())
                .setPositiveButton("Войти", (dialogInterface, i) -> {
                    if(user.getText().toString().contains("admin") && password.getText().toString().contains("admin"))
                        Toast.makeText(getApplicationContext(),"Настройки успешно применены!",
                            Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(getApplicationContext(),"Неправильный логин или пароль!",
                                Toast.LENGTH_LONG).show();
                });
        builder.show();
    }
}