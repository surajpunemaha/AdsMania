package suraj.android.adsmania;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Tata Strive on 10/24/2017.
 */
public class BootUpReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@ Boot completed @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

        Intent splash = new Intent(context, SplashActivity.class);
        splash.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(splash);
    }
}
