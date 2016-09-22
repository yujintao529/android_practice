package com.example.mypractice.other;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.SharedElementCallback;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.example.mypractice.Logger;
import com.example.mypractice.R;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.internal.util.RxJavaPluginUtils;
import rx.observables.GroupedObservable;
import rx.plugins.RxJavaPlugins;
import rx.schedulers.Schedulers;

/**
 * Created by yujintao on 16/9/21.
 */

public class RxAndroidAct extends AppCompatActivity {


    @BindView(R.id.button3)
    Button button3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_rxandroid);
        ButterKnife.bind(this);

    }

    @OnClick({R.id.button3})
    public void onClick(Button button) {
        Observable.from(new Integer[]{2, 4, 12, 5, 34325, 643, 2341, 234, 42, 1})
                .filter(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {

                        return integer > 300;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Logger.d("call " + integer);
                    }
                });
    }


    @OnClick(R.id.timer)
    public void repeat(Button button) {
        Observable.timer(1000, TimeUnit.MILLISECONDS).subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                Logger.d("call " + aLong);
            }
        });
    }


    @OnClick(R.id.create)
    public void onClick3(Button button) {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onStart();

                for (int i = 0; i < 10; i++) {
                    if (!subscriber.isUnsubscribed()) {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            subscriber.onError(e);
                        }
                        subscriber.onNext("yujintao" + i);
                    }
                }
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Logger.d("call --  " + s);
                    }
                });
    }

    public static class Data {
        public String name;
        public int id;
        public Data(String name) {
            this.name = name;
        }

        public Data(String name, int id) {
            this.name = name;
            this.id = id;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "name='" + name + '\'' +
                    ", id=" + id +
                    '}';
        }
    }

    @OnClick(R.id.buffer)
    public void buffer(Button button) {
        Observable.just(new Data("yujintao"), new Data("yujintao2"), new Data("yujintao3"), new Data("yujintao4"), new Data("yujintao5"), new Data("yujintao6")).buffer(10, TimeUnit.SECONDS, 3)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Data>>() {
                    @Override
                    public void call(List<Data> datas) {
                        for (int i = 0; i < datas.size(); i++) {
                            Logger.d("action call " + datas.get(i).name + " i=" + i);
                        }
                    }
                });
        Observable.from(new Object[]{}).window(new Func0<Observable<?>>() {
            @Override
            public Observable<?> call() {
                return null;
            }
        });
    }

    @OnClick(R.id.map)
    public void map() {
        Observable.just(new Data("yujintao"), new Data("yujintao2"), new Data("yujintao3"), new Data("yujintao4"), new Data("yujintao5"), new Data("yujintao6"))
                .map(new Func1<Data, String>() {
                    @Override
                    public String call(Data data) {
                        return data.name;
                    }
                }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Logger.d("call " + s);
            }
        });
    }

    @OnClick(R.id.scan)
    public void scan() {
        Observable.just(new Data("yujintao"), new Data("yujintao2"), new Data("yujintao3"), new Data("yujintao4"), new Data("yujintao5"), new Data("yujintao6"))
                .scan(new Func2<Data, Data, Data>() {
                    @Override
                    public Data call(Data data, Data data2) {
                        Logger.d("data1 " + data);
                        Logger.d("data2 " + data2);

                        return new Data(data.name+data2.name);
                    }
                }).subscribe(new Action1<Data>() {
            @Override
            public void call(Data data) {
                Logger.d("call "+data);
            }
        });
    }
    @OnClick(R.id.groupby)
    public void groupby() {
        Observable.just(new Data("yujintao",1), new Data("yujintao2",1), new Data("yujintao3",1), new Data("yujintao4"), new Data("yujintao5"), new Data("yujintao6"))
                .groupBy(new Func1<Data, Integer>() {
                    @Override
                    public Integer call(Data data) {
                        return data.id;
                    }
                })
                .subscribe(new Action1<GroupedObservable<Integer, Data>>() {
                    @Override
                    public void call(GroupedObservable<Integer, Data> objectDataGroupedObservable) {
                        Logger.d("objectDataGroupedObservable.getKey() - "+objectDataGroupedObservable.getKey());
                        objectDataGroupedObservable.subscribe(new Action1<Data>() {
                            @Override
                            public void call(Data data) {
                                Logger.d("call "+data.toString());
                            }
                        });
                    }
                });
    }



}