package sabria.notiny.library.task;

/**
 * Created by xiongwei,An Android project Engineer.
 * Date:2015-12-28  10:45
 * Base on Meilimei.com (PHP Service)
 * Describe:
 * Version:1.0
 * Open source
 */
public class TaskPool {

    private final int mMaxSize;
    private int mSize;
    private Task tail;


    TaskPool(int maxSize){
        this.mMaxSize=maxSize;
    }

    Task acquire(){
        Task acquired  = tail;
        if(acquired == null){
            acquired = new Task();
        }else{
            tail = acquired.prev;
            mSize -- ;
        }

        return acquired;

    }

    void release(Task task){
        if(mSize<mMaxSize){
            task.prev=tail;
            tail = task;
            mSize ++;
        }
    }


}
