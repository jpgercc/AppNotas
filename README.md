# Bloco de Notas Java

Um editor de texto simples em Java com interface gráfica Swing.

<img width="976" height="733" alt="Image" src="https://github.com/user-attachments/assets/acf08382-0e43-4102-8f78-6eec168195c4" />

## Funcionalidades

- ✏️ Edição de texto com quebra automática de linha
- 💾 Abrir e salvar arquivos de texto
- 🔍 Busca e navegação através do texto
- ↩️ Desfazer e refazer operações
- 📊 Análise estatística do texto (caracteres, palavras, linhas, frases)
- 🎨 Interface moderna com ícone personalizado

## Como executar

### Pré-requisitos
- Java JDK 8 ou superior instalado

### Compilação e execução

1. Compile todos os arquivos Java:
```bash
javac *.java
```

2. Execute o programa:
```bash
java Main
```

## Estrutura do projeto

- `Main.java` - Classe principal que inicia a aplicação
- `NotePadGUI.java` - Interface gráfica principal do editor
- `TextUtils.java` - Utilitários para análise de texto
- `FileUtils.java` - Utilitários para operações com arquivos
- `resources/icon.png` - Ícone da aplicação

## Funcionalidades detalhadas

### Menu Arquivo
- Novo documento
- Abrir arquivo existente
- Salvar documento
- Salvar como
- Sair

### Menu Editar
- Desfazer/Refazer
- Copiar/Colar/Recortar
- Selecionar tudo
- Buscar texto

### Menu Ferramentas
- Análise estatística do texto (contagem de caracteres, palavras, linhas, frases, caracteres únicos e mais comum)

### Busca
- Busca em tempo real
- Navegação entre resultados
- Destaque dos termos encontrados

## Desenvolvido com
- Java Swing para interface gráfica
- Algoritmos otimizados O(n) para análise de texto
- Padrão de design orientado a objetos

---

*Projeto de bloco de notas com funcionalidades avançadas de edição e análise de texto.*
