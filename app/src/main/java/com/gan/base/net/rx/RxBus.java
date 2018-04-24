package com.gan.base.net.rx;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.processors.PublishProcessor;


public class RxBus {

    private static volatile RxBus mDefaultInstance;

    private RxBus() {
    }

    public static RxBus getDefault() {
        if (mDefaultInstance == null) {
            synchronized (RxBus.class) {
                if (mDefaultInstance == null) {
                    mDefaultInstance = new RxBus();
                }
            }
        }
        return mDefaultInstance;
    }

    private final PublishProcessor _bus = PublishProcessor.create();

    public void send(Object o) {
        _bus.onNext(o);
    }

    public Flowable<Object> toObservable() {
        return _bus;
    }

    /**
     * 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者
     *
     * @param eventType 事件类型
     * @param <T>
     * @return
     */
    public <T> Flowable<T> toObservable(Class<T> eventType) {
        return _bus.ofType(eventType);
    }

    /**
     * 提供了一个新的事件,根据code进行分发
     *
     * @param code 事件code
     * @param o
     */
    public void post(int code, Object o) {
        _bus.onNext(new RxBusBaseMessage(code, o));

    }


    /**
     * 根据传递的code和 eventType 类型返回特定类型(eventType)的 被观察者
     * 对于注册了code为0，class为voidMessage的观察者，那么就接收不到code为0之外的voidMessage。
     *
     * @param <T>
     * @param code      事件code
     * @param eventType 事件类型
     * @return
     */
    public <T> Flowable toObservable(final int code, final Class<T> eventType) {
        return _bus.ofType(RxBusBaseMessage.class)
                .filter(new Predicate<RxBusBaseMessage>() {
                    @Override
                    public boolean test(RxBusBaseMessage rxBusBaseMessage) throws Exception {
                        //过滤code和eventType都相同的事件
                        return rxBusBaseMessage.getCode() == code && eventType.isInstance(rxBusBaseMessage.getObject());
                    }
                }).map(new Function<RxBusBaseMessage, Object>() {
                    @Override
                    public Object apply(RxBusBaseMessage o) throws Exception {
                        return o.getObject();
                    }

                }).cast(eventType);
    }

    /**
     * 判断是否有订阅者
     */
    public boolean hasObservers() {
        return _bus.hasSubscribers();
    }
}
