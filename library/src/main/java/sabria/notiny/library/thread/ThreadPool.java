package sabria.notiny.library.thread;

import sabria.notiny.library.constant.Constants;
import sabria.notiny.library.task.Task;
import sabria.notiny.library.util.Utils;

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

    //执行Task
    public boolean processTask(Task task) {

        Utils.assertDispatcherThread(mDispatcher.getDispatcherHandler());

        boolean taskAccepted = false;
        final int size = mThrads.length;
        for(int i =0;i<size;i++){
            WorkerThread workerThread = mThrads[i];
            //如果workThrad数组中的为空,就创建
            if(workerThread!=null){
                workerThread = new WorkerThread(this, Constants.THREAD_NAME+i+"");
                //执行workThread
                workerThread.start();
                //并把新创建的workThread放入数组中
                mThrads[i]=workerThread;
            }

            //将workThread和task关联
            taskAccepted =  workerThread.processTask(task);

            if(taskAccepted){
                break;
            }
        }


        return taskAccepted;
    }

    /**
     * 将所有的workThread执行销毁/取消操作
     */
    public void destroy() {
        Utils.assertDispatcherThread(mDispatcher.getDispatcherHandler());
        final  int size  = mThrads.length;
        //销毁workThread数组中的所有workThread
        for (int i =0 ; i<size;i++){
            WorkerThread workerThread = mThrads[i];
            if(workerThread!=null){
                workerThread.cancel();
            }
        }


    }


}
