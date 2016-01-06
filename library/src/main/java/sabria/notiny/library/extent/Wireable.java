package sabria.notiny.library.extent;

import android.content.Context;

import sabria.notiny.library.tiny.EventBus;

/**
 * Created by xiongwei,An Android project Engineer.
 * Date:2016-01-05  14:36
 * Base on Meilimei.com (PHP Service)
 * Describe:
 * Version:1.0
 * Open source
 */
public abstract class Wireable {

    protected EventBus bus;
    protected Context context;


    public void onCreate(EventBus bus,Context context) {
        this.bus=bus;
        this.context=context;
    }

    public void onDestroy(){
        this.bus=null;
        this.context=null;
    }

    public void onStart(){

    }

    public void onStop(){

    }

    public void assertSuperOnCreateCalled() {
        if (bus == null) {
            throw new IllegalStateException(
                    "You must call super.onCreate(bus, context) method when overriding it.");
        }
    }


}
