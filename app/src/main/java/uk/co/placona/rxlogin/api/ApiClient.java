package uk.co.placona.rxlogin.api;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Copyright (C) 2016 mplacona.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class ApiClient {
    private static final String BASE_URL = "http://demo9854024.mockable.io";


    public static Retrofit getRetrofitInstance() {

        // Add network interceptor for Stetho
        OkHttpClient okClient = new OkHttpClient
            .Builder()
            .addNetworkInterceptor(new StethoInterceptor())
            .addInterceptor(chain -> {
                Request request = chain.request();
                request.newBuilder()
                        .addHeader("Authorization", "YXJlIHlvdSBjdXJpb3VzIGVub3VnaCB0byBkZWNvZGUgdGhpcz8=").build();
                return chain.proceed(request);
            })
            .build();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
