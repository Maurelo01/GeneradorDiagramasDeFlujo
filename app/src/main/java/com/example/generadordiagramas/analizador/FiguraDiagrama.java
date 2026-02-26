package com.example.generadordiagramas.analizador;
import java.util.ArrayList;
public class FiguraDiagrama
{
    public String tipoForma;
    public String textoVisible;
    public ArrayList<FiguraDiagrama> bloqueInterno;
    public int indiceElemento;
    public FiguraDiagrama(String tipoForma, String textoVisible)
    {
        this.tipoForma = tipoForma;
        this.textoVisible = textoVisible;
        this.bloqueInterno = null;
        this.indiceElemento = 0;
    }
}