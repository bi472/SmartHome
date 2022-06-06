package com.example.smarthome;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

public class MQTTHelperPublish{
    public MqttAndroidClient mqttAndroidClient;

    public MQTTHelperPublish(Context context, String msg, String publishTopic, String clientId, String serverUri, String username, String password){
        mqttAndroidClient = new MqttAndroidClient(context, serverUri, clientId);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                Log.w("mqtt", s);
            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.w("Mqtt", mqttMessage.toString());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
        connect(msg, publishTopic, username, password, context);

    }

    private void connect(final String msg, final String publishTopic, final String username, final String password, Context context){
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setUserName(username);
        mqttConnectOptions.setPassword(password.toCharArray());

        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                        try {
                            byte[] encodedPayload = new byte[0];
                            encodedPayload = msg.getBytes("UTF-8");
                            MqttMessage message = new MqttMessage(encodedPayload);
                            message.setRetained(true);
                            message.setQos(2);
                            mqttAndroidClient.publish(publishTopic, message);
                            Log.i("MQTT", "publish success!!");
                            mqttAndroidClient.disconnect();
                        } catch (UnsupportedEncodingException | MqttException e) {
                            e.printStackTrace();
                        }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w("Mqtt", "Failed to connect. ");
                    Toast.makeText(context, "Не удалось подключиться к серверу. Проверьте правильность настроек MQTT сервера.", Toast.LENGTH_LONG).show();
                }
            });

        } catch (MqttException ex){
            ex.printStackTrace();
        }
    }
}
