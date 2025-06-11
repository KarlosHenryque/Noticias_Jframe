import java.util.*;
import java.util.stream.Collectors;

public class LeituraNoticias {
    private Set<Integer> lidas = new HashSet<>();
    private Set<Integer> paraLerDepois = new HashSet<>();

    public void marcarComoLida(int id) {
        lidas.add(id);
    }

    public void adicionarParaLerDepois(int id) {
        paraLerDepois.add(id);
    }

    public List<Noticias> obterListaOrdenada(List<Noticias> noticiasList, Set<Integer> idSet, String ordenacaoOpcao) {
        if (idSet.isEmpty()) {
            return Collections.emptyList();
        }

        List<Noticias> lista = idSet.stream()
                .filter(id -> id >= 0 && id < noticiasList.size())
                .map(noticiasList::get)
                .collect(Collectors.toList());

        switch (ordenacaoOpcao) {
            case "Título (A-Z)":
                lista.sort(Comparator.comparing(Noticias::getTitulo, String.CASE_INSENSITIVE_ORDER));
                break;
            case "Data de Publicação":
                lista.sort(Comparator.comparing(Noticias::getDataPublicacao));
                break;
            case "Tipo/Categoria":
                lista.sort(Comparator.comparing(n -> Optional.ofNullable(n.getTipo()).orElse("")));
                break;
            default:
                break;
        }
        return lista;
    }

    public Set<Integer> getLidas() {
        return lidas;
    }

    public Set<Integer> getParaLerDepois() {
        return paraLerDepois;
    }
}