package com.example.arthur.biblia_digital;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ListaAZ extends ActionBarActivity {

    public static String livro;
    private ListView listView;
    private final Context context = this;
    public static ArrayList<String> listaOrdenada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_az);
        setTitle("  Livros de A-Z");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.icone_az);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        Intent intent = getIntent();
        Bundle params = intent.getExtras();
        listaOrdenada = params.getStringArrayList("livros");
        Collections.sort(listaOrdenada);
        listView = (ListView) findViewById(R.id.listAZ);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, listaOrdenada);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, ListaCapitulos.class);
                Bundle params = new Bundle();
                params.putString("livro", (String) listView.getItemAtPosition(position));
                params.putInt("ordenado", 1);
                intent.putExtras(params);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lista_az, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
