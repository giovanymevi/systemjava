package vista;

import modelo.Usuario;
import javax.swing.*;

public class LoginFrame extends JFrame {
    public LoginFrame() {
        setTitle("Sistema de Caja - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(300, 150);
        setLocationRelativeTo(null);
        LoginDialog login = new LoginDialog(this);
        login.setVisible(true);
        Usuario usuario = login.getUsuarioAutenticado();
        if (usuario != null) {
            abrirVentanaSegunRol(usuario);
        } else {
            System.exit(0);
        }
    }

    private void abrirVentanaSegunRol(Usuario usuario) {
        if (usuario.getRol().equalsIgnoreCase("cajero")) {
            new VentanaCajero(usuario).setVisible(true);
        } else if (usuario.getRol().equalsIgnoreCase("admin")) {
            new VentanaAdmin(usuario).setVisible(true);
        }
        dispose();
    }
}