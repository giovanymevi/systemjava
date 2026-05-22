package vista;

import modelo.Usuario;
import controlador.LoginController;

import javax.swing.*;
import java.awt.*;

public class LoginDialog extends JDialog {
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnOk, btnCancel;
    private Usuario usuarioAutenticado;

    public LoginDialog(JFrame parent) {
        super(parent, "Login", true);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);

        gbc.gridx=0; gbc.gridy=0;
        add(new JLabel("Usuario:"), gbc);
        gbc.gridx=1;
        txtUsuario = new JTextField(10);
        add(txtUsuario, gbc);

        gbc.gridx=0; gbc.gridy=1;
        add(new JLabel("Contraseña:"), gbc);
        gbc.gridx=1;
        txtPassword = new JPasswordField(10);
        add(txtPassword, gbc);

        gbc.gridx=0; gbc.gridy=2;
        btnOk = new JButton("Ingresar");
        add(btnOk, gbc);
        gbc.gridx=1;
        btnCancel = new JButton("Salir");
        add(btnCancel, gbc);

        btnOk.addActionListener(e -> autenticar());
        btnCancel.addActionListener(e -> System.exit(0));

        pack();
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void autenticar() {
        String user = txtUsuario.getText();
        String pass = new String(txtPassword.getPassword());
        usuarioAutenticado = LoginController.autenticar(user, pass);
        if (usuarioAutenticado == null) {
            JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos");
        } else {
            dispose();
        }
    }

    public Usuario getUsuarioAutenticado() {
        return usuarioAutenticado;
    }
}