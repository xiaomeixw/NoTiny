package sabria.notiny.library.task.queue;

import java.lang.reflect.Method;

/**
 * Created by xiongwei,An Android project Engineer.
 * Date:2015-12-25  20:08
 * Base on Meilimei.com (PHP Service)
 * Describe:
 * Version:1.0
 * Open source
 */
public class ObjectsMeta {

    public static class SubscriberCallback {

        public SubscriberCallback(Method method) {
            this.method = method;
            //TODO
            this.queue=null;
        }

        public final Method method;
        public final String queue;
    }


}
