package com.example.dika.dipnis;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import static com.example.dika.dipnis.Global.homeUrl;

public class EventDescriptionActivity extends AppCompatActivity {

    private Global global;

    private static final int CAMERA = 0;
    private static final int GALLERY = 1;

    public static final int REQUEST_CAMERA = 1;

    public TextView tvTipDogadjajaBaza, tvVrstaIzvodjac, tvVrstaIzvodjacBaza, tvKratakOpisRezultat,
                    tvKratakOpisRezultatBaza, tvLokacijaBaza, tvDatumBaza, tvVremeBaza, tvOpisBaza;
    public TextView tvGalerijaOpis;
    public ImageView ivGalerija, ivLokacija;
    public Button btnSledeca, btnPrethodna, btnDodajSliku;
    public ConstraintLayout clProgressBar;
    public AlertDialog.Builder adb;

    public ArrayList<Bitmap> bmps;
    public int index;

    public String idDog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_description);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarOpisDogadjaja);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        global = new Global();

        //inicijalizacija liste bitmapa
        bmps = new ArrayList<>();
        index = 0;

        //Postavljanje promenjivih za kontrole
        tvTipDogadjajaBaza = (TextView) findViewById(R.id.EDTvTipDogadjajaBaza);
        tvVrstaIzvodjac = (TextView) findViewById(R.id.EDTvVrstaIzvodjac);
        tvVrstaIzvodjacBaza = (TextView) findViewById(R.id.EDTvVrstaIzvodjacBaza);
        tvKratakOpisRezultat = (TextView) findViewById(R.id.EDTvKratakOpisRezultat);
        tvKratakOpisRezultatBaza = (TextView) findViewById(R.id.EDTvKratakOpisRezultatBaza);
        tvLokacijaBaza = (TextView) findViewById(R.id.EDTvLokacijaBaza);
        tvDatumBaza = (TextView) findViewById(R.id.EDTvDatumBaza);
        tvVremeBaza = (TextView) findViewById(R.id.EDTvVremeBaza);
        tvOpisBaza = (TextView) findViewById(R.id.EDTvOpisBaza);
        tvGalerijaOpis = (TextView) findViewById(R.id.EDTvGalerijaOpis);
        ivGalerija = (ImageView) findViewById(R.id.EDIvGalerija);
        ivLokacija = (ImageView) findViewById(R.id.EDIvLokacija);
        btnSledeca = (Button) findViewById(R.id.EDBtnSledeca);
        btnPrethodna = (Button) findViewById(R.id.EDBtnPrethodna);
        btnDodajSliku = (Button) findViewById(R.id.EDBtnDodajSliku);
        clProgressBar = (ConstraintLayout) findViewById(R.id.EDClProgressBar);

        Intent intent = getIntent();
        idDog = intent.getStringExtra("idDogadjaja");
        //prikaz svih dogadjaja
        new MyAsyncTask().execute("showDetails", idDog);

        //klik na lokaciju
        ivLokacija.setClickable(true);
        ivLokacija.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventDescriptionActivity.this, MapsActivity.class);
                intent.putExtra("markerPosition", tvLokacijaBaza.getText().toString());
                startActivity(intent);
            }
        });

        btnSledeca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bmps.size() != 0) {
                    if (index < bmps.size() - 1)
                        ivGalerija.setImageBitmap(bmps.get(++index));
                    else ivGalerija.setImageBitmap(bmps.get(index = 0));
                }
            }
        });

        btnPrethodna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bmps.size() != 0) {
                    if (index > 0)
                        ivGalerija.setImageBitmap(bmps.get(--index));
                    else ivGalerija.setImageBitmap(bmps.get(index = bmps.size() - 1));
                }
            }
        });

        btnDodajSliku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvDatumBaza.getText().toString().compareTo(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime())) > 0) {
                    adb = new AlertDialog.Builder(EventDescriptionActivity.this);
                    adb.setTitle(getResources().getString(R.string.strAdbTitleObavestenje));
                    adb.setMessage(R.string.strEAdbDodajSlikuObavestenje);
                    adb.setPositiveButton(R.string.strAdbOK, null);
                    adb.setIcon(R.drawable.adb_obavestenje);
                } else {
                    final String[] adbItems = getResources().getStringArray(R.array.strAdbItems);
                    adb = new AlertDialog.Builder(EventDescriptionActivity.this);
                    adb.setTitle(getResources().getString(R.string.strAdbTitleSlika));
                    adb.setItems(adbItems, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            if (adbItems[i].equals("Slikaj")) {
                                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        startActivityForResult(intent, CAMERA);
                                    } else {
                                        requestPermissions(new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA);
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
    }

    /////////////////REZULTAT KAMERE ILI GALERIJE////////////////////
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == CAMERA) {
                //zapamti sliku u bitmapu
                Bitmap bm = (Bitmap) data.getExtras().get("data");
                //dodaje u listu bitmapa i postavlja je u imageView
                bmps.add(bm);
                ivGalerija.setImageBitmap(bmps.get(index = bmps.size() - 1));
                //konvertuje u string
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                String img = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
                //upis u bazu
                new MyAsyncTask().execute("addImage", idDog, img, homeUrl);
                //upis u lokalno skladiste
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
            else if (requestCode == GALLERY) {
                try {
                    //zapamti sliku u bitmapu
                    Bitmap bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                    //dodaje u listu bitmapa i postavlja je u imageView
                    bmps.add(bm);
                    ivGalerija.setImageBitmap(bmps.get(index = bmps.size() - 1));
                    //konvertuje u string i poziva asyncTask za upis u bazu
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                    String img = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
                    new MyAsyncTask().execute("addImage", idDog, img, homeUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            ivGalerija.setVisibility(View.VISIBLE);
            btnSledeca.setVisibility(View.VISIBLE);
            btnPrethodna.setVisibility(View.VISIBLE);
            tvGalerijaOpis.setVisibility(View.GONE);
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
            Intent intent = new Intent(EventDescriptionActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
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





    ////////////////////BACKGROUND WORKER KLASA///////////////////////
    public class MyAsyncTask extends AsyncTask<String, Object, String> {

        ArrayList<String> keys, values;
        HashMap<String, String> event;

        String eventsShowDetailsUrl, eventsAddImageUrl;

        @Override
        protected void onPreExecute() {
            setProgressBar();

            event = new HashMap<>();
            //deklaracija adresa skripti
            eventsShowDetailsUrl = homeUrl + "eventsShowDetails.php";
            eventsAddImageUrl = homeUrl + "eventsAddImage.php";
            //Deklaracija listi
            keys = new ArrayList<>();
            values = new ArrayList<>();

        }

        @Override
        protected String doInBackground(String... params) {
            String type = params[0], jsonStr;
            if (type.equals("showDetails")) {
                keys.add("id");
                values.add(params[1]);
                jsonStr = global.getJSON(eventsShowDetailsUrl, true, keys, values);
                if (jsonStr.equals("ConnectTimeout")) {
                    type = "Timeout";
                } else {
                    makeEventFromJSON(jsonStr);
                }
            }
            else if (params[0].equals("addImage")) {
                keys.add("id");
                keys.add("slika");
                keys.add("homeUrl");
                values.add(params[1]);
                values.add(params[2]);
                values.add(params[3]);
                jsonStr = global.getJSON(eventsAddImageUrl, true, keys, values);
                if (jsonStr.equals("ConnectTimeout")) {
                    type = "Timeout";
                } else if (jsonStr.equals("Success")) {
                    type = "Success";
                }
            }
            return type;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("showDetails")) {
                String tip = event.get("tipDogadjaja");

                if (tip.equals("Sportski")) {
                    tvVrstaIzvodjac.setText(R.string.strEREDTvVrstaSporta);
                    tvKratakOpisRezultat.setText(R.string.strEDTvRezultat);
                } else {
                    tvKratakOpisRezultat.setText(R.string.strEDTvKratakOpis);
                    if (tip.equals("Koncerti")) {
                        tvVrstaIzvodjac.setText(R.string.strEREDTvIzvodjacGrupa);
                    } else {
                        tvVrstaIzvodjac.setText(R.string.strEREDTvVrstaDogadjaja);
                    }
                }

                tvTipDogadjajaBaza.setText(tip);
                tvVrstaIzvodjacBaza.setText(event.get("vrstaIzvodjac"));
                tvKratakOpisRezultatBaza.setText(event.get("kratakOpis"));
                tvLokacijaBaza.setText(event.get("lokacija"));
                tvDatumBaza.setText(event.get("datum"));
                tvVremeBaza.setText(event.get("vreme"));
                tvOpisBaza.setText(event.get("opis"));

                if (bmps.size() != 0)
                    ivGalerija.setImageBitmap(bmps.get(index));
                else {
                    ivGalerija.setVisibility(View.GONE);
                    btnSledeca.setVisibility(View.GONE);
                    btnPrethodna.setVisibility(View.GONE);
                    tvGalerijaOpis.setVisibility(View.VISIBLE);
                }
            } else if (result.equals("Success")) {
                adb = new AlertDialog.Builder(EventDescriptionActivity.this);
                adb.setTitle(getResources().getString(R.string.strAdbTitleObavestenje));
                adb.setMessage(R.string.strAdbSlikaDodata);
                adb.setPositiveButton(R.string.strAdbOK, null);
                adb.setIcon(R.drawable.adb_success);
                adb.show();
            } else if (result.equals("Timeout")) {
                adb = new AlertDialog.Builder(EventDescriptionActivity.this);
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

        public void makeEventFromJSON(String jsonString) {
            try {
                //brisanje stare liste
                event.clear();
                //JSON objekat
                JSONObject detalji = new JSONObject(jsonString);

                String id = detalji.getString("idDogadjaja");
                String tipDogadjaja = detalji.getString("tipDogadjaja");
                String vrstaIzvodjac = detalji.getString("vrstaIzvodjac");
                String kratakOpis = detalji.getString("kratakOpis");
                String lokacija = detalji.getString("lokacija");
                String datumVreme = detalji.getString("datumVreme");
                datumVreme = datumVreme.substring(0, datumVreme.length() - 3);
                String[] dv = datumVreme.split(" ");
                String opis = detalji.getString("opis");
                String slikeUrl = detalji.getString("slike");
                String[] slike = slikeUrl.split(" ");

                //kreiranje key.value parova za svaki dogadjaj
                event.put("id", id);
                event.put("tipDogadjaja", tipDogadjaja);
                event.put("vrstaIzvodjac", vrstaIzvodjac);
                event.put("kratakOpis", kratakOpis);
                event.put("lokacija", lokacija);
                event.put("datum", dv[0]);
                event.put("vreme", dv[1]);
                event.put("opis", opis);

                for (String sl : slike) {
                    URL url = null;
                    try {
                        url = new URL(sl);
                        bmps.add(BitmapFactory.decodeStream(url.openConnection().getInputStream()));
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
