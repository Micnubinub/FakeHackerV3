package tbs.fakehackerv3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends Activity {
    public static P2PManager p2PManager;
    private final View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = null;
            switch (v.getId()) {
                case R.id.messaging:
                    i = new Intent(MainActivity.this, Messaging.class);
                    break;
                case R.id.remote:
                    i = new Intent(MainActivity.this, Remote.class);
                    break;
            }

            if (i != null) {
                startActivity(i);
            }
        }
    };

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.remote).setOnClickListener(listener);
        findViewById(R.id.messaging).setOnClickListener(listener);


    }

}
