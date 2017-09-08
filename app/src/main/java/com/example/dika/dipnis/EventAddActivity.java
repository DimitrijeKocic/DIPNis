package com.example.dika.dipnis;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Base64;
import android.view.LayoutInflater;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EventAddActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final int CAMERA = 0;
    private static final int GALLERY = 1;

    String dateNow, timeNow;

    public static TextView tvDatum, tvVreme;
    public EditText etVrstaIzvodjac, etKratakOpis;
    public TextView tvRezultat;
    public Spinner spinTipDogadjaja;
    public LinearLayout llDatum, llVreme, llRezultat;
    public ImageView ivSlika;
    public Button btnDodajSliku;
    public ConstraintLayout clProgressBar;
    public AlertDialog.Builder adb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarDodajDogadjaj);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Postavljanje promenjivih za kontrole
        tvDatum = (TextView) findViewById(R.id.EATvDatum);
        tvVreme = (TextView) findViewById(R.id.EATvVreme);
        etVrstaIzvodjac = (EditText) findViewById(R.id.EAEtVrstaIzvodjac);
        etKratakOpis = (EditText) findViewById(R.id.EAEtKratakOpis);
        tvRezultat = (TextView) findViewById(R.id.EATvRezultat);
        spinTipDogadjaja = (Spinner) findViewById(R.id.EASpinTipDogadjaja);
        llDatum = (LinearLayout) findViewById(R.id.EALlDatum);
        llVreme = (LinearLayout) findViewById(R.id.EALlVreme);
        llRezultat = (LinearLayout) findViewById(R.id.EALlRezultat);
        ivSlika = (ImageView) findViewById(R.id.EAIvSlika);
        btnDodajSliku = (Button) findViewById(R.id.EABtnDodajSliku);
        clProgressBar = (ConstraintLayout) findViewById(R.id.EAClProgressBar);

        //setovanje trenutnog datuma i vremena
        String[] dateTimeArr = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().getTime()).split(" ");
        dateNow = dateTimeArr[0];
        timeNow = dateTimeArr[1];
        tvDatum.setText(dateNow);
        tvVreme.setText(timeNow);

        //Postavljanje itema spinera za tip dogadjaja
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.strEASpinTipDogadjaja, R.layout.spinner_layout);
        adapter.setDropDownViewResource(R.layout.spinner_item_layout);
        spinTipDogadjaja.setAdapter(adapter);

        spinTipDogadjaja.setOnItemSelectedListener(this);

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
                adb = new AlertDialog.Builder(EventAddActivity.this);
                if (tvDatum.getText().toString().compareTo(dateNow) > 0) {
                    adb.setTitle(getResources().getString(R.string.strEAAdbTitleObavestenje));
                    adb.setMessage(R.string.strEADodajSlikuObavestenje);
                } else {
                    final String[] adbItems = getResources().getStringArray(R.array.strEAAdbItems);
                    adb.setTitle(getResources().getString(R.string.strEAAdbTitleSlika));
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
                ivSlika.setImageBitmap((Bitmap) data.getExtras().get("data"));
            }
            else if (requestCode == GALLERY) {
                try {
                    ivSlika.setImageBitmap(MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            ivSlika.setVisibility(View.VISIBLE);
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
            Intent intent = new Intent(EventAddActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    ////////////////EVENT ZA KLIK NA ITEM NEKOG SPINERA//////////////////////
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (spinTipDogadjaja.getSelectedItem().toString()) {
            case "Sportski":
                etVrstaIzvodjac.setHint(R.string.strEAEtHintVrstaSporta);
                tvRezultat.setVisibility(View.VISIBLE);
                llRezultat.setVisibility(View.VISIBLE);
                etKratakOpis.setVisibility(View.GONE);
                break;
            case "Koncerti":
                etVrstaIzvodjac.setHint(R.string.strEAEtHintIzvodjacGrupa);
                tvRezultat.setVisibility(View.GONE);
                llRezultat.setVisibility(View.GONE);
                etKratakOpis.setVisibility(View.VISIBLE);
                break;
            case "Ostali":
                etVrstaIzvodjac.setHint(R.string.strEAEtHintVrstaDogadjaja);
                tvRezultat.setVisibility(View.GONE);
                llRezultat.setVisibility(View.GONE);
                etKratakOpis.setVisibility(View.VISIBLE);
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
            tvDatum.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
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
            tvVreme.setText(hourOfDay + ":" + minute);
        }
    }
}
