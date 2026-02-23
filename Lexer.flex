import java_cup.runtime.*;
import java.util.ArrayList;

%%
%class Lexer
%cup
%line
%column
%public
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
%}
/***** EXPRESIONES REGULARES *****/
Letra = [a-zA-Z_]
Digito = [0-9]
Entero = {Digito}+
Decimal = {Entero}"."{Entero}
Identificador = {Letra}({Letra}|{Digito})*
Cadena = \"[^\"]*\"
Comentario = #[^\n]*\n?
ColorHex = H[0-9a-fA-F]{6}
EspaciosEnBlanco = [ \t\r\n]+ // Espacios en blaco para ignorar

%%
/***** REGLAS LEXICAS *****/

// Comentarios y espacios
{EspaciosEnBlanco} { /* Ignorar */ }
{Comentario} { /* Ignorar */ }

/* Separador de secciones */
"%%%%"
{
    return new Symbol(sym.SEPARADOR, yyline + 1, yycolumn + 1, yytext());
} 

/* Palabras Reservadas */
"INICIO"
{
    return new Symbol(sym.INICIO, yyline + 1, yycolumn + 1, yytext());
}
"FIN"
{
    return new Symbol(sym.FIN, yyline + 1, yycolumn + 1, yytext());
}
"VAR"
{
    return new Symbol(sym.VAR, yyline + 1, yycolumn + 1, yytext());
}
"SI"
{
    return new Symbol(sym.SI, yyline + 1, yycolumn + 1, yytext());
}
"ENTONCES"
{
    return new Symbol(sym.ENTONCES, yyline + 1, yycolumn + 1, yytext());
}
"MIENTRAS"
{
    return new Symbol(sym.MIENTRAS, yyline + 1, yycolumn + 1, yytext());
}
"HACER"
{
    return new Symbol(sym.HACER, yyline + 1, yycolumn + 1, yytext());
}
"MOSTRAR"
{
    return new Symbol(sym.MOSTRAR, yyline + 1, yycolumn + 1, yytext());
}
"LEER"
{
    return new Symbol(sym.LEER, yyline + 1, yycolumn + 1, yytext());
}
"FINSI"
{
    return new Symbol(sym.FINSI, yyline + 1, yycolumn + 1, yytext());
}
"FINMIENTRAS"
{
    return new Symbol(sym.FINMIENTRAS, yyline + 1, yycolumn + 1, yytext());
}

/* Instrucciones de configuracion */
"%DEFAULT"
{
    return new Symbol(sym.CONF_DEFAULT, yyline + 1, yycolumn + 1, yytext());
}
"%COLOR_TEXTO_SI"
{
    return new Symbol(sym.CONF_COLOR_TEXT_SI, yyline + 1, yycolumn + 1, yytext());
}
"%COLOR_SI"
{
    return new Symbol(sym.CONF_COLOR_SI, yyline + 1, yycolumn + 1, yytext());
}
"%FIGURA_SI"
{
    return new Symbol(sym.CONF_FIGURA_SI, yyline + 1, yycolumn + 1, yytext());
}
"%LETRA_SI"
{
    return new Symbol(sym.CONF_LETRA_SI, yyline + 1, yycolumn + 1, yytext());
}
"%LETRA_SIZE_SI"
{
    return new Symbol(sym.CONF_LETRA_SIZE_SI, yyline + 1, yycolumn + 1, yytext());
}
"%COLOR_TEXTO_MIENTRAS"
{
    return new Symbol(sym.CONF_COLOR_TEXT_MIENTRAS, yyline + 1, yycolumn + 1, yytext());
}
"%COLOR_MIENTRAS"
{
    return new Symbol(sym.CONF_COLOR_MIENTRAS, yyline + 1, yycolumn + 1, yytext());
}
"%FIGURA_MIENTRAS"
{
    return new Symbol(sym.CONF_FIGURA_MIENTRAS, yyline + 1, yycolumn + 1, yytext());
}
"%LETRA_MIENTRAS"
{
    return new Symbol(sym.CONF_LETRA_MIENTRAS, yyline + 1, yycolumn + 1, yytext());
}
"%LETRA_SIZE_MIENTRAS"
{
    return new Symbol(sym.CONF_LETRA_SIZE_MIENTRAS, yyline + 1, yycolumn + 1, yytext());
}
"%COLOR_TEXTO_BLOQUE"
{
    return new Symbol(sym.CONF_COLOR_TEXT_BLOQUE, yyline + 1, yycolumn + 1, yytext());
}
"%COLOR_BLOQUE"
{
    return new Symbol(sym.CONF_COLOR_BLOQUE, yyline + 1, yycolumn + 1, yytext());
}
"%FIGURA_BLOQUE"
{
    return new Symbol(sym.CONF_FIGURA_BLOQUE, yyline + 1, yycolumn + 1, yytext());
}
"%LETRA_BLOQUE"
{
    return new Symbol(sym.CONF_LETRA_BLOQUE, yyline + 1, yycolumn + 1, yytext());
}
"%LETRA_SIZE_BLOQUE"
{
    return new Symbol(sym.CONF_LETRA_SIZE_BLOQUE, yyline + 1, yycolumn + 1, yytext());
}

/* Figuras permitidas */
"ELIPSE"
{
    return new Symbol(sym.FIG_ELIPSE, yyline + 1, yycolumn + 1, yytext());
}
"CIRCULO"
{
    return new Symbol(sym.FIG_CIRCULO, yyline + 1, yycolumn + 1, yytext());
}
"PARALELOGRAMO"
{
    return new Symbol(sym.FIG_PARALELOGRAMO, yyline + 1, yycolumn + 1, yytext());
}
"RECTANGULO"
{
    return new Symbol(sym.FIG_RECTANGULO, yyline + 1, yycolumn + 1, yytext());
}
"ROMBO"
{
    return new Symbol(sym.FIG_ROMBO, yyline + 1, yycolumn + 1, yytext());
}
"RECTANGULO_REDONDEADO"
{
    return new Symbol(sym.FIG_REC_REDONDEADO, yyline + 1, yycolumn + 1, yytext());
}

/* Fuentes permitidas  */
"ARIAL"
{
    return new Symbol(sym.FONT_ARIAL, yyline + 1, yycolumn + 1, yytext());
}
"TIMES_NEW_ROMAN"
{
    return new Symbol(sym.FONT_TIMES, yyline + 1, yycolumn + 1, yytext());
}
"COMIC_SANS"
{
    return new Symbol(sym.FONT_COMIC, yyline + 1, yycolumn + 1, yytext());
}
"VERDANA"
{
    return new Symbol(sym.FONT_VERDANA, yyline + 1, yycolumn + 1, yytext());
}

/* Operadores Aritméticos */
"+"
{
    return new Symbol(sym.MAS, yyline + 1, yycolumn + 1, yytext());
} 
"-"
{
    return new Symbol(sym.MENOS, yyline + 1, yycolumn + 1, yytext());
} 
"*"
{
    return new Symbol(sym.POR, yyline + 1, yycolumn + 1, yytext());
} 
"/"
{
    return new Symbol(sym.DIV, yyline + 1, yycolumn + 1, yytext());
} 

/* Operadores relacionales */
"=="
{
    return new Symbol(sym.IGUALDAD, yyline + 1, yycolumn + 1, yytext());
} 
"!="
{
    return new Symbol(sym.DIFERENTE, yyline + 1, yycolumn + 1, yytext());
}
">" 
{
    return new Symbol(sym.MAYOR, yyline + 1, yycolumn + 1, yytext());
} 
"<" 
{
    return new Symbol(sym.MENOR, yyline + 1, yycolumn + 1, yytext());
} 
">="
{
    return new Symbol(sym.MAYOR_IGUAL, yyline + 1, yycolumn + 1, yytext());
} 
"<="
{
    return new Symbol(sym.MENOR_IGUAL, yyline + 1, yycolumn + 1, yytext());
} 

/* Operadores logicos */
"&&"
{
    return new Symbol(sym.AND, yyline + 1, yycolumn + 1, yytext());
} 
"||"
{
    return new Symbol(sym.OR, yyline + 1, yycolumn + 1, yytext());
} 
"!" 
{
    return new Symbol(sym.NOT, yyline + 1, yycolumn + 1, yytext());
} 

/* Signos de Agrupacion */
"="
{
    return new Symbol(sym.IGUAL_ASIG, yyline + 1, yycolumn + 1, yytext());
} 
"("
{
    return new Symbol(sym.PAR_IZQ, yyline + 1, yycolumn + 1, yytext());
} 
")"
{
    return new Symbol(sym.PAR_DER, yyline + 1, yycolumn + 1, yytext());
} 
"|"
{
    return new Symbol(sym.PIPE, yyline + 1, yycolumn + 1, yytext());
} 
","
{
    return new Symbol(sym.COMA, yyline + 1, yycolumn + 1, yytext());
} 

/* Patrones */
{Entero}
{
    return new Symbol(sym.ENTERO, yyline + 1, yycolumn + 1, yytext());
}
{Decimal}
{
    return new Symbol(sym.DECIMAL, yyline + 1, yycolumn + 1, yytext());
}
{ColorHex}
{
    return new Symbol(sym.COLOR_HEX, yyline + 1, yycolumn + 1, yytext());
}
{Identificador}
{
    return new Symbol(sym.ID, yyline + 1, yycolumn + 1, yytext());
}
{Cadena}
{
    return new Symbol(sym.CADENA, yyline + 1, yycolumn + 1, yytext());
}

/* Manejo de Errores Léxicos  */
. { 
    listaErrores.add(new ErrorLexico(yytext(), yyline + 1, yycolumn + 1, "No existe este símbolo en el lenguaje"));
    System.err.println("Error Léxico: " + yytext() + " en línea " + (yyline+1) + " y columna " + (yycolumn+1)); 
}