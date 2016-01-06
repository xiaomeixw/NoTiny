package sabria.notiny.library.thread;

import android.os.HandlerThread;

import java.util.ArrayList;
import java.util.HashMap;

import sabria.notiny.library.constant.Constants;
import sabria.notiny.library.task.Task;
import sabria.notiny.library.task.queue.SerialTaskQueue;
import sabria.notiny.library.thread.dispatcher.DispatcherHandler;
import sabria.notiny.library.thread.dispatcher.DispatcherLogic;

/**
 * Created by xiongwei,An Android project Engineer.
 * Date:2015-12-25  10:21
 * Base on Meilimei.com (PHP Service)
 * Describe: 分发,同时执行Task
 * Version:1.0
 * Open source
 */
public class Dispatcher {


    public final DispatcherHandler mDispatcherHandler;
    private final DispatcherLogic dispatcherLogic;
    private final ThreadPool mThreadPool;

    private final HashMap<String, SerialTaskQueue> mQueuesMap;
    private final ArrayList<SerialTaskQueue> mQueuesList;


    public DispatcherHandler getDispatcherHandler() {
        return mDispatcherHandler;
    }

    public Dispatcher(){

        //使用HanderThread简化Looper的书写
        HandlerThread handlerThread = new HandlerThread(Constants.HANDLER_THREAD_NAME);
        handlerThread.start();

        //DispatcherLogic执行任务
        dispatcherLogic = new DispatcherLogic();

        //ThreadPool
        mThreadPool = new ThreadPool(this, Constants.DEFAULT_WORK_THREAD_SIZE);

        //List & Map
        mQueuesList  = new ArrayList<>(4);
        mQueuesMap  = new HashMap<>(4);

        mDispatcherHandler = new DispatcherHandler(handlerThread.getLooper(), this);
    }


    /**
     * 把消息传递给DispatcherHandler去执行,然后待会还是会转回来到这个类中
     * @param task
     */
    public void onTaskProcessed(Task task) {
        mDispatcherHandler.postMesssageAlreadyProcessedTask(task);
    }

    public void destory(){
        mDispatcherHandler.postMessageDestroy();
    }

    public void dispatchEventToBackground(Task task){
        mDispatcherHandler.postMesssageProcessTask(task);
    }



    //---------->

    /**
     * 从DispatcherHandler中message传递过来的
     * @param task
     */
    public void handlerProcessTask(Task task) {
        dispatcherLogic.handlerProcessTask(mThreadPool,mDispatcherHandler,mQueuesMap,mQueuesList,task);
    }
    /**
     * 从DispatcherHandler中message传递过来的
     * @param task
     */
    public void handlerAlreadyProcessedTask(Task task) {
        dispatcherLogic.handlerAlreadyProcessedTask(mThreadPool,mDispatcherHandler,mQueuesMap,mQueuesList,task);
    }
    /**
     * 从DispatcherHandler中message传递过来的
     */
    public void handlerDestory() {
        dispatcherLogic.destroy(mThreadPool);
    }
}
