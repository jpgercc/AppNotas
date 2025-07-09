import javax.swing.*;
import javax.swing.text.*;
import javax.swing.undo.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.List;

public class NotePadGUI extends JFrame {
    private JTextArea textArea;
    private JFileChooser fileChooser;
    private UndoManager undoManager;
    private JLabel statusLabel;
    private JTextField searchField;
    private JButton nextButton, prevButton;
    private JPanel searchPanel;
    
    // Variáveis para busca
    private List<Integer> searchResults;
    private int currentSearchIndex = -1;
    private String lastSearchTerm = "";
    
    public NotePadGUI() {
        initComponents();
        setupLayout();
        setupMenus();
        setupEvents();
        setupIcon();
        
        setTitle("Bloco de Notas");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    private void initComponents() {
        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        
        fileChooser = new JFileChooser();
        undoManager = new UndoManager();
        textArea.getDocument().addUndoableEditListener(undoManager);
        
        statusLabel = new JLabel("Pronto");
        statusLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        
        searchField = new JTextField(20);
        nextButton = new JButton("Próximo");
        prevButton = new JButton("Anterior");
        
        searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Buscar:"));
        searchPanel.add(searchField);
        searchPanel.add(nextButton);
        searchPanel.add(prevButton);
        searchPanel.setVisible(false);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);
        add(searchPanel, BorderLayout.NORTH);
        add(statusLabel, BorderLayout.SOUTH);
    }
    
    private void setupMenus() {
        JMenuBar menuBar = new JMenuBar();
        
        // Menu Arquivo
        JMenu fileMenu = new JMenu("Arquivo");
        fileMenu.add(createMenuItem("Novo", KeyEvent.VK_N, e -> newFile()));
        fileMenu.add(createMenuItem("Abrir", KeyEvent.VK_O, e -> openFile()));
        fileMenu.add(createMenuItem("Salvar", KeyEvent.VK_S, e -> saveFile()));
        fileMenu.addSeparator();
        fileMenu.add(createMenuItem("Sair", KeyEvent.VK_Q, e -> System.exit(0)));
        
        // Menu Editar
        JMenu editMenu = new JMenu("Editar");
        editMenu.add(createMenuItem("Desfazer", KeyEvent.VK_Z, e -> undo()));
        editMenu.add(createMenuItem("Refazer", KeyEvent.VK_Y, e -> redo()));
        editMenu.addSeparator();
        editMenu.add(createMenuItem("Localizar", KeyEvent.VK_F, e -> toggleSearch()));
        
        // Menu Ferramentas
        JMenu toolsMenu = new JMenu("Ferramentas");
        toolsMenu.add(createMenuItem("Estatísticas", KeyEvent.VK_T, e -> showStats()));
        toolsMenu.add(createMenuItem("Duplicatas", KeyEvent.VK_D, e -> findDuplicates()));
        
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(toolsMenu);
        setJMenuBar(menuBar);
    }
    
    private JMenuItem createMenuItem(String text, int keyCode, ActionListener action) {
        JMenuItem item = new JMenuItem(text);
        item.setAccelerator(KeyStroke.getKeyStroke(keyCode, ActionEvent.CTRL_MASK));
        item.addActionListener(action);
        return item;
    }
    
    private void setupEvents() {
        // Busca
        searchField.addActionListener(e -> performSearch());
        nextButton.addActionListener(e -> nextResult());
        prevButton.addActionListener(e -> prevResult());
        
        // Atualizar status
        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                updateStatus();
            }
        });
        
        textArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                updateStatus();
            }
        });
    }
    
    private void newFile() {
        if (confirmClose()) {
            textArea.setText("");
            setTitle("Bloco de Notas OOP Simples - Novo Arquivo");
            updateStatus();
        }
    }
    
    private void openFile() {
        if (confirmClose() && fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                String content = FileUtils.loadFile(file);
                textArea.setText(content);
                setTitle("Bloco de Notas OOP Simples - " + file.getName());
                statusLabel.setText("Arquivo carregado: " + file.getName());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Erro ao abrir: " + e.getMessage());
            }
        }
    }
    
    private void saveFile() {
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                FileUtils.saveFile(file, textArea.getText());
                setTitle("Bloco de Notas OOP Simples - " + file.getName());
                statusLabel.setText("Arquivo salvo: " + file.getName());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar: " + e.getMessage());
            }
        }
    }
    
    private void undo() {
        if (undoManager.canUndo()) {
            undoManager.undo();
            updateStatus();
        }
    }
    
    private void redo() {
        if (undoManager.canRedo()) {
            undoManager.redo();
            updateStatus();
        }
    }
    
    private void toggleSearch() {
        searchPanel.setVisible(!searchPanel.isVisible());
        if (searchPanel.isVisible()) {
            searchField.requestFocus();
        }
        revalidate();
    }
    
    private void performSearch() {
        String searchTerm = searchField.getText();
        if (searchTerm.isEmpty()) {
            clearSearch();
            return;
        }
        
        if (!searchTerm.equals(lastSearchTerm)) {
            searchResults = TextUtils.searchText(textArea.getText(), searchTerm);
            lastSearchTerm = searchTerm;
            currentSearchIndex = -1;
        }
        
        if (!searchResults.isEmpty()) {
            nextResult();
        } else {
            statusLabel.setText("Nenhum resultado encontrado");
        }
    }
    
    private void nextResult() {
        if (searchResults == null || searchResults.isEmpty()) return;
        
        currentSearchIndex = (currentSearchIndex + 1) % searchResults.size();
        highlightResult();
    }
    
    private void prevResult() {
        if (searchResults == null || searchResults.isEmpty()) return;
        
        currentSearchIndex = currentSearchIndex <= 0 ? 
            searchResults.size() - 1 : currentSearchIndex - 1;
        highlightResult();
    }
    
    private void highlightResult() {
        if (currentSearchIndex >= 0 && currentSearchIndex < searchResults.size()) {
            int start = searchResults.get(currentSearchIndex);
            int end = start + searchField.getText().length();
            
            textArea.setSelectionStart(start);
            textArea.setSelectionEnd(end);
            textArea.requestFocus();
            
            statusLabel.setText(String.format("Resultado %d de %d", 
                currentSearchIndex + 1, searchResults.size()));
        }
    }
    
    private void clearSearch() {
        searchResults = null;
        currentSearchIndex = -1;
        lastSearchTerm = "";
        textArea.setSelectionStart(0);
        textArea.setSelectionEnd(0);
    }
    
    private void showStats() {
        String text = textArea.getText();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Não há texto para analisar");
            return;
        }
        
        TextUtils.TextStats stats = TextUtils.analyzeText(text);
        JOptionPane.showMessageDialog(this, stats.toString(), 
            "Estatísticas", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void findDuplicates() {
        String text = textArea.getText();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Não há texto para analisar");
            return;
        }
        
        List<String> duplicates = TextUtils.findDuplicateLines(text);
        if (duplicates.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhuma linha duplicada encontrada");
        } else {
            StringBuilder result = new StringBuilder("Linhas duplicadas:\n\n");
            for (String line : duplicates) {
                result.append("• ").append(line).append("\n");
            }
            JOptionPane.showMessageDialog(this, result.toString());
        }
    }
    
    private void updateStatus() {
        TextUtils.TextStats stats = TextUtils.analyzeText(textArea.getText());
        int caretPos = textArea.getCaretPosition();
        
        try {
            int line = textArea.getLineOfOffset(caretPos) + 1;
            int col = caretPos - textArea.getLineStartOffset(line - 1) + 1;
            statusLabel.setText(String.format(
                "Chars: %d | Palavras: %d | Linha: %d | Coluna: %d", 
                stats.characters, stats.words, line, col));
        } catch (BadLocationException e) {
            statusLabel.setText(String.format("Chars: %d | Palavras: %d", 
                stats.characters, stats.words));
        }
    }
    
    private boolean confirmClose() {
        // Poderia verificar se tem mudancas nao salvas
        return true;
    }
    
    /**
     * Configura o ícone da aplicação
     */
    private void setupIcon() {
        try {
            // Tenta carregar o ícone dos recursos
            URL iconURL = getClass().getResource("/resources/icon.png");
            if (iconURL != null) {
                ImageIcon icon = new ImageIcon(iconURL);
                setIconImage(icon.getImage());
            } else {
                // Se não encontrar o ícone nos recursos, tenta carregar do diretório local
                File iconFile = new File("resources/icon.png");
                if (iconFile.exists()) {
                    ImageIcon icon = new ImageIcon(iconFile.getAbsolutePath());
                    setIconImage(icon.getImage());
                } else {
                    // Cria um ícone simples programaticamente se não encontrar arquivo
                    setIconImage(createDefaultIcon());
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar ícone: " + e.getMessage());
            // Define um ícone padrão se houver erro
            setIconImage(createDefaultIcon());
        }
    }
    
    /**
     * Cria um ícone padrão simples
     */
    private Image createDefaultIcon() {
        int size = 32;
        BufferedImage icon = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = icon.createGraphics();
        
        // Configurar qualidade de renderização
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Desenhar fundo
        g2d.setColor(new Color(70, 130, 180)); // SteelBlue
        g2d.fillRoundRect(0, 0, size, size, 8, 8);
        
        // Desenhar símbolo de texto (linhas)
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        int margin = 6;
        for (int i = 0; i < 4; i++) {
            int y = margin + 4 + (i * 5);
            int width = (i == 3) ? size - margin * 2 - 8 : size - margin * 2;
            g2d.drawLine(margin, y, margin + width, y);
        }
        
        g2d.dispose();
        return icon;
    }
}