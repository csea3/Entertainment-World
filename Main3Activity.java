package com.example.lenovo.moviereviewapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.lenovo.moviereviewapp.ModelClass.MovieData;
import com.example.lenovo.moviereviewapp.ModelClass.Result;
import com.example.lenovo.moviereviewapp.RoomDatabase.FavouriteMovies;
import com.example.lenovo.moviereviewapp.TvModelClass.Tvmodelcalss;
import com.example.lenovo.moviereviewapp.ViewModel.FavMoviesViewModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class Main3Activity extends AppCompatActivity {
    private static final String POS = "POSITION";
    private static final String RESULT_KEY = "KEY";
    public static final String TAG = "error message";
    public static final String FKEY="FAV_KEY";
    List<com.example.lenovo.moviereviewapp.TvModelClass.Result> moviedata;
    GridLayoutManager gridLayoutManager;
    String sort;
    FavMoviesViewModel viewModel;
    List<FavouriteMovies> favMovies;
    ProgressBar progressBar;
    RecyclerView rv;
    int position;
    Toolbar toolbar;
    MaterialSearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rv = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressbar);
        moviedata = new ArrayList<>();
        favMovies = new ArrayList<>();
        sort = "popular";
        viewModel = ViewModelProviders.of(this).get(FavMoviesViewModel.class);
        if (savedInstanceState != null && savedInstanceState.containsKey(RESULT_KEY)) {
            position = savedInstanceState.getInt(POS);
            moviedata = (List<com.example.lenovo.moviereviewapp.TvModelClass.Result>) savedInstanceState.getSerializable(RESULT_KEY);
            rv.setAdapter(new TvImageAdapter(Main3Activity.this, moviedata));
            rv.setLayoutManager(new GridLayoutManager(Main3Activity.this, 2));
            rv.scrollToPosition(position);
        } else {
            if (checkInternetConnection()) {
                getTvData(sort);
            } else {
                Toast.makeText(getApplicationContext(), "Check internet connection", Toast.LENGTH_LONG).show();
            }

        }
    }
        public void getTvData(String sort) {
            if (moviedata != null) {
                moviedata.clear();
            }
            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(this);
            String api = BuildConfig.api_key;
            String url = "https://api.themoviedb.org/3/tv/" + sort + "?api_key=" + api;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Gson gson = new GsonBuilder().create();
                            Tvmodelcalss movies = gson.fromJson(response, Tvmodelcalss.class);
                            moviedata.addAll(movies.getResults());
                            progressBar.setVisibility(View.GONE);
                            rv.setAdapter(new TvImageAdapter(Main3Activity.this, moviedata));
                            gridLayoutManager = new GridLayoutManager(Main3Activity.this,2);
                            rv.setLayoutManager(gridLayoutManager);
                            gridLayoutManager.scrollToPosition(position);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "DATA NOT FOUND");
                }
            });
            queue.add(stringRequest);
        }

    protected void onSaveInstanceState(Bundle outState) {



        if (gridLayoutManager != null)
        {
            int pos = gridLayoutManager.findFirstCompletelyVisibleItemPosition();
            outState.putInt(POS, pos);
            outState.putSerializable(RESULT_KEY, (Serializable) favMovies);
        }
        outState.putSerializable(RESULT_KEY, (Serializable) moviedata);
        super.onSaveInstanceState(outState);


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort_movies, menu);
        MenuItem oursearchitem=menu.findItem(R.id.search);
        final SearchView sv= (SearchView) oursearchitem.getActionView();
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String url="https://api.themoviedb.org/3/search/tv?api_key=45fe43f5b89f01e5594985bcd8c55f30&language=en-US&page=1&include_adult=false&query="+newText;
                new Main3Activity.SearchAsynctask(this).execute(url);
                RecyclerView.Adapter adapter=new TvImageAdapter(Main3Activity.this,moviedata);
                new GridLayoutManager(Main3Activity.this,2);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.popular) {
            sort = "popular";
            getTvData(sort);
        }
        if(i==R.id.upcoming){
            sort="upcoming";
            getTvData(sort);
        }
        if (i == R.id.topRated) {
            sort = "top_rated";
            getTvData(sort);
        }
        if (i == R.id.favourite) {
            loadFavouriteMovies();
        }
        return true;
    }



    private boolean checkInternetConnection() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

    public void loadFavouriteMovies() {
        viewModel.getData().observe(this,new Observer<List<FavouriteMovies>>(){
            @Override
            public void onChanged(@Nullable List<FavouriteMovies> favouriteMovies) {
                favMovies =favouriteMovies;
                rv.setAdapter(new FavouriteMoviesAdapter(Main3Activity.this,favouriteMovies));
                rv.setLayoutManager(new GridLayoutManager(Main3Activity.this,2));
            }
        });



    }

    public  class SearchAsynctask extends AsyncTask<String, Void, String> {
        SearchView.OnQueryTextListener context;

        public SearchAsynctask(SearchView.OnQueryTextListener fragment) {
            this.context = fragment;
        }

        @Override
        protected String doInBackground(String... strings) {
            String id = strings[0];
            try {
                java.net.URL url = new URL(id);
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                Scanner scanner = new Scanner(inputStream);
                scanner.useDelimiter("\\A");
                boolean hasInput = scanner.hasNext();
                if (hasInput) {
                    return scanner.next();
                } else {
                    return null;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                int id;
                String poster;
                String title;
                Double vote;
                String desc;
                String date=null;
                moviedata = new ArrayList<>();
                JSONObject jsonObject = new JSONObject(s);
                JSONArray articlesnews = jsonObject.getJSONArray("results");
                for (int i = 0; i < articlesnews.length(); i++) {
                    JSONObject jp = articlesnews.getJSONObject(i);
                    title = jp.optString("title");
                    id=jp.optInt("id");
                    poster=jp.optString("poster_path");
                    date=jp.optString("release_date");
                    desc=jp.optString("overview");
                    vote=jp.optDouble("vote_average");
                    com.example.lenovo.moviereviewapp.TvModelClass.Result res=new com.example.lenovo.moviereviewapp.TvModelClass.Result(id,title,poster);
                    moviedata.add(res);

                    Log.i("tag","data parsed");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            rv.setAdapter(new TvImageAdapter(Main3Activity.this, moviedata));
            gridLayoutManager = new GridLayoutManager(Main3Activity.this,2);
            rv.setLayoutManager(gridLayoutManager);
            gridLayoutManager.scrollToPosition(position);

        }
    }
}




