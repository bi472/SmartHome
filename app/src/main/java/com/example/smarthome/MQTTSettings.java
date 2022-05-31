package com.example.smarthome;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

public class MQTTSettings extends Activity {

    public boolean queryCheck(String user, String password){
        int check;
        try {
            db = dbHelper.open();
            Cursor cursor = db.query(
                    "users",   // таблица
                    new String[]{"name_user", "password"},            // столбцы
                    "name_user=? and password=?",                  // столбцы для условия WHERE
                    new String[]{user, password},                  // значения для условия WHERE
                    null,                  // Don't group the rows
                    null,                  // Don't filter by row groups
                    null);                   // порядок сортировки
            check = cursor.getCount();
            cursor.close();
            dbHelper.close();
            Log.i("Количество столбцов", String.valueOf(check));
            if (check == 1)
                return true;
        }catch (Exception exception){
            Log.i("Запрос к БД", exception.getMessage());
        }
        return false;
    }

    DBHelper dbHelper;
    SQLiteDatabase db;

    EditText mqtt_server;
    EditText port;
    EditText user_mqtt;
    EditText password_mqtt;
    Button apply_mqtt;

    EditText switch_name;
    EditText topic;
    Button apply_switch;

    EditText delete_switch;
    Button apply_delete_switch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mqtt_settings);

        dbHelper = new DBHelper(this);

        mqtt_server = (EditText) findViewById(R.id.mqtt_server_ed);
        port = (EditText) findViewById(R.id.port_ed);
        user_mqtt = (EditText) findViewById(R.id.username_ed);
        password_mqtt = (EditText) findViewById(R.id.password_ed);
        apply_mqtt = (Button) findViewById(R.id.applyMqttt);

        switch_name = (EditText) findViewById(R.id.switch_name_ed);
        topic = (EditText) findViewById(R.id.switch_topic_ed);
        apply_switch = (Button) findViewById(R.id.applySwitch);

        delete_switch = findViewById(R.id.delete_switch_name_ed);
        apply_delete_switch = findViewById(R.id.deleteSwitch);

        apply_mqtt.setOnClickListener(view -> showInputDialog("mqtt"));
        apply_switch.setOnClickListener(view -> showInputDialog("switch"));
        apply_delete_switch.setOnClickListener(view -> showInputDialog("delete"));

        mqtt_server.setText(new Preferences(this).getMQTTServer());
        port.setText(new Preferences(this).getPort());
        user_mqtt.setText(new Preferences(this).getUsername());
        password_mqtt.setText(new Preferences(this).getPassword());
    }

    public void showInputDialog(String apply) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MQTTSettings.this);
        LinearLayout linearLayout = new LinearLayout(MQTTSettings.this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        final EditText user_admin = new EditText(this);
        final EditText password_admin = new EditText(this);

        user_admin.setInputType(InputType.TYPE_CLASS_TEXT );
        user_admin.setHint("Логин");
        password_admin.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        password_admin.setHint("Пароль");

        linearLayout.addView(user_admin);
        linearLayout.addView(password_admin);
        builder.setView(linearLayout);

        builder.setMessage("Введите  логин и пароль: ")
                .setNegativeButton("Отменить", (dialog, id) -> dialog.cancel())
                .setPositiveButton("Войти", (dialogInterface, i) -> {
                    try {
                        if (queryCheck(user_admin.getText().toString(),new String(Hex.encodeHex(DigestUtils.sha1(password_admin.getText().toString()))))) {
                            if (apply.contains("mqtt")) {
                                new Preferences(MQTTSettings.this).setMQTTServer(mqtt_server.getText().toString());
                                new Preferences(MQTTSettings.this).setPort(port.getText().toString());
                                new Preferences(MQTTSettings.this).setUsername(user_mqtt.getText().toString());
                                new Preferences(MQTTSettings.this).setPassword(password_mqtt.getText().toString());
                            }
                            else if (apply.contains("switch"))
                                queryAdd(switch_name.getText().toString(), topic.getText().toString());
                            else if (apply.contains("delete"))
                                queryDelete(delete_switch.getText().toString());
                            Toast.makeText(getApplicationContext(), "Настройки успешно применены!",
                                    Toast.LENGTH_LONG).show();
                        } else
                            Toast.makeText(getApplicationContext(), "Неправильный логин или пароль!",
                                    Toast.LENGTH_LONG).show();
                    } catch (Exception ex){
                        Log.e("Ошибка", ex.getMessage());
                    }
                });
        builder.show();
    }

    private void queryDelete(String name_switch) {
        db.delete("switches",
                "name_switch = ?",
                new String[] {name_switch});
    }

    private void queryAdd(String name_switch, String subscriptionTopic) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name_switch", name_switch);
        contentValues.put("subscriptionTopic", subscriptionTopic);
        db.insert("switches", null, contentValues);
    }
}