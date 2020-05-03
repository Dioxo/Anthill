package jeuDesFourmis.IHM;

import jeuDesFourmis.IHM.ButtonIcon;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class StartPauseButton extends JButton implements ActionListener {
    Icon iconStart, iconPause;

    public StartPauseButton(int size) {
        iconStart = new ButtonIcon(ButtonIcon.START, size, size);
        iconPause = new ButtonIcon(ButtonIcon.PAUSE, size, size);

        this.setIcon(iconStart);
        this.addActionListener(this);
    }

    @Override
    public void actionPerformed (ActionEvent evt) {
        if (evt.getSource () == this)
        {
            Icon icon = (this.getIcon() == iconStart) ? iconPause : iconStart;
            this.setIcon(icon);
        }
    }

    public boolean isStart(){
        return this.getIcon() == iconStart;
    }
}