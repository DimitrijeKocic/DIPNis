package com.example.dika.dipnis;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import static com.example.dika.dipnis.Global.homeUrl;

public class InitiativeAddActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Global global;

    private static final int CAMERA = 0;
    private static final int GALLERY = 1;
    private static final int LOCATION = 2;

    private Bitmap bmp;
    private boolean camera;

    private String dateNow, timeNow;

    public static TextView tvDatum, tvVreme;
    public EditText etVrstaRazlog, etKratakOpis, etLokacija, etOpis;
    public Spinner spinTipInicijative;
    public LinearLayout llDatum, llVreme;
    public ImageView ivSlika, ivLokacija;
    public Button btnDodajSliku, btnSacuvajInicijativu;
    public ConstraintLayout clProgressBar;
    public AlertDialog.Builder adb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initiative_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarDodajInicijativu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        global = new Global();
        camera = false;

        //Postavljanje promenjivih za kontrole
        tvDatum = (TextView) findViewById(R.id.IATvDatum);
        tvVreme = (TextView) findViewById(R.id.IATvVreme);
        etVrstaRazlog = (EditText) findViewById(R.id.IAEtVrstaRazlog);
        etKratakOpis = (EditText) findViewById(R.id.IAEtKratakOpis);
        etLokacija = (EditText) findViewById(R.id.IAEtLokacija);
        etOpis = (EditText) findViewById(R.id.IAEtOpis);
        spinTipInicijative = (Spinner) findViewById(R.id.IASpinTipInicijative);
        llDatum = (LinearLayout) findViewById(R.id.IALlDatum);
        llVreme = (LinearLayout) findViewById(R.id.IALlVreme);
        ivSlika = (ImageView) findViewById(R.id.IAIvSlika);
        ivLokacija = (ImageView) findViewById(R.id.IAIvLokacija);
        btnDodajSliku = (Button) findViewById(R.id.IABtnDodajSliku);
        btnSacuvajInicijativu = (Button) findViewById(R.id.IABtnSacuvajInicijativu);
        clProgressBar = (ConstraintLayout) findViewById(R.id.IAClProgressBar);

        //setovanje trenutnog datuma i vremena
        String[] dateTimeArr = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().getTime()).split(" ");
        dateNow = dateTimeArr[0];
        timeNow = dateTimeArr[1];
        tvDatum.setText(dateNow);
        tvVreme.setText(timeNow);

        //Postavljanje itema spinera za tip dogadjaja
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.strIASpinTipInicijative, R.layout.spinner_layout);
        adapter.setDropDownViewResource(R.layout.spinner_item_layout);
        spinTipInicijative.setAdapter(adapter);

        spinTipInicijative.setOnItemSelectedListener(this);

        //klik na lokaciju
        ivLokacija.setClickable(true);
        ivLokacija.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InitiativeAddActivity.this, MapsActivity.class);
                intent.putExtra("markerPosition", "noPosition");
                startActivityForResult(intent, LOCATION);
            }
        });

        //klik na kalenar - odabir datuma
        llDatum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "datePicker");
            }
        });

        //klik na sat - odabir vremena
        llVreme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "timePicker");
            }
        });

        btnDodajSliku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adb = new AlertDialog.Builder(InitiativeAddActivity.this);
                if (tvDatum.getText().toString().compareTo(dateNow) > 0) {
                    adb = new AlertDialog.Builder(InitiativeAddActivity.this);
                    adb.setTitle(getResources().getString(R.string.strAdbTitleObavestenje));
                    adb.setMessage(R.string.strIAdbDodajSlikuObavestenje);
                    adb.setPositiveButton(R.string.strAdbOK, null);
                    adb.setIcon(R.drawable.adb_obavestenje);
                } else {
                    final String[] adbItems = getResources().getStringArray(R.array.strAdbItems);
                    adb = new AlertDialog.Builder(InitiativeAddActivity.this);
                    adb.setTitle(getResources().getString(R.string.strAdbTitleSlika));
                    adb.setItems(adbItems, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            if (adbItems[i].equals("Slikaj")) {
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(intent, CAMERA);
                            } else if (adbItems[i].equals("Odaberi iz galerije")) {
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent, "Odaberi sliku"), GALLERY);
                            }
                        }
                    });
                    adb.setIcon(R.drawable.adb_slikaj);
                }
                adb.show();
            }
        });

        btnSacuvajInicijativu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String img;
                if (bmp != null) {
                    //konvertovanje slike u string
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                    img = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);

                    if (camera) {
                        File destination = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");
                        FileOutputStream fo;
                        try {
                            destination.createNewFile();
                            fo = new FileOutputStream(destination);
                            fo.write(stream.toByteArray());
                            fo.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else img = "";

                //provera za unos u tekst polja
                ArrayList<EditText> inputCheck = new ArrayList<>();
                EditText item = null;
                inputCheck.addAll(Arrays.asList(etVrstaRazlog, etKratakOpis, etLokacija, etOpis));
                for (EditText itemTmp : inputCheck) {
                    if (itemTmp.getText().toString().equals("")) {
                        item = itemTmp;
                        break;
                    }
                }
                if (item == null) {
                    String tipInicijative = spinTipInicijative.getSelectedItem().toString();
                    String vrstaRazlog = etVrstaRazlog.getText().toString();
                    String kratakOpis = etKratakOpis.getText().toString();
                    String lokacija = etLokacija.getText().toString();
                    String opis = etOpis.getText().toString();
                    String datumVreme = tvDatum.getText().toString() + " " + tvVreme.getText().toString() + ":00";
                    if (bmp != null && tvDatum.getText().toString().compareTo(dateNow) > 0) {
                        adb = new AlertDialog.Builder(InitiativeAddActivity.this);
                        adb.setTitle(getResources().getString(R.string.strAdbTitleObavestenje));
                        adb.setMessage(R.string.strIAdbInicijativaSacuvanaObavestenje);
                        adb.setPositiveButton(R.string.strAdbOK, null);
                        adb.setIcon(R.drawable.adb_obavestenje);
                        adb.show();
                        ivSlika.setVisibility(View.GONE);
                        bmp = null;
                        ivSlika.setImageBitmap(bmp);
                    } else {
                        new MyAsyncTask().execute("addInitiative", tipInicijative, vrstaRazlog, kratakOpis, lokacija, datumVreme, opis, img, homeUrl);
                        initialState();
                    }
                } else {
                    adb = new AlertDialog.Builder(InitiativeAddActivity.this);
                    adb.setTitle(getResources().getString(R.string.strAdbTitleObavestenje));
                    adb.setMessage(getResources().getString(R.string.strAdbObavezanUnos) + " " + item.getHint().toString() + ".");
                    adb.setPositiveButton(R.string.strAdbOK, null);
                    adb.setIcon(R.drawable.adb_obavestenje);
                    adb.show();
                }
            }
        });
    }

    /////////////////POCETNO STANJE ELEMENATA/////////////////////
    public void initialState() {
        spinTipInicijative.setSelection(0);
        etVrstaRazlog.setText("");
        etKratakOpis.setText("");
        etLokacija.setText("");
        tvDatum.setText(dateNow);
        tvVreme.setText(timeNow);
        etOpis.setText("");
        ivSlika.setVisibility(View.GONE);
        bmp = null;
        ivSlika.setImageBitmap(bmp);
    }

    /////////////////REZULTAT KAMERE ILI GALERIJE////////////////////
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == CAMERA) {
                bmp = (Bitmap) data.getExtras().get("data");
                ivSlika.setImageBitmap(bmp);
                camera = true;
                ivSlika.setVisibility(View.VISIBLE);
            }
            else if (requestCode == GALLERY) {
                try {
                    bmp = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                    ivSlika.setImageBitmap(bmp);
                    camera = false;
                    ivSlika.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //rezultat mape
            else if (requestCode == LOCATION) {
                etLokacija.setText(data.getStringExtra("lokacija"));
            }
        }
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
            Intent intent = new Intent(InitiativeAddActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    ////////////////EVENT ZA KLIK NA ITEM NEKOG SPINERA//////////////////////
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (spinTipInicijative.getSelectedItem().toString()) {
            case "Sportske":
                etVrstaRazlog.setHint(R.string.strIAEtHintVrstaSporta);
                break;
            case "Protesti":
                etVrstaRazlog.setHint(R.string.strIAEtHintRazlogProtesta);
                break;
            case "Humanitarne akcije":
                etVrstaRazlog.setHint(R.string.strIAEtHintVrstaHumanitarneAkcije);
                break;
            case "Ostale":
                etVrstaRazlog.setHint(R.string.strIAEtHintVrstaInicijative);
                break;
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    ////////POSTAVLJANJE I UKLANJANJE PROGRESS BAR-A////////
    public void setProgressBar() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        clProgressBar.setVisibility(View.VISIBLE);
    }
    public void unsetProgressBar() {
        clProgressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            //Trenutni datum da bude selektovan
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), R.style.dateTimePickerTheme, this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            tvDatum.setText(year + "-" + ((month + 1) < 10 ? "0" : "") + (month + 1) + "-" + (dayOfMonth < 10 ? "0" : "") + dayOfMonth);
        }
    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            //Trenutno vreme da bude selektovano
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), R.style.dateTimePickerTheme, this, hour, minute, true); //true je za 24hourformat
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            tvVreme.setText((hourOfDay < 10 ? "0" : "") + hourOfDay + ":" + (minute < 10 ? "0" : "") + minute);
        }
    }





    ////////////////////BACKGROUND WORKER KLASA///////////////////////
    public class MyAsyncTask extends AsyncTask<String, Object, String> {

        ArrayList<String> keys, values;

        String initiativesAddUrl;

        @Override
        protected void onPreExecute() {
            setProgressBar();

            //deklaracija adrese skripte
            initiativesAddUrl = homeUrl + "initiativesAdd.php";
            //Deklaracija listi
            keys = new ArrayList<>();
            values = new ArrayList<>();
        }

        @Override
        protected String doInBackground(String... params) {
            String type = "", jsonStr;
            keys.add("tipInicijative");
            keys.add("vrstaRazlog");
            keys.add("kratakOpis");
            keys.add("lokacija");
            keys.add("datumVreme");
            keys.add("opis");
            keys.add("slika");
            keys.add("homeUrl");
            values.add(params[1]);
            values.add(params[2]);
            values.add(params[3]);
            values.add(params[4]);
            values.add(params[5]);
            values.add(params[6]);
            values.add(params[7]);
            values.add(params[8]);
            jsonStr = global.getJSON(initiativesAddUrl, true, keys, values);
            if (jsonStr.equals("ConnectTimeout")) {
                type = "Timeout";
            } else if (jsonStr.equals("Success")) {
                type = "Success";
            }

            return type;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("Success")) {
                adb = new AlertDialog.Builder(InitiativeAddActivity.this);
                adb.setTitle(getResources().getString(R.string.strAdbTitleObavestenje));
                adb.setMessage(R.string.strIAdbInicijativaSacuvana);
                adb.setPositiveButton(R.string.strAdbOK, null);
                adb.setIcon(R.drawable.adb_success);
                adb.show();
            } else if (result.equals("Timeout")) {
                adb = new AlertDialog.Builder(InitiativeAddActivity.this);
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
            }
            unsetProgressBar();
        }
    }
}
