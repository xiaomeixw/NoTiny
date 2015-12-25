package sabria.notiny.library.thread;

import sabria.notiny.library.task.Task;

/**
 * Created by xiongwei,An Android project Engineer.
 * Date:2015-12-25  10:08
 * Base on Meilimei.com (PHP Service)
 * Describe:
 * Version:1.0
 * Open source
 */
public class ThreadPool {


    private final Dispatcher mDispatcher;

    private final WorkerThread[] mThrads;


    public ThreadPool(Dispatcher dispatcher,int size){
        this.mDispatcher=dispatcher;
        //初始化几个WorkThread
        this.mThrads=new WorkerThread[size];
    }




    //分发机制
    public void onTaskProcessed(Task task) {
        mDispatcher.onTaskProcessed(task);
    }

    //TODO
    public boolean processTask(Task task) {
        return false;
    }
}
