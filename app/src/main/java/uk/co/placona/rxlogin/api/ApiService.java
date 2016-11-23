package uk.co.placona.rxlogin.api;

import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rx.Observable;
import uk.co.placona.rxlogin.models.ForgotPasswordRequest;
import uk.co.placona.rxlogin.models.LoginRequest;
import uk.co.placona.rxlogin.models.User;

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

public interface ApiService {
    @GET("/api/user")
    Observable<Response<User>> user();

    @FormUrlEncoded
    @POST("/api/login")
    @Headers("Require-Authentication: false")
    Observable<Response<User>> login (
            @Field("email") String login,
            @Field("password") String password
    );

    @POST("/api/forgotPassword")
    @Headers("Require-Authentication: false")
    Observable<Response<Void>> forgotPassword(@Body ForgotPasswordRequest request);

    @GET("/api/logout")
    Observable<Response<Void>> logout();
}
