package com.example.dika.dipnis;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    //public static final String homeUrl = "http://24.135.176.151:8080/dipNisServer/";
    public static final String homeUrl = "http://160.99.9.136/dipnis/";


    public Button btnDogadjaji, btnInicijative, btnProblemi, btnPregledDogadjaja, btnPregledInicijativa,
            btnPregledProblema, btnDodajDogadjaj, btnDodajInicijativu, btnDodajProblem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarPocetna);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        btnDogadjaji = (Button) findViewById(R.id.btnDogadjaji);
        btnInicijative = (Button) findViewById(R.id.btnInicijative);
        btnProblemi = (Button) findViewById(R.id.btnProblemi);
        btnPregledDogadjaja = (Button) findViewById(R.id.btnPregledDogadjaja);
        btnPregledInicijativa = (Button) findViewById(R.id.btnPregledInicijativa);
        btnPregledProblema = (Button) findViewById(R.id.btnPregledProblema);
        btnDodajDogadjaj = (Button) findViewById(R.id.btnDodajDogadjaj);
        btnDodajInicijativu = (Button) findViewById(R.id.btnDodajInicijativu);
        btnDodajProblem = (Button) findViewById(R.id.btnDodajProblem);

        btnDogadjaji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnPregledDogadjaja.setVisibility(View.VISIBLE);
                btnDodajDogadjaj.setVisibility(View.VISIBLE);
                btnDogadjaji.setVisibility(View.INVISIBLE);
                btnPregledInicijativa.setVisibility(View.INVISIBLE);
                btnDodajInicijativu.setVisibility(View.INVISIBLE);
                btnInicijative.setVisibility(View.VISIBLE);
                btnPregledProblema.setVisibility(View.INVISIBLE);
                btnDodajProblem.setVisibility(View.INVISIBLE);
                btnProblemi.setVisibility(View.VISIBLE);
            }
        });

        btnInicijative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnPregledDogadjaja.setVisibility(View.INVISIBLE);
                btnDodajDogadjaj.setVisibility(View.INVISIBLE);
                btnDogadjaji.setVisibility(View.VISIBLE);
                btnPregledInicijativa.setVisibility(View.VISIBLE);
                btnDodajInicijativu.setVisibility(View.VISIBLE);
                btnInicijative.setVisibility(View.INVISIBLE);
                btnPregledProblema.setVisibility(View.INVISIBLE);
                btnDodajProblem.setVisibility(View.INVISIBLE);
                btnProblemi.setVisibility(View.VISIBLE);
            }
        });

        btnProblemi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnPregledDogadjaja.setVisibility(View.INVISIBLE);
                btnDodajDogadjaj.setVisibility(View.INVISIBLE);
                btnDogadjaji.setVisibility(View.VISIBLE);
                btnPregledInicijativa.setVisibility(View.INVISIBLE);
                btnDodajInicijativu.setVisibility(View.INVISIBLE);
                btnInicijative.setVisibility(View.VISIBLE);
                btnPregledProblema.setVisibility(View.VISIBLE);
                btnDodajProblem.setVisibility(View.VISIBLE);
                btnProblemi.setVisibility(View.INVISIBLE);
            }
        });

        btnPregledDogadjaja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EventReviewActivity.class);
                startActivity(intent);
            }
        });

        btnDodajDogadjaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EventAddActivity.class);
                startActivity(intent);
            }
        });

        btnPregledInicijativa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, InitiativeReviewActivity.class);
                startActivity(intent);
            }
        });

        btnDodajInicijativu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, InitiativeAddActivity.class);
                startActivity(intent);
            }
        });

        btnPregledProblema.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ProblemReviewActivity.class);
                startActivity(intent);
            }
        });

        btnDodajProblem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ProblemAddActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        btnPregledDogadjaja.setVisibility(View.INVISIBLE);
        btnDodajDogadjaj.setVisibility(View.INVISIBLE);
        btnDogadjaji.setVisibility(View.VISIBLE);
        btnPregledInicijativa.setVisibility(View.INVISIBLE);
        btnDodajInicijativu.setVisibility(View.INVISIBLE);
        btnInicijative.setVisibility(View.VISIBLE);
        btnPregledProblema.setVisibility(View.INVISIBLE);
        btnDodajProblem.setVisibility(View.INVISIBLE);
        btnProblemi.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.menuItem) {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
