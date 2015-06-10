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
    private SeekBar alarm, notification, all, ringer, media;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remote);

        //Switch
        flashLight = (Switch) findViewById(R.id.flash);
        wifi = (Switch) findViewById(R.id.wifi);
        bluetooth = (Switch) findViewById(R.id.bluetooth);

        //TextView
        takePictureFront = (TextView) findViewById(R.id.take_pic_front);
        takePictureBack = (TextView) findViewById(R.id.take_pic_back);
        previousTrack = (TextView) findViewById(R.id.previous);
        nextTrack = (TextView) findViewById(R.id.next);
        pausePlay = (TextView) findViewById(R.id.play_pause);

        //SeekBar
        alarm = (SeekBar) findViewById(R.id.volume_alarm);
        notification = (SeekBar) findViewById(R.id.volume_notification);
        all = (SeekBar) findViewById(R.id.volume_all);
        ringer = (SeekBar) findViewById(R.id.volume_ringer);
        media = (SeekBar) findViewById(R.id.volume_media);

    }
}
