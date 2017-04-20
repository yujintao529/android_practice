package com.example.mypractice.other;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.example.mypractice.Logger;
import com.example.mypractice.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.observables.GroupedObservable;
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

     Subscription subscription;
    @OnClick(R.id.interval)
    public void interval(){
        subscription= Observable.interval(2000,TimeUnit.MILLISECONDS).
        filter(new Func1<Long, Boolean>() {
            @Override
            public Boolean call(Long aLong) {
                Logger.d("Func1 call "+aLong);
                return aLong%2==0;
            }
        }).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        Logger.d("Action1 call "+ aLong);
                        if(aLong==10){
                            subscription.unsubscribe();
                        }
                    }
                });
    }

    @OnClick(R.id.repeat)
    public void repeat(){

    }
    @OnClick(R.id.debounce)
    public void debounce(){
        Observable.create(new Observable.OnSubscribe<Data>() {
            @Override
            public void call(Subscriber<? super Data> subscriber) {
                for(int i=0;i<20;i++){
                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    subscriber.onNext(new Data("yujintao",i));
                }
                subscriber.onCompleted();

            }
        }).debounce(1000,TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Data>() {
                    @Override
                    public void call(Data data) {
                        Logger.d("action1 call "+data.toString());
                    }
                });
    }

    @OnClick(R.id.throttleWithTimeout)
    public void throttleWithTimeout(){
        Observable.create(new Observable.OnSubscribe<Data>() {
            @Override
            public void call(Subscriber<? super Data> subscriber) {
                for(int i=0;i<20;i++){
                    try {
                        Thread.sleep(1100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    subscriber.onNext(new Data("yujintao",i));
                }
                subscriber.onCompleted();

            }
        }).throttleWithTimeout(1000,TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Data>() {
                    @Override
                    public void call(Data data) {
                        Logger.d("action1 call "+data.toString());
                    }
                });

    }
    @OnClick(R.id.Distinct)
    public void Distinct(){
        Observable.create(new Observable.OnSubscribe<Data>() {
            @Override
            public void call(Subscriber<? super Data> subscriber) {
                for(int i=0;i<20;i++){
                    subscriber.onNext(new Data("yujintao",i));
                }
                for(int i=0;i<10;i++){
                    subscriber.onNext(new Data("yujintao",i));
                }
                for(int i=0;i<30;i++){
                    subscriber.onNext(new Data("yujintao",i));
                }
                subscriber.onCompleted();

            }
        }).distinct(new Func1<Data, Integer>() {
            @Override
            public Integer call(Data data) {
                return data.id;
            }
        }).subscribe(new Action1<Data>() {
            @Override
            public void call(Data data) {
                Logger.d("action1 call "+data.toString());
            }
        });
    }
    @OnClick(R.id.sample)
    public void sample(){
        Observable.create(new Observable.OnSubscribe<Data>() {
            @Override
            public void call(Subscriber<? super Data> subscriber) {
                for(int i=0;i<20;i++){
                    try {
                        Thread.sleep(1100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    subscriber.onNext(new Data("yujintao",i));
                }
                subscriber.onCompleted();

            }
        }).throttleFirst(2000,TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Data>() {
            @Override
            public void call(Data data) {
                Logger.d("action1 call "+data.toString());
            }
        });
    }
    @OnClick(R.id.merge)
    public void merge(){
        Observable<Integer> observable=Observable.just(1,2,3,4);
        Observable<Integer> observable2=Observable.just(5,6,7,8);
        Observable.merge(observable,observable2).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Logger.d("action call "+integer);
            }
        });



    }

    /**
     * 这个例子有问题啊
     */
    @OnClick(R.id.join)
    public void join(){
        Observable<Integer> observable=Observable.just(82,34);
        Observable<Integer> observable2=Observable.just(100,200);
        observable.join(observable2, new Func1<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> call(Integer integer) {
                Logger.d("left fun1 "+integer);
                return Observable.just(integer);
            }
        }, new Func1<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> call(Integer integer) {
                Logger.d("right fun1 "+integer);
                return Observable.just(integer);
            }
        }, new Func2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer integer, Integer integer2) {
                Logger.d("result action "+(integer+integer2));
                return null;
            }
        }).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Logger.d("action1 call "+integer);
            }
        });


    }
    @OnClick(R.id.delay)
    public void delay(){
        Observable.just(1,2,4,5).delay(new Func1<Integer, Observable<Object>>() {
            @Override
            public Observable<Object> call(Integer integer) {
                Logger.d("func1 call "+integer);
                return Observable.empty().delay(integer+1,TimeUnit.SECONDS);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Logger.d("action1 call "+integer);
            }
        });

    }
    @OnClick(R.id.delaySubscription)
    public void delaySubscription(){
        Observable.just(1,2,4,5).doOnCompleted(new Action0() {
            @Override
            public void call() {

            }
        }).delaySubscription(1000,TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Logger.d("action1 call "+integer);
            }
        });
        Observable.just(1,2,4,5).delay(1000,TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Logger.d("action1 call "+integer);
            }
        });
    }
    @OnClick(R.id.backpressed)
    public void backpressed(){
        Observable.interval(1, TimeUnit.MILLISECONDS)
                .onBackpressureDrop()
                .observeOn(Schedulers.newThread());


    }
    @OnClick(R.id.collect)
    public void collect(){
        //collect 收集发射的数据然后统一进行发射
        Observable.just(1,2,4,5,6,7,8,9,4,42).collect(()->{
            return new ArrayList<>();
        }, (List<Integer> list,Integer integer)->{
            list.add(integer);
        }).subscribe((List<Integer> list)->{
                Logger.d("inner call "+list);
        });
    }


    @OnClick(R.id.flatmap)
    public void flatmap(){
        //concatMap是顺序执行的，flatmap是并行执行的。
        Observable.just(2,4,6,8,10).concatMap(new Func1<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> call(final Integer integer) {
                return Observable.create(new Observable.OnSubscribe<Integer>() {
                    @Override
                    public void call(Subscriber<? super Integer> subscriber) {
                        subscriber.onNext(integer);
                        subscriber.onNext(integer*2);
                        subscriber.onCompleted();
                        Logger.d("inner call "+integer+" "+Thread.currentThread().getName());
                    }
                }).subscribeOn(Schedulers.io());
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Logger.d("call "+integer);
            }
        });
    }

}