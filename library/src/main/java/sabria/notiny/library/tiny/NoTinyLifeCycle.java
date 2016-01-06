package sabria.notiny.library.tiny;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;

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
        if(INSTANCE==null){
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

    private static final String TAG ="NoTinyLifeCycle";
    private static final String KEY_BUS_ID = "sabria.notiny.library.tiny.id";
    private int mNextTransientBusId;
    private final SparseArray<NoTiny> mTransientBuses = new SparseArray<NoTiny>(3);
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if(savedInstanceState!=null){
            final int busId = savedInstanceState.getInt(KEY_BUS_ID, -1);
            if(busId>-1){
                NoTiny bus = mTransientBuses.get(busId);
                if(bus!=null){
                    mTransientBuses.delete(busId);
                    bus.getLifecycleCallbacks().attachContext(activity, mBuses.get(activity));
                    mBuses.put(activity, bus);

                    Log.d("NoTinyLifeCycle", " ### onCreated(), bus restored for " + activity +
                            ", busId: " + busId +
                            ", bus: " + bus +
                            ", active buses: " + mBuses.size() +
                            ", transient buses: " + mTransientBuses.size());


                }
            }

        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        NoTiny bus = mBuses.get(activity);
        if(bus!=null){
            bus.getLifecycleCallbacks().onStart();
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {}

    @Override
    public void onActivityPaused(Activity activity) {}

    @Override
    public void onActivityStopped(Activity activity) {
        onContextStopped(activity);
    }



    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        if(activity.isChangingConfigurations()){
            NoTiny bus = mBuses.get(activity);
            if(bus!=null){
                // Store this bus into transient list to
                // restore it later, when activity is recreated
                final int busId = mNextTransientBusId++;
                mTransientBuses.put(busId,bus);
                outState.putInt(KEY_BUS_ID,busId);
                Log.d(TAG, " ### storing transient bus, id: " + busId + ", bus: " + bus);
            }
        }
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        onContextDestroyed(activity);
    }



    //
    private void onContextStopped(Context context) {
        NoTiny bus = mBuses.get(context);
        if(bus!=null){
            bus.getLifecycleCallbacks().onStop();
        }
        Log.d(TAG, " ### STOPPED, bus count: " + mBuses.size());
    }


    private void onContextDestroyed(Context context) {
        NoTiny bus = mBuses.remove(context);
        if(bus!=null){
            boolean keepBusInstance =context instanceof Activity && ((Activity)context).isChangingConfigurations();
            if(!keepBusInstance){
                bus.getLifecycleCallbacks().onDestroy();
                Log.d(TAG, " ### destroying bus: " + bus);
            }
            Log.d(TAG, " ### onDestroy() " + context +
                    ", active buses: " + mBuses.size() +
                    ", transient buses: " + mTransientBuses.size());
        }
    }
}
