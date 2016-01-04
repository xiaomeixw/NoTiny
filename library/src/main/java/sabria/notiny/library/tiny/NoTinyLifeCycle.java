package sabria.notiny.library.tiny;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import java.util.WeakHashMap;

import sabria.notiny.library.NoTiny;
import sabria.notiny.library.thread.Dispatcher;

/**
 * Created by xiongwei,An Android project Engineer.
 * Date:2015-12-28  15:27
 * Base on Meilimei.com (PHP Service)
 * Describe:
 * Version:1.0
 * Open source
 */
public class NoTinyLifeCycle implements Application.ActivityLifecycleCallbacks {


    private static  NoTinyLifeCycle INSTANCE;

    public static interface LifecycleCallbacks {
        void attachContext(Context context,NoTiny noTiny);
        void onStart();
        void onStop();
        void onDestroy();
    }


    public static NoTinyLifeCycle get(Context context) {
        if(INSTANCE!=null){
            INSTANCE=new NoTinyLifeCycle(context);
        }
        return INSTANCE;
    }

    public NoTinyLifeCycle(Context context){
        final Application app = (Application) context.getApplicationContext();
        app.registerActivityLifecycleCallbacks(this);
    }

    private final WeakHashMap<Context, NoTiny> mBuses = new WeakHashMap<Context, NoTiny>();

    /**
     * 创建一个Tiny
     * @param context
     * @return
     */
    public NoTiny createNoTiny(Context context){
        NoTiny noTiny = new NoTiny(context);
        mBuses.put(context,noTiny);
        return noTiny;
    }


    //绑定dispatcher
    private Dispatcher mDispatcher;

    public Dispatcher getDispatcher() {
        if (mDispatcher == null) {
            mDispatcher = new Dispatcher();
        }
        return mDispatcher;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }


}
