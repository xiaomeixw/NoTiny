package sabria.notiny.library.task.queue;

import android.util.Log;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import sabria.notiny.library.constant.Constants;
import sabria.notiny.library.tiny.NoTinyImpl;
import sabria.notiny.library.anno.Produce;
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

    private final HashMap<Class<? extends Object>/*event class*/, SubscriberCallback> mEventCallbacks = new HashMap<Class<? extends Object>, SubscriberCallback>();
    private HashMap<Class<? extends Object>/*event class*/, Method> mProducerCallbacks;
    /**
     * 对method上的注解进行解析
     * @param obj
     */
    public ObjectsMeta(Object  obj) {


        Log.i(Constants.TAG, obj.getClass().getName());

        Method[] methods = obj.getClass().getMethods();
        Subscribe ann;
        Class<?>[] params;
        SubscriberCallback callback;

        for(Method method : methods){
            Log.i(Constants.TAG,method.getName());
            //方法是否是桥接方法或者是合成方法
            if(method.isBridge()||method.isSynthetic()){
                continue;
            }
            //返回此元素上存在的所有注释,包括继承的
            ann = method.getAnnotation(Subscribe.class);

            if(ann !=null){
                params  = method.getParameterTypes();
                callback = mEventCallbacks.put(params[0], new SubscriberCallback(method, ann));
                if(callback!=null){
                    throw new IllegalArgumentException("Only one @Subscriber can be defined "
                            + "for one event type in the same class. Event type: "
                            + params[0] + ". Class: " + obj.getClass());
                }
            }else if(method.isAnnotationPresent(Produce.class)){
                    if(mProducerCallbacks==null){
                        mProducerCallbacks = new HashMap<>();
                    }
                    mProducerCallbacks.put(method.getReturnType(),method);
            }
        }

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



    public void registerAtReceivers(Object obj, HashMap<Class<?>, HashSet<Object>> receivers) {
        Iterator<Class<? extends Object>> keys = mEventCallbacks.keySet().iterator();

        Class<? extends Object> key;
        HashSet<Object> eventReceivers;

        while (keys.hasNext()) {
            key = keys.next();
            eventReceivers = receivers.get(key);
            if (eventReceivers == null) {
                eventReceivers = new HashSet<Object>();
                receivers.put(key, eventReceivers);
            }
            if (!eventReceivers.add(obj)) {
                throw new IllegalArgumentException(
                        "Unable to registered receiver because it has already been registered: " + obj);
            }
        }
    }

    public void registerAtProducers(Object obj, HashMap<Class<?>, Object> producers) {
        if (mProducerCallbacks == null) {
            return; // this object has no @Produce methods
        }

        Class<? extends Object> key;
        final Iterator<Class<? extends Object>> keys = mProducerCallbacks.keySet().iterator();
        while (keys.hasNext()) {
            key = keys.next();
            if (producers.put(key, obj) != null) {
                throw new IllegalArgumentException(
                        "Unable to register producer, because another producer is already registered, " + obj);
            }
        }
    }

    public void dispatchEvents(Object obj, HashMap<Class<? extends Object>, HashSet<Object>> receivers, HashMap<Class<? extends Object>, ObjectsMeta> metas, NoTinyImpl callback) throws Exception {
        if(mProducerCallbacks==null){
            //no producers for this event type
            return;
        }

        //迭代取出HashMap
        Iterator<Map.Entry<Class<?>, Method>> iterator = mProducerCallbacks.entrySet().iterator();
        ObjectsMeta meta;
        Class<?> eventClass;
        SubscriberCallback subscriberCallback;

        while (iterator.hasNext()){
            Map.Entry<Class<?>, Method> next = iterator.next();
            eventClass = next.getKey();

            HashSet<Object> targetReceivers  = receivers.get(eventClass);
            if(targetReceivers!=null && targetReceivers.size() >0 ){
                Object event = mProducerCallbacks.get(eventClass).invoke(obj);
                if(event!=null){
                    for(Object receiver : targetReceivers){
                        meta = metas.get(receiver.getClass());
                        subscriberCallback = meta.mEventCallbacks.get(eventClass);
                        if(subscriberCallback !=null){
                            callback.dispatchEvent(subscriberCallback,receiver,event);
                        }
                    }

                }



            }


        }


    }

    public void dispatchEvents(HashMap<Class<? extends Object>, Object> producers, Object receiver, HashMap<Class<? extends Object>, ObjectsMeta> metas, NoTinyImpl callback) throws Exception {
        Iterator<Class<? extends Object>>
                eventClasses = mEventCallbacks.keySet().iterator();
        Object event;
        ObjectsMeta meta;
        Object producer;
        Class<? extends Object> eventClass;
        SubscriberCallback subscriberCallback;

        while (eventClasses.hasNext()) {
            eventClass = eventClasses.next();
            producer = producers.get(eventClass);
            if (producer != null) {
                meta = metas.get(producer.getClass());
                event = meta.mProducerCallbacks.get(eventClass).invoke(producer);
                if (event != null) {
                    subscriberCallback = mEventCallbacks.get(eventClass);
                    if (subscriberCallback != null) {
                        callback.dispatchEvent(subscriberCallback, receiver, event);
                    }
                }
            }
        }
    }

    public void unregisterFromReceivers(Object obj, HashMap<Class<?>, HashSet<Object>> receivers) {
        Iterator<Class<? extends Object>> keys = mEventCallbacks.keySet().iterator();

        Class<? extends Object> key;
        HashSet<Object> eventReceivers;
        boolean fail;
        while (keys.hasNext()) {
            key = keys.next();
            eventReceivers = receivers.get(key);
            if (eventReceivers == null) {
                fail = true;
            } else {
                fail = !eventReceivers.remove(obj);
            }
            if (fail) {
                throw new IllegalArgumentException(
                        "Unregistering receiver which was not registered before: " + obj);
            }
        }
    }

    public void unregisterFromProducers(Object obj, HashMap<Class<?>, Object> mEventProducers) {
        if (mProducerCallbacks == null) {
            return; // no need to unregister, as there is no @Produce methods
        }
        Iterator<Class<?>> iterator = mProducerCallbacks.keySet().iterator();
        while (iterator.hasNext()){
            Class<?> key = iterator.next();
            if(mEventProducers.remove(key)==null){
                throw new IllegalArgumentException(
                        "Unable to unregister producer, because it wasn't registered before, " + obj);
            }
        }
    }

    public SubscriberCallback getEventCallback(Class<?> eventClass) {
        return mEventCallbacks.get(eventClass);
    }




    public boolean hasRegisteredObject(Object obj,
                                       HashMap<Class<? extends Object>, HashSet<Object>> receivers,
                                       HashMap<Class<? extends Object>, Object> producers) {

        boolean registered = false;

        Iterator<Class<?>> iterator = mEventCallbacks.keySet().iterator();
        while (iterator.hasNext()){
            HashSet<Object> eventReceivers  = receivers.get(iterator.next());
            if(eventReceivers!=null){
                registered  = eventReceivers.contains(obj);
                if(registered){
                    break;
                }
            }
        }
        
        if(!registered){
            if(mProducerCallbacks!=null){
                Iterator<Class<?>> iterator2 = mProducerCallbacks.keySet().iterator();
                while (iterator2.hasNext()){
                    registered  = producers.containsKey(iterator2.next());
                    if(registered){
                        break;
                    }
                }

            }            
        }

        return registered;
    }
}
