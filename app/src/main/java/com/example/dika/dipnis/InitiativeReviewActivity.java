package com.example.dika.dipnis;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.dika.dipnis.Global.homeUrl;

public class InitiativeReviewActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Global global;

    public Spinner spinTipInicijative, spinVrstaRazlog;
    public ListView lvPrikaz;
    public CheckBox chkBuduceInicijative;
    public LinearLayout llVrstaRazlog;
    public TextView tvVrstaRazlog, tvPrikazNePostoji;
    public ConstraintLayout clProgressBar;
    public AlertDialog.Builder adb;

    public String spinTipInicijativeText, spinVrstaRazlogText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initiative_review);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarPregledInicijativa);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        global = new Global();

        //Postavljanje promenjivih za kontrole
        spinTipInicijative = (Spinner) findViewById(R.id.IRSpinTipInicijative);
        spinVrstaRazlog = (Spinner) findViewById(R.id.IRSpinVrstaRazlog);
        lvPrikaz = (ListView) findViewById(R.id.IRLvPrikaz);
        chkBuduceInicijative = (CheckBox) findViewById(R.id.IRChkBuduceInicijative);
        llVrstaRazlog = (LinearLayout) findViewById(R.id.IRLlVrstaRazlog);
        tvVrstaRazlog = (TextView) findViewById(R.id.IRTvVrstaRazlog);
        tvPrikazNePostoji = (TextView) findViewById(R.id.IRTvPrikazNePostoji);
        clProgressBar = (ConstraintLayout) findViewById(R.id.IRClProgressBar);

        //prikaz svih inicijativa
        new MyAsyncTask().execute("showAll");

        //Postavljanje itema spinera za tip inicijative
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.strIRSpinTipInicijative, R.layout.spinner_layout);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinTipInicijative.setAdapter(adapter);

        spinTipInicijativeText = spinTipInicijative.getSelectedItem().toString();
        spinVrstaRazlogText = "Sve";

        //cekiranje samo buducih inicijativa
        chkBuduceInicijative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chkBuduceInicijative.isChecked()) {
                    if (spinTipInicijativeText.equals("Sve"))
                        new MyAsyncTask().execute("showFuture");
                    else if (spinVrstaRazlogText.equals("Sve"))
                        new MyAsyncTask().execute("showFutureType", spinTipInicijativeText);
                    else new MyAsyncTask().execute("showFutureTypeKind", spinTipInicijativeText, spinVrstaRazlogText);
                } else {
                    if (spinTipInicijativeText.equals("Sve"))
                        new MyAsyncTask().execute("showAll");
                    else if (spinVrstaRazlogText.equals("Sve"))
                        new MyAsyncTask().execute("showType", spinTipInicijativeText);
                    else new MyAsyncTask().execute("showTypeKind", spinTipInicijativeText, spinVrstaRazlogText);
                }
            }
        });

        //event za klik na stavku listview-a
        lvPrikaz.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> hash =  (HashMap) parent.getItemAtPosition(position);
                String idInic = hash.get("id");
                Intent intent = new Intent(InitiativeReviewActivity.this, InitiativeDescriptionActivity.class);
                intent.putExtra("idInicijative", idInic);
                startActivity(intent);
            }
        });

        spinTipInicijative.setOnItemSelectedListener(this);
        spinVrstaRazlog.setOnItemSelectedListener(this);
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
            Intent intent = new Intent(InitiativeReviewActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    ////////////////EVENT ZA KLIK NA ITEM NEKOG SPINERA//////////////////////
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Spinner spinner = (Spinner) parent;
        if(spinner.getId() == R.id.IRSpinTipInicijative) { //spiner za tip inicijative
            spinTipInicijativeText = spinTipInicijative.getSelectedItem().toString();

            if (spinTipInicijativeText.equals("Sve")) {
                if (chkBuduceInicijative.isChecked())
                    new MyAsyncTask().execute("showFuture");
                else new MyAsyncTask().execute("showAll");
                llVrstaRazlog.setVisibility(View.GONE);
                spinVrstaRazlogText = "Sve";
            } else {
                //popunjavanje itema za spiner vrsta dogadjaja iz baze
                new MyAsyncTask().execute("spinnerKind", spinTipInicijativeText);

                spinVrstaRazlogText = "Sve";

                //postavljanje textView-a
                switch (spinTipInicijativeText) {
                    case "Sportske":
                        tvVrstaRazlog.setText(R.string.strIRIDTvVrstaSporta);
                        break;
                    case "Protesti":
                        tvVrstaRazlog.setText(R.string.strIRIDTvRazlogProtesta);
                        break;
                    case "Humanitarne akcije":
                        tvVrstaRazlog.setText(R.string.strIRIDTvVrstaHumanitarneAkcije);
                        break;
                    case "Ostale":
                        tvVrstaRazlog.setText(R.string.strIRIDTvVrstaInicijative);
                        break;
                }
                llVrstaRazlog.setVisibility(View.VISIBLE); //vidljivost spinera i textView-a

                if (chkBuduceInicijative.isChecked())
                    new MyAsyncTask().execute("showFutureType", spinTipInicijativeText);
                else new MyAsyncTask().execute("showType", spinTipInicijativeText);
            }

        } else if(spinner.getId() == R.id.IRSpinVrstaRazlog) { //spiner za vrstu inicijative
            spinTipInicijativeText = spinTipInicijative.getSelectedItem().toString();
            spinVrstaRazlogText = spinVrstaRazlog.getSelectedItem().toString();

            if (spinVrstaRazlogText.equals("Sve")) {
                if (chkBuduceInicijative.isChecked())
                    new MyAsyncTask().execute("showFutureType", spinTipInicijativeText);
                else new MyAsyncTask().execute("showType", spinTipInicijativeText);
            } else {
                if (chkBuduceInicijative.isChecked())
                    new MyAsyncTask().execute("showFutureTypeKind", spinTipInicijativeText, spinVrstaRazlogText);
                else new MyAsyncTask().execute("showTypeKind", spinTipInicijativeText, spinVrstaRazlogText);
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

        ArrayList<HashMap<String, String>> initiativesList;
        ArrayList<String> initiativesTypes;

        ArrayList<String> keys, values;

        String initiativesSpinnerKindUrl, initiativesShowAllUrl, initiativesShowFutureUrl, initiativesShowFutureTypeUrl, initiativesShowFutureTypeKindUrl, initiativesShowTypeUrl, initiativesShowTypeKindUrl;

        @Override
        protected void onPreExecute() {
            setProgressBar();

            //deklaracija adresa skripti
            initiativesSpinnerKindUrl = homeUrl + "initiativesSpinnerKind.php";
            initiativesShowAllUrl = homeUrl + "initiativesShowAll.php";
            initiativesShowFutureUrl = homeUrl + "initiativesShowFuture.php";
            initiativesShowFutureTypeUrl = homeUrl + "initiativesShowFutureType.php";
            initiativesShowFutureTypeKindUrl = homeUrl + "initiativesShowFutureTypeKind.php";
            initiativesShowTypeUrl = homeUrl + "initiativesShowType.php";
            initiativesShowTypeKindUrl = homeUrl + "initiativesShowTypeKind.php";

            //Deklaracija listi
            initiativesList = new ArrayList<>();
            initiativesTypes = new ArrayList<>();
            keys = new ArrayList<>();
            values = new ArrayList<>();
        }

        @Override
        protected String doInBackground(String... params) {
            String type = params[0], jsonStr;
            switch (type) {
                case "spinnerKind":
                    keys.add("tipInicijative");
                    values.add(params[1]);
                    jsonStr = global.getJSON(initiativesSpinnerKindUrl, true, keys, values);
                    if (jsonStr.equals("ConnectTimeout")) {
                        type = "Timeout";
                    } else {
                        makeStringArrFromJSON(jsonStr);
                    }
                    break;
                case "showAll":
                    keys = null;
                    values = null;
                    jsonStr = global.getJSON(initiativesShowAllUrl, false, keys, values);
                    if (jsonStr.equals("ConnectTimeout")) {
                        type = "Timeout";
                    } else {
                        makeListFromJSON(jsonStr);
                    }
                    break;
                case "showFuture":
                    keys = null;
                    values = null;
                    jsonStr = global.getJSON(initiativesShowFutureUrl, false, keys, values);
                    if (jsonStr.equals("ConnectTimeout")) {
                        type = "Timeout";
                    } else {
                        makeListFromJSON(jsonStr);
                    }
                    break;
                case "showFutureType":
                    keys.add("tipInicijative");
                    values.add(params[1]);
                    jsonStr = global.getJSON(initiativesShowFutureTypeUrl, true, keys, values);
                    if (jsonStr.equals("ConnectTimeout")) {
                        type = "Timeout";
                    } else {
                        makeListFromJSON(jsonStr);
                    }
                    break;
                case "showFutureTypeKind":
                    keys.add("tipInicijative");
                    keys.add("vrstaRazlog");
                    values.add(params[1]);
                    values.add(params[2]);
                    jsonStr = global.getJSON(initiativesShowFutureTypeKindUrl, true, keys, values);
                    if (jsonStr.equals("ConnectTimeout")) {
                        type = "Timeout";
                    } else {
                        makeListFromJSON(jsonStr);
                    }
                    break;
                case "showType":
                    keys.add("tipInicijative");
                    values.add(params[1]);
                    jsonStr = global.getJSON(initiativesShowTypeUrl, true, keys, values);
                    if (jsonStr.equals("ConnectTimeout")) {
                        type = "Timeout";
                    } else {
                        makeListFromJSON(jsonStr);
                    }
                    break;
                case "showTypeKind":
                    keys.add("tipInicijative");
                    keys.add("vrstaRazlog");
                    values.add(params[1]);
                    values.add(params[2]);
                    jsonStr = global.getJSON(initiativesShowTypeKindUrl, true, keys, values);
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
                //Postavljanje itema spinera za vrstu inicijative
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_layout, initiativesTypes);
                adapter.setDropDownViewResource(R.layout.spinner_item_layout);
                spinVrstaRazlog.setAdapter(adapter);
            } else if (result.equals("Timeout")) {
                adb = new AlertDialog.Builder(InitiativeReviewActivity.this);
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
                if (initiativesList.isEmpty()) {
                    lvPrikaz.setVisibility(View.GONE);
                    tvPrikazNePostoji.setVisibility(View.VISIBLE);
                } else {
                    //popunjavanje listView-a
                    SimpleAdapter listAdapter = new SimpleAdapter(InitiativeReviewActivity.this, initiativesList, R.layout.ei_list_item_layout,
                            new String[]{"id", "tipInicijative", "vrstaRazlog", "kratakOpis", "lokacija", "datumVreme"},
                            new int[]{R.id.EIListItemTvId, R.id.EIListItemTvTipDogadjajaInicijative, R.id.EIListItemTvVrstaIzvodjacRazlog, R.id.EIListItemTvKratakOpis, R.id.EIListItemTvLokacija, R.id.EIListItemTvDatumVreme});
                    lvPrikaz.setAdapter(listAdapter);
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
                initiativesList.clear();
                //ceo JSON string
                JSONObject jsonObject = new JSONObject(jsonString);
                //niz inicijativa
                JSONArray inicijative = jsonObject.getJSONArray("inicijative");
                for (int i = 0; i < inicijative.length(); i++) {
                    //svaka inicijativa posebno
                    JSONObject inicijativa = inicijative.getJSONObject(i);
                    String id = inicijativa.getString("idInicijative");
                    String tipInicijative = inicijativa.getString("tipInicijative");
                    String vrstaRazlog = inicijativa.getString("vrstaRazlog");
                    String kratakOpis = inicijativa.getString("kratakOpis");
                    String lokacija = inicijativa.getString("lokacija");
                    String datumVreme = inicijativa.getString("datumVreme");
                    datumVreme = datumVreme.substring(0, datumVreme.length() - 3);

                    //kreiranje key.value parova za svaku inicijativu
                    HashMap<String, String> dog = new HashMap<>();
                    dog.put("id", id);
                    dog.put("tipInicijative", tipInicijative);
                    dog.put("vrstaRazlog", vrstaRazlog);
                    dog.put("kratakOpis", kratakOpis);
                    dog.put("lokacija", lokacija);
                    dog.put("datumVreme", datumVreme);

                    //popunjavanje globalne liste key/value
                    initiativesList.add(dog);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //funkcija za postavljanje niza stringova za spiner za vrstu dogadjaja
        public void makeStringArrFromJSON(String jsonString) {
            try {
                if (initiativesTypes != null)
                    initiativesTypes.clear();
                initiativesTypes.add("Sve");

                JSONObject jsonObject = new JSONObject(jsonString);
                JSONArray vrsteInicijativa = jsonObject.getJSONArray("vrsteInicijative");

                for (int i = 0; i < vrsteInicijativa.length(); i++) {
                    JSONObject inicijativa = vrsteInicijativa.getJSONObject(i);
                    String vrstaRazlog = inicijativa.getString("vrstaRazlog");

                    if (!initiativesTypes.contains(vrstaRazlog))
                        initiativesTypes.add(vrstaRazlog);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
