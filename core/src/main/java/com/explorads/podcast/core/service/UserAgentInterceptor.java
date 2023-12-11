package com.explorads.podcast.core.service;

import com.explorads.podcast.core.ClientConfig;
import okhttp3.Interceptor;
import okhttp3.Response;

import java.io.IOException;

public class UserAgentInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        return chain.proceed(chain.request().newBuilder()
                .header("User-Agent", ClientConfig.USER_AGENT)
                .build());
    }
}
