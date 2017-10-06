package com.example.dika.dipnis;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import static com.example.dika.dipnis.Global.dogadjajInicijativaProblem;
import static com.example.dika.dipnis.Global.homeUrl;

public class AddActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Global global;

    private static final int CAMERA = 0;
    private static final int GALLERY = 1;
    private static final int LOCATION = 2;

    public static final int REQUEST_CAMERA = 1;

    private Bitmap bmp;
    private boolean camera;

    private String dateNow, timeNow;

    public static TextView tvDatum, tvVreme;

    public TextView tvTitle;
    public RelativeLayout rlKratakOpis;
    public LinearLayout llDatumVreme;
    public TextView tvTip;
    public EditText etVrsta, etKratakOpis, etTim1, etTim2, etTim1Poeni, etTim2Poeni, etLokacija, etOpis;
    public TextView tvRezultat;
    public Spinner spinTip;
    public LinearLayout llDatum, llVreme, llRezultat;
    public ImageView ivSlika, ivLokacija;
    public Button btnDodajSliku, btnSacuvaj;
    public ConstraintLayout clProgressBar;
    public AlertDialog.Builder adb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarDodaj);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        dogadjajInicijativaProblem = intent.getStringExtra("type");

        global = new Global();

        camera = false;

        //Postavljanje promenjivih za kontrole
        tvDatum = (TextView) findViewById(R.id.ATvDatum);
        tvVreme = (TextView) findViewById(R.id.ATvVreme);
        tvTitle = (TextView) findViewById(R.id.titleDodaj);
        rlKratakOpis = (RelativeLayout) findViewById(R.id.ARlKratakOpis);
        llDatumVreme = (LinearLayout) findViewById(R.id.ALlDatumVreme);
        tvTip = (TextView) findViewById(R.id.ATvTip);
        etVrsta = (EditText) findViewById(R.id.AEtVrsta);
        etKratakOpis = (EditText) findViewById(R.id.AEtKratakOpis);
        etTim1 = (EditText) findViewById(R.id.AEtTim1);
        etTim2 = (EditText) findViewById(R.id.AEtTim2);
        etTim1Poeni = (EditText) findViewById(R.id.AEtTim1Poeni);
        etTim2Poeni = (EditText) findViewById(R.id.AEtTim2Poeni);
        etLokacija = (EditText) findViewById(R.id.AEtLokacija);
        etOpis = (EditText) findViewById(R.id.AEtOpis);
        tvRezultat = (TextView) findViewById(R.id.ATvRezultat);
        spinTip = (Spinner) findViewById(R.id.ASpinTip);
        llDatum = (LinearLayout) findViewById(R.id.ALlDatum);
        llVreme = (LinearLayout) findViewById(R.id.ALlVreme);
        llRezultat = (LinearLayout) findViewById(R.id.ALlRezultat);
        ivSlika = (ImageView) findViewById(R.id.AIvSlika);
        ivLokacija = (ImageView) findViewById(R.id.AIvLokacija);
        btnDodajSliku = (Button) findViewById(R.id.ABtnDodajSliku);
        btnSacuvaj = (Button) findViewById(R.id.ABtnSacuvaj);
        clProgressBar = (ConstraintLayout) findViewById(R.id.AClProgressBar);

        if (!dogadjajInicijativaProblem.equals("problem")) {
            //setovanje trenutnog datuma i vremena
            String[] dateTimeArr = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().getTime()).split(" ");
            dateNow = dateTimeArr[0];
            timeNow = dateTimeArr[1];
            tvDatum.setText(dateNow);
            tvVreme.setText(timeNow);
        }

        switch (dogadjajInicijativaProblem) {
            case "dogadjaj":
                //postavljanje naslova forme
                tvTitle.setText(R.string.strTitleDodajDogadjaj);
                //postavljanje spinnera tip
                ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.strSpinTipDogadjaja, R.layout.spinner_layout);
                adapter1.setDropDownViewResource(R.layout.spinner_item_layout);
                spinTip.setAdapter(adapter1);
                //postavljanje ostalih karakteristicnih stringova
                tvTip.setText(R.string.strTvTipDogadjaja);
                btnSacuvaj.setText(R.string.strBtnSacuvajDogadjaj);
                rlKratakOpis.setVisibility(View.VISIBLE);
                llDatumVreme.setVisibility(View.VISIBLE);
                etVrsta.setVisibility(View.VISIBLE);
                break;
            case "inicijativa":
                //postavljanje naslova forme
                tvTitle.setText(R.string.strTitleDodajInicijativu);
                //postavljanje spinnera tip
                ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.strSpinTipInicijative, R.layout.spinner_layout);
                adapter2.setDropDownViewResource(R.layout.spinner_item_layout);
                spinTip.setAdapter(adapter2);
                //postavljanje ostalih karakteristicnih stringova
                tvTip.setText(R.string.strTvTipInicijative);
                btnSacuvaj.setText(R.string.strBtnSacuvajInicijativu);
                rlKratakOpis.setVisibility(View.VISIBLE);
                llDatumVreme.setVisibility(View.VISIBLE);
                etVrsta.setVisibility(View.VISIBLE);
                tvRezultat.setVisibility(View.GONE);
                llRezultat.setVisibility(View.GONE);
                etKratakOpis.setVisibility(View.VISIBLE);
                break;
            case "problem":
                //postavljanje naslova forme
                tvTitle.setText(R.string.strTitleDodajProblem);
                btnSacuvaj.setText(R.string.strBtnSacuvajProblem);
                //postavljanje spinnera tip
                ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this, R.array.strSpinTipProblema, R.layout.spinner_layout);
                adapter3.setDropDownViewResource(R.layout.spinner_item_layout);
                spinTip.setAdapter(adapter3);
                //postavljanje ostalih karakteristicnih stringova
                tvTip.setText(R.string.strTvTipProblema);
                break;
        }

        spinTip.setOnItemSelectedListener(this);

        //klik na lokaciju
        ivLokacija.setClickable(true);
        ivLokacija.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddActivity.this, MapsActivity.class);
                intent.putExtra("markerPosition", "noPosition");
                startActivityForResult(intent, LOCATION);
            }
        });

        if (!dogadjajInicijativaProblem.equals("problem")) {
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
        }

        btnDodajSliku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adb = new AlertDialog.Builder(AddActivity.this);
                if (!dogadjajInicijativaProblem.equals("problem") && tvDatum.getText().toString().compareTo(dateNow) > 0) {
                    adb.setTitle(getResources().getString(R.string.strAdbTitleObavestenje));
                    if (dogadjajInicijativaProblem.equals("dogadjaj"))
                        adb.setMessage(R.string.strEAdbDodajSlikuObavestenje);
                    else adb.setMessage(R.string.strIAdbDodajSlikuObavestenje);
                    adb.setPositiveButton(R.string.strAdbOK, null);
                    adb.setIcon(R.drawable.adb_obavestenje);
                } else {
                    final String[] adbItems = getResources().getStringArray(R.array.strAdbItems);
                    adb.setTitle(getResources().getString(R.string.strAdbTitleSlika));
                    adb.setItems(adbItems, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            if (adbItems[i].equals("Slikaj")) {
                                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        startActivityForResult(intent, CAMERA);
                                    } else {
                                        requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                                    }
                                } else {
                                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    startActivityForResult(intent, CAMERA);
                                }
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

        btnSacuvaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String img;
                if (bmp != null) {
                    //konvertovanje slike u string
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                    img = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);

                    if (camera) {
                        File destinationFolder = new File(Environment.getExternalStorageDirectory() + "/DIPNis");
                        destinationFolder.mkdir();
                        File destination = new File(destinationFolder, "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()) + ".jpg");
                        FileOutputStream fo;
                        try {
                            destination.createNewFile();
                            fo = new FileOutputStream(destination);
                            fo.write(stream.toByteArray());
                            fo.close();
                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(destination)));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else img = "";

                //provera za unos u tekst polja
                ArrayList<EditText> inputCheck = new ArrayList<>();
                EditText item = null;
                String tip = spinTip.getSelectedItem().toString();
                if (dogadjajInicijativaProblem.equals("problem"))
                    inputCheck.addAll(Arrays.asList(etLokacija, etOpis));
                else if (tip.equals("Sportski"))
                    inputCheck.addAll(Arrays.asList(etVrsta, etLokacija, etOpis));
                else
                    inputCheck.addAll(Arrays.asList(etVrsta, etKratakOpis, etLokacija, etOpis));
                for (EditText itemTmp : inputCheck) {
                    if (itemTmp.getText().toString().equals("")) {
                        item = itemTmp;
                        break;
                    }
                }
                if (item == null) {
                    String lokacija = etLokacija.getText().toString();
                    String opis = etOpis.getText().toString();
                    if (!dogadjajInicijativaProblem.equals("problem")) {
                        String vrsta = etVrsta.getText().toString();
                        String datumVreme = tvDatum.getText().toString() + " " + tvVreme.getText().toString() + ":00";
                        String kratakOpis;
                        if (tip.equals("Sportski")) {
                            String tim1 = etTim1.getText().toString();
                            String tim2 = etTim2.getText().toString();
                            String tim1Poeni = etTim1Poeni.getText().toString();
                            String tim2Poeni = etTim2Poeni.getText().toString();
                            kratakOpis = tim1 + " " + tim1Poeni + ":" + tim2Poeni + " " + tim2;
                        } else {
                            kratakOpis = etKratakOpis.getText().toString();
                        }
                        if (bmp != null && tvDatum.getText().toString().compareTo(dateNow) > 0) {
                            adb = new AlertDialog.Builder(AddActivity.this);
                            adb.setTitle(getResources().getString(R.string.strAdbTitleObavestenje));
                            if (dogadjajInicijativaProblem.equals("dogadjaj"))
                                adb.setMessage(R.string.strEAdbDogadjajSacuvanObavestenje);
                            else adb.setMessage(R.string.strIAdbInicijativaSacuvanaObavestenje);
                            adb.setPositiveButton(R.string.strAdbOK, null);
                            adb.setIcon(R.drawable.adb_obavestenje);
                            adb.show();
                            ivSlika.setVisibility(View.GONE);
                            bmp = null;
                            ivSlika.setImageBitmap(bmp);
                        } else {
                            new MyAsyncTask().execute("add", tip, lokacija, opis, img, homeUrl, vrsta, kratakOpis, datumVreme);
                            initialState();
                        }
                    } else {
                        new MyAsyncTask().execute("add", tip, lokacija, opis, img, homeUrl);
                        initialState();
                    }
                } else {
                    adb = new AlertDialog.Builder(AddActivity.this);
                    adb.setTitle(getResources().getString(R.string.strAdbTitleObavestenje));
                    adb.setMessage(getResources().getString(R.string.strAdbObavezanUnos) + " " + (item.getHint().toString().equals("") ? "Rezultat" : item.getHint().toString()) + ".");
                    adb.setPositiveButton(R.string.strAdbOK, null);
                    adb.setIcon(R.drawable.adb_obavestenje);
                    adb.show();
                }
            }
        });
    }

    /////////////////POCETNO STANJE ELEMENATA/////////////////////
    public void initialState() {
        spinTip.setSelection(0);
        etVrsta.setText("");
        etTim1.setText("");
        etTim1Poeni.setText("");
        etTim2.setText("");
        etTim2Poeni.setText("");
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
            //rezultat kamere
            if (requestCode == CAMERA) {
                bmp = (Bitmap) data.getExtras().get("data");
                ivSlika.setImageBitmap(bmp);
                camera = true;
                ivSlika.setVisibility(View.VISIBLE);
            }
            //rezultat galerije
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, CAMERA);
                    }
                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
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
            Intent intent = new Intent(AddActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    ////////////////EVENT ZA KLIK NA ITEM NEKOG SPINERA//////////////////////
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (spinTip.getSelectedItem().toString()) {
            case "Sportski":
                etVrsta.setHint(R.string.strEtHintVrstaSporta);
                tvRezultat.setVisibility(View.VISIBLE);
                llRezultat.setVisibility(View.VISIBLE);
                etKratakOpis.setVisibility(View.GONE);
                break;
            case "Koncerti":
                etVrsta.setHint(R.string.strEtHintIzvodjacGrupa);
                tvRezultat.setVisibility(View.GONE);
                llRezultat.setVisibility(View.GONE);
                etKratakOpis.setVisibility(View.VISIBLE);
                break;
            case "Ostali":
                etVrsta.setHint(R.string.strEtHintVrstaDogadjaja);
                tvRezultat.setVisibility(View.GONE);
                llRezultat.setVisibility(View.GONE);
                etKratakOpis.setVisibility(View.VISIBLE);
                break;
            case "Sportske":
                etVrsta.setHint(R.string.strEtHintVrstaSporta);
                break;
            case "Protesti":
                etVrsta.setHint(R.string.strEtHintRazlogProtesta);
                break;
            case "Humanitarne akcije":
                etVrsta.setHint(R.string.strEtHintVrstaHumanitarneAkcije);
                break;
            case "Ostale":
                etVrsta.setHint(R.string.strEtHintVrstaInicijative);
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

        String addUrl;

        @Override
        protected void onPreExecute() {
            setProgressBar();

            //deklaracija adrese skripte
            addUrl = homeUrl + "add.php";
            //Deklaracija listi
            keys = new ArrayList<>();
            values = new ArrayList<>();
        }

        @Override
        protected String doInBackground(String... params) {
            String type = "", jsonStr;
            keys.add("dip");
            keys.add("tip");
            keys.add("lokacija");
            keys.add("opis");
            keys.add("slika");
            keys.add("homeUrl");
            values.add(dogadjajInicijativaProblem);
            values.add(params[1]);
            values.add(params[2]);
            values.add(params[3]);
            values.add(params[4]);
            values.add(params[5]);
            if (!dogadjajInicijativaProblem.equals("problem")) {
                keys.add("vrsta");
                keys.add("kratakOpis");
                keys.add("datumVreme");
                values.add(params[6]);
                values.add(params[7]);
                values.add(params[8]);
            }
            jsonStr = global.getJSON(addUrl, keys, values);
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
                adb = new AlertDialog.Builder(AddActivity.this);
                adb.setTitle(getResources().getString(R.string.strAdbTitleObavestenje));
                switch (dogadjajInicijativaProblem) {
                    case "dogadjaj":
                        adb.setMessage(R.string.strEAdbDogadjajSacuvan);
                        break;
                    case "inicijativa":
                        adb.setMessage(R.string.strIAdbInicijativaSacuvana);
                        break;
                    case "problem":
                        adb.setMessage(R.string.strPAdbProblemSacuvan);
                        break;
                }
                adb.setPositiveButton(R.string.strAdbOK, null);
                adb.setIcon(R.drawable.adb_success);
                adb.show();
            } else if (result.equals("Timeout")) {
                adb = new AlertDialog.Builder(AddActivity.this);
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
