package com.example.dika.dipnis;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
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

import static com.example.dika.dipnis.Global.dogadjajInicijativaProblem;
import static com.example.dika.dipnis.Global.homeUrl;

public class ReviewActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Global global;

    public TextView tvTitle;
    public LinearLayout llVrsta;
    public TextView tvTip, tvVrsta;
    public Spinner spinTip, spinVrsta;
    public CheckBox chkBuduci;
    public TextView tvPrikaz, tvPrikazNePostoji;
    public ListView lvPrikaz;
    public ConstraintLayout clProgressBar;
    public AlertDialog.Builder adb;

    public String spinTipText, spinVrstaText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarPregled);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        dogadjajInicijativaProblem = intent.getStringExtra("type");

        global = new Global();

        //Postavljanje promenjivih za kontrole
        tvTitle = (TextView) findViewById(R.id.titlePregled);
        llVrsta = (LinearLayout) findViewById(R.id.RLlVrsta);
        tvTip = (TextView) findViewById(R.id.RTvTip);
        tvVrsta = (TextView) findViewById(R.id.RTvVrsta);
        spinTip = (Spinner) findViewById(R.id.RSpinTip);
        spinVrsta = (Spinner) findViewById(R.id.RSpinVrsta);
        chkBuduci = (CheckBox) findViewById(R.id.RChkBuduci);
        tvPrikaz = (TextView) findViewById(R.id.RTvPrikaz);
        tvPrikazNePostoji = (TextView) findViewById(R.id.RTvPrikazNePostoji);
        lvPrikaz = (ListView) findViewById(R.id.RLvPrikaz);
        clProgressBar = (ConstraintLayout) findViewById(R.id.RClProgressBar);

        switch (dogadjajInicijativaProblem) {
            case "dogadjaj":
                //postavljanje naslova forme
                tvTitle.setText(R.string.strTitlePregledDogadjaja);
                //postavljanje spinnera tip
                ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.strSpinTipDogadjajaAll, R.layout.spinner_layout);
                adapter1.setDropDownViewResource(R.layout.spinner_item_layout);
                spinTip.setAdapter(adapter1);
                //postavljanje ostalih karakteristicnih stringova
                tvTip.setText(R.string.strTvTipDogadjaja);
                chkBuduci.setVisibility(View.VISIBLE);
                chkBuduci.setText(R.string.strChkBuduciDogadjaji);
                tvPrikaz.setText(R.string.strTvPrikazDogadjaji);
                break;
            case "inicijativa":
                //postavljanje naslova forme
                tvTitle.setText(R.string.strTitlePregledInicijativa);
                //postavljanje spinnera tip
                ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.strSpinTipInicijativeAll, R.layout.spinner_layout);
                adapter2.setDropDownViewResource(R.layout.spinner_item_layout);
                spinTip.setAdapter(adapter2);
                //postavljanje ostalih karakteristicnih stringova
                tvTip.setText(R.string.strTvTipInicijative);
                chkBuduci.setVisibility(View.VISIBLE);
                chkBuduci.setText(R.string.strChkBuduceInicijative);
                tvPrikaz.setText(R.string.strTvPrikazInicijative);
                break;
            case "problem":
                //postavljanje naslova forme
                tvTitle.setText(R.string.strTitlePregledProblema);
                //postavljanje spinnera tip
                ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this, R.array.strSpinTipProblemaAll, R.layout.spinner_layout);
                adapter3.setDropDownViewResource(R.layout.spinner_item_layout);
                spinTip.setAdapter(adapter3);
                //postavljanje ostalih karakteristicnih stringova
                tvTip.setText(R.string.strTvTipProblema);
                tvPrikaz.setText(R.string.strTvPrikazProblemi);
                break;
        }
        //prikaz liste
        new MyAsyncTask().execute("showAll");

        spinTipText = spinTip.getSelectedItem().toString();
        spinVrstaText = "Sve";

        //cekiranje samo buducih
        chkBuduci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkBuduci.isChecked()) {
                    if (spinTipText.equals("Svi") || spinTipText.equals("Sve"))
                        new MyAsyncTask().execute("showFuture");
                    else if (spinVrstaText.equals("Sve"))
                        new MyAsyncTask().execute("showFutureType", spinTipText);
                    else
                        new MyAsyncTask().execute("showFutureTypeKind", spinTipText, spinVrstaText);
                } else {
                    if (spinTipText.equals("Svi") || spinTipText.equals("Sve"))
                        new MyAsyncTask().execute("showAll");
                    else if (spinVrstaText.equals("Sve"))
                        new MyAsyncTask().execute("showType", spinTipText);
                    else new MyAsyncTask().execute("showTypeKind", spinTipText, spinVrstaText);
                }
            }
        });

        //hashObject za klik na stavku listview-a
        lvPrikaz.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String identificator;
                if (!dogadjajInicijativaProblem.equals("problem")) {
                    ListItem item = (ListItem) parent.getItemAtPosition(position);
                    identificator = item.id;
                } else {
                    ListItemP item =  (ListItemP) parent.getItemAtPosition(position);
                    identificator = item.id;
                }
                Intent intent = new Intent(ReviewActivity.this, DescriptionActivity.class);
                intent.putExtra("id", identificator);
                startActivity(intent);
            }
        });

        spinTip.setOnItemSelectedListener(this);
        spinVrsta.setOnItemSelectedListener(this);
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
            Intent intent = new Intent(ReviewActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    ////////////////EVENT ZA KLIK NA ITEM NEKOG SPINERA//////////////////////
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Spinner spinner = (Spinner) parent;
        //spiner za tip
        if(spinner.getId() == R.id.RSpinTip) {
            spinTipText = spinTip.getSelectedItem().toString();

            if (!dogadjajInicijativaProblem.equals("problem")) {
                if (spinTipText.equals("Svi") || spinTipText.equals("Sve")) {
                    if (chkBuduci.isChecked())
                        new MyAsyncTask().execute("showFuture");
                    else new MyAsyncTask().execute("showAll");
                    llVrsta.setVisibility(View.GONE);
                    spinVrstaText = "Sve";
                } else {
                    //popunjavanje itema za spiner vrsta
                    new MyAsyncTask().execute("spinnerKind", spinTipText);
                    if (chkBuduci.isChecked())
                        new MyAsyncTask().execute("showFutureType", spinTipText);
                    else new MyAsyncTask().execute("showType", spinTipText);

                    spinVrstaText = "Sve";

                    //postavljanje textView-a
                    switch (spinTipText) {
                        case "Sportski":
                            tvVrsta.setText(R.string.strTvVrstaSporta);
                            break;
                        case "Koncerti":
                            tvVrsta.setText(R.string.strTvIzvodjacGrupa);
                            break;
                        case "Ostali":
                            tvVrsta.setText(R.string.strTvVrstaDogadjaja);
                            break;
                        case "Sportske":
                            tvVrsta.setText(R.string.strTvVrstaSporta);
                            break;
                        case "Protesti":
                            tvVrsta.setText(R.string.strTvRazlogProtesta);
                            break;
                        case "Humanitarne akcije":
                            tvVrsta.setText(R.string.strTvVrstaHumanitarneAkcije);
                            break;
                        case "Ostale":
                            tvVrsta.setText(R.string.strTvVrstaInicijative);
                            break;
                    }
                    llVrsta.setVisibility(View.VISIBLE); //vidljivost spinera i textView-a
                }
            } else {
                if (spinTipText.equals("Svi"))
                    new MyAsyncTask().execute("showAll");
                else new MyAsyncTask().execute("showType", spinTipText);
            }

        } //spiner za vrrstu dogadjaja
        else if(spinner.getId() == R.id.RSpinVrsta) {
            spinTipText = spinTip.getSelectedItem().toString();
            spinVrstaText = spinVrsta.getSelectedItem().toString();

            if (spinVrstaText.equals("Sve") || spinVrstaText.equals("Sve")) {
                if (chkBuduci.isChecked())
                    new MyAsyncTask().execute("showFutureType", spinTipText);
                else new MyAsyncTask().execute("showType", spinTipText);
            } else {
                if (chkBuduci.isChecked())
                    new MyAsyncTask().execute("showFutureTypeKind", spinTipText, spinVrstaText);
                else new MyAsyncTask().execute("showTypeKind", spinTipText, spinVrstaText);
            }
        }
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

        ArrayList<ListItem> itemsList;
        ArrayList<ListItemP> problemsList;
        ArrayList<String> types;

        ArrayList<String> keys, values;

        String spinnerKindUrl, showAllUrl, showFutureUrl, showFutureTypeUrl, showFutureTypeKindUrl, showTypeUrl, showTypeKindUrl;

        @Override
        protected void onPreExecute() {
            setProgressBar();

            //deklaracija adresa skripti
            spinnerKindUrl = homeUrl + "spinnerKind.php";
            showAllUrl = homeUrl + "showAll.php";
            showFutureUrl = homeUrl + "showFuture.php";
            showFutureTypeUrl = homeUrl + "showFutureType.php";
            showFutureTypeKindUrl = homeUrl + "showFutureTypeKind.php";
            showTypeUrl = homeUrl + "showType.php";
            showTypeKindUrl = homeUrl + "showTypeKind.php";

            //Deklaracija listi
            itemsList = new ArrayList<>();
            problemsList = new ArrayList<>();
            types = new ArrayList<>();
            keys = new ArrayList<>();
            values = new ArrayList<>();
        }

        @Override
        protected String doInBackground(String... params) {
            String type = params[0], jsonStr;
            switch (type) {
                case "spinnerKind":
                    keys.add("dip");
                    keys.add("tip");
                    values.add(dogadjajInicijativaProblem);
                    values.add(params[1]);
                    jsonStr = global.getJSON(spinnerKindUrl, keys, values);
                    if (jsonStr.equals("ConnectTimeout")) {
                        type = "Timeout";
                    } else {
                        makeStringArrFromJSON(jsonStr);
                    }
                    break;
                case "showAll":
                    keys.add("dip");
                    values.add(dogadjajInicijativaProblem);
                    jsonStr = global.getJSON(showAllUrl, keys, values);
                    if (jsonStr.equals("ConnectTimeout")) {
                        type = "Timeout";
                    } else {
                        makeListFromJSON(jsonStr);
                    }
                    break;
                case "showFuture":
                    keys.add("dip");
                    values.add(dogadjajInicijativaProblem);
                    jsonStr = global.getJSON(showFutureUrl, keys, values);
                    if (jsonStr.equals("ConnectTimeout")) {
                        type = "Timeout";
                    } else {
                        makeListFromJSON(jsonStr);
                    }
                    break;
                case "showFutureType":
                    keys.add("dip");
                    keys.add("tip");
                    values.add(dogadjajInicijativaProblem);
                    values.add(params[1]);
                    jsonStr = global.getJSON(showFutureTypeUrl, keys, values);
                    if (jsonStr.equals("ConnectTimeout")) {
                        type = "Timeout";
                    } else {
                        makeListFromJSON(jsonStr);
                    }
                    break;
                case "showFutureTypeKind":
                    keys.add("dip");
                    keys.add("tip");
                    keys.add("vrsta");
                    values.add(dogadjajInicijativaProblem);
                    values.add(params[1]);
                    values.add(params[2]);
                    jsonStr = global.getJSON(showFutureTypeKindUrl, keys, values);
                    if (jsonStr.equals("ConnectTimeout")) {
                        type = "Timeout";
                    } else {
                        makeListFromJSON(jsonStr);
                    }
                    break;
                case "showType":
                    keys.add("dip");
                    keys.add("tip");
                    values.add(dogadjajInicijativaProblem);
                    values.add(params[1]);
                    jsonStr = global.getJSON(showTypeUrl, keys, values);
                    if (jsonStr.equals("ConnectTimeout")) {
                        type = "Timeout";
                    } else {
                        makeListFromJSON(jsonStr);
                    }
                    break;
                case "showTypeKind":
                    keys.add("dip");
                    keys.add("tip");
                    keys.add("vrsta");
                    values.add(dogadjajInicijativaProblem);
                    values.add(params[1]);
                    values.add(params[2]);
                    jsonStr = global.getJSON(showTypeKindUrl, keys, values);
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
            if (result.equals("spinnerKind")) {
                //Postavljanje itema spinera za vrstu dogadjaja
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_layout, types);
                adapter.setDropDownViewResource(R.layout.spinner_item_layout);
                spinVrsta.setAdapter(adapter);
            } else if (result.equals("Timeout")) {
                adb = new AlertDialog.Builder(ReviewActivity.this);
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
            } else if (dogadjajInicijativaProblem.equals("problem")) {
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
            } else {
                if (itemsList.isEmpty()) {
                    lvPrikaz.setVisibility(View.GONE);
                    tvPrikazNePostoji.setVisibility(View.VISIBLE);
                } else {
                    //popunjavanje listView-a
                    MyAdapterEI myAdapterEI =new MyAdapterEI(getApplicationContext(), R.layout.ei_list_item_layout, itemsList);
                    lvPrikaz.setAdapter(myAdapterEI);
                    //listAdapter.notifyDataSetChanged();
                    lvPrikaz.setVisibility(View.VISIBLE);
                    tvPrikazNePostoji.setVisibility(View.GONE);
                }
            }

            unsetProgressBar();
        }

        //funkcija za postavljanje key value liste za popunjavanje listView-a prikaz dogadjaja
        public void makeListFromJSON(String jsonString) {
            try {
                if (dogadjajInicijativaProblem.equals("problem")) {
                    //brisanje stare liste
                    problemsList.clear();
                } else {
                    //brisanje stare liste
                    itemsList.clear();
                }
                //ceo JSON string
                JSONObject jsonObject = new JSONObject(jsonString);
                //niz objekata
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                for (int i = 0; i < jsonArray.length(); i++) {
                    //svaki dogadjaj posebno
                    JSONObject object = jsonArray.getJSONObject(i);
                    String id = object.getString("id");
                    String tip = object.getString("tip");
                    String lokacija = object.getString("lokacija");

                    Bitmap bmp = null;
                    if (dogadjajInicijativaProblem.equals("problem")) {
                        String opis = object.getString("opis");
                        URL url = null;
                        try {
                            url = new URL(object.getString("img"));
                            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        }catch (IOException e) {
                            e.printStackTrace();
                        }
                        //popunjavanje globalne liste key/value
                        problemsList.add(new ListItemP(id, tip, opis, lokacija, bmp));
                    } else {
                        String vrsta = object.getString("vrsta");
                        String kratakOpis = object.getString("kratakOpis");
                        String datumVreme = object.getString("datumVreme");
                        datumVreme = datumVreme.substring(0, datumVreme.length() - 3);
                        switch (tip) {
                            case "Sportski":
                                bmp = BitmapFactory.decodeResource(getResources(), R.drawable.sport_icon);
                                break;
                            case "Koncerti":
                                bmp = BitmapFactory.decodeResource(getResources(), R.drawable.concert_icon);
                                break;
                            case "Ostali":
                                bmp = BitmapFactory.decodeResource(getResources(), R.drawable.event_icon);
                                break;
                            case "Sportske":
                                bmp = BitmapFactory.decodeResource(getResources(), R.drawable.sport_icon);
                                break;
                            case "Protesti":
                                bmp = BitmapFactory.decodeResource(getResources(), R.drawable.protest_icon);
                                break;
                            case "Humanitarne akcije":
                                bmp = BitmapFactory.decodeResource(getResources(), R.drawable.humanity_icon);
                                break;
                            case "Ostale":
                                bmp = BitmapFactory.decodeResource(getResources(), R.drawable.initiative_icon);
                                break;
                        }
                        //popunjavanje globalne liste key/value
                        itemsList.add(new ListItem(id, vrsta, datumVreme, kratakOpis, lokacija, bmp));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //funkcija za postavljanje niza stringova za spiner za vrstu dogadjaja
        public void makeStringArrFromJSON(String jsonString) {
            try {
                if (types != null)
                    types.clear();
                types.add("Sve");

                JSONObject jsonObject = new JSONObject(jsonString);
                JSONArray vrste = jsonObject.getJSONArray("vrste");

                for (int i = 0; i < vrste.length(); i++) {
                    JSONObject object = vrste.getJSONObject(i);
                    String vrsta = object.getString("vrsta");

                    if (!types.contains(vrsta))
                        types.add(vrsta);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
