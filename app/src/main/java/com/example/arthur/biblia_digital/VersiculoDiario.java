package com.example.arthur.biblia_digital;

/**
 * Created by Arthur on 17/04/2015.
 */
public class VersiculoDiario {

    private int id;
    private int capitulo;
    private String livro;
    private String versiculo;
    private int id_versiculo;
    private int dia;
    private int ano;

    public VersiculoDiario(int id, String livro, int capitulo, String versiculo, int id_versiculo, int dia, int ano) {
        this.id = id;
        this.livro = livro;
        this.capitulo = capitulo;
        this.versiculo = versiculo;
        this.id_versiculo = id_versiculo;
        this.dia = dia;
        this.ano = ano;
    }

    public VersiculoDiario(String livro, int capitulo, String versiculo, int id_versiculo, int dia, int ano) {
        this.livro = livro;
        this.capitulo = capitulo;
        this.versiculo = versiculo;
        this.id_versiculo = id_versiculo;
        this.dia = dia;
        this.ano = ano;
    }

    public VersiculoDiario(){

    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public int getId_versiculo() {
        return id_versiculo;
    }

    public void setId_versiculo(int id_versiculo) {
        this.id_versiculo = id_versiculo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCapitulo() {
        return capitulo;
    }

    public void setCapitulo(int capitulo) {
        this.capitulo = capitulo;
    }

    public String getLivro() {
        return livro;
    }

    public void setLivro(String livro) {
        this.livro = livro;
    }

    public String getVersiculo() {
        return versiculo;
    }

    public void setVersiculo(String versiculo) {
        this.versiculo = versiculo;
    }
}
