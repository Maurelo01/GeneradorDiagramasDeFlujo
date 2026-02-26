package com.example.generadordiagramas.analizador;
import java_cup.runtime.*;
import java.util.ArrayList;

%%
%class Lexer
%cup
%line
%column
%public
%unicode
%ignorecase

%{
    public static class ErrorLexico
    {
        public String lexema;
        public int linea;
        public int columna;
        public String descripcion;

        public ErrorLexico(String lexema, int linea, int columna, String descripcion)
        {
            this.lexema = lexema;
            this.linea = linea;
            this.columna = columna;
            this.descripcion = descripcion;
        }
    }

    public ArrayList<ErrorLexico> listaErrores = new ArrayList<>();
    private Symbol symbol(int type, Object value)
    {
        return new Symbol(type, yyline + 1, yycolumn + 1, value);
    }

    private void reportarError(String lexema, String descripcion)
    {
        listaErrores.add(new ErrorLexico(lexema, yyline + 1, yycolumn + 1, descripcion));
        System.err.println("Error Léxico en línea " + (yyline + 1) + ", columna " + (yycolumn + 1) + ": " + descripcion + " '" + lexema + "'");
    }
%}

/***** EXPRESIONES REGULARES *****/
Letra = [a-zA-Z_]
Digito = [0-9]
Entero = {Digito}+
Decimal = {Entero}"."{Entero}
Identificador = {Letra}({Letra}|{Digito})*
Cadena = \"[^\"]*\"
CadenaSinCerrar = \"[^\"\n]*
Comentario = #[^\n]*\n?
ColorHex = H[0-9a-fA-F]{6}
EspaciosEnBlanco = [ \t\r\n\f]+

%%
/***** REGLAS LEXICAS *****/

{EspaciosEnBlanco}
{
    /* Ignorar */
}

{Comentario}
{
    /* Ignorar */
}

/* Separador de secciones */
"%%%%"
{
    return symbol(sym.SEPARADOR, yytext());
}

/* Palabras Reservadas */
"INICIO"
{
    return symbol(sym.INICIO, yytext());
}

"FIN"
{
    return symbol(sym.FIN, yytext());
}

"VAR"
{
    return symbol(sym.VAR, yytext());
}

"SI"
{
    return symbol(sym.SI, yytext());
}

"ENTONCES"
{
    return symbol(sym.ENTONCES, yytext());
}

"MIENTRAS"
{
    return symbol(sym.MIENTRAS, yytext());
}

"HACER"
{
    return symbol(sym.HACER, yytext());
}

"MOSTRAR"
{
    return symbol(sym.MOSTRAR, yytext());
}

"LEER"
{
    return symbol(sym.LEER, yytext());
}

"FINSI"
{
    return symbol(sym.FINSI, yytext());
}

"FINMIENTRAS"
{
    return symbol(sym.FINMIENTRAS, yytext());
}

/* Instrucciones de configuracion */
"%DEFAULT"
{
    return symbol(sym.CONF_DEFAULT, yytext());
}

"%COLOR_TEXTO_SI"
{
    return symbol(sym.CONF_COLOR_TEXT_SI, yytext());
}

"%COLOR_SI"
{
    return symbol(sym.CONF_COLOR_SI, yytext());
}

"%FIGURA_SI"
{
    return symbol(sym.CONF_FIGURA_SI, yytext());
}

"%LETRA_SI"
{
    return symbol(sym.CONF_LETRA_SI, yytext());
}

"%LETRA_SIZE_SI"
{
    return symbol(sym.CONF_LETRA_SIZE_SI, yytext());
}

"%COLOR_TEXTO_MIENTRAS"
{
    return symbol(sym.CONF_COLOR_TEXT_MIENTRAS, yytext());
}

"%COLOR_MIENTRAS"
{
    return symbol(sym.CONF_COLOR_MIENTRAS, yytext());
}

"%FIGURA_MIENTRAS"
{
    return symbol(sym.CONF_FIGURA_MIENTRAS, yytext());
}

"%LETRA_MIENTRAS"
{
    return symbol(sym.CONF_LETRA_MIENTRAS, yytext());
}

"%LETRA_SIZE_MIENTRAS"
{
    return symbol(sym.CONF_LETRA_SIZE_MIENTRAS, yytext());
}

"%COLOR_TEXTO_BLOQUE"
{
    return symbol(sym.CONF_COLOR_TEXT_BLOQUE, yytext());
}

"%COLOR_BLOQUE"
{
    return symbol(sym.CONF_COLOR_BLOQUE, yytext());
}

"%FIGURA_BLOQUE"
{
    return symbol(sym.CONF_FIGURA_BLOQUE, yytext());
}

"%LETRA_BLOQUE"
{
    return symbol(sym.CONF_LETRA_BLOQUE, yytext());
}

"%LETRA_SIZE_BLOQUE"
{
    return symbol(sym.CONF_LETRA_SIZE_BLOQUE, yytext());
}

/* Figuras permitidas */
"ELIPSE"
{
    return symbol(sym.FIG_ELIPSE, yytext());
}

"CIRCULO"
{
    return symbol(sym.FIG_CIRCULO, yytext());
}

"PARALELOGRAMO"
{
    return symbol(sym.FIG_PARALELOGRAMO, yytext());
}

"RECTANGULO"
{
    return symbol(sym.FIG_RECTANGULO, yytext());
}

"ROMBO"
{
    return symbol(sym.FIG_ROMBO, yytext());
}

"RECTANGULO_REDONDEADO"
{
    return symbol(sym.FIG_REC_REDONDEADO, yytext());
}

/* Fuentes permitidas  */
"ARIAL"
{
    return symbol(sym.FONT_ARIAL, yytext());
}

"TIMES_NEW_ROMAN"
{
    return symbol(sym.FONT_TIMES, yytext());
}

"COMIC_SANS"
{
    return symbol(sym.FONT_COMIC, yytext());
}

"VERDANA"
{
    return symbol(sym.FONT_VERDANA, yytext());
}

/* Operadores Aritméticos */
"+"
{
    return symbol(sym.MAS, yytext());
}

"-"
{
    return symbol(sym.MENOS, yytext());
}

"*"
{
    return symbol(sym.POR, yytext());
}

"/"
{
    return symbol(sym.DIV, yytext());
}

/* Operadores relacionales */
"=="
{
    return symbol(sym.IGUALDAD, yytext());
}

"!="
{
    return symbol(sym.DIFERENTE, yytext());
}

">"
{
    return symbol(sym.MAYOR, yytext());
}

"<"
{
    return symbol(sym.MENOR, yytext());
}

">="
{
    return symbol(sym.MAYOR_IGUAL, yytext());
}

"<="
{
    return symbol(sym.MENOR_IGUAL, yytext());
}

/* Operadores logicos */
"&&"
{
    return symbol(sym.AND, yytext());
}

"||"
{
    return symbol(sym.OR, yytext());
}

"!"
{
    return symbol(sym.NOT, yytext());
}

/* Signos de Agrupacion */
"="
{
    return symbol(sym.IGUAL_ASIG, yytext());
}

"("
{
    return symbol(sym.PAR_IZQ, yytext());
}

")"
{
    return symbol(sym.PAR_DER, yytext());
}

"|"
{
    return symbol(sym.PIPE, yytext());
}

","
{
    return symbol(sym.COMA, yytext());
}

/* Patrones */
{ColorHex}
{
    return symbol(sym.COLOR_HEX, yytext());
}

{Cadena}
{
    return symbol(sym.CADENA, yytext());
}

{Decimal}
{
    return symbol(sym.DECIMAL, yytext());
}

{Entero}
{
    return symbol(sym.ENTERO, yytext());
}

{Identificador}
{
    return symbol(sym.ID, yytext());
}

/* Manejo de Errores Léxicos  */

{CadenaSinCerrar}
{
    reportarError(yytext(), "Cadena sin cerrar - falta comilla de cierre");
}

/* Configuraciones no reconocidas (deben ir antes del catch-all) */
"%"[a-zA-Z_][a-zA-Z0-9_]*
{
    reportarError(yytext(), "Configuración no reconocida");
}

/* Caracteres individuales no permitidos */
"@"
{
    reportarError(yytext(), "Carácter '@' no permitido en el lenguaje");
}

"$"
{
    reportarError(yytext(), "Carácter '$' no permitido en el lenguaje");
}

"^"
{
    reportarError(yytext(), "Carácter '^' no permitido en el lenguaje");
}

"&"
{
    reportarError(yytext(), "Carácter '&' no permitido en el lenguaje (use '&&' para AND)");
}

"~"
{
    reportarError(yytext(), "Carácter '~' no permitido en el lenguaje");
}

"`"
{
    reportarError(yytext(), "Carácter '`' no permitido en el lenguaje");
}

"\\"
{
    reportarError(yytext(), "Carácter '\\' no permitido en el lenguaje");
}

";"
{
    reportarError(yytext(), "Carácter ';' no permitido en el lenguaje");
}

":"
{
    reportarError(yytext(), "Carácter ':' no permitido en el lenguaje");
}

"["
{
    reportarError(yytext(), "Carácter '[' no permitido en el lenguaje");
}

"]"
{
    reportarError(yytext(), "Carácter ']' no permitido en el lenguaje");
}

"{"
{
    reportarError(yytext(), "Carácter '{' no permitido en el lenguaje");
}

"}"
{
    reportarError(yytext(), "Carácter '}' no permitido en el lenguaje");
}

"?"
{
    reportarError(yytext(), "Carácter '?' no permitido en el lenguaje");
}

/* Catch-all para cualquier otro carácter no reconocido */
.
{
    reportarError(yytext(), "Token no reconocido");
}