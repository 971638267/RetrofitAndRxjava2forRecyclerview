package com.gan.base.net.service;



import com.gan.base.net.requestbean.MovieInfo;
import com.gan.base.net.resultbean.HttpResult;
import com.gan.base.net.resultbean.UserInfoResult;

import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface NetService {

    @FormUrlEncoded
    @POST("in_theaters")
    Flowable<HttpResult<List<MovieInfo>>> inTheaters(@FieldMap Map<String,Object> map);


    @FormUrlEncoded
    @POST("login")
    Flowable<HttpResult<UserInfoResult>> postLogin(@Field("lgiName") String loginName, @Field("lgiPwd") String loginPwd);

}
