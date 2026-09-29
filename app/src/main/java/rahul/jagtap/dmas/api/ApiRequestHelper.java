package rahul.jagtap.dmas.api;


import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import rahul.jagtap.dmas.App;
import rahul.jagtap.dmas.BuildConfig;
import rahul.jagtap.dmas.utils.MySSLSocketFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiRequestHelper {

    public static interface OnRequestComplete {
        public void onSuccess(Object object);

        public void onFailure(String apiResponse);
    }

    private static ApiRequestHelper instance;
    private App app;
    private ApiService apiService;
    static Gson gson;

    public static synchronized ApiRequestHelper init(App app) {
        if (null == instance) {
            instance = new ApiRequestHelper();
            instance.setApplication(app);
            gson = new GsonBuilder()
                    .setLenient()
                    .create();
            instance.createRestAdapter();
        }
        return instance;
    }

    /**
     * API Calls
     */

    public <T> Call<T> callRetrofit(Call<T> call, final OnRequestComplete onRequestComplete) {
        call.enqueue(new Callback<T>() {
            public void onResponse(Call<T> call, Response<T> response) {
                if (response.isSuccessful()) {
                    onRequestComplete.onSuccess(response.body());
                    return;
                } else {
                    onRequestComplete.onFailure("");
//                    try {
//                        if (response.errorBody() != null) {
//                            onRequestComplete.onFailure(Html.fromHtml(response.errorBody().string()).toString());
//                        } else
//                            onRequestComplete.onFailure(Html.fromHtml(response.errorBody().string()).toString());
//                    } catch (IOException e) {
//                        onRequestComplete.onFailure(Utils.UNPROPER_RESPONSE);
//                        e.printStackTrace();
//                    }
                }
            }

            public void onFailure(Call<T> call, Throwable t) {
//                if (t != null && t.getMessage() != null)
//                    Log.e("server msg", Html.fromHtml(t.getMessage()) + "");
//                if (t != null && t.getMessage() != null) {
//                    if (t.getMessage().contains("Unable to resolve host")) {
//                        onRequestComplete.onFailure(Utils.NO_INTERNET_MSG);
//                    } else if (t.getMessage().contains("timeout") || t.getMessage().equalsIgnoreCase("socket closed")
//                            || t.getMessage().contains("socket closed") || t.getMessage().contains("Failed to connect")
//                            || t.getMessage().contains("canceled"))
//                        onRequestComplete.onFailure("");
//                    else if (!t.getMessage().contains("timeout") || !t.getMessage().equalsIgnoreCase("socket closed") ||
//                            !t.getMessage().contains("Failed to connect") || !t.getMessage().contains("canceled"))
//                        onRequestComplete.onFailure(Html.fromHtml(t.getMessage()) + "");
//                    else
//                        onRequestComplete.onFailure(Utils.UNPROPER_RESPONSE);
//                } else {
//                    onRequestComplete.onFailure(Utils.UNPROPER_RESPONSE);
//                }
                onRequestComplete.onFailure("");
            }
        });
        return call;
    }
    /**
     * End API Calls
     */

    /**
     * REST Adapter Configuration
     */
    private void createRestAdapter() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson));
        Retrofit retrofit = builder.client(getClient().build()).build();
        apiService = retrofit.create(ApiService.class);
    }

    public ApiService getApiService() {
        return apiService;
    }

    @NonNull
    public OkHttpClient.Builder getClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(BuildConfig.DEBUG ? Level.BODY : Level.NONE);


        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.readTimeout(60, TimeUnit.SECONDS);
        httpClient.connectTimeout(60, TimeUnit.SECONDS);
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            httpClient.sslSocketFactory(new MySSLSocketFactory(sslSocketFactory), (X509TrustManager) trustAllCerts[0]);
            httpClient.hostnameVerifier((hostname, session) -> true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // add your other interceptors …

        // add logging as last interceptor
        httpClient.interceptors().add(logging);
        return httpClient;
    }

    /**
     * End REST Adapter Configuration
     */

    public App getApplication() {
        return app;
    }

    public void setApplication(App app) {
        this.app = app;
    }

    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(MultipartBody.FORM, descriptionString);
    }
}
