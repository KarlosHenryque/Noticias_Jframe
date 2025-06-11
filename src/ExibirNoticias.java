import java.util.*;

public class ExibirNoticias {

    public static String formatarNoticias(List<Noticias> noticias) {
        StringBuilder sb = new StringBuilder();
        for (Noticias noticia : noticias) {
            sb.append(noticia.toString()).append("\n");
            sb.append("------------------------------------\n");
        }
        return sb.toString();
    }
}