package fse.team2.slickclient.services;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.logging.Level;

import fse.team2.slickclient.utils.LoggerService;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Intercepts outgoing and incoming http calls. Useful for attaching Authorization tokens and
 * custom redirection for URLs.
 */
public class InterceptorService implements Interceptor {
    @NotNull
    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder newRequestBuilder = request.newBuilder()
                .addHeader("Content-Type", "application/json;charset=utf8")
                .addHeader("Accept", "application/json;charset=utf8");


        if (request.url().toString().contains("/account")) {
            newRequestBuilder.addHeader("Authorization", UserServiceImpl.getInstance(HttpServiceImpl.getInstance()).getUser().getToken());
        }

        Request newRequest = newRequestBuilder.build();

        LoggerService.log(Level.INFO, "Request Intercepted: " + newRequest.url() + " | " + newRequest.method() + " | " + newRequest.header("Authorization"));
        return chain.proceed(newRequest);
    }
}
