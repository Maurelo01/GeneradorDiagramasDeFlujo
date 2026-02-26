package com.example.generadordiagramas
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.generadordiagramas.analizador.FiguraDiagrama

class VistaDiagrama @JvmOverloads constructor(contexto: Context, atributos: AttributeSet? = null, estiloPorDefecto: Int = 0) : View(contexto, atributos, estiloPorDefecto)
{
    private var listaNodos: List<FiguraDiagrama> = ArrayList()
    private val pincelRelleno = Paint().apply{
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    private val pincelBorde = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 6f
    }
    private val pincelTexto = Paint().apply {
        isAntiAlias = true
        color = Color.BLACK
        textSize = 40f
        textAlign = Paint.Align.CENTER
    }
    private val pincelLinea = Paint().apply {
        isAntiAlias = true
        color = Color.DKGRAY
        strokeWidth = 8f
    }

    fun establecerDiagrama(nuevaLista: List<FiguraDiagrama>)
    {
        this.listaNodos = nuevaLista
        invalidate()
    }

    override fun onDraw(lienzo: Canvas)
    {
        super.onDraw(lienzo)
        if (listaNodos.isEmpty()) return
        val centroX = width / 2f
        val inicioY = 80f
        dibujarBloque(lienzo, listaNodos, centroX, inicioY)
    }

    private fun dibujarBloque(lienzo: Canvas, lista: List<FiguraDiagrama>, x: Float, yInicial: Float): Float
    {
        var yActual = yInicial
        val anchoFigura = 420f
        val altoFigura = 120f
        val espacioVertical = 80f
        for (i in lista.indices)
        {
            val nodo = lista[i]
            val izquierda = x - (anchoFigura / 2)
            val arriba = yActual
            val derecha = x + (anchoFigura / 2)
            val abajo = yActual + altoFigura
            pincelRelleno.color = when (nodo.tipoForma)
            {
                "INICIO", "FIN" -> Color.parseColor("#A5D6A7")
                "PROCESO" -> Color.parseColor("#90CAF9")
                "IO" -> Color.parseColor("#FFE082")
                "CONDICION" -> Color.parseColor("#CE93D8")
                else -> Color.WHITE
            }
            lienzo.drawRoundRect(izquierda, arriba, derecha, abajo, 30f, 30f, pincelRelleno)
            lienzo.drawRoundRect(izquierda, arriba, derecha, abajo, 30f, 30f, pincelBorde)
            val textoY = arriba + (altoFigura / 2) + (pincelTexto.textSize / 3)
            lienzo.drawText(nodo.textoVisible, x, textoY, pincelTexto)
            yActual = abajo
            if (nodo.bloqueInterno != null && nodo.bloqueInterno.isNotEmpty())
            {
                lienzo.drawLine(derecha, arriba + altoFigura / 2, derecha + 60f, arriba + altoFigura / 2, pincelLinea)
                yActual = dibujarBloque(lienzo, nodo.bloqueInterno, x + 200f, yActual + 40f)
            }
            if (i < lista.size - 1)
            {
                lienzo.drawLine(x, yActual, x, yActual + espacioVertical, pincelLinea)
                yActual += espacioVertical
            }
        }
        return yActual
    }
}