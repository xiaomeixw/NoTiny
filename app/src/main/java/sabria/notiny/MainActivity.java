package sabria.notiny;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import sabria.notiny.library.NoTiny;
import sabria.notiny.library.anno.Produce;
import sabria.notiny.library.anno.Subscribe;
import sabria.notiny.library.tiny.EventBus;


public class MainActivity extends AppCompatActivity {

    EventBus mGlobalBus;
    //TinyBus mGlobalBus;
    ProgressBar mProgressBar0;
    ProgressBar mProgressBar1;
    ProgressBar mProgressBar2;
    ProgressBar mProgressBar3;
    ProgressBar mProgressBar4;
    private ProgressStatusEvent mProgressStatusEvent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressBar0 = (ProgressBar) findViewById(R.id.progress1);
        mProgressBar1 = (ProgressBar) findViewById(R.id.progress2);
        mProgressBar2 = (ProgressBar) findViewById(R.id.progress3);
        mProgressBar3 = (ProgressBar) findViewById(R.id.progress4);
        mProgressBar4 = (ProgressBar) findViewById(R.id.progress5);

        mGlobalBus = NoTiny.getDefault(this);
        //mGlobalBus = TinyBus.from(this);
        findViewById(R.id.go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i =0 ; i<5;i++){
                    mGlobalBus.post(new ProcessData(20,i));
                }

            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        mGlobalBus.register(this);
    }

    @Override
    public void onStop() {
        mGlobalBus.unregister(this);
        super.onStop();
    }

    //
    public static class ProcessData {
        public final int totalSteps;
        public final int rank;

        public ProcessData(int totalSteps,int rank) {
            this.totalSteps = totalSteps;
            this.rank=rank;
        }
    }

    public static class ProgressStatusEvent {
        public final int step;
        public final int totalSteps;
        public final int rank;

        public ProgressStatusEvent(int step, int totalSteps,int rank) {
            this.step = step;
            this.totalSteps = totalSteps;
            this.rank=rank;
        }
    }

    @Produce
    public ProgressStatusEvent getProgressStatusEvent() {
        return mProgressStatusEvent;
    }

    @Subscribe
    public void onProgressStatusEvent(ProgressStatusEvent event) {
        mProgressStatusEvent = event;
        int rank = event.rank;
        if(rank==0){
            mProgressBar0.setMax(event.totalSteps);
            mProgressBar0.setProgress(event.step);
        }else if(rank==1){
            mProgressBar1.setMax(event.totalSteps);
            mProgressBar1.setProgress(event.step);
        }else if(rank==2){
            mProgressBar2.setMax(event.totalSteps);
            mProgressBar2.setProgress(event.step);
        }else if(rank==3){
            mProgressBar3.setMax(event.totalSteps);
            mProgressBar3.setProgress(event.step);
        }else if(rank==4){
            mProgressBar4.setMax(event.totalSteps);
            mProgressBar4.setProgress(event.step);
        }

    }

    @Subscribe(mode= Subscribe.Mode.Backgroud)
    public void onProcessData(ProcessData data) {
        for(int i=0; i<data.totalSteps; i++) {
            mGlobalBus.post(new ProgressStatusEvent(i + 1, data.totalSteps,data.rank));
            try {
                synchronized (this) {
                    wait(500);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
