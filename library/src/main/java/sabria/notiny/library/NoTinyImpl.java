package sabria.notiny.library;

import android.content.Context;
import android.os.Handler;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import sabria.notiny.library.anno.Subscribe;
import sabria.notiny.library.task.Task;
import sabria.notiny.library.task.TaskQueue;
import sabria.notiny.library.task.queue.ObjectsMeta;
import sabria.notiny.library.tiny.NoTinyLifeCycle;
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
    // list of delayed tasks accessed from different threads 延迟启动的threads
    private HashMap<Class<?>, Task> mDelayedTasks;
    //引用
    NoTiny noTiny;
    TaskQueue mTaskQueue;



    //NoTinyLifeCycle.LifecycleCallbacks method
    @Override
    public void attachContext(Context context,NoTiny noTiny) {
        mContextRef = context == null ? null : new WeakReference<>(context);
        this.noTiny=noTiny;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {

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
