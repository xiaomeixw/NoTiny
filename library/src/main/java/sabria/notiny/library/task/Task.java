package sabria.notiny.library.task;


import sabria.notiny.library.task.queue.ObjectsMeta;
import sabria.notiny.library.test.TestRun;

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

    public Task prev;

    public Task() { }

    public int code;

    public static final int CODE_POST = 2;
    public static final int CODE_DISPATCH_FROM_BACKGROUND = 10;
    public static final int CODE_DISPATCH_TO_BACKGROUND = 11;



    // dispatch in background
    public ObjectsMeta.SubscriberCallback subscriberCallback;


    public static Task obtainTask(int code) {
        Task task;
        synchronized (POOL) {
            task = POOL.acquire();
        }
        task.code = code;
        task.prev = null;
        return task;
    }

    @Override
    public void run() {

        switch (code){
            case CODE_DISPATCH_FROM_BACKGROUND:
                callbacks.onPostFromBackground(this);
                break;
        }




        //执行二叉树10万次
        for(int i =0 ;i<100000;i++){
            TestRun.main();
        }
    }



    //状态处理回调
    public TaskCallbacks  callbacks;
    public  interface TaskCallbacks {
        void onPostFromBackground(Task task);
        void onDispatchInBackground(Task task) throws Exception;


    }
    public Task setTaskCallbacks(TaskCallbacks callbacks) {
        this.callbacks = callbacks;
        return this;
    }




    //all set null
    public void recycle() {
    }


}
