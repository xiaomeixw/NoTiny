package sabria.notiny.library.task.queue;

import sabria.notiny.library.task.Task;
import sabria.notiny.library.task.TaskQueue;

/**
 * Created by xiongwei,An Android project Engineer.
 * Date:2015-12-25  14:54
 * Base on Meilimei.com (PHP Service)
 * Describe: 带计数功能的TaskQueue
 * Version:1.0
 * Open source
 */
public class SerialTaskQueue extends TaskQueue {

    private final String mQuueName;

    //是否正在运行
    private boolean mProcessing;

    //int默认值是0
    private int mSize;

    public SerialTaskQueue(String queueName){
        mQuueName = queueName;
    }

    public String getQuueName() {
        return mQuueName;
    }

    public void setProcessing(boolean mProcessing) {
        this.mProcessing = mProcessing;
    }

    public boolean isProcessing() {
        return mProcessing;
    }

    @Override
    public void offer(Task task) {
        super.offer(task);
        mSize++;
    }

    @Override
    public Task poll() {
        mSize--;
        return super.poll();
    }

    @Override
    public void unpoll(Task task) {
        super.unpoll(task);
        mSize++;
    }


    public int getSize() {
        return mSize;
    }
}
