# 📰 Sistema de Notícias - Java Swing

Este é um projeto desenvolvido em **Java** com **Swing**, que consome a **API de notícias do IBGE** para permitir a busca, visualização e gerenciamento de notícias por meio de uma interface gráfica intuitiva e interativa.  
Todas as listas de notícias (favoritas, lidas e "ler depois") são **salvas localmente em arquivos JSON**, garantindo persistência entre as execuções do sistema.

---

## 📸 Interface do Sistema

![image](https://github.com/user-attachments/assets/744cf6ac-c261-438b-954e-1b1fb6f1f467)

---

## 🚀 Funcionalidades

- 🔍 **Buscar notícias por título**
- 📅 **Buscar notícias por data**
- ⭐ **Adicionar ou remover favoritos**
- ✅ **Marcar notícia como lida**
- 📚 **Adicionar à lista "Ler Depois"**
- 📂 **Exibir listas de:**
  - Notícias favoritas
  - Notícias lidas
  - Notícias para ler depois
- 🖼️ **Interface gráfica com `JFrame` e componentes Swing**
- 🧾 **Exibição completa dos detalhes da notícia:**
  - ID  
  - Título  
  - Introdução  
  - Data  
  - Link  
  - Tipo  
  - Fonte  

---

## 🎮 Menu Interativo

O menu principal apresenta as seguintes opções numeradas:

```text
0 - Buscar notícia por título  
1 - Buscar notícia por data  
2 - Adicionar aos favoritos  
3 - Remover dos favoritos  
4 - Marcar como lida  
5 - Adicionar à lista "ler depois"  
6 - Exibir notícias favoritas  
7 - Exibir notícias lidas  
8 - Exibir lista "ler depois"  
9 - Sair do sistema  
