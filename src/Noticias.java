import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Noticias {
    private int id;
    private String titulo;
    private String introducao;
    private LocalDateTime dataPublicacao;
    private String link;
    private String tipo;
    private String fonte;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public Noticias(int id, String titulo, String introducao, String dataPublicacao, String link, String tipo, String fonte) {
        this.id = id;
        this.titulo = titulo;
        this.introducao = introducao;
        this.dataPublicacao = LocalDateTime.parse(dataPublicacao, FORMATTER);
        this.link = link;
        this.tipo = tipo;
        this.fonte = fonte;
    }

    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public LocalDateTime getDataPublicacao() {
        return dataPublicacao;
    }

    public String getTipo() {
        return tipo;
    }

    @Override
    public String toString() {
        return "id: " + id + "\n" +
                "Título: " + titulo + "\n" +
                "Introdução: " + introducao + "\n" +
                "Data de Publicação: " + dataPublicacao.format(FORMATTER) + "\n" +
                "Link: " + link + "\n" +
                "Tipo: " + tipo + "\n" +
                "Fonte: " + fonte + "\n";
    }
}