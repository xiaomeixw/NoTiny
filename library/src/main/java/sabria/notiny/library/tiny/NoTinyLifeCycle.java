package sabria.notiny.library.tiny;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

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
