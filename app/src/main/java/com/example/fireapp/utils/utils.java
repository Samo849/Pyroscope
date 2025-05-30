package com.example.fireapp.utils;

import com.example.fireapp.models.classificatonsModel;
import com.example.fireapp.models.sensorDataModel;

import org.json.JSONArray;
import org.json.JSONObject;

public class utils {

    public static sensorDataModel parseSensorData(JSONObject response) {
        try {
            JSONObject endDeviceIds = response.optJSONObject("end_device_ids");
            String deviceId = endDeviceIds != null ? endDeviceIds.optString("device_id", "") : "";
            String applicationId = "";
            if (endDeviceIds != null) {
                JSONObject appIds = endDeviceIds.optJSONObject("application_ids");
                applicationId = appIds != null ? appIds.optString("application_id", "") : "";
            }

            String timeOfDetection = response.optString("received_at", "");

            JSONObject uplinkMessage = response.optJSONObject("uplink_message");
            String signalQuality = "";
            Double latitude = 46.035946;
            Double longitude = 14.538270;

            classificatonsModel classifications = null;

            if (uplinkMessage != null) {
                JSONArray rxMetadata = uplinkMessage.optJSONArray("rx_metadata");
                if (rxMetadata != null && rxMetadata.length() > 0) {
                    JSONObject rx0 = rxMetadata.optJSONObject(0);
                    if (rx0 != null) {
                        signalQuality = rx0.optString("snr", "");
                        JSONObject location = rx0.optJSONObject("location");
                        if (location != null) {
                            latitude = location.has("latitude") ? location.optDouble("latitude") : null;
                            longitude = location.has("longitude") ? location.optDouble("longitude") : null;
                        }
                    }
                }
                JSONObject decodedPayload = uplinkMessage.optJSONObject("decoded_payload");

                // get the data from decoded payload
                if (decodedPayload != null) {
                    String dataString = decodedPayload.optString("data", "");
                    double fire = 0.0, normal = 0.0, wind = 0.0, rain = 0.0;
                    if (!dataString.isEmpty()) {
                        String[] parts = dataString.split(",");
                        for (String part : parts) {
                            String[] keyValue = part.trim().split(":");
                            if (keyValue.length == 2) {
                                String key = keyValue[0].trim().toLowerCase();
                                double value = 0.0;
                                try {
                                    value = Double.parseDouble(keyValue[1].trim());
                                } catch (NumberFormatException ignored) {}
                                switch (key) {
                                    case "fire": fire = value; break;
                                    case "normal": normal = value; break;
                                    case "wind": wind = value; break;
                                    case "rain": rain = value; break;
                                }
                            }
                        }
                    }
                    classifications = new classificatonsModel(fire, normal, wind, rain);
                    if(classifications == null) {
                        classifications = new classificatonsModel(0.00, 0.90, 0.10, 0.00);

                    }
                }
            }

            // Add random variance if coordinates exist
            double variance = 0.0005;
            if (latitude != null && longitude != null) {
                latitude += (Math.random() * 2 - 1) * variance;
                longitude += (Math.random() * 2 - 1) * variance;
            }

            return new sensorDataModel(deviceId, applicationId, timeOfDetection, signalQuality, classifications, latitude, longitude);
        } catch (Exception e) {
            e.printStackTrace();
            // if fails return an empty object. This should never happen.
            return new sensorDataModel("", "", "", "", null, 0.0, 0.0);
        }
    }
}

