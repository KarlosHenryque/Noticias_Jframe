import com.google.gson.*;
import java.time.LocalDate;
import java.util.*;

public class UtilNoticias {

    public static List<Noticias> converterJsonParaLista(String json) {
        List<Noticias> noticiasList = new ArrayList<>();

        try {
            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
            JsonArray jsonArray = jsonObject.getAsJsonArray("items");

            int id = 0;
            for (JsonElement elem : jsonArray) {
                JsonObject obj = elem.getAsJsonObject();

                String titulo = obj.get("titulo").getAsString();
                String introducao = obj.get("introducao").getAsString();
                String dataPublicacao = obj.get("data_publicacao").getAsString();
                String link = obj.get("link").getAsString();
                String tipo = obj.get("tipo").getAsString();
                String fonte = obj.get("editorias").getAsString();

                Noticias noticia = new Noticias(id++, titulo, introducao, dataPublicacao, link, tipo, fonte);
                noticiasList.add(noticia);
            }
        } catch (Exception e) {
            System.err.println("Erro ao converter JSON: " + e.getMessage());
        }

        return noticiasList;
    }
}