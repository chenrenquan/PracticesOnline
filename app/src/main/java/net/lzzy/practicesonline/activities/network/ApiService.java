package net.lzzy.practicesonline.activities.network;

import android.text.TextUtils;

import net.lzzy.practicesonline.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author lzzy_Colo on 2019/4/19
 * Description:
 */
public class ApiService {
    public static final OkHttpClient CLIENT = new OkHttpClient();

    public static String get(String address) throws IOException {
        //todo:1
        URL url = new URL(address);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(6 * 1000);
            connection.setReadTimeout(6 * 1000);
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
            reader.close();
            return builder.toString();
        } finally {
            connection.disconnect();
        }
    }

    public static void post(String address, JSONObject json) throws IOException {
        //todo:2
        URL url = new URL(address);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.getDoOutput();
        connection.setChunkedStreamingMode(0);
        connection.addRequestProperty("Content-Type", "application/json");
        byte[] data = json.toString().getBytes(StandardCharsets.UTF_8);
        connection.addRequestProperty("Content-Length", String.valueOf(data.length));
        connection.setUseCaches(false);
        try (OutputStream stream = connection.getOutputStream()) {
            stream.write(data);
            stream.flush();
        } finally {
            connection.disconnect();
        }
    }

    public static String okGet(String address) throws IOException {
        Request request = new Request.Builder().url(address).build();
        try (Response response = CLIENT.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                throw new IOException("错误码:" + response.code());
            }
        }
    }

    public static String okGet(String address, String args, HashMap<String, Object> headers) throws IOException {
        if (!TextUtils.isEmpty(args)) {
            address = address.concat("?").concat(args);

        }
        Request.Builder builder = new Request.Builder().url(address);
        if (headers != null && headers.size() > 0) {
            for (Object o : headers.entrySet()) {
                Map.Entry entry = (Map.Entry) o;
                String key = entry.getKey().toString();
                Object val = entry.getValue();
                if (val instanceof String) {
                    builder = builder.header(key, val.toString());
                } else if (val instanceof List) {
                    for (String v : ApiService.<List<String>>cast(val)) {
                        builder = builder.addHeader(key, v);
                    }
                }
            }
        }
        Request request = builder.build();
        try (Response response = CLIENT.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                throw new IOException("错误码:" + response.code());
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object obj) {
        return (T) obj;
    }

    public static int okPost(String address, JSONObject json) throws IOException {
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), json.toString());
        Request request = new Request.Builder()
                .url(address)
                .post(body)
                .build();
        try (Response response = CLIENT.newCall(request).execute()) {
            return response.code();
        }
    }

    public static String okRequest(String address, JSONObject json) throws IOException {
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), json.toString());
        Request request = new Request.Builder()
                .url(address)
                .post(body)
                .build();
        try (Response response = CLIENT.newCall(request).execute()) {
            return response.body().string();
        }
    }
}















































































