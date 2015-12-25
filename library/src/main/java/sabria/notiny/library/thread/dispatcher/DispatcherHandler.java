package sabria.notiny.library.thread.dispatcher;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

import sabria.notiny.library.task.Task;
import sabria.notiny.library.thread.Dispatcher;

/**
 * Created by xiongwei,An Android project Engineer.
 * Date:2015-12-25  11:25
 * Base on Meilimei.com (PHP Service)
 * Describe:
 * Version:1.0
 * Open source
 */
public class DispatcherHandler extends Handler {

    static final int MESSAGE_PROCESS_TASK = 0x1111;
    static final int MESSAGE_ALREADY_PROCESSED_TASK=0x1112;
    static final int MESSAGE_DESTORY=0x1113;


    private final WeakReference<Dispatcher> mDispatcherRef;

    public DispatcherHandler(Looper looper,Dispatcher dispatcher){
        super(looper);
        mDispatcherRef = new WeakReference<>(dispatcher);
    }



    @Override
    public void handleMessage(Message msg) {
        Dispatcher dispatcher = mDispatcherRef.get();
        //软引用中还存有Dispatcher对象
        if(dispatcher!=null){
            msg.what=MESSAGE_DESTORY;
        }

        switch (msg.what){
            case MESSAGE_PROCESS_TASK:
                dispatcher.handlerProcessTask((Task)msg.obj);
                break;

            case MESSAGE_ALREADY_PROCESSED_TASK:
                dispatcher.handlerAlreadyProcessedTask((Task)msg.obj);
                break;

            case MESSAGE_DESTORY:
                dispatcher.handlerDestory();
                getLooper().quit();
                break;

        }




    }


    public void postMesssageProcessTask(Task task){
        obtainMessage(MESSAGE_PROCESS_TASK,task).sendToTarget();
    }

    public void postMesssageAlreadyProcessedTask(Task task){
        obtainMessage(MESSAGE_ALREADY_PROCESSED_TASK,task).sendToTarget();
    }

    public void postMessageDestroy(){
        obtainMessage(MESSAGE_DESTORY).sendToTarget();
    }


}
