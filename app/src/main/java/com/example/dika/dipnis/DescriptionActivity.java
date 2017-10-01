package com.example.dika.dipnis;

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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

import static com.example.dika.dipnis.Global.dogadjajInicijativaProblem;
import static com.example.dika.dipnis.Global.homeUrl;

public class DescriptionActivity extends AppCompatActivity {

    private Global global;

    private static final int CAMERA = 0;
    private static final int GALLERY = 1;

    public static final int REQUEST_CAMERA = 1;

    public TextView tvTitle;
    public RelativeLayout rlDescription;
    public LinearLayout llVrsta, llKratakOpisRezultat, llDatumVreme;
    public TextView tvTip, tvTipBaza, tvVrsta, tvVrstaBaza, tvKratakOpisRezultat, tvKratakOpisRezultatBaza,
                    tvLokacijaBaza, tvDatumBaza, tvVremeBaza, tvOpisBaza, tvGalerijaOpis;
    public ImageView ivGalerija, ivLokacija;
    public Button btnSledeca, btnPrethodna, btnDodajSliku;
    public ConstraintLayout clProgressBar;
    public AlertDialog.Builder adb;

    public ArrayList<Bitmap> bmps;
    public int index;

    public String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarOpis);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        global = new Global();

        //inicijalizacija liste bitmapa
        bmps = new ArrayList<>();
        index = 0;

        //Postavljanje promenjivih za kontrole
        tvTitle = (TextView) findViewById(R.id.titleOpis);
        rlDescription = (RelativeLayout) findViewById(R.id.rlDescription);
        llVrsta = (LinearLayout) findViewById(R.id.DLlVrsta);
        llKratakOpisRezultat = (LinearLayout) findViewById(R.id.DLlKratakOpisRezultat);
        llDatumVreme = (LinearLayout) findViewById(R.id.DLlDatumVreme);
        tvTip = (TextView) findViewById(R.id.DTvTip);
        tvTipBaza = (TextView) findViewById(R.id.DTvTipBaza);
        tvVrsta = (TextView) findViewById(R.id.DTvVrsta);
        tvVrstaBaza = (TextView) findViewById(R.id.DTvVrstaBaza);
        tvKratakOpisRezultat = (TextView) findViewById(R.id.DTvKratakOpisRezultat);
        tvKratakOpisRezultatBaza = (TextView) findViewById(R.id.DTvKratakOpisRezultatBaza);
        tvLokacijaBaza = (TextView) findViewById(R.id.DTvLokacijaBaza);
        tvDatumBaza = (TextView) findViewById(R.id.DTvDatumBaza);
        tvVremeBaza = (TextView) findViewById(R.id.DTvVremeBaza);
        tvOpisBaza = (TextView) findViewById(R.id.DTvOpisBaza);
        tvGalerijaOpis = (TextView) findViewById(R.id.DTvGalerijaOpis);
        ivGalerija = (ImageView) findViewById(R.id.DIvGalerija);
        ivLokacija = (ImageView) findViewById(R.id.DIvLokacija);
        btnSledeca = (Button) findViewById(R.id.DBtnSledeca);
        btnPrethodna = (Button) findViewById(R.id.DBtnPrethodna);
        btnDodajSliku = (Button) findViewById(R.id.DBtnDodajSliku);
        clProgressBar = (ConstraintLayout) findViewById(R.id.DClProgressBar);

        switch (dogadjajInicijativaProblem) {
            case "dogadjaj":
                //postavljanje naslova forme
                tvTitle.setText(getResources().getText(R.string.strTitleOpisDogadjaja));
                break;
            case "inicijativa":
                //postavljanje naslova forme
                tvTitle.setText(getResources().getText(R.string.strTitleOpisInicijative));
                break;
            case "problem":
                //postavljanje naslova forme
                tvTitle.setText(getResources().getText(R.string.strTitleOpisProblema));
                break;
        }
        //prikaz opisa
        new MyAsyncTask().execute("showDetails", id);

        //klik na lokaciju
        ivLokacija.setClickable(true);
        ivLokacija.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DescriptionActivity.this, MapsActivity.class);
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
                if (tvDatumBaza.getText().toString().compareTo(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime())) > 0 && !dogadjajInicijativaProblem.equals("problem")) {
                    adb = new AlertDialog.Builder(DescriptionActivity.this);
                    adb.setTitle(getResources().getString(R.string.strAdbTitleObavestenje));
                    if (dogadjajInicijativaProblem.equals("dogadjaj"))
                        adb.setMessage(R.string.strEAdbDodajSlikuObavestenje);
                    else adb.setMessage(R.string.strIAdbDodajSlikuObavestenje);
                    adb.setPositiveButton(R.string.strAdbOK, null);
                    adb.setIcon(R.drawable.adb_obavestenje);
                } else {
                    final String[] adbItems = getResources().getStringArray(R.array.strAdbItems);
                    adb = new AlertDialog.Builder(DescriptionActivity.this);
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
            Bitmap bm = null;
            String img = "";
            if (requestCode == CAMERA) {
                //zapamti sliku u bitmapu
                bm = (Bitmap) data.getExtras().get("data");
                //konvertuje u string
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                img = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
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
                    bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                    //konvertuje u string
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                    img = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //dodaje u listu bitmapa i postavlja je u imageView
            bmps.add(bm);
            ivGalerija.setImageBitmap(bmps.get(index = bmps.size() - 1));
            //upis u bazu
            new MyAsyncTask().execute("addImage", id, img, homeUrl);

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
            Intent intent = new Intent(DescriptionActivity.this, AboutActivity.class);
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
        HashMap<String, String> hashObject;

        String showDetailsUrl, addImageUrl;

        @Override
        protected void onPreExecute() {
            setProgressBar();

            hashObject = new HashMap<>();
            //deklaracija adresa skripti
            showDetailsUrl = homeUrl + "showDetails.php";
            addImageUrl = homeUrl + "addImage.php";
            //Deklaracija listi
            keys = new ArrayList<>();
            values = new ArrayList<>();
        }

        @Override
        protected String doInBackground(String... params) {
            String type = params[0], jsonStr;
            switch (type) {
                case "showDetails":
                    keys.add("dip");
                    keys.add("id");
                    values.add(dogadjajInicijativaProblem);
                    values.add(params[1]);
                    jsonStr = global.getJSON(showDetailsUrl, keys, values);
                    if (jsonStr.equals("ConnectTimeout")) {
                        type = "Timeout";
                    } else {
                        makeEventFromJSON(jsonStr);
                    }
                    break;
                case "addImage":
                    keys.add("dip");
                    keys.add("id");
                    keys.add("slika");
                    keys.add("homeUrl");
                    values.add(dogadjajInicijativaProblem);
                    values.add(params[1]);
                    values.add(params[2]);
                    values.add(params[3]);
                    jsonStr = global.getJSON(addImageUrl, keys, values);
                    if (jsonStr.equals("ConnectTimeout")) {
                        type = "Timeout";
                    } else if (jsonStr.equals("Success")) {
                        type = "Success";
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
            if (result.equals("showDetails")) {
                rlDescription.setVisibility(View.VISIBLE);

                String tip = hashObject.get("tip");
                if (dogadjajInicijativaProblem.equals("problem")) {
                    tvTip.setText(R.string.strTvTipProblema);
                    llVrsta.setVisibility(View.GONE);
                    llKratakOpisRezultat.setVisibility(View.GONE);
                    llDatumVreme.setVisibility(View.GONE);
                }
                else {
                    if (dogadjajInicijativaProblem.equals("dogadjaj"))
                        tvTip.setText(R.string.strTvTipDogadjaja);
                    else tvTip.setText(R.string.strTvTipInicijative);
                    tvKratakOpisRezultat.setText(R.string.strTvKratakOpis);
                    switch (tip) {
                        case "Sportski":
                            tvVrsta.setText(R.string.strTvVrstaSporta);
                            tvKratakOpisRezultat.setText(R.string.strTvRezultatColon);
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
                    tvVrstaBaza.setText(hashObject.get("vrsta"));
                    tvKratakOpisRezultatBaza.setText(hashObject.get("kratakOpis"));
                    tvDatumBaza.setText(hashObject.get("datum"));
                    tvVremeBaza.setText(hashObject.get("vreme"));
                }
                tvTipBaza.setText(tip);
                tvLokacijaBaza.setText(hashObject.get("lokacija"));
                tvOpisBaza.setText(hashObject.get("opis"));

                if (bmps.size() != 0)
                    ivGalerija.setImageBitmap(bmps.get(index));
                else {
                    ivGalerija.setVisibility(View.GONE);
                    btnSledeca.setVisibility(View.GONE);
                    btnPrethodna.setVisibility(View.GONE);
                    tvGalerijaOpis.setVisibility(View.VISIBLE);
                }
            } else if (result.equals("Success")) {
                adb = new AlertDialog.Builder(DescriptionActivity.this);
                adb.setTitle(getResources().getString(R.string.strAdbTitleObavestenje));
                adb.setMessage(R.string.strAdbSlikaDodata);
                adb.setPositiveButton(R.string.strAdbOK, null);
                adb.setIcon(R.drawable.adb_success);
                adb.show();
            } else if (result.equals("Timeout")) {
                adb = new AlertDialog.Builder(DescriptionActivity.this);
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
                hashObject.clear();
                //JSON objekat
                JSONObject jsonObject = new JSONObject(jsonString);

                //dogadjaji, inicijative, problemi
                String id = jsonObject.getString("id");
                String tip = jsonObject.getString("tip");
                String lokacija = jsonObject.getString("lokacija");
                String opis = jsonObject.getString("opis");
                hashObject.put("id", id);
                hashObject.put("tip", tip);
                hashObject.put("lokacija", lokacija);
                hashObject.put("opis", opis);

                //samo dogadjaji i inicijative
                if (!dogadjajInicijativaProblem.equals("problem")) {
                    String vrsta = jsonObject.getString("vrsta");
                    String kratakOpis = jsonObject.getString("kratakOpis");
                    String datumVreme = jsonObject.getString("datumVreme");
                    datumVreme = datumVreme.substring(0, datumVreme.length() - 3);
                    String[] dv = datumVreme.split(" ");
                    hashObject.put("vrsta", vrsta);
                    hashObject.put("kratakOpis", kratakOpis);
                    hashObject.put("datum", dv[0]);
                    hashObject.put("vreme", dv[1]);
                }

                String slikeUrl = jsonObject.getString("slike");
                String[] slike = slikeUrl.split(" ");
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
