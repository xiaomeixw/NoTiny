package sabria.notiny.library;

import android.content.Context;

import sabria.notiny.library.task.TaskQueue;

/**
 * Created by xiongwei,An Android project Engineer.
 * Date:2015-12-28  11:06
 * Base on Meilimei.com (PHP Service)
 * Describe:
 * Version:1.0
 * Open source
 */
public class Go {

    final TaskQueue mTaskQueue;


    public Go(Context context) {
        mTaskQueue = new TaskQueue();
    }


    public static void run() {


    }



}
