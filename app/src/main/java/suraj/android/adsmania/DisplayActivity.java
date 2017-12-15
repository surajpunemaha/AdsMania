package suraj.android.adsmania;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.util.ArrayList;

public class DisplayActivity extends Activity implements MediaPlayer.OnCompletionListener
{
    VideoView videov_ads;
    TextView txtv_marquee;
    WebView webv_marquee;
    ImageView imgv_ads;

    ArrayList<String> paths1;
    File adsPathFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download");
    File[] listOfFiles = adsPathFolder.listFiles();

    static int i=0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        setUpUi();


        paths1 = new ArrayList<String>();
        for(int j=0;j<listOfFiles.length;j++)
        {
            if(listOfFiles[j].isFile())
            {
                paths1.add(listOfFiles[j].getAbsolutePath());
            }
        }

        try
        {
            videov_ads.setVideoPath(paths1.get(i));
            videov_ads.setOnCompletionListener(this);
            videov_ads.start();
        }
        catch (Exception e)
        {
            Toast.makeText(DisplayActivity.this, ""+e, Toast.LENGTH_SHORT).show();
        }
    }

    public void setUpUi()
    {
        imgv_ads = (ImageView) findViewById(R.id.imgv_ads);
        videov_ads = (VideoView) findViewById(R.id.videov_ads);
        webv_marquee = (WebView) findViewById(R.id.webv_marquee);
        webv_marquee.loadUrl("file:///android_asset/marquee.html");
    }

    @Override
    public void onCompletion(MediaPlayer mp)
    {
        i++;
        videov_ads.stopPlayback();
        videov_ads.setVideoPath(paths1.get(i));
        videov_ads.start();

        if(i==paths1.size()-1)
        {
            i=0;
        }
    }

    public void showNotification()
    {

    }
}