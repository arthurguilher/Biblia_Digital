package com.example.arthur.biblia_digital;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class ListaHistorico extends ActionBarActivity {

    private ArrayList<Historico> listaHistorico = new ArrayList<Historico>();
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_historico);

        //Pega a lista do histórico do banco de dados
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        listaHistorico = dbHandler.listaHistorico();

        ArrayList<String> listaString = new ArrayList<String>();
        for (int i = 0; i < listaHistorico.size(); i++){
            Historico historico = listaHistorico.get(i);
            listaString.add(historico.getLivro() + " - Capítulo " + historico.getCapitulo());
        }

        listView = (ListView) findViewById(R.id.list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, listaString);
        listView.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lista_historico, menu);
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
