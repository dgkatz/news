package com.strattondesign.news;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<ArrayList<News>> {

    private static final String NEWS_URL = "http://content.guardianapis.com/search?q=debate&tag=politics/politics&show-fields=trailText&from-date=2014-01-01&api-key=test";
    private static final int URL_LOADER = 0;
    private static final String LOG_TAG = "MainActivity.LOG_TAG";
    private static final int HANDLER_DELAY = 30000;

    private NewsAdapter mNewsAdatper;
    private ListView mListView;
    private ArrayList<News> mNews;
    private TextView mNodataText;
    private SwipeRefreshLayout mSwipeLayout;
    private Handler mHandler = new Handler();

    /**
     * onCreate method, this will run every time the Activity is created, device rotated, etc.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNews = new ArrayList<>();

        mNewsAdatper = new NewsAdapter(this, mNews);
        mListView = (ListView) findViewById(R.id.list_view);
        mNodataText = (TextView) findViewById(R.id.no_data);
        mNodataText.setVisibility(View.GONE);
        mListView.setAdapter(mNewsAdatper);
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        mSwipeLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");

                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        refresh();
                    }
                }
        );


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = mNews.get(position).getLink();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        // Run the above code block on the main thread after 30 seconds
        mHandler.post(runnableCode);
    }

    // Define the code block to be executed
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            // Do something here on the main thread
            Log.d("Handlers", "Called on main thread");
            refresh();
            mHandler.postDelayed(runnableCode, HANDLER_DELAY);
        }
    };

    /**
     * Refresh news list
     */
    private void refresh() {
        if (isNetworkConnected()) {
            // This is required because of a bug. See: http://stackoverflow.com/questions/26858692/swiperefreshlayout-setrefreshing-not-showing-indicator-initially
            mSwipeLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeLayout.setRefreshing(true);
                }
            });

            mHandler.removeCallbacks(runnableCode);
            // Prepare the loader.  Either re-connect with an existing one,
            // or start a new one.
            getSupportLoaderManager().initLoader(URL_LOADER, null, this).forceLoad();
        } else {
            mNodataText.setText(R.string.device_not_connected);
            mNodataText.setVisibility(View.VISIBLE);
        }
    }

    /**
     * This method checks whether mobile is connected to internet and returns true if connected
     * @return true if connected
     */
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        refresh();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<ArrayList<News>> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case URL_LOADER:
                // Returns a new AsyncTaskLoader
                Log.d(LOG_TAG, "onCreateLoader");
                return new RssListLoader(this);
            default:
                // An invalid id was passed in
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<News>> loader, ArrayList<News> data) {
        mSwipeLayout.setRefreshing(false);
        mNews.clear();
        if (data != null) {
            mNews.addAll(data);
        }
        mNewsAdatper.notifyDataSetChanged();
        if (mNews.size() == 0) {
            mNodataText.setText(R.string.no_data_available);
            mNodataText.setVisibility(View.VISIBLE);
        } else {
            mNodataText.setVisibility(View.GONE);
        }

        mHandler.postDelayed(runnableCode, HANDLER_DELAY);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<News>> loader) {
        Log.d(LOG_TAG, "onLoaderReset");
    }

    /**
     * Async task loader to load RSS data
     */
    public static class RssListLoader extends AsyncTaskLoader<ArrayList<News>> {
        public RssListLoader(Context context) {
            super(context);
        }

        @Override
        public ArrayList<News> loadInBackground() {
            Log.d(LOG_TAG, "loadInBackground");
            return QueryUtils.queryNews(NEWS_URL);
        }
    }
}
