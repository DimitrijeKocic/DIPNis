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

public class EventReviewActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Global global;

    public Spinner spinTipDogadjaja, spinVrstaIzvodjac;
    public ListView lvPrikaz;
    public CheckBox chkBuduciDogadjaji;
    public LinearLayout llVrstaIzvodjac;
    public TextView tvVrstaIzvodjac, tvPrikazNePostoji;
    public ConstraintLayout clProgressBar;
    public AlertDialog.Builder adb;

    public String spinTipDogadjajaText, spinVrstaIzvodjacText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_review);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarPregledDogadjaja);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        global = new Global();

        //Postavljanje promenjivih za kontrole
        spinTipDogadjaja = (Spinner) findViewById(R.id.ERSpinTipDogadjaja);
        spinVrstaIzvodjac = (Spinner) findViewById(R.id.ERSpinVrstaIzvodjac);
        lvPrikaz = (ListView) findViewById(R.id.ERLvPrikaz);
        chkBuduciDogadjaji = (CheckBox) findViewById(R.id.ERChkBuduciDogadjaji);
        llVrstaIzvodjac = (LinearLayout) findViewById(R.id.ERLlVrstaIzvodjac);
        tvVrstaIzvodjac = (TextView) findViewById(R.id.ERTvVrstaIzvodjac);
        tvPrikazNePostoji = (TextView) findViewById(R.id.ERTvPrikazNePostoji);
        clProgressBar = (ConstraintLayout) findViewById(R.id.ERClProgressBar);
        adb = new AlertDialog.Builder(EventReviewActivity.this);

        //prikaz svih dogadjaja
        new MyAsyncTask().execute("showAll");

        //Postavljanje itema spinera za tip dogadjaja
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.strERSpinTipDogadjaja, R.layout.spinner_layout);
        adapter.setDropDownViewResource(R.layout.spinner_item_layout);
        spinTipDogadjaja.setAdapter(adapter);

        spinTipDogadjajaText = spinTipDogadjaja.getSelectedItem().toString();
        spinVrstaIzvodjacText = "Sve";

        //cekiranje samo buducih dogadjaja
        chkBuduciDogadjaji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chkBuduciDogadjaji.isChecked()) {
                    if (spinTipDogadjajaText.equals("Svi"))
                        new MyAsyncTask().execute("showFuture");
                    else if (spinVrstaIzvodjacText.equals("Sve"))
                        new MyAsyncTask().execute("showFutureType", spinTipDogadjajaText);
                    else new MyAsyncTask().execute("showFutureTypeKind", spinTipDogadjajaText, spinVrstaIzvodjacText);
                } else {
                    if (spinTipDogadjajaText.equals("Svi"))
                        new MyAsyncTask().execute("showAll");
                    else if (spinVrstaIzvodjacText.equals("Sve"))
                        new MyAsyncTask().execute("showType", spinTipDogadjajaText);
                    else new MyAsyncTask().execute("showTypeKind", spinTipDogadjajaText, spinVrstaIzvodjacText);
                }
            }
        });

        //event za klik na stavku listview-a
        lvPrikaz.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> hash =  (HashMap) parent.getItemAtPosition(position);
                String idDog = hash.get("id");
                Intent intent = new Intent(EventReviewActivity.this, EventDescriptionActivity.class);
                intent.putExtra("idDogadjaja", idDog);
                startActivity(intent);
            }
        });

        spinTipDogadjaja.setOnItemSelectedListener(this);
        spinVrstaIzvodjac.setOnItemSelectedListener(this);
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
            Intent intent = new Intent(EventReviewActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    ////////////////EVENT ZA KLIK NA ITEM NEKOG SPINERA//////////////////////
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Spinner spinner = (Spinner) parent;
        if(spinner.getId() == R.id.ERSpinTipDogadjaja) { //spiner za tip dogadjaja
            spinTipDogadjajaText = spinTipDogadjaja.getSelectedItem().toString();

            if (spinTipDogadjajaText.equals("Svi")) {
                if (chkBuduciDogadjaji.isChecked())
                    new MyAsyncTask().execute("showFuture");
                else new MyAsyncTask().execute("showAll");
                llVrstaIzvodjac.setVisibility(View.GONE);
                spinVrstaIzvodjacText = "Sve";
            } else {
                //popunjavanje itema za spiner vrsta dogadjaja iz baze
                new MyAsyncTask().execute("spinnerKind", spinTipDogadjajaText);

                spinVrstaIzvodjacText = "Sve";

                //postavljanje textView-a
                switch (spinTipDogadjajaText) {
                    case "Sportski":
                        tvVrstaIzvodjac.setText(R.string.strEREDTvVrstaSporta);
                        break;
                    case "Koncerti":
                        tvVrstaIzvodjac.setText(R.string.strEREDTvIzvodjacGrupa);
                        break;
                    case "Ostali":
                        tvVrstaIzvodjac.setText(R.string.strEREDTvVrstaDogadjaja);
                        break;
                }
                llVrstaIzvodjac.setVisibility(View.VISIBLE); //vidljivost spinera i textView-a

                if (chkBuduciDogadjaji.isChecked())
                    new MyAsyncTask().execute("showFutureType", spinTipDogadjajaText);
                else new MyAsyncTask().execute("showType", spinTipDogadjajaText);
            }

        } else if(spinner.getId() == R.id.ERSpinVrstaIzvodjac) { //spiner za vrrstu dogadjaja
            spinTipDogadjajaText = spinTipDogadjaja.getSelectedItem().toString();
            spinVrstaIzvodjacText = spinVrstaIzvodjac.getSelectedItem().toString();

            if (spinVrstaIzvodjacText.equals("Sve")) {
                if (chkBuduciDogadjaji.isChecked())
                    new MyAsyncTask().execute("showFutureType", spinTipDogadjajaText);
                else new MyAsyncTask().execute("showType", spinTipDogadjajaText);
            } else {
                if (chkBuduciDogadjaji.isChecked())
                    new MyAsyncTask().execute("showFutureTypeKind", spinTipDogadjajaText, spinVrstaIzvodjacText);
                else new MyAsyncTask().execute("showTypeKind", spinTipDogadjajaText, spinVrstaIzvodjacText);
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

        ArrayList<HashMap<String, String>> eventsList;
        ArrayList<String> eventsTypes;

        ArrayList<String> keys, values;

        String eventsSpinnerKindUrl, eventsShowAllUrl, eventsShowFutureUrl, eventsShowFutureTypeUrl, eventsShowFutureTypeKindUrl, eventsShowTypeUrl, eventsShowTypeKindUrl;

        @Override
        protected void onPreExecute() {
            setProgressBar();

            //deklaracija adresa skripti
            eventsSpinnerKindUrl = homeUrl + "eventsSpinnerKind.php";
            eventsShowAllUrl = homeUrl + "eventsShowAll.php";
            eventsShowFutureUrl = homeUrl + "eventsShowFuture.php";
            eventsShowFutureTypeUrl = homeUrl + "eventsShowFutureType.php";
            eventsShowFutureTypeKindUrl = homeUrl + "eventsShowFutureTypeKind.php";
            eventsShowTypeUrl = homeUrl + "eventsShowType.php";
            eventsShowTypeKindUrl = homeUrl + "eventsShowTypeKind.php";

            //Deklaracija listi
            eventsList = new ArrayList<>();
            eventsTypes = new ArrayList<>();
            keys = new ArrayList<>();
            values = new ArrayList<>();
        }

        @Override
        protected String doInBackground(String... params) {
            String type = params[0], jsonStr;
            switch (type) {
                case "spinnerKind":
                    keys.add("tipDogadjaja");
                    values.add(params[1]);
                    jsonStr = global.getJSON(eventsSpinnerKindUrl, true, keys, values);
                    if (jsonStr.equals("ConnectTimeout")) {
                        type = "Timeout";
                    } else {
                        makeStringArrFromJSON(jsonStr);
                    }
                    break;
                case "showAll":
                    keys = null;
                    values = null;
                    jsonStr = global.getJSON(eventsShowAllUrl, false, keys, values);
                    if (jsonStr.equals("ConnectTimeout")) {
                        type = "Timeout";
                    } else {
                        makeListFromJSON(jsonStr);
                    }
                    break;
                case "showFuture":
                    keys = null;
                    values = null;
                    jsonStr = global.getJSON(eventsShowFutureUrl, false, keys, values);
                    if (jsonStr.equals("ConnectTimeout")) {
                        type = "Timeout";
                    } else {
                        makeListFromJSON(jsonStr);
                    }
                    break;
                case "showFutureType":
                    keys.add("tipDogadjaja");
                    values.add(params[1]);
                    jsonStr = global.getJSON(eventsShowFutureTypeUrl, true, keys, values);
                    if (jsonStr.equals("ConnectTimeout")) {
                        type = "Timeout";
                    } else {
                        makeListFromJSON(jsonStr);
                    }
                    break;
                case "showFutureTypeKind":
                    keys.add("tipDogadjaja");
                    keys.add("vrstaIzvodjac");
                    values.add(params[1]);
                    values.add(params[2]);
                    jsonStr = global.getJSON(eventsShowFutureTypeKindUrl, true, keys, values);
                    if (jsonStr.equals("ConnectTimeout")) {
                        type = "Timeout";
                    } else {
                        makeListFromJSON(jsonStr);
                    }
                    break;
                case "showType":
                    keys.add("tipDogadjaja");
                    values.add(params[1]);
                    jsonStr = global.getJSON(eventsShowTypeUrl, true, keys, values);
                    if (jsonStr.equals("ConnectTimeout")) {
                        type = "Timeout";
                    } else {
                        makeListFromJSON(jsonStr);
                    }
                    break;
                case "showTypeKind":
                    keys.add("tipDogadjaja");
                    keys.add("vrstaIzvodjac");
                    values.add(params[1]);
                    values.add(params[2]);
                    jsonStr = global.getJSON(eventsShowTypeKindUrl, true, keys, values);
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
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_layout, eventsTypes);
                adapter.setDropDownViewResource(R.layout.spinner_item_layout);
                spinVrstaIzvodjac.setAdapter(adapter);
            } else if (result.equals("Timeout")) {
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
                if (eventsList.isEmpty()) {
                    lvPrikaz.setVisibility(View.GONE);
                    tvPrikazNePostoji.setVisibility(View.VISIBLE);
                } else {
                    //popunjavanje listView-a
                    SimpleAdapter listAdapter = new SimpleAdapter(EventReviewActivity.this, eventsList, R.layout.ei_list_item_layout,
                            new String[]{"id", "tipDogadjaja", "vrstaIzvodjac", "kratakOpis", "lokacija", "datumVreme"},
                            new int[]{R.id.EIListItemTvId, R.id.EIListItemTvTipDogadjajaInicijative, R.id.EIListItemTvVrstaIzvodjacRazlog, R.id.EIListItemTvKratakOpis, R.id.EIListItemTvLokacija, R.id.EIListItemTvDatumVreme});
                    lvPrikaz.setAdapter(listAdapter);
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
                //brisanje stare liste
                eventsList.clear();
                //ceo JSON string
                JSONObject jsonObject = new JSONObject(jsonString);
                //niz objekata
                JSONArray dogadjaji = jsonObject.getJSONArray("dogadjaji");
                for (int i = 0; i < dogadjaji.length(); i++) {
                    //svaki dogadjaj posebno
                    JSONObject dogadjaj = dogadjaji.getJSONObject(i);
                    String id = dogadjaj.getString("idDogadjaja");
                    String tipDogadjaja = dogadjaj.getString("tipDogadjaja");
                    String vrstaIzvodjac = dogadjaj.getString("vrstaIzvodjac");
                    String kratakOpis = dogadjaj.getString("kratakOpis");
                    String lokacija = dogadjaj.getString("lokacija");
                    String datumVreme = dogadjaj.getString("datumVreme");
                    datumVreme = datumVreme.substring(0, datumVreme.length() - 3);

                    //kreiranje key.value parova za svaki dogadjaj
                    HashMap<String, String> dog = new HashMap<>();
                    dog.put("id", id);
                    dog.put("tipDogadjaja", tipDogadjaja);
                    dog.put("vrstaIzvodjac", vrstaIzvodjac);
                    dog.put("kratakOpis", kratakOpis);
                    dog.put("lokacija", lokacija);
                    dog.put("datumVreme", datumVreme);

                    //popunjavanje globalne liste key/value
                    eventsList.add(dog);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //funkcija za postavljanje niza stringova za spiner za vrstu dogadjaja
        public void makeStringArrFromJSON(String jsonString) {
            try {
                if (eventsTypes != null)
                    eventsTypes.clear();
                eventsTypes.add("Sve");

                JSONObject jsonObject = new JSONObject(jsonString);
                JSONArray vrstedogadjaja = jsonObject.getJSONArray("vrsteDogadjaja");

                for (int i = 0; i < vrstedogadjaja.length(); i++) {
                    JSONObject dogadjaj = vrstedogadjaja.getJSONObject(i);
                    String vrstaIzvodjac = dogadjaj.getString("vrstaIzvodjac");

                    if (!eventsTypes.contains(vrstaIzvodjac))
                        eventsTypes.add(vrstaIzvodjac);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
