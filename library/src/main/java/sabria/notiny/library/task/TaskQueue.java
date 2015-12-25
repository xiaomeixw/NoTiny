package sabria.notiny.library.task;

/**
 * Created by xiongwei,An Android project Engineer.
 * Date:2015-12-25  13:43
 * Base on Meilimei.com (PHP Service)
 * Describe:一个简单的FIFO容器
 * 模仿BlockingQueue 实现FIFO队列
 *      队列是一种数据结构．它有两个基本操作：在队列尾部加人一个元素，
 *      和从队列头部移除一个元素就是说，队列以一种先进先出的方式管理数据，
 *      如果你试图向一个已经满了的阻塞队列中添加一个元素或者是从一个空的阻塞队列中移除一个元索，
 *      将导致线程阻塞．在多线程进行合作时，阻塞队列是很有用的工具。
 *      工作者线程可以定期地把中间结果存到阻塞队列中而其他工作者线线程把中间结果取出并在将来修改它们。
 *      队列会自动平衡负载。如果第一个线程集运行得比第二个慢，则第二个线程集在等待结果时就会阻塞。
 *      如果第一个线程集运行得快，那么它将等待第二个线程集赶上来。
 * Version:1.0
 * Open source
 */
public class TaskQueue implements Queue{

    protected Task head;//头部
    protected Task tail;//尾部

    /**
     * 添加一个Task到队列底部
     *
     * 所谓FIFO的添加就是在队列最尾部tail添加一个新传入的Task
     *
     * 若:Tail为空---》表明Queue中是空无Task的--->插入一个Task表示此时插入到Tail尾部
     * 所以:tail = task; --->因为Queue只有这一个插入的元素,所以此时的head也就是tail也就是这个新插入的task
     * 所以有tail = head = task;
     *
     *
     * 若:Tail不为空--->根据FIFOLILO(新进先出,后进后出)的原则,此时放入1个Task就是放在尾部所以tail = task;
     * 因为有多个task在Queue中,所以tail.pre
     *
     *
     *
     * @param task
     */
    public void offer(Task task){
        if(tail == null){
            tail = head = task;
        } else {
            tail.prev = task;
            tail = task;
        }

    }

    /**
     * 移除一个head-task,并将下一个task置为head,然后返回它
     * @return
     */
    public Task poll(){
        //如果顶部都没有task,那就返回null
        if(head == null){
            return null;
        }else{
            Task task = head;
            //TODO prev是指紧邻的前一个同胞元素？？？歧义
            head = head.prev;
            if(head == null){
                tail = null;
            }
            return task;
        }
    }

    /**
     * task重新返回到头部中
     * no worker threads available,return task back into the queue
     * task执行被拒绝,将task返回到Queue中,此时的返回时返回到头部
     * @param task
     */
    @Override
    public void unpoll(Task task) {
        if(head == null){
            head = tail = task;
        }else{
            task.prev = head;
            head = task;
        }
    }

    /**
     * 队列是否为空
     * @return
     */
    @Override
    public boolean isEmpty() {
        return head == null ;
    }


}
