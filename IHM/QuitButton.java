package jeuDesFourmis.IHM;

import javax.swing.*;

public class QuitButton extends JButton {

    public QuitButton() {
        super("QUIT");
        this.addActionListener(actionEvent -> System.exit(0));
    }
}
