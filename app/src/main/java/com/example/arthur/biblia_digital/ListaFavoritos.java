package com.example.arthur.biblia_digital;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ListaFavoritos extends ActionBarActivity {

    private int index;
    private ArrayList<Favorito> listaFavoritos = new ArrayList<Favorito>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_favoritos);
        setTitle("Favoritos");

        //Pegar lista de todos os favoritos do bd
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        listaFavoritos = dbHandler.listaFavoritos();

        ListView listView = (ListView) findViewById(R.id.list);
        SimpleAdapter adapter = new SimpleAdapter(this, listarFavoritos(), R.layout.favorito,
                new String[] {"livro", "versiculo"}, new int[] {R.id.livro, R.id.versiculo});
        listView.setAdapter(adapter);

        registerForContextMenu(listView);
    }

    //Método para listar os favoritos numa lista com subitem
    private List<Map<String, Object>> listarFavoritos() {

        List<Map<String, Object>> viagens = new ArrayList<>();
        ArrayList<String> listaString = new ArrayList<String>();
        Collections.sort(listaString);
        for (int i = 0; i < listaFavoritos.size(); i++){
            Map<String, Object> item = new HashMap<String, Object>();
            Favorito favorito = listaFavoritos.get(i);
            item.put("livro", favorito.getLivro() + " " + favorito.getCapitulo() + " " + (favorito.getId_versiculo()+1));
            item.put("versiculo", favorito.getVersiculo());
            viagens.add(item);
        }
        return viagens;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lista_favoritos, menu);
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

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.list) {
            ListView lv = (ListView) v;
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
            index = acmi.position;
            menu.setHeaderTitle("Opções do favorito");
            menu.add("Excluir");
        }
    }

    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
                dbHandler.excluirFavorito(listaFavoritos.get(index));
                //Atualizar a intent depois de ter excluído
                Intent intent = getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                startActivity(intent);
                break;
            case 1:
                System.out.println("Teste");
                break;
        }
        return false;
    }

}
