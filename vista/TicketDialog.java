package vista;

import javax.swing.*;
import java.awt.*;

public class TicketDialog extends JDialog {
    public TicketDialog(JFrame parent, String ticketText) {
        super(parent, "Ticket de Venta", true);
        setLayout(new BorderLayout());
        JTextArea textArea = new JTextArea(ticketText);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        add(new JScrollPane(textArea), BorderLayout.CENTER);
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        add(btnCerrar, BorderLayout.SOUTH);
        setSize(400, 300);
        setLocationRelativeTo(parent);
    }
}