package sabria.notiny.library;

import android.content.Context;

import sabria.notiny.library.tiny.EventBus;

/**
 * Created by xiongwei,An Android project Engineer.
 * Date:2015-12-28  15:05
 * Base on Meilimei.com (PHP Service)
 * Describe:
 * Version:1.0
 * Open source
 */
public class NoTiny implements EventBus {


    static volatile NoTiny defaultInstance;


    public static EventBus getDefault(Context context) {
        if (defaultInstance == null) {
            synchronized (EventBus.class) {
                if (defaultInstance == null) {
                    defaultInstance = getNoTiny(context);
                }
            }
        }
        return defaultInstance;
    }

    public static NoTiny getNoTiny(Context context) {

        //NoTinyLifeCycle noTinyLifeCycle = NoTinyLifeCycle.get();
        //noTinyLifeCycle.getBusInContext(context);
        defaultInstance = new NoTiny();
        return defaultInstance;
    }


    @Override
    public void register(Object object) {

    }

    @Override
    public void unregister(Object object) {

    }

    @Override
    public void post(Object event) {

    }

    @Override
    public boolean hasRegistered(Object object) {
        return false;
    }

    @Override
    public void postDelayed(Object event, long delayMillis) {

    }

    @Override
    public void cancelDelayed(Class<?> eventClass) {

    }
}
