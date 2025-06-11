import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MenuNoticias extends JFrame {

    private static final String ARQUIVO_DADOS = "dados.json";

    static class DadosPersistidos {
        Set<Integer> favoritos = new HashSet<>();
        Set<Integer> lidas = new HashSet<>();
        Set<Integer> paraLerDepois = new HashSet<>();
        Set<String> nomesUsuarios = new HashSet<>();
    }

    private JTextField nomeField;
    private JTextField buscaTituloField;
    private JTextField buscaDataField;
    private JTextField idFavoritoField;
    private JTextField idRemoverField;
    private JComboBox<String> listaFavoritosCombo;
    private JComboBox<String> ordenacaoCombo;
    private JTextArea resultadosArea;

    private List<Noticias> noticiasList;
    private Favorito favoritos;
    private LeituraNoticias leitura;
    private String nomeUsuario;

    public MenuNoticias() {
        inicializarDados();

        setTitle("Sistema de Notícias");
        setSize(1200, 1100);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));

        configurarFechamentoJanela();

        mostrarDialogBoasVindas();

        criarInterface();

        carregarNoticias();

        carregarDadosPersistidos();
    }

    private void inicializarDados() {
        favoritos = new Favorito();
        leitura = new LeituraNoticias();
        noticiasList = new ArrayList<>();
    }

    private void configurarFechamentoJanela() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                salvarESair();
            }
        });
    }

    private void mostrarDialogBoasVindas() {
        JDialog dialog = new JDialog(this, "Boas-vindas", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel iconLabel = new JLabel("📰");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        panel.add(iconLabel, gbc);

        JLabel titleLabel = new JLabel("Bem-vindo ao Sistema de Notícias!");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(titleLabel, gbc);

        JLabel nameLabel = new JLabel("Por favor, digite seu nome:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 20, 20, 20);
        panel.add(nameLabel, gbc);

        nomeField = new JTextField(20);
        nomeField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        Dimension size = nomeField.getPreferredSize();
        size.height = 40;
        nomeField.setPreferredSize(size);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(nomeField, gbc);

        dialog.add(panel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton cancelButton = new JButton("❌ Cancelar");
        JButton okButton = new JButton("✅ OK");

        cancelButton.addActionListener(e -> System.exit(0));
        okButton.addActionListener(e -> {
            nomeUsuario = nomeField.getText().trim();
            if (nomeUsuario.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Nome não pode ficar vazio!", "Erro", JOptionPane.ERROR_MESSAGE);
            } else {
                dialog.dispose();
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(okButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void criarInterface() {
        setLayout(new BorderLayout());

        JPanel leftPanel = createLeftPanel();
        add(leftPanel, BorderLayout.WEST);

        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);

        setTitle("Sistema de Notícias - " + nomeUsuario);
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "📋 MENU DE AÇÕES",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12)
        ));
        panel.setPreferredSize(new Dimension(280, 0));
        panel.setBackground(new Color(248, 249, 250));

        panel.add(createBuscarNoticiaPanel());
        panel.add(Box.createVerticalStrut(10));

        panel.add(createAdicionarNoticiaPanel());
        panel.add(Box.createVerticalStrut(10));

        panel.add(createRemoverNoticiaPanel());
        panel.add(Box.createVerticalStrut(10));

        panel.add(createListarNoticiasPanel());
        panel.add(Box.createVerticalStrut(10));

        panel.add(createAcoesistemaPanel());

        return panel;
    }

    private JPanel createBuscarNoticiaPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("🔍 Buscar Notícia"));
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(3, 3, 3, 3);
        panel.add(new JLabel("Título:"), gbc);

        buscaTituloField = new JTextField(12);
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(buscaTituloField, gbc);

        JButton buscarTituloBtn = new JButton("🔍 Buscar");
        buscarTituloBtn.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(buscarTituloBtn, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.insets = new Insets(8, 3, 3, 3);
        panel.add(new JLabel("Data (dd/MM/yyyy):"), gbc);

        buscaDataField = new JTextField(12);
        gbc.gridx = 0; gbc.gridy = 4; gbc.insets = new Insets(3, 3, 3, 3);
        panel.add(buscaDataField, gbc);

        JButton buscarDataBtn = new JButton("📅 Buscar");
        buscarDataBtn.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(buscarDataBtn, gbc);

        buscarTituloBtn.addActionListener(e -> buscarPorTitulo());
        buscarDataBtn.addActionListener(e -> buscarPorData());

        return panel;
    }

    private JPanel createAdicionarNoticiaPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("➕ Adicionar Notícia"));
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(3, 3, 3, 3);
        panel.add(new JLabel("ID:"), gbc);

        idFavoritoField = new JTextField(12);
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(idFavoritoField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Lista:"), gbc);

        String[] listas = {"Favoritos", "Lidas", "Para Ler Depois"};
        listaFavoritosCombo = new JComboBox<>(listas);
        listaFavoritosCombo.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(listaFavoritosCombo, gbc);

        JButton adicionarBtn = new JButton("➕ Adicionar");
        adicionarBtn.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(adicionarBtn, gbc);

        adicionarBtn.addActionListener(e -> adicionarNoticia());

        return panel;
    }

    private JPanel createRemoverNoticiaPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("➖ Remover Notícia"));
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(3, 3, 3, 3);
        panel.add(new JLabel("ID:"), gbc);

        idRemoverField = new JTextField(12);
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(idRemoverField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Lista:"), gbc);

        JComboBox<String> listaRemoverCombo = new JComboBox<>(new String[]{"Favoritos", "Lidas", "Para Ler Depois"});
        listaRemoverCombo.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(listaRemoverCombo, gbc);

        JButton removerBtn = new JButton("➖ Remover");
        removerBtn.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(removerBtn, gbc);

        removerBtn.addActionListener(e -> removerNoticia(listaRemoverCombo));

        return panel;
    }

    private JPanel createListarNoticiasPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("📋 Listar Notícias"));
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(3, 3, 3, 3);
        panel.add(new JLabel("Lista:"), gbc);

        String[] tiposLista = {"Favoritos", "Lidas", "Para Ler Depois"};
        JComboBox<String> tipoListaCombo = new JComboBox<>(tiposLista);
        tipoListaCombo.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(tipoListaCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Ordenar:"), gbc);

        String[] ordenacao = {"Título (A-Z)", "Data de Publicação", "Tipo/Categoria"};
        ordenacaoCombo = new JComboBox<>(ordenacao);
        ordenacaoCombo.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(ordenacaoCombo, gbc);

        JButton listarBtn = new JButton("📋 Listar");
        listarBtn.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(listarBtn, gbc);

        listarBtn.addActionListener(e -> listarNoticias(tipoListaCombo));

        return panel;
    }

    private JPanel createAcoesistemaPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("⚙️ Ações do Sistema"));
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();

        JButton carregarBtn = new JButton("📥 Carregar Dados");
        carregarBtn.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        gbc.gridx = 0; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 3, 3, 3);
        panel.add(carregarBtn, gbc);

        JButton salvarBtn = new JButton("💾 Salvar e Sair");
        salvarBtn.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(salvarBtn, gbc);

        carregarBtn.addActionListener(e -> carregarNoticias());
        salvarBtn.addActionListener(e -> salvarESair());

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "📰 RESULTADOS",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12)
        ));

        resultadosArea = new JTextArea();
        resultadosArea.setEditable(false);
        resultadosArea.setFont(new Font("Consolas", Font.PLAIN, 11));
        resultadosArea.setBackground(new Color(248, 249, 250));
        resultadosArea.setText("Bem-vindo ao Sistema de Notícias, " + nomeUsuario + "!\n" +
                "Use o menu à esquerda para navegar pelas funcionalidades.");

        JScrollPane scrollPane = new JScrollPane(resultadosArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        panel.add(scrollPane, BorderLayout.CENTER);

        // Painel inferior com botão de limpar
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(new Color(248, 249, 250));

        JButton limparBtn = new JButton("🗑️ Limpar Resultados");
        limparBtn.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        limparBtn.addActionListener(e -> limparResultados());
        bottomPanel.add(limparBtn);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }


    private void carregarDadosPersistidos() {
        DadosPersistidos dados = carregarDados();

        if (dados.favoritos != null) {
            favoritos.setFavoritos(new HashSet<>(dados.favoritos));
        }

        // Restaurar lidas
        if (dados.lidas != null) {
            for (int id : dados.lidas) {
                leitura.marcarComoLida(id);
            }
        }

        // Restaurar para ler depois
        if (dados.paraLerDepois != null) {
            for (int id : dados.paraLerDepois) {
                leitura.adicionarParaLerDepois(id);
            }
        }

        if (dados.nomesUsuarios == null) {
            dados.nomesUsuarios = new HashSet<>();
        }
        dados.nomesUsuarios.add(nomeUsuario);

        // Mostrar mensagem de dados carregados
        if (!favoritos.getFavoritos().isEmpty() || !leitura.getLidas().isEmpty() || !leitura.getParaLerDepois().isEmpty()) {
            resultadosArea.append("\n\n✅ Dados anteriores carregados com sucesso!");
            resultadosArea.append("\n- Favoritos: " + favoritos.getFavoritos().size());
            resultadosArea.append("\n- Lidas: " + leitura.getLidas().size());
            resultadosArea.append("\n- Para Ler Depois: " + leitura.getParaLerDepois().size());
        }
    }

    private DadosPersistidos carregarDados() {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(ARQUIVO_DADOS)) {
            DadosPersistidos dados = gson.fromJson(reader, DadosPersistidos.class);
            if (dados == null) {
                dados = new DadosPersistidos();
            }
            return dados;
        } catch (IOException e) {
            // No direct output here, as it's handled by returning a new DadosPersistidos
            return new DadosPersistidos();
        }
    }

    private void salvarDados(DadosPersistidos dados) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(ARQUIVO_DADOS)) {
            gson.toJson(dados, writer);
        } catch (IOException e) {
            // Re-throw as a RuntimeException or handle with JOptionPane
            JOptionPane.showMessageDialog(this, "Erro ao salvar dados: " + e.getMessage(), "Erro de Salvamento", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void limparResultados() {
        resultadosArea.setText("Resultados limpos.\nUse o menu à esquerda para buscar notícias.");
    }

    private void carregarNoticias() {
        try {
            ConexaoAPI conexaoAPI = new ConexaoAPI();
            String jsonResponse = conexaoAPI.obterNoticias();

            if (jsonResponse != null) {
                noticiasList = UtilNoticias.converterJsonParaLista(jsonResponse);
                resultadosArea.setText("✅ Notícias carregadas com sucesso!\n" +
                        "Total de notícias: " + noticiasList.size() + "\n\n" +
                        "Use as opções do menu para buscar e organizar as notícias.");
            } else {
                resultadosArea.setText("❌ Erro ao carregar notícias da API.");
            }
        } catch (Exception e) {
            resultadosArea.setText("❌ Erro ao conectar com a API: " + e.getMessage());
        }
    }

    private void buscarPorTitulo() {
        String titulo = buscaTituloField.getText().trim();
        if (titulo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite um título para buscar!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String tituloBusca = RemoverAcentos.removerAcentos(titulo.toLowerCase());
        List<Noticias> resultado = noticiasList.stream()
                .filter(n -> RemoverAcentos.removerAcentos(n.getTitulo().toLowerCase()).contains(tituloBusca))
                .collect(Collectors.toList());

        if (resultado.isEmpty()) {
            resultadosArea.setText("❌ Nenhuma notícia encontrada com o título: " + titulo);
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("🔍 Resultados da busca por título: \"").append(titulo).append("\"\n");
            sb.append("Encontradas: ").append(resultado.size()).append(" notícias\n\n");

            for (Noticias noticia : resultado) {
                sb.append("═══════════════════════════════════════\n");
                sb.append(noticia.toString()).append("\n");
            }
            resultadosArea.setText(sb.toString());
        }
        resultadosArea.setCaretPosition(0);
    }

    private void buscarPorData() {
        String dataStr = buscaDataField.getText().trim();
        if (dataStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite uma data para buscar!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate dataBusca = LocalDate.parse(dataStr, formatter);

            List<Noticias> resultado = noticiasList.stream()
                    .filter(n -> n.getDataPublicacao().toLocalDate().isEqual(dataBusca))
                    .collect(Collectors.toList());

            if (resultado.isEmpty()) {
                resultadosArea.setText("❌ Nenhuma notícia encontrada para a data: " + dataStr);
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("📅 Resultados da busca por data: ").append(dataStr).append("\n");
                sb.append("Encontradas: ").append(resultado.size()).append(" notícias\n\n");

                for (Noticias noticia : resultado) {
                    sb.append("═══════════════════════════════════════\n");
                    sb.append(noticia.toString()).append("\n");
                }
                resultadosArea.setText(sb.toString());
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Formato de data inválido! Use: dd/MM/yyyy", "Erro", JOptionPane.ERROR_MESSAGE);
        }
        resultadosArea.setCaretPosition(0);
    }

    private void adicionarNoticia() {
        try {
            int id = Integer.parseInt(idFavoritoField.getText().trim());
            String lista = (String) listaFavoritosCombo.getSelectedItem();

            if (id < 0 || id >= noticiasList.size()) {
                JOptionPane.showMessageDialog(this, "ID inválido! Use um ID entre 0 e " + (noticiasList.size() - 1), "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            switch (lista) {
                case "Favoritos":
                    if (favoritos.contem(id)) {
                        JOptionPane.showMessageDialog(this, "Notícia ID " + id + " já está nos Favoritos.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    } else {
                        favoritos.adicionarFavorito(id);
                        resultadosArea.setText("✅ Notícia ID " + id + " adicionada à lista: " + lista + "\n\n" +
                                "Detalhes da notícia:\n" +
                                "═══════════════════════════════════════\n" +
                                noticiasList.get(id).toString());
                    }
                    break;
                case "Lidas":
                    if (leitura.getLidas().contains(id)) {
                        JOptionPane.showMessageDialog(this, "Notícia ID " + id + " já está nas Lidas.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    } else {
                        leitura.marcarComoLida(id);
                        resultadosArea.setText("✅ Notícia ID " + id + " adicionada à lista: " + lista + "\n\n" +
                                "Detalhes da notícia:\n" +
                                "═══════════════════════════════════════\n" +
                                noticiasList.get(id).toString());
                    }
                    break;
                case "Para Ler Depois":
                    if (leitura.getParaLerDepois().contains(id)) {
                        JOptionPane.showMessageDialog(this, "Notícia ID " + id + " já está na lista Para Ler Depois.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    } else {
                        leitura.adicionarParaLerDepois(id);
                        resultadosArea.setText("✅ Notícia ID " + id + " adicionada à lista: " + lista + "\n\n" +
                                "Detalhes da notícia:\n" +
                                "═══════════════════════════════════════\n" +
                                noticiasList.get(id).toString());
                    }
                    break;
            }
            idFavoritoField.setText("");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Digite um ID válido (número inteiro)!", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removerNoticia(JComboBox<String> listaCombo) {
        try {
            int id = Integer.parseInt(idRemoverField.getText().trim());
            String lista = (String) listaCombo.getSelectedItem();
            boolean removido = false;
            String mensagemSucesso = "";

            if (id < 0 || id >= noticiasList.size()) {
                JOptionPane.showMessageDialog(this, "ID inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            switch (lista) {
                case "Favoritos":
                    if (favoritos.contem(id)) {
                        favoritos.removerFavorito(id);
                        removido = true;
                        mensagemSucesso = "✅ Notícia ID " + id + " removida da lista: " + lista;
                    } else {
                        JOptionPane.showMessageDialog(this, "Notícia ID " + id + " não encontrada na lista " + lista + ".", "Aviso", JOptionPane.WARNING_MESSAGE);
                    }
                    break;
                case "Lidas":
                    if (leitura.getLidas().contains(id)) {
                        leitura.getLidas().remove(id);
                        removido = true;
                        mensagemSucesso = "✅ Notícia ID " + id + " removida da lista: " + lista;
                    } else {
                        JOptionPane.showMessageDialog(this, "Notícia ID " + id + " não encontrada na lista " + lista + ".", "Aviso", JOptionPane.WARNING_MESSAGE);
                    }
                    break;
                case "Para Ler Depois":
                    if (leitura.getParaLerDepois().contains(id)) {
                        leitura.getParaLerDepois().remove(id);
                        removido = true;
                        mensagemSucesso = "✅ Notícia ID " + id + " removida da lista: " + lista;
                    } else {
                        JOptionPane.showMessageDialog(this, "Notícia ID " + id + " não encontrada na lista " + lista + ".", "Aviso", JOptionPane.WARNING_MESSAGE);
                    }
                    break;
            }

            if (removido) {
                resultadosArea.setText(mensagemSucesso);
                idRemoverField.setText("");
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Digite um ID válido!", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void listarNoticias(JComboBox<String> tipoCombo) {
        String tipo = (String) tipoCombo.getSelectedItem();
        Set<Integer> ids = null;

        switch (tipo) {
            case "Favoritos":
                ids = favoritos.getFavoritos();
                break;
            case "Lidas":
                ids = leitura.getLidas();
                break;
            case "Para Ler Depois":
                ids = leitura.getParaLerDepois();
                break;
        }

        if (ids == null || ids.isEmpty()) {
            resultadosArea.setText("📋 Lista \"" + tipo + "\" está vazia.");
            return;
        }

        String ordenacaoSelecionada = (String) ordenacaoCombo.getSelectedItem();
        List<Noticias> listaOrdenada = leitura.obterListaOrdenada(noticiasList, ids, ordenacaoSelecionada);

        if (listaOrdenada.isEmpty()) {
            resultadosArea.setText("📋 Nenhuma notícia válida encontrada na lista \"" + tipo + "\".");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("📋 Lista: ").append(tipo).append(" (").append(listaOrdenada.size()).append(" notícias)\n");
        sb.append("🔄 Ordenação: ").append(ordenacaoSelecionada).append("\n\n");

        for (Noticias noticia : listaOrdenada) {
            sb.append("═══════════════════════════════════════\n");
            sb.append(noticia.toString()).append("\n");
        }

        resultadosArea.setText(sb.toString());
        resultadosArea.setCaretPosition(0);
    }

    private void salvarESair() {
        int resposta = JOptionPane.showConfirmDialog(this,
                "Deseja salvar os dados antes de sair?",
                "Confirmar Saída",
                JOptionPane.YES_NO_CANCEL_OPTION);

        if (resposta == JOptionPane.YES_OPTION) {
            DadosPersistidos dados = new DadosPersistidos();
            dados.favoritos = favoritos.getFavoritos();
            dados.lidas = leitura.getLidas();
            dados.paraLerDepois = leitura.getParaLerDepois();
            dados.nomesUsuarios = new HashSet<>();
            dados.nomesUsuarios.add(nomeUsuario);

            salvarDados(dados);

            JOptionPane.showMessageDialog(this, "Dados salvos com sucesso!");
            System.exit(0);
        } else if (resposta == JOptionPane.NO_OPTION) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new MenuNoticias().setVisible(true);
        });
    }
}