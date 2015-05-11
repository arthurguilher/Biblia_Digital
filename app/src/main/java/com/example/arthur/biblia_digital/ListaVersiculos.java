package com.example.arthur.biblia_digital;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;


public class ListaVersiculos extends ActionBarActivity {

    public static int capitulo;
    private int idVersiculo;
    public static String livro;
    private ArrayList<String> listaVersiculos = new ArrayList<String>();
    private final Context context = this;
    private int paginas;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private NodeList nodesLivros = MainActivity.nodeLivros;
    private NodeList nodesCapitulos;
    public static int busca = 0;
    public static int versiculoFavorito;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_versiculos);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.icone_biblia_aberta);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());

        Intent intent = getIntent();
        Bundle params = intent.getExtras();
        capitulo = params.getInt("capitulo");
        livro = params.getString("livro");
        paginas = params.getInt("qntCapitulos");
        busca = params.getInt("busca");
        setTitle("  " + livro + " - Capítulo " + capitulo);

        if (busca == 1) {
            nodesCapitulos = MainActivity.nodeCapitulos;
            versiculoFavorito = params.getInt("idVersiculo");
        } else {
            nodesCapitulos = ListaCapitulos.nodeCapitulos;
        }

        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(capitulo-1);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setTitle("  " + livro + " - Capítulo " + (mPager.getCurrentItem() + 1));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


    }

    public ArrayList<String> listarVersiculo(int capitulo) {
        ArrayList<String> listaAuxiliar = new ArrayList<String>();
        for (int temp = 0; temp < nodesLivros.getLength(); temp++) {
            Node nNode = nodesLivros.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                if (eElement.getAttribute("n").equalsIgnoreCase(livro.toLowerCase())) {
                    for (int i = 0; i < nodesCapitulos.getLength(); i++) {
                        Node nNode2 = nodesCapitulos.item(i);
                        if (nNode2.getNodeType() == Node.ELEMENT_NODE) {
                            Element eElement2 = (Element) nNode2;
                            NodeList versiculos = eElement2.getChildNodes();
                            if (Integer.parseInt(eElement2.getAttribute("n")) == capitulo) {
                                for (int j = 0; j < versiculos.getLength(); j++) {
                                    Node nNode3 = versiculos.item(j);
                                    if (nNode3.getNodeType() == Node.ELEMENT_NODE) {
                                        Element eElement3 = (Element) nNode3;
                                        listaVersiculos.add(eElement2.getElementsByTagName("v").item(Integer.parseInt(eElement3.getAttribute("n")) - 1).getTextContent());
                                        listaAuxiliar.add(eElement3.getAttribute("n") + " - " + eElement2.getElementsByTagName("v").item(Integer.parseInt(eElement3.getAttribute("n")) - 1).getTextContent());
                                    }
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }
        return listaAuxiliar;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lista_versiculos, menu);
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

    /*public void alertMessage() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked
                        MyDBHandler db = new MyDBHandler(context, null, null, 1);
                        Favorito favorito = new Favorito(livro, capitulo, listaVersiculos.get(idVersiculo), idVersiculo);
                        db.adicionarFavorito(favorito);
                        Toast.makeText(context, "O versículo foi adicionado aos favoritos", Toast.LENGTH_LONG).show();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE: // No button clicked // do nothing
                        //Toast.makeText(context, "No Clicked", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Deseja adicionar este versículo aos favoritos?").setPositiveButton("Sim", dialogClickListener).setNegativeButton("Não", dialogClickListener).show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.list) {
            ListView lv = (ListView) v;
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
            //YourObject obj = (YourObject) lv.getItemAtPosition(acmi.position);

            menu.setHeaderTitle("Opções do versículo");
            menu.add(lv.getItemAtPosition(acmi.position).toString());
            System.out.println(acmi.position);
            //menu.add(obj.name);
        }
    }
*/
    @Override
    public void onBackPressed() {
        //if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        //} else {
            // Otherwise, select the previous step.
          //  mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        //}
    }


    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            MyDBHandler db = new MyDBHandler(context, null, null, 1);
            if (db.ultimoHistorico().getCapitulo() != mPager.getCurrentItem() + 1 && db.ultimoHistorico().getLivro() != livro) {
             db.adicionarHistorico(new Historico(livro, mPager.getCurrentItem() + 1));
            }

            return new ListaVersiculosFragment(listarVersiculo(position+1), listaVersiculos, livro, position+1);
        }

        @Override
        public int getCount() {
            return paginas;
        }
    }

}



