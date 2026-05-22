package controlador;

import modelo.Usuario;

public class LoginController {
    public static Usuario autenticar(String user, String pass) {
        if (user.equals("admin") && pass.equals("admin")) {
            return new Usuario("Administrador Principal", "admin");
        } else if (user.equals("cajero") && pass.equals("123")) {
            return new Usuario("Cajero de Turno", "cajero");
        }
        return null;
    }
}