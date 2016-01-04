package sabria.notiny.library.task;


import java.lang.ref.WeakReference;

import sabria.notiny.library.NoTiny;
import sabria.notiny.library.task.queue.ObjectsMeta;

/**
 * Created by xiongwei,An Android project Engineer.
 * Date:2015-12-25  10:22
 * Base on Meilimei.com (PHP Service)
 * Describe:
 * Version:1.0
 * Open source
 */
public class Task implements Runnable{

    private static final TaskPool POOL = new TaskPool(32);

    //状态处理回调
    public TaskCallbacks  callbacks;
    public  interface TaskCallbacks {
        void onPostFromBackground(Task task);
        void onPostDelayed(Task task);
        void onDispatchInBackground(Task task) throws Exception;
    }
    public Task setTaskCallbacks(TaskCallbacks callbacks) {
        this.callbacks = callbacks;
        return this;
    }


    //
    public Task prev;

    public Task() { }

    public int code;
    public Object obj;
    public NoTiny bus;


    public static final int CODE_REGISTER = 1;
    public static final int CODE_UNREGISTER = 2;
    public static final int CODE_POST = 3;
    public static final int CODE_POST_DELAYED = 4;
    public static final int CODE_DISPATCH_FROM_BACKGROUND = 10;
    public static final int CODE_DISPATCH_TO_BACKGROUND = 11;



    // dispatch in background
    public ObjectsMeta.SubscriberCallback subscriberCallback;
    public WeakReference<Object> receiverRef;


    public static Task obtainTask(NoTiny noTiny,int code,Object obj) {
        Task task;
        synchronized (POOL) {
            task = POOL.acquire();
        }
        task.bus=noTiny;
        task.code = code;
        task.obj=obj;
        task.prev = null;
        return task;
    }

    @Override
    public void run() {
        switch (code){
            case CODE_DISPATCH_FROM_BACKGROUND:
                callbacks.onPostFromBackground(this);
                break;

            case CODE_POST_DELAYED:
                callbacks.onPostDelayed(this);
                break;

            default:
                throw new IllegalStateException(String.valueOf(code));

        }
    }








    //all set null
    public void recycle() {
    }


}
