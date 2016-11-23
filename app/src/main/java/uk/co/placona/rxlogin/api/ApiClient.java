package uk.co.placona.rxlogin.api;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import okhttp3.CertificatePinner;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.co.placona.rxlogin.BuildConfig;

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
    private static final String BASE_URL = "https://loginapi.androidsecurity.info/";


    public static Retrofit getRetrofitInstance() {

        // Add certificate pinner
        CertificatePinner certificatePinner = new CertificatePinner.Builder()
                .add("loginapi.androidsecurity.info", BuildConfig.api_certificate)
                .build();

        // Add network interceptor for Stetho
        OkHttpClient okClient = new OkHttpClient
            .Builder()
            .certificatePinner(certificatePinner)
            .addNetworkInterceptor(new StethoInterceptor())
            .addInterceptor(chain -> {
                Request request = chain.request();

                if(request.header("Require-Authentication") == null){
                    String credential = Credentials.basic(BuildConfig.api_login, BuildConfig.api_password);
                    request = request.newBuilder()
                            .addHeader("Authorization", credential)
                            .build();
                }

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
