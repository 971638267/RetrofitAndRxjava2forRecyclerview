package com.gan.base.net.networks;


import android.graphics.BitmapFactory;

import com.gan.base.net.requestbean.BaseRequest4List;
import com.gan.base.net.requestbean.MovieInfo;
import com.gan.base.net.resultbean.HttpResult;
import com.gan.base.net.resultbean.UserInfoResult;
import com.gan.base.net.subscribers.ProgressSubscriber;
import com.gan.base.net.utils.LogUtils;
import com.gan.base.net.utils.RetrofitUtils;

import org.reactivestreams.Subscriber;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.functions.Function;


/**
 * Created by gan on 2017/2/20 0003.
 */
public class NetWorks extends RetrofitUtils {
    private NetWorks() {
        super();
    }

    public void inTheaters(Subscriber<List<MovieInfo>> subscriber, BaseRequest4List requset) {
        Flowable observable = service.inTheaters(requset.toMap()).map(new HttpResultFunc<List<MovieInfo>>());
        setSubscribe(observable, subscriber);
    }

    /**
     * 登录
     *
     * @param subscriber
     * @param loginName
     * @param loginPwd
     */
    public void postLogin(ProgressSubscriber<UserInfoResult> subscriber, String loginName, String loginPwd) {
        Flowable observable = service.postLogin(loginName, loginPwd).map(new HttpResultFunc<UserInfoResult>());
        setSubscribe(observable, subscriber);
    }

    /**
     * 用来统一处理Http的resultCode,并将HttpResult的Data部分剥离出来返回给subscriber
     */
    private class HttpResultFunc<T> implements Function<HttpResult<T>, T> {
        @Override
        public T apply(HttpResult<T> tHttpResult) throws Exception {
            return tHttpResult.getSubjects();
        }
    }


    //在访问HttpMethods时创建单例
    private static class SingletonHolder {
        private static final NetWorks INSTANCE = new NetWorks();
    }

    //获取单例
    public static NetWorks getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
