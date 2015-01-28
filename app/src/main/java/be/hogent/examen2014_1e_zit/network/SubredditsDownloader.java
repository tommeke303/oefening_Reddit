package be.hogent.examen2014_1e_zit.network;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import be.hogent.examen2014_1e_zit.interfaces.SubredditsListener;
import be.hogent.examen2014_1e_zit.models.subreddits.Data;
import be.hogent.examen2014_1e_zit.models.subreddits.Listing;
import be.hogent.examen2014_1e_zit.models.subreddits.SubReddit;
import be.hogent.examen2014_1e_zit.models.subreddits.SubRedditData;

/**
 * Created by jbuy519 on 04/12/2014.
 */
public class SubredditsDownloader {
    /**
     * TAG die gebruikt wordt om output te loggen. Gebruik enkel deze tag om te loggen!
     */
    public static final String TAG = SubredditsDownloader.class.getName();

    private SubredditsListener subredditsListener;

    public SubredditsDownloader(SubredditsListener subredditsListener) {
        this.subredditsListener = subredditsListener;
    }

    public void download(URL url){
        new SubredditsTask().execute(url);
    }

    /**
     * De uiteindelijke klasse voor het downloaden van de meest populaire subreddits.
     */
    public class SubredditsTask extends AsyncTask<URL,Integer,List<SubRedditData>> {

        /**
         * TAG die gebruikt wordt om output te loggen. Gebruik enkel deze tag om te loggen!
         */
        public final String TAG = SubredditsTask.class.getName();


        /**
         * TODO: Opdracht 3.1
         */
        @Override
        protected List<SubRedditData> doInBackground(URL... params) {
            Gson gson = new GsonBuilder().create();
            Listing l = gson.fromJson(openConnection(params[0]), Listing.class);

            List<SubReddit> s = l.getData().getChildren();

            List<SubRedditData> lijst = new ArrayList<>();
            ListIterator<SubReddit> it = s.listIterator();

            while (it.hasNext()){
                lijst.add(it.next().getSubRedditData());
            }

            return lijst;


        }

        /**
         * TODO: Opdracht 3.1 : zorg ervoor dat na het downloaden de listener het gevraagde uitvoert
         */
        @Override
        protected void onPostExecute(List<SubRedditData> subRedditDatas) {
            super.onPostExecute(subRedditDatas);
            subredditsListener.downloadCompleted(subRedditDatas);
        }
    }

    public String openConnection(URL url) {

        try {

            //Create new HTTP URL connection
            URLConnection connection = url.openConnection();
            HttpURLConnection httpURLConnection =
                    (HttpURLConnection) connection;
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream in = httpURLConnection.getInputStream();
                return convertStreamToString(in);
            }
        } catch (MalformedURLException e) {
            Log.d(TAG, "Malformed URL exception");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG, "IO Exception");
            e.printStackTrace();

        }
        return null;
    }

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

}
