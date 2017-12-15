package suraj.android.adsmania;

import android.os.Environment;

public class Utilities
{

    /*------------------------------LOCATION_INFO---------------------------------------------*/
    //public static final String LOCATION_ID = "1"; // HADAPSAR
    public static final String LOCATION_ID = "2"; // AUNDH

     /*------------------------------ RESTART INFO ---------------------------------------------*/

    public static final int RESTART_HOURS = 3;
    public static final int RESTART_MINUTES = 00;

    /*------------------------------STORAGE INFO---------------------------------------------*/

    public static final String SD_CARD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String ADS_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Download";
    public static final String ADS_DIR_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/AdsMania";


    /*------------------------------URL's---------------------------------------------*/

    //public static final String URL_MARQUEE = "http://192.168.70.2/AdsMania/marquee.php";
    //public static final String URL_LOC_WISE_URLS = "http://192.168.70.2/AdsMania/get_urls_for_loc.php";

    //public static final String URL_MARQUEE = "http://192.168.43.240/AdsMania/marquee.php";
    //public static final String URL_LOC_WISE_URLS = "http://192.168.43.240/AdsMania/get_urls_for_loc.php";

    public static final String URL_MARQUEE = "http://surajbang.esy.es/AdsMania/marquee.php";
    public static final String URL_LOC_WISE_URLS = "http://surajbang.esy.es/AdsMania/get_urls_for_loc.php";


    /*------------------------------HTML INFO---------------------------------------------*/
    public static final String START_TAG = " <html> <body bgcolor='#4662de'> <marquee scrolldelay='200' " +
                        "style='font-family:Book Antiqua; color:#FFFFFF'> <font size='4'>";

    public static final String END_TAG = "</font> </marquee> </body> </html>";
}
