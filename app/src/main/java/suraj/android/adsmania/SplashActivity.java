package suraj.android.adsmania;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class SplashActivity extends Activity
{
    RelativeLayout layout_splash;
    ProgressBar progressBar;
    ArrayList<String> al_mTexts;
    ArrayList<String> al_adUrls;
    TextView txtv_ads, txtv_mania, txtv_desc;

    String loc_name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        File adsDirectory = new File(Utilities.ADS_DIR_PATH);
        if (!adsDirectory.exists()) {
            Toast.makeText(SplashActivity.this, "Creating directory", Toast.LENGTH_SHORT).show();
            adsDirectory.mkdir();
        }

        setUpUi();
        deletePreviousFiles();
        fetchMarquee();

    }

    public void setUpUi()
    {
        txtv_desc = (TextView) findViewById(R.id.txtv_desc);

        txtv_ads = (TextView) findViewById(R.id.txtv_ads);
        txtv_mania = (TextView) findViewById(R.id.txtv_mania);

        Typeface cooljazz = Typeface.createFromAsset(getAssets(), "fonts/cooljazz.ttf");
        txtv_ads.setTypeface(cooljazz);
        txtv_mania.setTypeface(cooljazz);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        al_mTexts = new ArrayList<String>();
        al_adUrls = new ArrayList<String>();
    }


    class DownloadTask extends AsyncTask<ArrayList<String>, String, Void>
    {
        String file_name = "";
        String total_urls = "";
        String current_no ="";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(ArrayList<String>... passed_url)
        {
            ArrayList<String> al_url = new ArrayList<String>();
            al_url = passed_url[0];
            total_urls = ""+al_url.size();
            for (int s = 0; s < al_url.size(); s++) {
                int count = 0;
                try {
                    //URL url = new URL(f_url[0]);
                    URL url = new URL(al_url.get(s));
                    URLConnection connection = url.openConnection();
                    connection.connect();

                    StringTokenizer tokenizer = new StringTokenizer("" + al_url.get(s), "/");
                    file_name = "";
                    current_no = ""+(s+1);
                    while (tokenizer.hasMoreTokens()) {
                        file_name = tokenizer.nextToken();
                    }

                    System.out.println("************************************************************************************" + al_url.get(s));
                    System.out.println("************************************************************************************" + file_name);

                    int lenght_of_file = connection.getContentLength();

                    File outputFile = new File(Utilities.ADS_DIR_PATH, file_name);

                    InputStream input = new BufferedInputStream(url.openStream(), 1024);
                    OutputStream output = new FileOutputStream(outputFile);

                    byte data[] = new byte[1024];
                    long total = 0;

                    while ((count = input.read(data)) != -1) {
                        total += count;
                        publishProgress("" + (int) ((total * 100) / lenght_of_file));

                        output.write(data, 0, count);

                    }

                    output.flush();
                    output.close();
                    input.close();
                } catch (Exception e) {
                    //Toast.makeText(DownloadTask.this, ""+e, Toast.LENGTH_SHORT).show();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            Intent display_intent = new Intent(SplashActivity.this, NewDisplay.class);
            display_intent.putExtra("LOC_NAME", loc_name);
            startActivity(display_intent);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            //super.onProgressUpdate(values);
            progressBar.setProgress(Integer.parseInt(values[0]));
            txtv_desc.setText("Downloading"+" ("+current_no+"/"+total_urls+")... "+ file_name + "     " + values[0] + "%");
        }
    }


    public void fetchAdsURL(String loc_id) {

        final ArrayList<String> al_videoURLs = new ArrayList<String>();
        al_videoURLs.clear();

        final ProgressDialog pd = new ProgressDialog(SplashActivity.this);
        pd.setMessage("Fetching Video URLs...");
        pd.show();

        String base = Utilities.URL_LOC_WISE_URLS;

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("loc_id", Utilities.LOCATION_ID);

        client.post(base, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                pd.dismiss();
                try {
                    JSONObject root = new JSONObject(response);
                    if (root.getString("message").equals("failed")) {
                        Toast.makeText(SplashActivity.this, "There is query problem on server", Toast.LENGTH_SHORT).show();
                    } else if (root.getString("message").equals("not_found")) {
                        Toast.makeText(SplashActivity.this, "No records found", Toast.LENGTH_SHORT).show();
                    } else if (root.getString("message").equals("success")) {
                        JSONArray arr = root.getJSONArray("results");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            loc_name = obj.getString("loc_name");
                            al_videoURLs.add(obj.getString("ad_url"));

                        }

                        DownloadTask task = new DownloadTask();
                        task.execute(al_videoURLs);
                    } else {
                        Toast.makeText(SplashActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(SplashActivity.this, "Error while parsing url data.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                pd.dismiss();
                Toast.makeText(SplashActivity.this, "Failure occured\nPlease Check your internet connection", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void fetchMarquee() {
        al_mTexts.clear();

        final ProgressDialog pd = new ProgressDialog(SplashActivity.this);
        pd.setMessage("Fetching marquee's...");
        pd.show();

        String base = Utilities.URL_MARQUEE;

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        client.post(base, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                pd.dismiss();
                try {
                    JSONObject root = new JSONObject(response);
                    if (root.getString("message").equals("failed")) {
                        Toast.makeText(SplashActivity.this, "There is query problem on server", Toast.LENGTH_SHORT).show();
                    } else if (root.getString("message").equals("not_found")) {
                        Toast.makeText(SplashActivity.this, "No records found", Toast.LENGTH_SHORT).show();
                    } else if (root.getString("message").equals("success")) {
                        JSONArray arr = root.getJSONArray("results");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            al_mTexts.add(obj.getString("m_text"));
                        }

                        createFileForMarquee();
                    } else {
                        Toast.makeText(SplashActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                pd.dismiss();
                Toast.makeText(SplashActivity.this, "Failure occured", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void createFileForMarquee() {
        String marquee_line = " ** ";
        for (int line = 0; line < al_mTexts.size(); line++) {
            marquee_line = marquee_line + al_mTexts.get(line);
            marquee_line = marquee_line + " ** ";
        }

        try {
            File marqueeFile = new File(Utilities.SD_CARD_PATH, "marquee.html");
            FileWriter writer = new FileWriter(marqueeFile);

            String content = Utilities.START_TAG;
            content = content + "\n";
            content = content + marquee_line;
            content = content + "\n";
            content = content + Utilities.END_TAG;

            writer.append(content);
            writer.flush();
            writer.close();

            fetchAdsURL(Utilities.LOCATION_ID);

        } catch (Exception e) {
            Toast.makeText(SplashActivity.this, "" + e, Toast.LENGTH_SHORT).show();
        }
    }

    public void deletePreviousFiles()
    {
        File adsDirPath = new File(Utilities.ADS_DIR_PATH);
        File[] listOfFiles = adsDirPath.listFiles();
        ArrayList<String> filePaths = new ArrayList<String>();

        if (listOfFiles.length > 0)
        {
            txtv_desc.setText("Deleting Previous Files...");
            for (int j = 0; j < listOfFiles.length; j++)
            {
                if (listOfFiles[j].isFile())
                {
                    filePaths.add(listOfFiles[j].getAbsolutePath());
                }
            }

            for (int j = 0; j < filePaths.size(); j++)
            {
                txtv_desc.setText("Deleting... "+listOfFiles[j].getName());
                File to_be_delete = new File(filePaths.get(j));
                to_be_delete.delete();
            }
        }
    }
}
