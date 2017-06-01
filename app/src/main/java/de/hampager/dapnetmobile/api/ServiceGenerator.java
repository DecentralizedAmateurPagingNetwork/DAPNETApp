package de.hampager.dapnetmobile.api;

import android.text.TextUtils;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {
    public static Retrofit retrofit;
    private static String API_BASE_URL = "http://www.hampager.de:8080/";
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    // No need to instantiate this class.
    private ServiceGenerator() {
    }
    /*
    public static <S> S createService(Class<S> serviceClass) {
        return createService(serviceClass, null, null);
    }*/
    public static <S> S createService(Class<S> serviceClass) {
        builder.client(httpClient.build());
        retrofit = builder.build();
        return retrofit.create(serviceClass);
    }
    public static <S> S createService(Class<S> serviceClass, String username, String password) {
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            String authToken = Credentials.basic(username, password);
            return createService(serviceClass, authToken);
        }
        //Old code will create stack overflow: return createService(serviceClass, null, null);
        builder.client(httpClient.build());
        retrofit = builder.build();
        return retrofit.create(serviceClass);
    }

    private static <S> S createService(Class<S> serviceClass, final String authToken) {
        Retrofit retrofit = null;
        if (!TextUtils.isEmpty(authToken)) {
            AuthenticationInterceptor interceptor = new AuthenticationInterceptor(authToken);

            if (!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(interceptor);

                builder.client(httpClient.build());
                retrofit = builder.build();
            }
        }

        return retrofit.create(serviceClass);
    }

    public static void changeApiBaseUrl(String newApiBaseUrl) {
        API_BASE_URL = newApiBaseUrl;

        builder = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(API_BASE_URL);
    }

}
