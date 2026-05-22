package vista;

import modelo.Usuario;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> mostrarLogin());
    }

    public static void mostrarLogin() {
        LoginDialog login = new LoginDialog(null);
        login.setVisible(true);
        Usuario u = login.getUsuarioAutenticado();
        if (u != null) {
            if ("admin".equalsIgnoreCase(u.getRol())) {
                new VentanaAdmin(u).setVisible(true);
            } else {
                new VentanaCajero(u).setVisible(true);
            }
        }
    }
}