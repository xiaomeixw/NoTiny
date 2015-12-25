package sabria.notiny.library.thread;

import android.os.Process;

import java.util.concurrent.atomic.AtomicBoolean;

import sabria.notiny.library.task.Task;
import sabria.notiny.library.util.Utils;

/**
 * Created by xiongwei,An Android project Engineer.
 * Date:2015-12-25  10:00
 * Base on Meilimei.com (PHP Service)
 * Describe: 探索线程与分发
 * Version:1.0
 * Open source
 */
public class WorkerThread extends Thread{


    //原子操作--》get/set
    private final AtomicBoolean mRunning;
    private ThreadPool mThreadPool;
    //锁
    private final Object mLock;


    private Task mTask;


    public  WorkerThread(ThreadPool threadPool){
        this.mThreadPool=threadPool;
        mLock = new Object();
        mRunning = new AtomicBoolean(true);
    }

    public void cancel(){
        mRunning.set(false);
    }


    /**
     * 使用wait和notify实现的消费者生产者经典模式
     */
    @Override
    public void run() {
        //执行为跑后台线程,为UI线程给予更多资源
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        //初始化的AtomicBoolean mRunning为ture,只有显示调用cancel thread才设置为false,停止thread运行
        while (mRunning.get()){

            //等待Task任务
            synchronized (mLock){
                while (mTask==null){
                    try {
                        Utils.log("队列空，等待数据");
                        mLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            //执行任务
            try {
                mTask.callback.onDispatchInBackground(mTask);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }finally {
                //不管是否发生异常,都执行到ThreadPool的分发
                Task task  = this.mTask;
                synchronized (mLock){
                    mTask = null;
                }
                //分发机制处理
                mThreadPool.onTaskProcessed(task);
            }


        }

    }
}
