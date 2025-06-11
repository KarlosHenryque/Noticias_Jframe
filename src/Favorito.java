import java.util.*;

public class Favorito {

    private Set<Integer> favoritos;

    public Favorito() {
        favoritos = new HashSet<>();
    }

    public void adicionarFavorito(int idNoticia) {
        favoritos.add(idNoticia);
    }

    public void removerFavorito(int idNoticia) {
        favoritos.remove(idNoticia);
    }

    public List<Noticias> getFavoritosList(List<Noticias> noticiasList) {
        List<Noticias> favList = new ArrayList<>();
        for (int id : favoritos) {
            if (id >= 0 && id < noticiasList.size()) {
                favList.add(noticiasList.get(id));
            }
        }
        return favList;
    }

    public Set<Integer> getFavoritos() {
        return favoritos;
    }

    public void setFavoritos(Set<Integer> favoritos) {
        this.favoritos = favoritos != null ? favoritos : new HashSet<>();
    }

    public boolean contem(int idNoticia) {
        return favoritos.contains(idNoticia);
    }
}