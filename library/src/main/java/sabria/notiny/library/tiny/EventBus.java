package sabria.notiny.library.tiny;

/**
 * Created by xiongwei,An Android project Engineer.
 * Date:2015-12-28  15:06
 * Base on Meilimei.com (PHP Service)
 * Describe:
 * Version:1.0
 * Open source
 */
public interface EventBus {

    void register(Object object);

    void unregister(Object object);

    void post(Object event);

    boolean hasRegistered(Object object);

    void postDelayed(Object event, long delayMillis);

    void cancelDelayed(Class<?> eventClass);

}
