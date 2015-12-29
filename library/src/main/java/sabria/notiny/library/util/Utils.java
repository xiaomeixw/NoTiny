package sabria.notiny.library.util;

import android.util.Log;

import sabria.notiny.library.thread.dispatcher.DispatcherHandler;

/**
 * Created by xiongwei,An Android project Engineer.
 * Date:2015-12-25  10:24
 * Base on Meilimei.com (PHP Service)
 * Describe:
 * Version:1.0
 * Open source
 */
public class Utils {

    private static final String TAG="Thread-Dispatcher";

    public static void log(String text){
        Log.i(TAG,text);
    }

    public static void assertDispatcherThread(DispatcherHandler mDispatcherHandler) {
        if (Thread.currentThread() != mDispatcherHandler.getLooper().getThread()) {
            throw new IllegalStateException("method accessed from wrong thread");
        }
    }

}
