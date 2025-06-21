package app;

import views.LoginWindow;
import dao.CursoDAO;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            // Establecer el look and feel del sistema
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Iniciar la aplicación mostrando la ventana de login
            SwingUtilities.invokeLater(() -> {
                new LoginWindow().setVisible(true);
            });
            
            // Registrar el hook de cierre
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                CursoDAO.cerrarFactory();
            }));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 