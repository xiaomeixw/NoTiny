package sabria.notiny.library.tiny;

import android.content.Context;
import android.os.Handler;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import sabria.notiny.library.NoTiny;
import sabria.notiny.library.anno.Subscribe;
import sabria.notiny.library.extent.Wireable;
import sabria.notiny.library.task.Task;
import sabria.notiny.library.task.queue.ObjectsMeta;
import sabria.notiny.library.util.Utils;

/**
 * Created by xiongwei,An Android project Engineer.
 * Date:2016-01-04  10:16
 * Base on Meilimei.com (PHP Service)
 * Describe:
 * Version:1.0
 * Open source
 */
public class NoTinyImpl implements ObjectsMeta.EventDispatchCallback,NoTinyLifeCycle.LifecycleCallbacks,Task.TaskCallbacks{

    //软引用context
    private WeakReference<Context> mContextRef;
    //list of delayed tasks accessed from different threads 延迟启动的threads
    private HashMap<Class<?>, Task> mDelayedTasks;
    //引用
    NoTiny noTiny;
    private ArrayList<Wireable> mWireables;
    private Handler mHandler;

    public WeakReference<Context> getContextRef() {
        return mContextRef;
    }

    //NoTinyLifeCycle.LifecycleCallbacks method
    @Override
    public void attachContext(Context context,NoTiny noTiny) {
        mContextRef = context == null ? null : new WeakReference<>(context);
        this.noTiny=noTiny;
        this.mHandler=noTiny.getMainHandler();
    }

    public void attachWireable(ArrayList<Wireable> wireables){
        this.mWireables=wireables;
    }

    @Override
    public void onStart() {
        if(mWireables != null){
            for(Wireable wireable : mWireables) {
                wireable.onStart();
            }
        }
    }

    @Override
    public void onStop() {
        if (mWireables != null) {
            for (Wireable wireable : mWireables) {
                wireable.onStop();
            }
        }
    }

    @Override
    public void onDestroy() {
        if (mWireables != null) {
            for (Wireable wireable : mWireables) {
                wireable.onDestroy();
            }
        }
        synchronized (this){
            if(mDelayedTasks !=null && mDelayedTasks.size()>0){
                Handler handler = Utils.getMainHandlerNotNull(mHandler);
                Collection<Task> tasks = mDelayedTasks.values();
                for(Task task : tasks){
                    handler.removeCallbacks(task);
                    task.recycle();
                }
                mDelayedTasks.clear();
            }
        }
    }





    //Task.TaskCallbacks method
    @Override
    public void onPostFromBackground(Task task) {
        noTiny.onPostFromBackground(task);
    }

    @Override
    public void onPostDelayed(Task task) {
        noTiny.onPostDelayed(task,mDelayedTasks);
    }

    @Override
    public void onDispatchInBackground(Task task) throws Exception {
        noTiny.onDispatchInBackground(task);
    }


    //ObjectsMeta.EventDispatchCallback method
    @Override
    public void dispatchEvent(ObjectsMeta.SubscriberCallback subscriberCallback, Object receiver, Object event) throws Exception {

        //检查用户指定是走主线程还是子线程

        if(subscriberCallback.mode == Subscribe.Mode.Backgroud){

            Task task = Task.obtainTask(noTiny, Task.CODE_DISPATCH_TO_BACKGROUND, event).setTaskCallbacks(this);
            task.subscriberCallback=subscriberCallback;
            task.receiverRef=new WeakReference<>(receiver);

            //检查context是否为空
            Context context = Utils.getNotNullContext(mContextRef);

            //其实这里就是创建了一个新的Dispatcher
            NoTinyLifeCycle.get(context).getDispatcher().dispatchEventToBackground(task);

        }else{
            //invoke(object,obj...)
            subscriberCallback.method.invoke(receiver,event);
        }
    }



    //noTiny method
    public void postDelayed(Object event, long delayMillis, Handler handler) {
        Task task;
        synchronized (this){
            if(mDelayedTasks == null){
                mDelayedTasks = new HashMap<Class<?>, Task>();
            }

            task = mDelayedTasks.get(event.getClass());

            if(task==null){
                task = Task.obtainTask(noTiny,Task.CODE_POST_DELAYED,event).setTaskCallbacks(this);
                mDelayedTasks.put(event.getClass(),task);
            }else{
                handler.removeCallbacks(task);
                task.obj=event;
            }

        }

        handler.postDelayed(task,delayMillis);

    }

    public void cancelDelayed(Class<?> eventClass, Handler handler) {
        Task task = null;
        synchronized (this){
            if(mDelayedTasks !=null){
                task = mDelayedTasks.remove(eventClass);
            }
        }

        if(task!=null){
            handler.removeCallbacks(task);
            task.recycle();
        }

    }


}
