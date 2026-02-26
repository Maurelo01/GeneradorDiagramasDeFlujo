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

        // Inicialmente deshabilitar botones de reportes
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
            Toast.makeText(this, "Por favor ingrese código", Toast.LENGTH_SHORT).show()
            return
        }

        try
        {
            val lector = StringReader(codigoEntrada)
            val lexer = Lexer(lector)
            parser = Parser(lexer)

            // Realizar análisis
            val resultado = parser!!.parse()
            val diagrama = resultado.value as? ArrayList<FiguraDiagrama>

            // Verificar si hay errores
            if (parser!!.listaErrores.isEmpty())
            {
                // No hay errores, Mostrar diagrama y habilitar reportes
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
                // Hay errores No mostrar diagrama, solo reporte de errores
                scrollDiagrama.visibility = ScrollView.GONE
                txtMensaje.visibility = TextView.VISIBLE
                txtMensaje.text = "Se encontraron ${parser!!.listaErrores.size} errores.\nPresione 'Reporte de Errores' para ver detalles."

                // Solo habilitar reporte de errores
                btnReporteErrores.isEnabled = true
                btnReporteOperadores.isEnabled = false
                btnReporteControl.isEnabled = false

                Toast.makeText(this, "Se encontraron ${parser!!.listaErrores.size} errores", Toast.LENGTH_LONG).show()
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
        if (parser == null || parser!!.listaErrores.isEmpty())
        {
            Toast.makeText(this, "No hay errores para mostrar", Toast.LENGTH_SHORT).show()
            return
        }

        val reporte = StringBuilder()
        reporte.append("REPORTE DE ERRORES\n")
        reporte.append("=".repeat(80) + "\n\n")
        reporte.append(String.format("%-20s %-8s %-10s %-15s %s\n", "Lexema", "Línea", "Columna", "Tipo", "Descripción"))
        reporte.append("-".repeat(80) + "\n")

        for (error in parser!!.listaErrores)
        {
            reporte.append(String.format("%-20s %-8d %-10d %-15s %s\n", error.lexema.take(20), error.linea, error.columna, "Sintáctico", error.descripcion))
        }

        reporte.append("\n")
        reporte.append("Total de errores: ${parser!!.listaErrores.size}")

        mostrarDialogoReporte("Reporte de Errores", reporte.toString())
    }

    private fun mostrarReporteOperadores()
    {
        if (parser == null || parser!!.listaOperadores.isEmpty())
        {
            Toast.makeText(this, "No hay operadores para mostrar", Toast.LENGTH_SHORT).show()
            return
        }

        val reporte = StringBuilder()
        reporte.append("REPORTE DE OPERADORES MATEMÁTICOS\n")
        reporte.append("=".repeat(80) + "\n\n")
        reporte.append(String.format("%-20s %-8s %-10s %s\n",
            "Operador", "Línea", "Columna", "Ocurrencia"))
        reporte.append("-".repeat(80) + "\n")

        for (op in parser!!.listaOperadores)
        {
            reporte.append(String.format("%-20s %-8d %-10d %s\n", op.operador, op.linea, op.columna, op.ocurrencia))
        }

        reporte.append("\n")
        reporte.append("Total de operadores: ${parser!!.listaOperadores.size}")

        mostrarDialogoReporte("Reporte de Operadores Matemáticos", reporte.toString())
    }

    private fun mostrarReporteControl()
    {
        if (parser == null || parser!!.listaControl.isEmpty())
        {
            Toast.makeText(this, "No hay estructuras de control para mostrar", Toast.LENGTH_SHORT).show()
            return
        }

        val reporte = StringBuilder()
        reporte.append("REPORTE DE ESTRUCTURAS DE CONTROL\n")
        reporte.append("=".repeat(80) + "\n\n")
        reporte.append(String.format("%-20s %-8s %s\n",
            "Objeto", "Línea", "Condición"))
        reporte.append("-".repeat(80) + "\n")

        for (ctrl in parser!!.listaControl)
        {
            reporte.append(String.format("%-20s %-8d %s\n", ctrl.objeto, ctrl.linea, ctrl.condicion))
        }

        reporte.append("\n")
        reporte.append("Total de estructuras: ${parser!!.listaControl.size}")

        mostrarDialogoReporte("Reporte de Estructuras de Control", reporte.toString())
    }

    private fun mostrarDialogoReporte(titulo: String, contenido: String)
    {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(titulo)

        val scrollView = ScrollView(this)
        val textView = TextView(this)
        textView.text = contenido
        textView.setPadding(40, 40, 40, 40)
        textView.textSize = 12f
        textView.typeface = android.graphics.Typeface.MONOSPACE

        scrollView.addView(textView)
        builder.setView(scrollView)

        builder.setPositiveButton("Cerrar") { dialog, _ ->
            dialog.dismiss()
        }
        builder.setNeutralButton("Copiar") { _, _ ->
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = android.content.ClipData.newPlainText("Reporte", contenido)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Reporte copiado al portapapeles", Toast.LENGTH_SHORT).show()
        }

        builder.create().show()
    }
}