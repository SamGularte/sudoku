package ui.custom.button;

import javax.swing.*;
import java.awt.event.ActionListener;

public class buttonCheckGameStatusButton extends JButton {

    public buttonCheckGameStatusButton(final ActionListener actionListener){
        this.setText("Verificar jogo");
        this.addActionListener(actionListener);
    }
}
