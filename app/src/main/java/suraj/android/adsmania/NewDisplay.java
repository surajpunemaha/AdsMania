package suraj.android.adsmania;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

public class NewDisplay extends Activity implements MediaPlayer.OnCompletionListener
{
    VideoView videov_ads;
    TextView txtv_time;
    WebView webv_marquee;
    Button btn_location;

    ArrayList<String> filePaths;
    File adsDirPath;
    File[] listOfFiles;
    int index=0;

    String loc_name="";

    BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_display);

        Intent i =getIntent();
        loc_name = i.getExtras().getString("LOC_NAME");


        enableBroadcastReceiver();
        getAdFilesPaths();
        setUpUi();
        displayAds();
    }

    public void getAdFilesPaths()
    {
        adsDirPath = new File(Utilities.ADS_DIR_PATH);
        listOfFiles = adsDirPath.listFiles();
        filePaths = new ArrayList<String>();

        for (int j=0; j<listOfFiles.length; j++)
        {
            if(listOfFiles[j].isFile())
            {
                filePaths.add(listOfFiles[j].getAbsolutePath());
            }
        }
    }

    public void setUpUi()
    {
        txtv_time = (TextView) findViewById(R.id.txtv_time);

        Date currentTime = Calendar.getInstance().getTime();
        txtv_time.setText(currentTime.getHours()+":"+currentTime.getMinutes());

        Typeface seven_seg = Typeface.createFromAsset(getAssets(), "fonts/seven_seg.ttf");
        txtv_time.setTypeface(seven_seg);

        btn_location = (Button) findViewById(R.id.btn_location);
        btn_location.setText(loc_name);
        videov_ads = (VideoView) findViewById(R.id.videov_ads);
        videov_ads.setOnCompletionListener(this);
        webv_marquee = (WebView) findViewById(R.id.webv_marquee);
        //webv_marquee.loadUrl("file:///android_asset/marquee.html");
        webv_marquee.loadUrl("file://"+Utilities.SD_CARD_PATH+"/marquee.html");
    }

    public void displayAds()
    {
        try {
            videov_ads.setVideoPath(filePaths.get(index));
            videov_ads.start();
        }catch (Exception e)
        {
            Toast.makeText(NewDisplay.this, "Unable to play video\n"+e, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onCompletion(MediaPlayer mp)
    {
        videov_ads.stopPlayback();

        if(index==filePaths.size()-1)
        {
            index=0;
        }
        else
        index++;

        videov_ads.setVideoPath(filePaths.get(index));
        videov_ads.start();
    }

    public void enableBroadcastReceiver()
    {
        receiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                Date currentTime = Calendar.getInstance().getTime();
                String time = currentTime.getHours()+":"+currentTime.getMinutes();
                int hrs = currentTime.getHours();
                int min = currentTime.getMinutes();

                txtv_time.setText(time);

                if(hrs==Utilities.RESTART_HOURS && min==Utilities.RESTART_MINUTES)
                {
                    Toast.makeText(NewDisplay.this, "Restarting...", Toast.LENGTH_SHORT).show();
                    try
                    {
                        //Runtime.getRuntime().exec(new String[]{"/system/bin/su","-c","reboot now"});
                        Intent sd = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
                        sd.putExtra("android.intent.extra.KEY_CONFIRM", false);
                        sd.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(sd);
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(NewDisplay.this, "Exception "+e, Toast.LENGTH_SHORT).show();
                        Log.e("ERORRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR", ""+e);
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_TIME_TICK);

        registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        unregisterReceiver(receiver);
    }
}

