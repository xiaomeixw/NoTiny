package sabria.notiny.library.thread.dispatcher;

import java.util.ArrayList;
import java.util.HashMap;

import sabria.notiny.library.task.Task;
import sabria.notiny.library.task.queue.SerialTaskQueue;
import sabria.notiny.library.thread.ThreadPool;

/**
 * Created by xiongwei,An Android project Engineer.
 * Date:2015-12-25  12:23
 * Base on Meilimei.com (PHP Service)
 * Describe:
 * Version:1.0
 * Open source
 */
public class DispatcherLogic {




    public void handlerProcessTask(ThreadPool mThreadPool,DispatcherHandler mDispatcherHandler, HashMap<String, SerialTaskQueue> mQueuesMap, ArrayList<SerialTaskQueue> mQueuesList, Task task) {
        assertDispatcherThread(mDispatcherHandler);

        //创建一个Queue,然后放入这个Queue队列中
        SerialTaskQueue taskQueue = mQueuesMap.get(task.subscriberCallback.queue);
        //如果Map中没有
        if(taskQueue == null){
             taskQueue  = new SerialTaskQueue(task.subscriberCallback.queue);
             //放入Map中
             mQueuesMap.put(task.subscriberCallback.queue, taskQueue);
             //放入List中
             mQueuesList.add(taskQueue);
        }

        //插入Queue
        taskQueue.offer(task);



        //然后执行下一个Task
        processNextTask(mThreadPool,mDispatcherHandler,mQueuesList);

    }

    private void processNextTask(ThreadPool mThreadPool,DispatcherHandler mDispatcherHandler,ArrayList<SerialTaskQueue> mQueuesList) {
        assertDispatcherThread(mDispatcherHandler);

        SerialTaskQueue nextQueue = null;
        for(SerialTaskQueue queue : mQueuesList){

            if(queue.isProcessing() || queue .isEmpty()){
                continue;
            }
            nextQueue = queue;
            break;
        }

        if(nextQueue == null){
            return;//nothing to do process for now
        }

        Task task = nextQueue.poll();
        boolean taskAccepted = mThreadPool.processTask(task);

        if(taskAccepted){
            nextQueue.setProcessing(true);
        }else{
            //回滚task： no worker threads available, return task back into the queue
            nextQueue.unpoll(task);
        }

    }

    /**
     * 必需是DispatcherHandler线程操作
     * @param mDispatcherHandler
     */
    private void assertDispatcherThread(DispatcherHandler mDispatcherHandler) {
        if(Thread.currentThread() != mDispatcherHandler.getLooper().getThread()){
            throw new IllegalStateException("method accessed from wrong thread");
        }
    }
}
