package com.example.arthur.biblia_digital;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static com.example.arthur.biblia_digital.MainActivity.nodeCapitulos;


public class MainActivity extends ActionBarActivity  {

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
        Button botaoLivros = (Button) findViewById(R.id.botao_livros);
        Button botaoFavoritos = (Button) findViewById(R.id.botao_favoritos);
        Button botaoBuscaVoz = (Button) findViewById(R.id.botao_busca_voz);
        Button botaoUltimaLeitura = (Button) findViewById(R.id.botao_ultima_leitura);
        botaoLivros.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final String[] items = new String[]{"Ordem natural", "Ordem de A-Z"};
                final Integer[] icons = new Integer[]{R.drawable.icone_biblia, R.drawable.icone_az};
                ListAdapter adapter = new MainActivity.ArrayAdapterWithIcon(context, items, icons);
                new AlertDialog.Builder(context).setTitle("Abrir livros na")
                        .setAdapter(adapter, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                Intent intent;
                                switch (item){
                                    case 0:
                                        intent = new Intent(context, ListaLivros.class);
                                        startActivity(intent);
                                        break;
                                    case 1:
                                        intent = new Intent(context, ListaAZ.class);
                                        Bundle params = new Bundle();
                                        params.putStringArrayList("livros", listaLivros);
                                        intent.putExtras(params);
                                        startActivity(intent);
                                        break;
                                }
                            }
                        }).show();
            }
        });
        botaoFavoritos.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, ListaFavoritos.class);
                startActivity(intent);
            }
        });
        botaoBuscaVoz.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startVoiceRecognitionActivity();
            }
        });
        botaoUltimaLeitura.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MyDBHandler db = new MyDBHandler(context, null, null, 1);
                Historico historico = db.ultimoHistorico();
                Intent intent = new Intent(context, ListaVersiculos.class);
                Bundle params2 = new Bundle();
                params2.putInt("busca", 1);
                params2.putInt("capitulo", historico.getCapitulo());
                params2.putString("livro", historico.getLivro());
                params2.putInt("qntCapitulos", qntCapitulos(historico.getLivro()));
                intent.putExtras(params2);
                startActivity(intent);
            }
        });


        /******* Carregar o XML ********/
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
        textView.setText(ultimoVersiculoDiario.getVersiculo());

            }
        TextView textView2 = (TextView) findViewById(R.id.versiculo);
        textView2.setText(ultimoVersiculoDiario.getLivro() + " " + ultimoVersiculoDiario.getCapitulo() + ":" + ultimoVersiculoDiario.getId_versiculo());
        System.out.println("Dia: " + ultimoVersiculoDiario.getDia());
       }

    private void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "pt-BR");
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);

        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Fale agora...");
        startActivityForResult(intent, REQUEST_CODE);
    }

    public void versiculoDiario() {
        GregorianCalendar date = new GregorianCalendar();
        int dia = date.get(Calendar.DAY_OF_YEAR);
        int ano = date.get(Calendar.YEAR);
        ArrayList<String> livros = new ArrayList<String>();
        ArrayList<Integer> capitulos = new ArrayList<Integer>();
        ArrayList<Integer> versiculos = new ArrayList<Integer>();
        String livro = "";
        int capitulo = 0;
        int idVersiculo = 0;
        if (dia >= 350 && dia <= 360) {
            livros.add(listaLivros.get(39));
            livros.add(listaLivros.get(22));
            livros.add(listaLivros.get(41));

            Collections.shuffle(livros);
            livro = livros.get(0);
            System.out.println(" ************** " + livro);
            if (livro.equalsIgnoreCase("Mateus")) {
                capitulos.add(1);
                capitulos.add(2);
                Collections.shuffle(capitulos);
                capitulo = capitulos.get(0);
                if (capitulo == 1) {
                    idVersiculo = 21;
                } else {
                    idVersiculo = 10;
                }
            } else if (livro.equalsIgnoreCase("Isaías")) {
                //break;
                //case "Isaías":
                capitulos.add(7);
                capitulos.add(9);
                Collections.shuffle(capitulos);
                capitulo = capitulos.get(0);
                if (capitulo == 7) {
                    idVersiculo = 14;
                } else {
                    idVersiculo = 6;
                }
                // break;
            } else {
                //case "Lucas":
                    capitulos.add(2);
                    capitulos.add(1);
                    Collections.shuffle(capitulos);
                    capitulo = capitulos.get(0);
                    if (capitulo == 2) {
                        versiculos.add(30);
                        versiculos.add(31);
                        versiculos.add(35);

                    } else {
                        versiculos.add(10);
                        versiculos.add(11);
                        versiculos.add(12);
                        versiculos.add(15);
                        versiculos.add(20);
                    }
                    Collections.shuffle(versiculos);
                    idVersiculo = versiculos.get(0);
                    //break;
            }

            qntCapitulos(livro);
            qntVersiculos(livro, capitulo);

        } else {
            for (int i = 0; i < listaLivros.size(); i++){
                livros.add(listaLivros.get(i));
            }

            Collections.shuffle(livros);
            livro = livros.get(0);

            for (int i = 1; i < qntCapitulos(livro)+1; i++) {
                capitulos.add(i);
            }
            Collections.shuffle(capitulos);
            capitulo = capitulos.get(0);

            for (int i = 1; i < qntVersiculos(livro, capitulo)+1; i++) {
                versiculos.add(i);
            }
            Collections.shuffle(versiculos);
            idVersiculo = versiculos.get(0);
        }
        System.out.println("############# " + nodeCapitulos.getLength());
        //System.out.println("************  " + livro + " " + capitulo + " " + idVersiculo);
        String versiculo = buscarVersiculo(livro, capitulo, idVersiculo);
        MyDBHandler db = new MyDBHandler(context, null, null, 1);
        final VersiculoDiario ultimoVersiculoDiario = db.ultimoVersiculoDiario();
        if (ultimoVersiculoDiario == null){
            db.adicionarVersiculoDiario(new VersiculoDiario(livro, capitulo, versiculo, idVersiculo, dia, ano));
        } else if (ultimoVersiculoDiario.getDia() != dia){
            db.adicionarVersiculoDiario(new VersiculoDiario(livro, capitulo, versiculo, idVersiculo, dia, ano));
        }
        TextView versiculoDiario = (TextView) findViewById(R.id.versiculoDiario);
        versiculoDiario.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String clipboad = "\""+ ultimoVersiculoDiario.getVersiculo() +"\" ("+ultimoVersiculoDiario.getLivro()+" " + ultimoVersiculoDiario.getCapitulo()+":"+(ultimoVersiculoDiario.getId_versiculo())+")" ;
                final String [] items = new String[] {"Adicionar aos favoritos", "Copiar texto", "Compartilhar"};
                final Integer[] icons = new Integer[] {R.drawable.icone_favorito,
                        R.drawable.icone_copiar, R.drawable.icone_compartilhar};
                ListAdapter adapter = new ArrayAdapterWithIcon(context, items, icons);

                new AlertDialog.Builder(context).setTitle("Opções do versículo diário")
                        .setAdapter(adapter, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                switch (item) {
                                    //Adicionar aos favoritos
                                    case 0:
                                        MyDBHandler db = new MyDBHandler(context, null, null, 1);
                                        Favorito favorito = new Favorito(ultimoVersiculoDiario.getLivro(), ultimoVersiculoDiario.getCapitulo(), ultimoVersiculoDiario.getVersiculo(), ultimoVersiculoDiario.getId_versiculo()-1);
                                        String msg = "";
                                        if (db.adicionarFavorito(favorito)) {
                                            msg = "O versículo foi adicionado aos favoritos";
                                        } else {
                                            msg = "Este versículo já está no seus favoritos";
                                        }
                                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                                        break;
                                    //Copiar texto
                                    case 1:
                                        ClipboardManager myClipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                        ClipData myClip = ClipData.newPlainText("text", clipboad);
                                        myClipboard.setPrimaryClip(myClip);
                                        Toast.makeText(context, "O texto do versículo foi copiado.", Toast.LENGTH_LONG).show();
                                        break;
                                    //Compartilhar
                                    case 2:
                                        final String[] items = new String[]{"Facebook", "Gmail", "Mensagem"};
                                        final Integer[] icons = new Integer[]{R.drawable.icone_facebook, R.drawable.icone_gmail,
                                                R.drawable.icone_mensagem};
                                        ListAdapter adapter2 = new ArrayAdapterWithIcon(context, items, icons);
                                        new AlertDialog.Builder(context).setTitle("Compartilhar via")
                                                .setAdapter(adapter2, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int item) {
                                                        switch (item) {
                                                            //Facebook
                                                            case 0:
                                                                break;
                                                            //Gmail
                                                            case 1:
                                                                try {
                                                                    Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                                                                    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Versículo para sua meditação");
                                                                    emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, clipboad);
                                                                    emailIntent.setType("plain/text");
                                                                    final PackageManager pm = context.getPackageManager();
                                                                    final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
                                                                    ResolveInfo best = null;
                                                                    for (final ResolveInfo info : matches)
                                                                        if (info.activityInfo.packageName.endsWith(".gm") ||
                                                                                info.activityInfo.name.toLowerCase().contains("gmail"))
                                                                            best = info;
                                                                    if (best != null)
                                                                        emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
                                                                    startActivity(emailIntent);
                                                                } catch (Exception e) {
                                                                    Toast.makeText(context, "Aplicativo do Gmail não localizado.", Toast.LENGTH_LONG).show();
                                                                }
                                                                break;
                                                            //Mensagem
                                                            case 2:
                                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) //At least KitKat
                                                                {
                                                                    String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(context); //Need to change the build to API 19

                                                                    Intent sendIntent = new Intent(Intent.ACTION_SEND);
                                                                    sendIntent.setType("text/plain");
                                                                    sendIntent.putExtra(Intent.EXTRA_TEXT, clipboad);

                                                                    if (defaultSmsPackageName != null)//Can be null in case that there is no default, then the user would be able to choose any app that support this intent.
                                                                    {
                                                                        sendIntent.setPackage(defaultSmsPackageName);
                                                                    }
                                                                    context.startActivity(sendIntent);

                                                                } else //For early versions, do what worked for you before.
                                                                {
                                                                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                                                                    sendIntent.setData(Uri.parse("sms:"));
                                                                    sendIntent.putExtra("sms_body", clipboad);
                                                                    context.startActivity(sendIntent);
                                                                }
                                                                break;
                                                        }
                                                    }
                                                }).show();
                                        break;


                                }
                            }
                        }).show();
            }
        });
    }

    public String buscarVersiculo(String livro, int capitulo, int idVersiculo){
       String versiculo = "";
        ArrayList<String> listaAuxiliar = new ArrayList<String>();
        for (int temp = 0; temp < nodeLivros.getLength(); temp++) {
            Node nNode = nodeLivros.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                if (eElement.getAttribute("n").equalsIgnoreCase(livro.toLowerCase())) {
                    System.out.println(" %%%%%%%%%%%%%% " + livro.toLowerCase() + " " + nodeCapitulos.getLength());
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

   /* @Override
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

     }*/

    @Override
    protected void onActivityResult (int requestCode, int resultCode,Intent intentRetorno){

        Intent intent;

        if (resultCode == RESULT_OK) {


            if (requestCode == REQUEST_CODE) {

                ArrayList<String> matches = intentRetorno.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String resultadoQuebrado[] = matches.get(0).toLowerCase().split(" ");

                Toast.makeText(context, matches.get(0), Toast.LENGTH_LONG).show();

                //Se falar apenas o nome do livro
                if (resultadoQuebrado.length == 1 && contemLivro(resultadoQuebrado[0])) {

                    intent = new Intent(context, ListaCapitulos.class);
                    Bundle params = new Bundle();
                    params.putString("livro", livro);
                    intent.putExtras(params);
                    startActivity(intent);
                }
                //Se falar o nome do livro e a palavra "capítulo"
                if (contemLivro(resultadoQuebrado[0]) && resultadoQuebrado.length == 3
                        && (resultadoQuebrado[1].equals("capitulo") || resultadoQuebrado[1].equals("capítulo"))) {

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
                    try {
                        if (Integer.parseInt(resultadoQuebrado[2]) > 0 && Integer.parseInt(resultadoQuebrado[2]) <= qntCapitulos) {
                            intent = new Intent(context, ListaVersiculos.class);
                            Bundle params = new Bundle();
                            params.putInt("busca", 1);
                            params.putString("livro", livro);
                            params.putInt("qntCapitulos", qntCapitulos);
                            params.putInt("capitulo", Integer.parseInt(resultadoQuebrado[2]));
                            intent.putExtras(params);
                            startActivity(intent);
                        } else {
                            Toast.makeText(context, "Capítulo inválido", Toast.LENGTH_LONG).show();
                        }
                    }catch (NumberFormatException e){
                        e.printStackTrace();
                    }

                }

                //Se falar o nome do livro e o capítulo (apenas o número)
                if (resultadoQuebrado.length == 2 && contemLivro(resultadoQuebrado[0])) {

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
                    try {
                        if (Integer.parseInt(resultadoQuebrado[1]) > 0 && Integer.parseInt(resultadoQuebrado[1]) <= qntCapitulos) {
                            intent = new Intent(context, ListaVersiculos.class);
                            Bundle params = new Bundle();
                            params.putInt("busca", 1);
                            params.putString("livro", livro);
                            params.putInt("qntCapitulos", qntCapitulos);
                            params.putInt("capitulo", Integer.parseInt(resultadoQuebrado[1]));
                            intent.putExtras(params);
                            startActivity(intent);
                        } else {
                            Toast.makeText(context, "Capítulo inválido", Toast.LENGTH_LONG).show();
                        }
                    } catch (NumberFormatException e){
                        e.printStackTrace();
                    }
                }

                //Toast.makeText(context, "Erro ao buscar: " + matches.get(0), Toast.LENGTH_LONG).show();
            } else if (resultCode == RecognizerIntent.RESULT_AUDIO_ERROR) {
                Toast.makeText(context, "Audio Error", Toast.LENGTH_LONG).show();
        } else if (resultCode == RecognizerIntent.RESULT_CLIENT_ERROR) {
                Toast.makeText(context, "Client Error", Toast.LENGTH_LONG).show();
        } else if (resultCode == RecognizerIntent.RESULT_NETWORK_ERROR) {
                Toast.makeText(context, "Network Error", Toast.LENGTH_LONG).show();
        } else if (resultCode == RecognizerIntent.RESULT_NO_MATCH) {
                Toast.makeText(context, "No match", Toast.LENGTH_LONG).show();
        } else if (resultCode == RecognizerIntent.RESULT_SERVER_ERROR) {
                Toast.makeText(context, "Server Error", Toast.LENGTH_LONG).show();
        }
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
    public class ArrayAdapterWithIcon extends ArrayAdapter<String> {

        private List<Integer> images;

        public ArrayAdapterWithIcon(Context context, List<String> items, List<Integer> images) {
            super(context, android.R.layout.select_dialog_item, items);
            this.images = images;
        }

        public ArrayAdapterWithIcon(Context context, String[] items, Integer[] images) {
            super(context, android.R.layout.select_dialog_item, items);
            this.images = Arrays.asList(images);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setCompoundDrawablesWithIntrinsicBounds(images.get(position), 0, 0, 0);
            textView.setCompoundDrawablePadding(
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getContext().getResources().getDisplayMetrics()));
            return view;
        }

    }

}
