package com.example.arthur.biblia_digital;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;


public class ListaCapitulos extends ActionBarActivity implements SensorEventListener{

    public static String livro = "";
    public static int qntCapitulos;
    public static NodeList nodeCapitulos;
    private final Context context = this;
    private ListView listView;
    private int index;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private ArrayList<String> listaCapitulos = new ArrayList<String>();
    private int paginas = MainActivity.listaLivros.size();
    private ArrayList<String> listaLivros = new ArrayList<String>();
    private int ordenado = 0;
    private String livroAnterior = "";
    public static final int TIPO_SENSOR = Sensor.TYPE_ACCELEROMETER;
    private SensorManager sensorManager;
    private Sensor sensor;
    private float valorAnteriorX = 0;
    private float valorAnteriorY = 0;
    private float x = 0;
    private float y = 0;
    private float z = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_capitulos);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.icone_biblia_aberta);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        Intent intent = getIntent();
        Bundle params = intent.getExtras();
        livro = params.getString("livro");
        ordenado = params.getInt("ordenado");
        setTitle("  " + livro);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(TIPO_SENSOR);
        if(sensor == null) {
            System.out.println("Sensor não disponível");
            Toast.makeText(this, "sensor nao disponível", Toast.LENGTH_LONG).show();
        }

        if (ordenado == 0){
            listaLivros = MainActivity.listaLivros;
        } else{
            listaLivros = ListaAZ.listaOrdenada;
        }

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(listaLivros.indexOf(livro));
        livro = listaLivros.get(listaLivros.indexOf(livro));
        qntCapitulos = qntdCapitulos(livro);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                livroAnterior = livro;
                setTitle("  " + listaLivros.get(mPager.getCurrentItem()));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }

    public ArrayList<String> listarCapitulos(int qntCapitulos){
        ArrayList<String> lista = new ArrayList<String>();
        for (int i = 1; i < qntCapitulos+1; i++) {
            lista.add("Capítulo " + i);
        }
        return lista;
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                /*case 0:
                    livro = listaLivros.get(0);
                    qntCapitulos = qntdCapitulos(livro);
                    System.out.println("case 0");
                    break;
                case 1:
                    //livro = "Êxodo";
                    //qntCapitulos = 40;
                    System.out.println("case 1");
                    break;
                case 2:
                    livro = listaLivros.get(1);
                    qntCapitulos = qntdCapitulos(livro);
                    System.out.println("case 2");
                    break;
                default:
                    System.out.println("chamado");
                    livro = listaLivros.get(position);
                    qntCapitulos = qntdCapitulos(livro);
                   break;*/
            }
            //livro = listaLivros.get(position+1);
            //qntCapitulos = qntdCapitulos(livro);
            System.out.println("Livro **** " + livro);
            return new ListaCapitulosFragment(listarCapitulos(qntCapitulos), livro);
        }

        @Override
        public int getCount() {
            return paginas;
        }
    }

    public int qntdCapitulos(String livro){
        /********** Código para armazenar os capítulos em um array*********/
        int qntCapitulos = 0;
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
        //System.out.println("Livro: " + qntCapitulos);
        return qntCapitulos;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {


        valorAnteriorX = x;
        valorAnteriorY = y;


        float[] values = SensorUtil.fixAcelerometro(this, event);

        x = values[0];
        y = values[1];
        z = values[2];

        //System.out.println(x + " " + y + " " + z);


        //System.out.println(x + " *** " + y);

        if ((x > 4 && x < 7) && (y > 7 && y < 10)){
            System.out.println("Passar livro");
            //new ScreenSlidePagerAdapter(getSupportFragmentManager());
            //new ListaCapitulosFragment(listarCapitulos(qntCapitulos), livro);
            x = 0;
            y = 0;
            /*System.out.println((x-valorAnteriorX) + " *** " + (y-valorAnteriorY));
            if ((x-valorAnteriorX)>1 && ((y-valorAnteriorY) < 1 && (y-valorAnteriorY) > -1)){

            }*/
        }


    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lista_capitulos, menu);
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
