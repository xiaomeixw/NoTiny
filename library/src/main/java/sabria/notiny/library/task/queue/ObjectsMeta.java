package sabria.notiny.library.task.queue;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;

import sabria.notiny.library.NoTinyImpl;
import sabria.notiny.library.anno.Subscribe;

/**
 * Created by xiongwei,An Android project Engineer.
 * Date:2015-12-28  11:59
 * Base on Meilimei.com (PHP Service)
 * Describe:
 * Version:1.0
 * Open source
 */
public class ObjectsMeta {

    public ObjectsMeta(Class<?> objClass) {
    }

    public static class SubscriberCallback {

        public SubscriberCallback(Method method,Subscribe ann) {
            this.method=method;
            this.mode=ann.mode();
            this.queue=ann.queue();
        }

        public final Method method;

        public final int mode;

        public final String queue;



    }




    /**
     * Implementation of this callback handles actual event dispatching.
     */
    public static interface EventDispatchCallback {
        void dispatchEvent(SubscriberCallback subscriberCallback,
                           Object receiver, Object event) throws Exception;
    }

    public void registerAtReceivers(Object obj, HashMap<Class<?>, HashSet<Object>> mEventSubscribers) {
    }

    public void registerAtProducers(Object obj, HashMap<Class<?>, Object> mEventProducers) {
    }

    public void dispatchEvents(Object obj, HashMap<Class<? extends Object>, HashSet<Object>> receivers, HashMap<Class<? extends Object>, ObjectsMeta> metas, NoTinyImpl callback) throws Exception {
    }

    public void dispatchEvents(HashMap<Class<? extends Object>, Object> producers, Object receiver, HashMap<Class<? extends Object>, ObjectsMeta> metas, NoTinyImpl callback) throws Exception {

    }

    public void unregisterFromReceivers(Object obj, HashMap<Class<?>, HashSet<Object>> mEventSubscribers) {
    }

    public void unregisterFromProducers(Object obj, HashMap<Class<?>, Object> mEventProducers) {
    }

    public SubscriberCallback getEventCallback(Class<?> objClass) {
        return null;
    }




    public boolean hasRegisteredObject(Object obj,
                                       HashMap<Class<? extends Object>, HashSet<Object>> receivers,
                                       HashMap<Class<? extends Object>, Object> producers) {
        return false;
    }
}
