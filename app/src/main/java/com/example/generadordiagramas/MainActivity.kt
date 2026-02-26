package com.example.generadordiagramas

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import com.example.generadordiagramas.analizador.Lexer
import com.example.generadordiagramas.analizador.Parser
import com.example.generadordiagramas.analizador.FiguraDiagrama
import java.io.StringReader

class MainActivity : AppCompatActivity()
{
    private lateinit var editTextoEntrada: EditText
    private lateinit var btnAnalizar: Button
    private lateinit var btnReporteErrores: Button
    private lateinit var btnReporteOperadores: Button
    private lateinit var btnReporteControl: Button
    private lateinit var vistaDiagrama: VistaDiagrama
    private lateinit var scrollDiagrama: ScrollView
    private lateinit var txtMensaje: TextView
    private var parser: Parser? = null
    private var lexer: Lexer? = null
    private var erroresCompletos: ArrayList<ErrorCompleto> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        inicializarVistas()
        configurarEventos()
    }

    private fun inicializarVistas()
    {
        editTextoEntrada = findViewById(R.id.editTextoEntrada)
        btnAnalizar = findViewById(R.id.btnAnalizar)
        btnReporteErrores = findViewById(R.id.btnReporteErrores)
        btnReporteOperadores = findViewById(R.id.btnReporteOperadores)
        btnReporteControl = findViewById(R.id.btnReporteControl)
        vistaDiagrama = findViewById(R.id.vistaDiagrama)
        scrollDiagrama = findViewById(R.id.scrollDiagrama)
        txtMensaje = findViewById(R.id.txtMensaje)
        btnReporteErrores.isEnabled = false
        btnReporteOperadores.isEnabled = false
        btnReporteControl.isEnabled = false
    }

    private fun configurarEventos()
    {
        btnAnalizar.setOnClickListener {
            analizarCodigo()
        }
        btnReporteErrores.setOnClickListener {
            mostrarReporteErrores()
        }
        btnReporteOperadores.setOnClickListener {
            mostrarReporteOperadores()
        }
        btnReporteControl.setOnClickListener {
            mostrarReporteControl()
        }
    }

    private fun analizarCodigo()
    {
        val codigoEntrada = editTextoEntrada.text.toString()
        if (codigoEntrada.isEmpty())
        {
            Toast.makeText(this, "No has escrito el código aun", Toast.LENGTH_SHORT).show()
            return
        }

        try
        {
            val lector = StringReader(codigoEntrada)
            lexer = Lexer(lector)
            parser = Parser(lexer!!)
            // Realizar análisis
            val resultado = parser!!.parse()
            val diagrama = resultado.value as? ArrayList<FiguraDiagrama>
            // Combinar errores léxicos y sintácticos
            erroresCompletos.clear()
            // Agregar errores léxicos
            for (errorLex in lexer!!.listaErrores)
            {
                erroresCompletos.add(ErrorCompleto(
                    errorLex.lexema,
                    errorLex.linea,
                    errorLex.columna,
                    "Léxico",
                    errorLex.descripcion
                ))
            }
            // Agregar errores sintácticos
            for (errorSin in parser!!.listaErrores)
            {
                erroresCompletos.add(ErrorCompleto(
                    errorSin.lexema,
                    errorSin.linea,
                    errorSin.columna,
                    "Sintáctico",
                    errorSin.descripcion
                ))
            }
            // Ordenar por línea y columna
            erroresCompletos.sortWith(compareBy({ it.linea }, { it.columna }))
            // Verificar si hay errores
            if (erroresCompletos.isEmpty())
            {
                // No hay errores, mostrar diagrama y habilitar reportes
                vistaDiagrama.establecerDiagrama(diagrama ?: ArrayList(), parser!!.config)
                scrollDiagrama.visibility = ScrollView.VISIBLE
                txtMensaje.visibility = TextView.GONE
                // Habilitar reportes de operadores y control
                btnReporteErrores.isEnabled = false
                btnReporteOperadores.isEnabled = true
                btnReporteControl.isEnabled = true
                Toast.makeText(this, "Análisis exitoso - ${diagrama?.size ?: 0} elementos", Toast.LENGTH_SHORT).show()
            }
            else
            {
                // Hay errores, no mostrar diagrama, solo reporte de errores
                scrollDiagrama.visibility = ScrollView.GONE
                txtMensaje.visibility = TextView.VISIBLE
                txtMensaje.text = "Se encontraron ${erroresCompletos.size} errores.\nPresione 'Reporte de Errores' para ver detalles."
                // Solo habilitar reporte de errores
                btnReporteErrores.isEnabled = true
                btnReporteOperadores.isEnabled = false
                btnReporteControl.isEnabled = false
                Toast.makeText(this, "Se encontraron ${erroresCompletos.size} errores", Toast.LENGTH_LONG).show()
            }

        }
        catch (e: Exception)
        {
            Toast.makeText(this, "Error durante el análisis: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
            scrollDiagrama.visibility = ScrollView.GONE
            txtMensaje.visibility = TextView.VISIBLE
            txtMensaje.text = "Error crítico durante el análisis:\n${e.message}"
            btnReporteErrores.isEnabled = false
            btnReporteOperadores.isEnabled = false
            btnReporteControl.isEnabled = false
        }
    }

    private fun mostrarReporteErrores()
    {
        if (erroresCompletos.isEmpty()) return
        val tabla = TableLayout(this).apply {
            isStretchAllColumns = true
            setBackgroundColor(android.graphics.Color.BLACK)
        }
        tabla.addView(crearFilaTabla(true, "Lexema", "Línea", "Col", "Tipo", "Descripción"))
        for (error in erroresCompletos)
        {
            tabla.addView(crearFilaTabla(false, error.lexema, error.linea.toString(), error.columna.toString(), error.tipo, error.descripcion))
        }
        mostrarDialogoTabla("Reporte de Errores", tabla)
    }

    private fun mostrarReporteOperadores()
    {
        if (parser == null || parser!!.listaOperadores.isEmpty()) return
        val tabla = TableLayout(this).apply {
            isStretchAllColumns = true
            setBackgroundColor(android.graphics.Color.BLACK)
        }
        tabla.addView(crearFilaTabla(true, "Operador", "Línea", "Columna", "Ocurrencia"))
        for (op in parser!!.listaOperadores)
        {
            tabla.addView(crearFilaTabla(false, op.operador, op.linea.toString(), op.columna.toString(), op.ocurrencia))
        }
        mostrarDialogoTabla("Reporte de Operadores", tabla)
    }

    private fun mostrarReporteControl()
    {
        if (parser == null || parser!!.listaControl.isEmpty()) return
        val tabla = TableLayout(this).apply {
            isStretchAllColumns = true
            setBackgroundColor(android.graphics.Color.BLACK)
        }
        tabla.addView(crearFilaTabla(true, "Objeto", "Línea", "Condición"))
        for (ctrl in parser!!.listaControl)
        {
            tabla.addView(crearFilaTabla(false, ctrl.objeto, ctrl.linea.toString(), ctrl.condicion))
        }
        mostrarDialogoTabla("Reporte Estructuras de Control", tabla)
    }
    private fun crearFilaTabla(esCabecera: Boolean, vararg textos: String): TableRow
    {
        val fila = TableRow(this)
        fila.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT)
        fila.setPadding(0, 0, 0, 2)
        fila.setBackgroundColor(android.graphics.Color.BLACK)
        val colorFondo = if (esCabecera) android.graphics.Color.LTGRAY else android.graphics.Color.WHITE
        for (texto in textos)
        {
            val tv = TextView(this)
            tv.text = texto
            tv.setPadding(15, 15, 15, 15)
            tv.setBackgroundColor(colorFondo)
            tv.setTextColor(android.graphics.Color.BLACK)
            if (esCabecera) tv.setTypeface(null, android.graphics.Typeface.BOLD)
            val params = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT)
            params.setMargins(1, 1, 1, 1)
            fila.addView(tv, params)
        }
        return fila
    }

    private fun mostrarDialogoTabla(titulo: String, tabla: TableLayout)
    {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(titulo)
        val scrollView = ScrollView(this)
        val horizontalScroll = HorizontalScrollView(this)
        horizontalScroll.addView(tabla)
        scrollView.addView(horizontalScroll)
        builder.setView(scrollView)
        builder.setPositiveButton("Cerrar") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }

    data class ErrorCompleto(
        val lexema: String,
        val linea: Int,
        val columna: Int,
        val tipo: String,
        val descripcion: String
    )
}