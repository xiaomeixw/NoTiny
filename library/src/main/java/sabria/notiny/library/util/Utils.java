package sabria.notiny.library.util;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.lang.ref.WeakReference;

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


    public static void assertObjectAndWorkerThread(Object obj,Thread mMainThread) {
        if (obj == null) {
            throw new NullPointerException("Object must not be null");
        }
        if (mMainThread != Thread.currentThread()) {
            throw new IllegalStateException("You must call this method from the same thread, "
                    + "in which NoTiny was created. Created: " + mMainThread
                    + ", current thread: " + Thread.currentThread());
        }
    }

    public static Handler getMainHandlerNotNull(Handler mMainHandler) {
        if (mMainHandler == null) {
            throw new IllegalStateException("You can only call post() from a background "
                    + "thread, if the thread, in which NoTiny was created, had a Looper. "
                    + "Solution: create NoTiny in MainThread or in another thread with Looper.");
        }
        return mMainHandler;
    }

    public static Context getNotNullContext(WeakReference<Context> mContextRef) {
        Context context = mContextRef == null ? null : mContextRef.get();
        if (context == null) {
            throw new IllegalStateException(
                    "You must create bus with NoTiny.getDefault(Context) method to use this function.");
        }
        return context;
    }
}
