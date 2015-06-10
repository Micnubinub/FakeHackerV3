package tbs.fakehackerv3;

import android.app.Activity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Created by Michael on 6/10/2015.
 */
public class Remote extends Activity {
    private static Switch flashLight, wifi, bluetooth;
    private TextView takePictureFront, takePictureBack, previousTrack, nextTrack, pausePlay;
    private SeekBar alarm, notificatiol, all, ringer, media;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remote);

        //Switch
        flashLight, wifi, bluetooth;
        //TextView
        takePictureFront, takePictureBack, previousTrack, nextTrack, pausePlay;
        //SeekBar
        alarm, notificatiol, all, ringer, media;

    }
}
