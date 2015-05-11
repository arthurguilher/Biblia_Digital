package com.example.arthur.biblia_digital;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Checkable;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ListaFavoritos extends ActionBarActivity {

    private int index;
    private ArrayList<Favorito> listaFavoritos = new ArrayList<Favorito>();
    private final Context context = this;
    private ArrayList <Favorito> favoritosParaExcluir = new ArrayList<Favorito>();
    public static ArrayList<View> views = new ArrayList<View>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_favoritos);
        setTitle("  Favoritos");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.icone_favoritos);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        //Pegar lista de todos os favoritos do bd
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        listaFavoritos = dbHandler.listaFavoritos();

        final ListView listView = (ListView) findViewById(R.id.list);
        SimpleAdapter adapter = new SimpleAdapter(this, listarFavoritos(), R.layout.favorito,
                new String[] {"livro", "versiculo"}, new int[] {R.id.livro, R.id.versiculo});
        listView.setAdapter(adapter);

        //listView.setAdapter(adapter);
        //listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (views.contains(view)){
                        views.remove(view);
                        favoritosParaExcluir.remove(listaFavoritos.get(position));
                    } else {
                        favoritosParaExcluir.add(listaFavoritos.get(position));
                        views.add(view);
                    }
                for (int i = 0; i < views.size(); i++){
                    views.get(i).setSelected(true);
                }
            }
        });

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


        if (id == R.id.action_settings) {
            if (favoritosParaExcluir.isEmpty()){
                    Toast.makeText(context, "Não há nenhum favorito selecionado", Toast.LENGTH_LONG).show();
            } else {
                alertMessage();
            }
            return true;
        }

        if (item.getItemId() == R.id.action_example) {
            //startVoiceRecognitionActivity();
            //Toast.makeText(getActivity(), "Example action.", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.list) {
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
            index = acmi.position;
            menu.setHeaderTitle("Opções do favorito");
            menu.add("Excluir");
            menu.add("Abrir capítulo");
        }
    }

    public boolean onContextItemSelected(MenuItem item) {
        Intent intent;
        if (item.getTitle().equals("Excluir")){
            MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
            dbHandler.excluirFavorito(listaFavoritos.get(index));
            //Atualizar a intent depois de ter excluído
            intent = getIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            finish();
            startActivity(intent);
        }
        if (item.getTitle().equals("Abrir capítulo")){

            for (int temp = 0; temp < MainActivity.nodeLivros.getLength(); temp++) {
                Node nNode = MainActivity.nodeLivros.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    if (eElement.getAttribute("n").equalsIgnoreCase(listaFavoritos.get(index).getLivro())) {
                        ListaCapitulos.nodeCapitulos = eElement.getChildNodes();
                    }
                }
            }


            intent = new Intent(context, ListaVersiculos.class);
            Bundle params = new Bundle();
            params.putInt("busca", 1);
            params.putInt("capitulo", listaFavoritos.get(index).getCapitulo());
            params.putString("livro", listaFavoritos.get(index).getLivro());
            params.putInt("idVersiculo", listaFavoritos.get(index).getId_versiculo());
            params.putInt("qntCapitulos", MainActivity.qntCapitulos(listaFavoritos.get(index).getLivro()));
            intent.putExtras(params);
            startActivity(intent);
        }
        /*switch (item.getItemId()) {
            case 0:
                MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
                dbHandler.excluirFavorito(listaFavoritos.get(index));
                //Atualizar a intent depois de ter excluído
                intent = getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                startActivity(intent);
                break;
            case 1:
                intent = new Intent(context, ListaVersiculos.class);
                Bundle params = new Bundle();
                params.putInt("capitulo", index + 1);
                intent.putExtras(params);
                startActivity(intent);
                break;
        }*/
        return false;
    }

    public void alertMessage() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                            MyDBHandler db = new MyDBHandler(context, null, null, 1);
                            db.excluirFavoritos(favoritosParaExcluir);
                            Intent intent = getIntent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            finish();
                            startActivity(intent);
                              break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Deseja excluir os favoritos selecionados?").setPositiveButton("Sim", dialogClickListener).setNegativeButton("Não", dialogClickListener).show();
    }

    public void ShowDialog() {
        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        final SeekBar seek = new SeekBar(this);
        seek.setMax(30);

        popDialog.setIcon(android.R.drawable.btn_star_big_on);
        popDialog.setTitle("Ajuste o tamanho da fonte");
        popDialog.setView(seek);

        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Do something here with new value
                //txtView.setText("Value of : " + progress);
            }

            public void onStartTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub

            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }
        });


        // Button OK
        popDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }

                });


        popDialog.create();
        popDialog.show();


        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int p=0;
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
// TODO Auto-generated method stub
                if (p < 30) {
                    p = 30;
                    seek.setProgress(p);
                }
                Toast.makeText(getBaseContext(), String.valueOf(p), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
// TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
// TODO Auto-generated method stub
                p = progress;
                for (int i = 0; i < listaFavoritos.size(); i++) {
                    TextView t1 = (TextView) findViewById(R.id.livro);
                    TextView t2 = (TextView) findViewById(R.id.versiculo);
                    t1.setTextSize(p);
                    t2.setTextSize(p);
                }
            }
        });
    }

}
