import java.io.StringReader;

public class Main 
{
    public static void main(String[] args)
    {
        String entrada = 
            "INICIO\n" +
            "VAR a=10\n" +
            "VAR b=20\n" +
            "SI (a<b) ENTONCES\n" +
            "MOSTRAR \"a es menor que b\"\n" +
            "FIN SI\n" +
            "MIENTRAS (a<15) HACER\n" +
            "a=a+1\n" +
            "MOSTRAR a\n" +
            "FIN MIENTRAS\n" +
            "MOSTRAR \"Fin del programa\"\n" +
            "FIN\n" +
            "%%%%\n" +
            "%DEFAULT=1\n" +
            "%COLOR_TEXTO_SI=12,45,1|1\n" +
            "%FIGURA_MIENTRAS CIRCULO 1\n" +
            "%DEFAULT 3\n";

        try 
        {
            System.out.println("Iniciando análisis...");
            Lexer lexer = new Lexer(new StringReader(entrada));
            Parser parser = new Parser(lexer);
            parser.parse();
            System.out.println("\n--- RESULTADOS ---");
            System.out.println("Análisis completado exitosamente.");
            System.out.println("Cantidad de errores léxicos encontrados: " + lexer.listaErrores.size());
            System.out.println("Cantidad de errores sintácticos encontrados: " + parser.listaErrores.size());
            
        }
        catch (Exception e)
        {
            System.err.println("error durante el analisis:");
            e.printStackTrace();
        }
    }
}