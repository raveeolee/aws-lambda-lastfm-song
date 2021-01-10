package dev.may_i;

import com.google.gson.Gson;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class RequestExecutor {
    private final Gson gson;

    public RequestExecutor(Gson gson) {
        this.gson = gson;
    }

    public <T> T executeRequest(Request request, OkHttpClient client, Class<T> tClass) {
        Call call = client.newCall(request);
        try (Response response = call.execute()) {
            if (response.code() == 200)
                return gson.fromJson(response.body().string(), tClass);
            throw new RequestFailedException("Request failed " + request.toString() + " " + response.code());
        } catch (IOException e) {
            throw new RequestFailedException("Failed to execute request " + request.toString());
        }
    }
}
