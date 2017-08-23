package com.dash.dashapp.Utils;

import android.content.Context;
import android.os.AsyncTask;

import com.dash.dashapp.Interface.RSSUpdateListener;
import com.dash.dashapp.Model.News;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by sebas on 8/5/2017.
 */

public class HandleXML {

    private static final String TAG = "HandleXML";
    private static final int NUMBER_FIRST_BATCH = 15;
    private String title = "title";
    private String link = "link";
    private String description = "description";
    private String urlString = null;
    private XmlPullParserFactory xmlFactoryObject;
    public volatile boolean parsingComplete = true;
    public Context context = null;
    private RSSUpdateListener dbListener;

    public HandleXML(String url, Context context) {
        this.context = context;
        this.urlString = url;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public void parseXMLAndStoreIt(XmlPullParser myParser) {
        int event;
        News news = null;
        String text = null;
        int articleNumber = 0;
        ArrayList<News> newsList = new ArrayList<>();

        try {
            event = myParser.getEventType();

            while (event != XmlPullParser.END_DOCUMENT) {
                String name = myParser.getName();

                switch (event) {
                    case XmlPullParser.START_TAG:
                        if ("item".equals(name)) {
                            news = new News();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = myParser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (news != null) {
                            switch (name) {
                                case "item":
                                    // If that news doesn't exist
                                    if (!selectNews(news.getGuid())) {
                                        // Add it to the database
                                        addNews(news);
                                    }
                                    // Add it to the first batch no matter what
                                    newsList.add(news);
                                    articleNumber++;
                                    if (articleNumber == NUMBER_FIRST_BATCH){
                                        dbListener.onFirstBatchNewsCompleted(newsList);
                                    }
                                    break;
                                case "guid":
                                    news.setGuid(text);
                                    break;
                                case "title":
                                    news.setTitle(text);
                                    break;
                                case "pubDate":
                                    news.setPubDate(text);
                                    break;
                                case "content:encoded":
                                    news.setContent(text);
                                    break;
                            }
                        }
                        break;
                }

                event = myParser.next();
            }
            parsingComplete = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addNews(News news) {
        MyDBHandler dbHandler = new MyDBHandler(context, null);
        dbHandler.addNews(news);

    }

    private boolean selectNews(String newsId) {
        boolean isInDb;

        MyDBHandler dbHandler = new MyDBHandler(context, null);
        isInDb = dbHandler.findNews(newsId) != null;

        return isInDb;
    }

    public void fetchXML(RSSUpdateListener dbListener) {
        this.dbListener = dbListener;
        UpdateDB updateDB = new UpdateDB(dbListener);
        updateDB.execute();
    }

    public class UpdateDB extends AsyncTask<Void, Void, Void> { //change Object to required type
        private RSSUpdateListener dbListener;

        public UpdateDB(RSSUpdateListener dbListener) {
            this.dbListener = dbListener;
        }

        // required methods

        @Override
        protected void onPreExecute() {
            dbListener.onUpdateStarted();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);

                // Starts the query
                conn.connect();
                InputStream stream = conn.getInputStream();

                xmlFactoryObject = XmlPullParserFactory.newInstance();
                XmlPullParser myparser = xmlFactoryObject.newPullParser();

                myparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                myparser.setInput(stream, null);

                parseXMLAndStoreIt(myparser);
                stream.close();
            } catch (Exception e) {
                e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            dbListener.onDatabaseUpdateCompleted();
            super.onPostExecute(aVoid);
        }
    }


}
