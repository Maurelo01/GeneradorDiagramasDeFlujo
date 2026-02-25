package com.example.generadordiagramas

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.io.StringReader

import com.example.generadordiagramas.analizador.Lexer
import com.example.generadordiagramas.analizador.Parser

class MainActivity : AppCompatActivity() {

    private lateinit var etCodigo: EditText
    private lateinit var btnCompilar: Button
    private lateinit var btnReportes: Button
    private var listaLexicos = ArrayList<Lexer.ErrorLexico>()
    private var listaSintacticos = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        etCodigo = findViewById(R.id.etCodigo)
        btnCompilar = findViewById(R.id.btnCompilar)
        btnReportes = findViewById(R.id.btnReportes)
        btnCompilar.setOnClickListener()
        {
            val codigoTexto = etCodigo.text.toString()
            if (codigoTexto.isBlank())
            {
                Toast.makeText(this, "Por favor ingresa algún código", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            compilarCodigo(codigoTexto)
        }
        btnReportes.setOnClickListener()
        {
            mostrarVentanaErrores()
        }
    }

    private fun compilarCodigo(codigo: String)
    {
        try
        {
            val lexer = Lexer(StringReader(codigo))
            val parser = Parser(lexer)
            parser.parse()
            listaLexicos = lexer.listaErrores ?: ArrayList()
            listaSintacticos = parser.listaErrores as? ArrayList<Any> ?: ArrayList()
            val totalErrores = listaLexicos.size + listaSintacticos.size
            if (totalErrores == 0)
            {
                btnReportes.visibility = View.GONE
                Toast.makeText(this, "¡Compilación Exitosa!", Toast.LENGTH_SHORT).show()
            }
            else
            {
                btnReportes.visibility = View.VISIBLE
                Toast.makeText(this, "Se encontraron $totalErrores errores", Toast.LENGTH_LONG).show()
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
            Toast.makeText(this, "Ocurrió un error en el análisis", Toast.LENGTH_LONG).show()
        }
    }

    private fun mostrarVentanaErrores()
    {
        val reporte = StringBuilder()
        if (listaLexicos.isNotEmpty())
        {
            reporte.append(" ERRORES LÉXICOS ")
            for (error in listaLexicos)
            {
                reporte.append(" Símbolo '${error.lexema}' no reconocido en Línea: ${error.linea}, Columna: ${error.columna}")
            }
        }
        if (listaSintacticos.isNotEmpty())
        {
            reporte.append(" ERRORES SINTÁCTICOS ")
            for (errorObj in listaSintacticos)
            {
                if (errorObj is Parser.ErrorSintactico)
                {
                    reporte.append(" Se esperaba otro token cerca de '${errorObj.lexema}' en Línea: ${errorObj.linea}, Columna: ${errorObj.columna}")
                }
            }
        }
        AlertDialog.Builder(this).setTitle("Reporte de Errores").setMessage(reporte.toString()).setPositiveButton("Entendido") { dialog, _ -> dialog.dismiss()}.show()
    }
}