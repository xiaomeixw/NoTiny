package sabria.notiny.library.task;

/**
 * Created by xiongwei,An Android project Engineer.
 * Date:2015-12-25  13:51
 * Base on Meilimei.com (PHP Service)
 * Describe:
 * Version:1.0
 * Open source
 */
public interface Queue {

     void offer(Task task);

     Task poll();

     void unpoll(Task task);

     boolean isEmpty();

}
