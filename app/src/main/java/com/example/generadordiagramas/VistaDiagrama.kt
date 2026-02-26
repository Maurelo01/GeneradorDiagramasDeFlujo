package com.example.generadordiagramas
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.example.generadordiagramas.analizador.FiguraDiagrama
import com.example.generadordiagramas.analizador.Parser.ConfiguracionVisual

class VistaDiagrama @JvmOverloads constructor(contexto: Context, atributos: AttributeSet? = null, estiloPorDefecto: Int = 0) : View(contexto, atributos, estiloPorDefecto)
{
    private var listaNodos: List<FiguraDiagrama> = ArrayList()
    private var configVisual: ConfiguracionVisual? = null

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
        strokeWidth = 12f
    }

    fun establecerDiagrama(nuevaLista: List<FiguraDiagrama>, config: ConfiguracionVisual? = null)
    {
        this.listaNodos = nuevaLista
        this.configVisual = config
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

    private fun interpretarColor(colorStr: String?, colorDefaultHex: String): Int
    {
        if (colorStr == null) return Color.parseColor(colorDefaultHex)
        return try
        {
            if (colorStr.startsWith("H"))
            {
                Color.parseColor("#" + colorStr.substring(1))
            }
            else if (colorStr.startsWith("#"))
            {
                Color.parseColor(colorStr)
            }
            else
            {
                Color.parseColor(colorDefaultHex)
            }
        }
        catch (e: Exception)
        {
            Color.parseColor(colorDefaultHex)
        }
    }

    private fun obtenerFuente(nombreFuente: String?): android.graphics.Typeface
    {
        return when (nombreFuente)
        {
            "ARIAL" -> android.graphics.Typeface.SANS_SERIF
            "TIMES_NEW_ROMAN" -> android.graphics.Typeface.SERIF
            "COMIC_SANS" -> android.graphics.Typeface.create("casual", android.graphics.Typeface.NORMAL)
            "VERDANA" -> android.graphics.Typeface.SANS_SERIF
            else -> android.graphics.Typeface.DEFAULT
        }
    }

    private fun dibujarFormaDinamica(lienzo: Canvas, figura: String?, izquierda: Float, arriba: Float, derecha: Float, abajo: Float, x: Float)
    {
        val altoFigura = abajo - arriba
        val rutaPoligono = Path()
        when (figura ?: "RECTANGULO")
        {
            "ELIPSE", "CIRCULO" -> {
                lienzo.drawOval(izquierda, arriba, derecha, abajo, pincelRelleno)
                lienzo.drawOval(izquierda, arriba, derecha, abajo, pincelBorde)
            }
            "ROMBO" -> {
                rutaPoligono.moveTo(x, arriba)
                rutaPoligono.lineTo(derecha, arriba + (altoFigura / 2))
                rutaPoligono.lineTo(x, abajo)
                rutaPoligono.lineTo(izquierda, arriba + (altoFigura / 2))
                rutaPoligono.close()
                lienzo.drawPath(rutaPoligono, pincelRelleno)
                lienzo.drawPath(rutaPoligono, pincelBorde)
            }
            "PARALELOGRAMO" -> {
                val inclinacion = 40f
                rutaPoligono.moveTo(izquierda + inclinacion, arriba)
                rutaPoligono.lineTo(derecha, arriba)
                rutaPoligono.lineTo(derecha - inclinacion, abajo)
                rutaPoligono.lineTo(izquierda, abajo)
                rutaPoligono.close()
                lienzo.drawPath(rutaPoligono, pincelRelleno)
                lienzo.drawPath(rutaPoligono, pincelBorde)
            }
            "RECTANGULO_REDONDEADO" -> {
                val rect = RectF(izquierda, arriba, derecha, abajo)
                lienzo.drawRoundRect(rect, 30f, 30f, pincelRelleno)
                lienzo.drawRoundRect(rect, 30f, 30f, pincelBorde)
            }
            else -> {
                lienzo.drawRect(izquierda, arriba, derecha, abajo, pincelRelleno)
                lienzo.drawRect(izquierda, arriba, derecha, abajo, pincelBorde)
            }
        }
    }

    private fun dibujarBloque(lienzo: Canvas, lista: List<FiguraDiagrama>, x: Float, yInicial: Float): Float
    {
        var yActual = yInicial
        val anchoFigura = 420f
        val altoFigura = 120f
        val espacioVertical = 120f
        val cv = configVisual ?: ConfiguracionVisual()
        for (i in lista.indices)
        {
            val nodo = lista[i]
            val izquierda = x - (anchoFigura / 2)
            val arriba = yActual
            val derecha = x + (anchoFigura / 2)
            val abajo = yActual + altoFigura
            when (nodo.tipoForma)
            {
                "INICIO", "FIN" -> {
                    pincelRelleno.color = Color.parseColor("#A5D6A7")
                    pincelTexto.color = Color.BLACK
                    dibujarFormaDinamica(lienzo, "ELIPSE", izquierda, arriba, derecha, abajo, x)
                }
                "PROCESO" -> {
                    // Verificar si hay configuración específica para este índice
                    val idx = nodo.indiceElemento
                    val colorFondo = cv.coloresBloques[idx] ?: cv.colorBloque
                    val colorTexto = cv.coloresTextoBloque[idx] ?: cv.colorTextoBloque
                    val figura = cv.figurasBloque[idx] ?: cv.figuraBloque
                    val fuente = cv.fuentesBloque[idx] ?: cv.fuenteBloque
                    val tamaño = cv.sizesBloques[idx] ?: cv.sizeBloque
                    pincelTexto.typeface = obtenerFuente(fuente)
                    pincelTexto.textSize = tamaño.toFloat()
                    pincelRelleno.color = interpretarColor(colorFondo, "#90CAF9")
                    pincelTexto.color = interpretarColor(colorTexto, "#000000")
                    dibujarFormaDinamica(lienzo, figura, izquierda, arriba, derecha, abajo, x)
                }
                "IO" -> {
                    pincelRelleno.color = Color.parseColor("#FFE082")
                    pincelTexto.color = Color.BLACK
                    dibujarFormaDinamica(lienzo, "PARALELOGRAMO", izquierda, arriba, derecha, abajo, x)
                }
                "CONDICION" -> {
                    val esMientras = nodo.textoVisible.startsWith("MIENTRAS")
                    val idx = nodo.indiceElemento
                    if (esMientras)
                    {
                        val colorFondo = cv.coloresMientras[idx] ?: cv.colorMientras
                        val colorTexto = cv.coloresTextoMientras[idx] ?: cv.colorTextoMientras
                        val figura = cv.figurasMientras[idx] ?: cv.figuraMientras
                        val fuente = cv.fuentesMientras[idx] ?: cv.fuenteMientras
                        val tamaño = cv.sizesMientras[idx] ?: cv.sizeMientras
                        pincelTexto.typeface = obtenerFuente(fuente)
                        pincelTexto.textSize = tamaño.toFloat()
                        pincelRelleno.color = interpretarColor(colorFondo, "#CE93D8")
                        pincelTexto.color = interpretarColor(colorTexto, "#000000")
                        dibujarFormaDinamica(lienzo, figura, izquierda, arriba, derecha, abajo, x)
                    }
                    else
                    {
                        val colorFondo = cv.coloresSi[idx] ?: cv.colorSi
                        val colorTexto = cv.coloresTextoSi[idx] ?: cv.colorTextoSi
                        val figura = cv.figurasSi[idx] ?: cv.figuraSi
                        val fuente = cv.fuentesSi[idx] ?: cv.fuenteSi
                        val tamaño = cv.sizesSi[idx] ?: cv.sizeSi
                        pincelTexto.typeface = obtenerFuente(fuente)
                        pincelTexto.textSize = tamaño.toFloat()
                        pincelRelleno.color = interpretarColor(colorFondo, "#CE93D8")
                        pincelTexto.color = interpretarColor(colorTexto, "#000000")
                        dibujarFormaDinamica(lienzo, figura, izquierda, arriba, derecha, abajo, x)
                    }
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
            lienzo.drawText(textoMostrar, x, textoY, pincelTexto)
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