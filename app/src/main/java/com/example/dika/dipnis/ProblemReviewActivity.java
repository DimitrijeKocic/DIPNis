package com.example.dika.dipnis;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import static com.example.dika.dipnis.Global.homeUrl;

public class ProblemReviewActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Global global;

    public Spinner spinTipProblema;
    public ListView lvPrikaz;
    public TextView tvPrikazNePostoji;
    public ConstraintLayout clProgressBar;
    public AlertDialog.Builder adb;

    public String spinTipProblemaText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_review);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarPregledProblema);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        global = new Global();

        //Postavljanje promenjivih za kontrole
        spinTipProblema = (Spinner) findViewById(R.id.PRSpinTipProblema);
        lvPrikaz = (ListView) findViewById(R.id.PRLvPrikaz);
        tvPrikazNePostoji = (TextView) findViewById(R.id.PRTvPrikazNePostoji);
        clProgressBar = (ConstraintLayout) findViewById(R.id.PRClProgressBar);

        //prikaz svih problema
        new MyAsyncTask().execute("showAll");

        //Postavljanje itema spinera za tip problema
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.strPRSpinTipProblema, R.layout.spinner_layout);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinTipProblema.setAdapter(adapter);

        spinTipProblemaText = spinTipProblema.getSelectedItem().toString();

        //event za klik na stavku listview-a
        lvPrikaz.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListItemP item =  (ListItemP) parent.getItemAtPosition(position);
                String idPr = item.id;
                Intent intent = new Intent(ProblemReviewActivity.this, ProblemDescriptionActivity.class);
                intent.putExtra("idProblema", idPr);
                startActivity(intent);
            }
        });

        spinTipProblema.setOnItemSelectedListener(this);
    }

    /////////////KREIRANJE MENIJA/////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //////EVENT ZA KLIK NA STAVKU MENIJA/////
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.menuItem) {
            Intent intent = new Intent(ProblemReviewActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        spinTipProblemaText = spinTipProblema.getSelectedItem().toString();
        if (spinTipProblemaText.equals("Svi"))
            new MyAsyncTask().execute("showAll");
        else new MyAsyncTask().execute("showType", spinTipProblemaText);
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    ////////POSTAVLJANJE I UKLANJANJE PROGRESS BAR-A////////
    public void setProgressBar() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        clProgressBar.setVisibility(View.VISIBLE);
    }
    public void unsetProgressBar() {
        clProgressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }





    ////////////////////BACKGROUND WORKER KLASA///////////////////////
    public class MyAsyncTask extends AsyncTask<String, Object, String> {

        ArrayList<ListItemP> problemsList;

        ArrayList<String> keys, values;

        String problemsShowAllUrl, problemsShowTypeUrl;

        @Override
        protected void onPreExecute() {
            setProgressBar();

            //deklaracija adresa skripti
            problemsShowAllUrl = homeUrl + "problemsShowAll.php";
            problemsShowTypeUrl = homeUrl + "problemsShowType.php";

            //Deklaracija listi
            problemsList = new ArrayList<>();
            keys = new ArrayList<>();
            values = new ArrayList<>();
        }

        @Override
        protected String doInBackground(String... params) {
            String type = params[0], jsonStr;
            switch (type) {
                case "showAll":
                    keys = null;
                    values = null;
                    jsonStr = global.getJSON(problemsShowAllUrl, false, keys, values);
                    if (jsonStr.equals("ConnectTimeout")) {
                        type = "Timeout";
                    } else {
                        makeListFromJSON(jsonStr);
                    }
                    break;
                case "showType":
                    keys.add("tipProblema");
                    values.add(params[1]);
                    jsonStr = global.getJSON(problemsShowTypeUrl, true, keys, values);
                    if (jsonStr.equals("ConnectTimeout")) {
                        type = "Timeout";
                    } else {
                        makeListFromJSON(jsonStr);
                    }
                    break;
            }
            return type;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("Timeout")) {
                adb = new AlertDialog.Builder(ProblemReviewActivity.this);
                adb.setTitle(getResources().getString(R.string.strAdbTitleObavestenje));
                adb.setMessage(R.string.strAdbGreska);
                adb.setPositiveButton(R.string.strAdbOK, new Dialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                adb.setIcon(R.drawable.adb_obavestenje);
                adb.show();
            } else {
                if (problemsList.isEmpty()) {
                    lvPrikaz.setVisibility(View.GONE);
                    tvPrikazNePostoji.setVisibility(View.VISIBLE);
                } else {
                    //popunjavanje listView-a
                    MyAdapterP myAdapterP = new MyAdapterP(getApplicationContext(), R.layout.p_list_item_layout, problemsList);
                    lvPrikaz.setAdapter(myAdapterP);
                    //listAdapter.notifyDataSetChanged();
                    lvPrikaz.setVisibility(View.VISIBLE);
                    tvPrikazNePostoji.setVisibility(View.GONE);
                }
            }

            unsetProgressBar();
        }

        //funkcija za postavljanje key value liste za popunjavanje listView-a prikaz inicijativa
        public void makeListFromJSON(String jsonString) {
            try {
                //brisanje stare liste
                problemsList.clear();
                //ceo JSON string
                JSONObject jsonObject = new JSONObject(jsonString);
                //niz inicijativa
                JSONArray problems = jsonObject.getJSONArray("problemi");
                for (int i = 0; i < problems.length(); i++) {
                    //svaka inicijativa posebno
                    JSONObject problem = problems.getJSONObject(i);
                    String id = problem.getString("idProblema");
                    String tipProblema = problem.getString("tipProblema");
                    String opis = problem.getString("opis");
                    String lokacija = problem.getString("lokacija");
                    Bitmap bmp = null;
                    URL url = null;
                    try {
                        url = new URL(problem.getString("img"));
                        bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    }catch (IOException e) {
                        e.printStackTrace();
                    }

                    //popunjavanje globalne liste key/value
                    problemsList.add(new ListItemP(id, tipProblema, opis, lokacija, bmp));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
