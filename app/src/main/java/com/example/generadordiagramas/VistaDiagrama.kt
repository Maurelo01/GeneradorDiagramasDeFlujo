package com.example.generadordiagramas
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
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
        textSize = 60f;
        textAlign = Paint.Align.CENTER
    }
    private val pincelLinea = Paint().apply {
        isAntiAlias = true
        color = Color.DKGRAY
        strokeWidth = 12f
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
        val posicionX = 400f
        val inicioY = 80f
        dibujarBloque(lienzo, listaNodos, posicionX, inicioY)
    }

    private fun dibujarBloque(lienzo: Canvas, lista: List<FiguraDiagrama>, x: Float, yInicial: Float): Float
    {
        var yActual = yInicial
        val anchoFigura = 420f
        val altoFigura = 120f
        val espacioVertical = 120f
        for (i in lista.indices)
        {
            val nodo = lista[i]
            val izquierda = x - (anchoFigura / 2)
            val arriba = yActual
            val derecha = x + (anchoFigura / 2)
            val abajo = yActual + altoFigura
            val rutaPoligono = Path()
            when (nodo.tipoForma) {
                "INICIO", "FIN" -> {
                    pincelRelleno.color = Color.parseColor("#A5D6A7")
                    lienzo.drawOval(izquierda, arriba, derecha, abajo, pincelRelleno)
                    lienzo.drawOval(izquierda, arriba, derecha, abajo, pincelBorde)
                }
                "PROCESO" -> {
                    pincelRelleno.color = Color.parseColor("#90CAF9")
                    lienzo.drawRect(izquierda, arriba, derecha, abajo, pincelRelleno)
                    lienzo.drawRect(izquierda, arriba, derecha, abajo, pincelBorde)
                }
                "IO" -> {
                    pincelRelleno.color = Color.parseColor("#FFE082")
                    val inclinacion = 40f
                    rutaPoligono.moveTo(izquierda + inclinacion, arriba)
                    rutaPoligono.lineTo(derecha, arriba)
                    rutaPoligono.lineTo(derecha - inclinacion, abajo)
                    rutaPoligono.lineTo(izquierda, abajo)
                    rutaPoligono.close()
                    lienzo.drawPath(rutaPoligono, pincelRelleno)
                    lienzo.drawPath(rutaPoligono, pincelBorde)
                }
                "CONDICION" -> {
                    pincelRelleno.color = Color.parseColor("#CE93D8")
                    rutaPoligono.moveTo(x, arriba)
                    rutaPoligono.lineTo(derecha, arriba + (altoFigura / 2))
                    rutaPoligono.lineTo(x, abajo)
                    rutaPoligono.lineTo(izquierda, arriba + (altoFigura / 2))
                    rutaPoligono.close()
                    lienzo.drawPath(rutaPoligono, pincelRelleno)
                    lienzo.drawPath(rutaPoligono, pincelBorde)
                }
            }
            val textoMostrar = if (nodo.tipoForma == "CONDICION" && !nodo.textoVisible.startsWith("MIENTRAS"))
            {
                "SI ${nodo.textoVisible}"
            }
            else
            {
                nodo.textoVisible
            }
            val textoY = arriba + (altoFigura / 2) + (pincelTexto.textSize / 3)
            lienzo.drawText(nodo.textoVisible, x, textoY, pincelTexto)
            yActual = abajo
            if (nodo.bloqueInterno != null && nodo.bloqueInterno.isNotEmpty())
            {
                lienzo.drawLine(derecha, arriba + altoFigura / 2, derecha + 200f, arriba + altoFigura / 2, pincelLinea)
                lienzo.drawText("V", derecha + 75f, arriba + altoFigura / 2 - 15f, pincelTexto)
                val yFinalRama = dibujarBloque(lienzo, nodo.bloqueInterno, x + 600f, arriba)
                if (yFinalRama > yActual)
                {
                    yActual = yFinalRama
                }
            }
            if (i < lista.size - 1)
            {
                lienzo.drawLine(x, yActual, x, yActual + espacioVertical, pincelLinea)
                if (nodo.tipoForma == "CONDICION")
                {
                    lienzo.drawText("F", x + 30f, abajo + 60f, pincelTexto)
                }
                yActual += espacioVertical
            }
        }
        return yActual
    }
}