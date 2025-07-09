public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                javax.swing.UIManager.setLookAndFeel(
                    javax.swing.UIManager.getLookAndFeel());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new NotePadGUI().setVisible(true);
        });
    }
}
