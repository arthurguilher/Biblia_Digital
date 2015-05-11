package com.example.arthur.biblia_digital;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks  {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private final Context context = this;
    public static NodeList nodeLivros;
    public static ArrayList<String> listaLivros = new ArrayList<String>();
    private static final int REQUEST_CODE = 1234;
    public static NodeList nodeCapitulos;
    private Button speakButton;
    private String livro;
    private int qntCapitulos;
    private boolean aux = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //context.deleteDatabase("database.db");
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        //Botão para falar
        speakButton = (Button) findViewById(R.id.speakButton);
        speakButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {
                startVoiceRecognitionActivity();
            }

        });


        /******* Ler o XML ********/
        try {
            AssetManager assetManager = getAssets();
            InputStream is = assetManager.open("biblia.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);
            doc.getDocumentElement().normalize();
            nodeLivros = doc.getElementsByTagName("b");
            for (int temp = 0; temp < nodeLivros.getLength(); temp++) {
                Node nNode = nodeLivros.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    listaLivros.add(eElement.getAttribute("n"));
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (ParserConfigurationException e) {
            System.out.println(e.getMessage());
        } catch (SAXException e) {
            System.out.println(e.getMessage());
        }
        versiculoDiario();
        MyDBHandler db = new MyDBHandler(context, null, null, 1);
        VersiculoDiario ultimoVersiculoDiario = db.ultimoVersiculoDiario();
        if (ultimoVersiculoDiario != null){
        TextView textView = (TextView) findViewById(R.id.versiculoDiario);
        textView.setText("Versículo do dia: \n" +
               ultimoVersiculoDiario.getVersiculo() + " \n" + ultimoVersiculoDiario.getLivro() + " " + ultimoVersiculoDiario.getCapitulo() + ":" + ultimoVersiculoDiario.getId_versiculo());

            }
       }

    private void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "pt-BR");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Fale agora...");
        startActivityForResult(intent, REQUEST_CODE);
    }

    public void versiculoDiario() {
        GregorianCalendar date = new GregorianCalendar();
        int dia = date.get(Calendar.DAY_OF_YEAR);
        int ano = date.get(Calendar.YEAR);
        ArrayList<String> livros = new ArrayList<String>();
        for (int i = 0; i < listaLivros.size(); i++){
            livros.add(listaLivros.get(i));
        }
        Collections.shuffle(livros);
        String livro = livros.get(0);
        ArrayList<Integer> capitulos = new ArrayList<Integer>();
        for (int i = 1; i < qntCapitulos(livro)+1; i++) {
            capitulos.add(i);
        }
        Collections.shuffle(capitulos);
        int capitulo = capitulos.get(0);
        ArrayList<Integer> versiculos = new ArrayList<Integer>();
        for (int i = 1; i < qntVersiculos(livro, capitulo)+1; i++) {
            versiculos.add(i);
        }
        Collections.shuffle(versiculos);
        int idVersiculo = versiculos.get(0);
        String versiculo = buscarVersiculo(livro, capitulo, idVersiculo);
        MyDBHandler db = new MyDBHandler(context, null, null, 1);
        VersiculoDiario ultimoVersiculoDiario = db.ultimoVersiculoDiario();
        if (ultimoVersiculoDiario == null){
            db.adicionarVersiculoDiario(new VersiculoDiario(livro, capitulo, versiculo, idVersiculo, dia, ano));
        } else if (ultimoVersiculoDiario.getAno() != ano && ultimoVersiculoDiario.getDia() != dia){
            db.adicionarVersiculoDiario(new VersiculoDiario(livro, capitulo, versiculo, idVersiculo, dia, ano));
        }
    }

    public String buscarVersiculo(String livro, int capitulo, int idVersiculo){
       String versiculo = "";
        ArrayList<String> listaAuxiliar = new ArrayList<String>();
        for (int temp = 0; temp < nodeLivros.getLength(); temp++) {
            Node nNode = nodeLivros.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                if (eElement.getAttribute("n").equalsIgnoreCase(livro.toLowerCase())) {
                    for (int i = 0; i < nodeCapitulos.getLength(); i++) {
                        Node nNode2 = nodeCapitulos.item(i);
                        if (nNode2.getNodeType() == Node.ELEMENT_NODE) {
                            Element eElement2 = (Element) nNode2;
                            NodeList versiculos = eElement2.getChildNodes();
                            if (Integer.parseInt(eElement2.getAttribute("n")) == capitulo) {
                                for (int j = 0; j < versiculos.getLength(); j++) {
                                    Node nNode3 = versiculos.item(j);
                                    if (nNode3.getNodeType() == Node.ELEMENT_NODE) {
                                        Element eElement3 = (Element) nNode3;
                                        if (Integer.parseInt(eElement3.getAttribute("n")) == idVersiculo) {
                                            versiculo = eElement2.getElementsByTagName("v").item(Integer.parseInt(eElement3.getAttribute("n")) - 1).getTextContent();
                                        break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return versiculo;
    }

    public static int qntCapitulos(String livro){
        int valor = 0;
        for (int temp = 0; temp < MainActivity.nodeLivros.getLength(); temp++) {
            Node nNode = MainActivity.nodeLivros.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                if (eElement.getAttribute("n").equalsIgnoreCase(livro)) {
                    nodeCapitulos = eElement.getChildNodes();
                    for (int i = 0; i < nodeCapitulos.getLength(); i++) {
                        Node nNode2 = nodeCapitulos.item(i);
                        if (nNode2.getNodeType() == Node.ELEMENT_NODE) {
                            //element = (Element) nNode2;
                            valor++;
                        }
                    }
                    break;
                }
            }
        }
        return valor;
    }

    public int qntVersiculos(String livro, int capitulo) {
        int qntVersiculos = 0;
        ArrayList<String> listaAuxiliar = new ArrayList<String>();
        for (int temp = 0; temp < nodeLivros.getLength(); temp++) {
            Node nNode = nodeLivros.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                if (eElement.getAttribute("n").equalsIgnoreCase(livro.toLowerCase())) {
                    for (int i = 0; i < nodeCapitulos.getLength(); i++) {
                        Node nNode2 = nodeCapitulos.item(i);
                        if (nNode2.getNodeType() == Node.ELEMENT_NODE) {
                            Element eElement2 = (Element) nNode2;
                            NodeList versiculos = eElement2.getChildNodes();
                            if (Integer.parseInt(eElement2.getAttribute("n")) == capitulo) {
                                for (int j = 0; j < versiculos.getLength(); j++) {
                                    Node nNode3 = versiculos.item(j);
                                    if (nNode3.getNodeType() == Node.ELEMENT_NODE) {
                                        qntVersiculos++;
                                    }
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }
        return qntVersiculos;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
        Intent intent;
        switch (position) {

            case 0:
                mTitle = getString(R.string.title_section1);
                intent = new Intent(this, ListaLivros.class);
                startActivity(intent);
                break;
            case 1:
                mTitle = getString(R.string.title_section2);
                intent = new Intent(this, ListaAZ.class);
                Bundle params = new Bundle();
                params.putStringArrayList("livros", listaLivros);
                intent.putExtras(params);
                startActivity(intent);
                /*fragmentManager.beginTransaction()
                        .replace(R.id.container, FavoritoFragment.newInstance("string1", "string2")).commit();*/
                break;
            case 2:
                mTitle = getString(R.string.title_section3);
                intent = new Intent(this, ListaFavoritos.class);
                startActivity(intent);
                break;
            case 3:
                mTitle = getString(R.string.title_section4);
               intent = new Intent(this, ListaHistorico.class);
                startActivity(intent);
                /*fragmentManager.beginTransaction()
                        .replace(R.id.container, HistoricoFragment.newInstance("string1", "string2")).commit();*/
                break;
            case 4:
                mTitle = getString(R.string.title_section5);
                MyDBHandler db = new MyDBHandler(context, null, null, 1);
                Historico historico = db.ultimoHistorico();
                intent = new Intent(this, ListaVersiculos.class);
                Bundle params2 = new Bundle();
                params2.putInt("busca", 1);
                params2.putInt("capitulo", historico.getCapitulo());
                params2.putString("livro", historico.getLivro());
                params2.putInt("qntCapitulos", qntCapitulos(historico.getLivro()));
                intent.putExtras(params2);
                startActivity(intent);
                break;
        }

     }


    @Override
    protected void onActivityResult (int requestCode, int resultCode,Intent intentRetorno){

        Intent intent;

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {

            ArrayList<String> matches = intentRetorno.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String resultadoQuebrado[] = matches.get(0).toLowerCase().split(" ");

            contemLivro(resultadoQuebrado[0]);
            //System.out.println(livro);

            if (resultadoQuebrado.length == 1 && contemLivro(resultadoQuebrado[0])){


                intent = new Intent(context, ListaCapitulos.class);
                Bundle params = new Bundle();
                params.putString("livro", livro);
                intent.putExtras(params);
                startActivity(intent);
            }

            if (resultadoQuebrado.length == 3 && (resultadoQuebrado[1].equals("capitulo") || resultadoQuebrado[1].equals("capítulo"))){

                for (int temp = 0; temp < MainActivity.nodeLivros.getLength(); temp++) {
                    Node nNode = MainActivity.nodeLivros.item(temp);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        if (eElement.getAttribute("n").equalsIgnoreCase(livro)) {
                            nodeCapitulos = eElement.getChildNodes();
                            for (int i = 0; i < nodeCapitulos.getLength(); i++) {
                                Node nNode2 = nodeCapitulos.item(i);
                                if (nNode2.getNodeType() == Node.ELEMENT_NODE) {
                                    //element = (Element) nNode2;
                                    qntCapitulos++;
                                }
                            }
                            break;
                        }
                    }
                }

                intent = new Intent(context, ListaVersiculos.class);
                Bundle params = new Bundle();
                params.putInt("busca", 1);
                params.putString("livro", livro);
                params.putInt("qntCapitulos", qntCapitulos);
                params.putInt("capitulo", Integer.parseInt(resultadoQuebrado[2]));
                intent.putExtras(params);
                startActivity(intent);

            }

            if (resultadoQuebrado.length == 2 && contemLivro(resultadoQuebrado[0])){
                intent = new Intent(context, ListaVersiculos.class);
                Bundle params = new Bundle();
                params.putString("livro", livro);
                params.putInt("capitulo", Integer.parseInt(resultadoQuebrado[1]));
                intent.putExtras(params);
                startActivity(intent);
            }

            Toast.makeText(context, "Erro ao buscar: " + matches.get(0), Toast.LENGTH_LONG).show();
        }

    }

    /*public String nomeLivroOriginal(String livro) {
        String retorno = "";
        Collator collator = Collator.getInstance(new Locale("pt", "BR"));
        collator.setStrength(Collator.PRIMARY);
        for (String lista : listaLivros) {
            if (collator.compare(lista, livro) == 0) {
                retorno = lista;
                break;
            }
        }
        return retorno;
    }*/

    public boolean contemLivro(String livro) {
        Collator collator = Collator.getInstance(new Locale("pt", "BR"));
        collator.setStrength(Collator.PRIMARY);
        for (String lista : listaLivros) {
            if (collator.compare(lista, livro) == 0) {
                this.livro = lista;
                System.out.println(livro);
                return true;
            }
        }
        return false;
    }

    public void onSectionAttached(int number) {
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
