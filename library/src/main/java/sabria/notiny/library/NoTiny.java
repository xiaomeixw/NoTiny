package sabria.notiny.library;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import sabria.notiny.library.constant.Constants;
import sabria.notiny.library.extent.Wireable;
import sabria.notiny.library.task.Task;
import sabria.notiny.library.task.TaskQueue;
import sabria.notiny.library.task.queue.ObjectsMeta;
import sabria.notiny.library.tiny.EventBus;
import sabria.notiny.library.tiny.NoTinyImpl;
import sabria.notiny.library.tiny.NoTinyLifeCycle;
import sabria.notiny.library.util.Utils;

/**
 * Created by xiongwei,An Android project Engineer.
 * Date:2015-12-28  15:05
 * Base on Meilimei.com (PHP Service)
 * Describe:
 * Version:1.0
 * Open source
 */
public class NoTiny implements EventBus {


    static volatile NoTiny defaultInstance;
    private static final String TAG = "notiny";

    //
    NoTinyImpl mImpl;
    Thread mMainThread;
    TaskQueue mTaskQueue;
    boolean mProcessing;
    Handler mMainHandler;

    // subscribers and producers methods for a class
    private static final HashMap<Class<?>, ObjectsMeta> OBJECTS_METAS = new HashMap<Class<?>, ObjectsMeta>();
    // subscribers for certain event type
    private final HashMap<Class<?>, HashSet<Object>> mEventSubscribers = new HashMap<Class<?>, HashSet<Object>>();
    // producers for certain event type
    private final HashMap<Class<?>, Object> mEventProducers = new HashMap<Class<?>, Object>();


    public NoTiny(Context context) {
        
        mImpl  = new NoTinyImpl();

        mTaskQueue = new TaskQueue();
        mMainThread  = Thread.currentThread();

        final Looper looper = Looper.myLooper();
        //如果looper 不为null,就new Handler(looper)
        mMainHandler = looper == null ? null : new Handler(looper);

        //必须放在mMainHandler初始化后,因为要用getHandler获取mMainHandler对象
        mImpl.attachContext(context,this);

    }


    public static synchronized EventBus getDefault(Context context) {
        final NoTinyLifeCycle noTinyLifeCycle = NoTinyLifeCycle.get(context);
        NoTiny noTiny = noTinyLifeCycle.createNoTiny(context);
        if(noTiny==null){
            noTiny = noTinyLifeCycle.createNoTiny(context);
        }
        return noTiny;
    }

    /*public static EventBus getDefault(Context context) {
        if (defaultInstance == null) {
            synchronized (EventBus.class) {
                if (defaultInstance == null) {
                    defaultInstance = getNoTiny(context);
                }
            }
        }
        return defaultInstance;
    }*/

    public static NoTiny getNoTiny(Context context) {
        final NoTinyLifeCycle noTinyLifeCycle = NoTinyLifeCycle.get(context);
        NoTiny noTiny = noTinyLifeCycle.createNoTiny(context);
        if(noTiny==null){
            noTiny = noTinyLifeCycle.createNoTiny(context);
        }
        return noTiny;
    }


    @Override
    public void register(Object object) {
        Utils.assertObjectAndWorkerThread(object,mMainThread);
        Log.d(Constants.TAG, "NoTinyLifeCycle=" + object);
        mTaskQueue.offer(Task.obtainTask(this,Task.CODE_REGISTER,object));
        if(!mProcessing){
            processQueue();
        }
    }



    @Override
    public void unregister(Object object) {
        Utils.assertObjectAndWorkerThread(object,mMainThread);
        mTaskQueue.offer(Task.obtainTask(this,Task.CODE_UNREGISTER,object));
        if(!mProcessing){
            processQueue();
        }
    }

    @Override
    public boolean hasRegistered(Object object) {
        Utils.assertObjectAndWorkerThread(object, mMainThread);
        ObjectsMeta meta = OBJECTS_METAS.get(object.getClass());
        return meta!=null && meta.hasRegisteredObject(object,mEventSubscribers,mEventProducers);

    }

    @Override
    public void post(Object event) {
        if (event == null) {
            throw new NullPointerException("Event must not be null");
        }
        //判断调用post方法的线程是主线程还是子线程
        if(mMainThread == Thread.currentThread()){
            //主线程调用post()-->创建一个task并添加它到队列底部
            Task task = Task.obtainTask(this, Task.CODE_POST, event);
            mTaskQueue.offer(task);
            //TODO 扯犊子啊,这里掉了个！,找BUG啊
            if(!mProcessing){
                processQueue();
            }
        }else{
            //子线程调用post();
            if(mMainThread.isAlive()){//判断线程是否处于活动状态，相当于 run 是否还在执行
                Task task = Task.obtainTask(this, Task.CODE_DISPATCH_FROM_BACKGROUND, event).setTaskCallbacks(mImpl);
                Utils.getMainHandlerNotNull(mMainHandler).post(task);
            }
        }

    }



    @Override
    public void postDelayed(Object event, long delayMillis) {
        if (event == null) {
            throw new NullPointerException("Event must not be null");
        }
        Handler handler = Utils.getMainHandlerNotNull(mMainHandler);
        if(handler.getLooper().getThread().isAlive()){
            mImpl.postDelayed(event, delayMillis, handler);
        }
    }

    @Override
    public void cancelDelayed(Class<?> eventClass) {
        if(eventClass == null){
            throw new NullPointerException("Event class must not be null");
        }
        mImpl.cancelDelayed(eventClass, Utils.getMainHandlerNotNull(mMainHandler));
    }


    /**
     * 执行队列
     */
    private void processQueue() {
        Task task;
        ObjectsMeta meta;
        Object obj;
        Class<?> objClass;
        mProcessing = true;
        try {
            //如果队列的顶部一直有task
            while((task=mTaskQueue.poll())!=null){
                obj=task.obj;
                Log.i(Constants.TAG,"obj="+obj);
                objClass = obj.getClass();
                switch (task.code){
                    case Task.CODE_REGISTER:{
                        meta = OBJECTS_METAS.get(objClass);
                        if(meta == null){
                            //TODO 无语,这里开始传成objClass，导致反射时反射的是class不是具体的activity
                            meta = new ObjectsMeta(obj);
                            OBJECTS_METAS.put(objClass, meta);
                        }
                        meta.registerAtReceivers(obj, mEventSubscribers);
                        meta.registerAtProducers(obj,mEventProducers);
                        try{
                            meta.dispatchEvents(obj, mEventSubscribers, OBJECTS_METAS, mImpl);
                            meta.dispatchEvents(mEventProducers, obj, OBJECTS_METAS, mImpl);
                        }catch (Exception e){
                            throw handleExceptionOnEventDispatch(e);
                        }
                        break;
                    }

                    case Task.CODE_UNREGISTER:{
                        meta = OBJECTS_METAS.get(objClass);
                        meta.unregisterFromReceivers(obj, mEventSubscribers);
                        meta.unregisterFromProducers(obj, mEventProducers);
                        break;

                    }

                    case Task.CODE_POST:{
                        final HashSet<Object> receivers = mEventSubscribers.get(objClass);
                        if (receivers != null) {
                            ObjectsMeta.SubscriberCallback subscriberCallback;
                            try {
                                for (Object receiver : receivers) {
                                    meta = OBJECTS_METAS.get(receiver.getClass());
                                    subscriberCallback = meta.getEventCallback(objClass);
                                    Log.i(Constants.TAG,"meta="+meta);
                                    mImpl.dispatchEvent(subscriberCallback, receiver, obj);
                                }
                            } catch (Exception e) {
                                throw handleExceptionOnEventDispatch(e);
                            }
                        }
                        break;

                    }
                    default: throw new IllegalStateException("unexpected task code: " + task.code);
                }

                task.recycle();
            }
        } finally {
            mProcessing = false;
        }


    }


    //GET
    public NoTinyLifeCycle.LifecycleCallbacks getLifecycleCallbacks() {
        return mImpl;
    }

    public Handler getMainHandler() {
        return mMainHandler;
    }

    private RuntimeException handleExceptionOnEventDispatch(Exception e) {
        if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        }
        if (e instanceof InvocationTargetException) {
            // Extract subscriber method name to give developer more details
            String method = Log.getStackTraceString(e.getCause());
            int start = method.indexOf("at") + 3;
            method = method.substring(start, method.indexOf('\n', start));
            Log.e(TAG, "Exception in @Subscriber method: " + method + ". See stack trace for more details.");
        }
        return new RuntimeException(e);
    }


    //从NoTinyImpl反转过来的执行方案：Task.TaskCallbacks method
    public void onPostFromBackground(Task task) {
        task.code = Task.CODE_POST;
        mTaskQueue.offer(task);
        if(!mProcessing){
            processQueue();
        }
    }


    public void onPostDelayed(Task task,HashMap<Class<?>, Task> mDelayedTasks) {
        synchronized (this){
            mDelayedTasks.remove(task.obj.getClass());
            task.code = Task.CODE_POST;
        }
        mTaskQueue.offer(task);
        if(!mProcessing){
            processQueue();
        }

    }

    public void onDispatchInBackground(Task task) throws Exception {
        final Object receiver  = task.receiverRef.get();
        if(receiver !=null){
            Method callbackMethod  = task.subscriberCallback.method;
            if(callbackMethod.getParameterTypes().length == 2){
                // expect callback with two parameters
                callbackMethod.invoke(receiver,task.obj,task.bus);
            }else{
                // expect callback with a single parameter
                callbackMethod.invoke(receiver,task.obj);
            }
        }
    }

    //wire
    ArrayList<Wireable> mWireables;
    public NoTiny wire(Wireable wireable){
        Utils.assertObjectAndWorkerThread(wireable,mMainThread);
        Context context = Utils.getNotNullContext(mImpl.getContextRef());

        //第一次的为null,之后的都是添加到这个ArrayList数组中
        if (mWireables == null) {
            mWireables = new ArrayList<Wireable>();
            mImpl.attachWireable(mWireables);
        }

        mWireables.add(wireable);

        wireable.onCreate(this, context.getApplicationContext());
        wireable.assertSuperOnCreateCalled();

        if(context instanceof Application || context instanceof Service){
            wireable.onStart();
        }

        return this;
    }


    public <T extends Wireable> T unwire(Class<T> wireClass){
        Utils.assertObjectAndWorkerThread(wireClass,mMainThread);
        Context context = Utils.getNotNullContext(mImpl.getContextRef());
        Wireable  wireable = getWireable(wireClass);
        if (wireable != null) {

            if (context instanceof Application
                    || context instanceof Service) {
                wireable.onStop();
                wireable.onDestroy();
            }

            mWireables.remove(wireable);
        }
        return (T) wireable;
    }

    public boolean hasWireable(Class<? extends Wireable> wireClass) {
        return getWireable(wireClass) != null;
    }

    private Wireable getWireable(Class<? extends Wireable> wireClass) {
        if (mWireables == null) {
            return null;
        }
        for(Wireable wireable : mWireables) {
            if (wireClass.equals(wireable.getClass())) {
                return wireable;
            }
        }
        return null;
    }




}
