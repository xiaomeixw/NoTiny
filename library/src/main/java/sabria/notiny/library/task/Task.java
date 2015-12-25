package sabria.notiny.library.task;

import sabria.notiny.library.task.queue.ObjectsMeta;

/**
 * Created by xiongwei,An Android project Engineer.
 * Date:2015-12-25  10:22
 * Base on Meilimei.com (PHP Service)
 * Describe:
 * Version:1.0
 * Open source
 */
public class Task {

    public ObjectsMeta.SubscriberCallback subscriberCallback;

    public Task prev;

    //三个状态处理回调
    public static interface TaskCallbacks {
        void onPostFromBackground(Task task);
        void onPostDelayed(Task task);
        void onDispatchInBackground(Task task) throws Exception;
    }


    public TaskCallbacks  callback;
}
