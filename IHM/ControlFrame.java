package jeuDesFourmis.IHM;

import jeuDesFourmis.Model.Fourmiliere;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ControlFrame extends JFrame {
    Fourmiliere f;
    JLabel nbFourmis;
    private String txtLabelFourmis = "Nombre de Fourmis : ";
    JLabel nbGraines;
    private String txtLabelGraines = "Nombre de Graines : ";
    JLabel sizeF;
    private String txtLabelSizeF = "Taille actuelle : ";

    public ControlFrame(){
        super("Projet Fourmiliere");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // La fourmiliere
        f = new Fourmiliere(200,200);
        //ajouter listener pour modifier statistiques selon besoin
        LabelListener labelListener = new LabelListener();
        f.addMouseListener(labelListener);
        f.addMouseWheelListener(labelListener);
        f.addMouseMotionListener(labelListener);
        f.addComponentListener(labelListener);

        //ajouter listener pour resize de toute la fenetre
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent componentEvent) {
                //eviter de grandir la fenetre, seulement utiliser le space necessaire
                ControlFrame.this.pack();
            }
        });

        //Creer les differents composants
        Box centralBox = Box.createVerticalBox();
        centralBox.add( Box.createVerticalGlue() );
        centralBox.add(f);
        centralBox.add( Box.createVerticalGlue() );
        this.add(centralBox);
        createRightPanel();
        addQuitButton();
        addLeftPanel();
        addLoupeButton();

        this.pack();
        this.setVisible(true);

        // la fourmiliere a le focus depuis le debut
        f.grabFocus();

        //centrer la fenetre
        this.setLocationRelativeTo(null);
    }

    private void createRightPanel(){
        Box box = Box.createVerticalBox();

        StartPauseButton startPauseButton = new StartPauseButton(100);
        startPauseButton.addActionListener(actionEvent -> {
            if (startPauseButton.isStart())
                f.launchSimulation();
            else
                f.pauseSimulation();
        });

        nbFourmis = new JLabel(txtLabelFourmis + "000");
        nbGraines = new JLabel(txtLabelGraines + "000");
        sizeF = new JLabel(txtLabelSizeF + f.getLargeur() + "x" +f.getHauteur());

        box.add(Box.createVerticalGlue());
        box.add(startPauseButton);
        box.add(Box.createRigidArea(new Dimension(30,30)));
        box.add(nbFourmis);
        box.add(Box.createRigidArea(new Dimension(10,10)));
        box.add(nbGraines);
        box.add(Box.createRigidArea(new Dimension(10,10)));
        box.add(sizeF);
        box.add(Box.createVerticalGlue());

        Box v = Box.createHorizontalBox();
        v.add(Box.createRigidArea(new Dimension(30,30)));
        v.add(box);
        v.add(Box.createRigidArea(new Dimension(30,30)));

        this.add(v, BorderLayout.EAST);
    }



    private void addLoupeButton() {
        JButton loupe = new JButton("LOUPE");
        loupe.addActionListener(actionEvent -> f.showHideLoupe());
        this.add(loupe,BorderLayout.NORTH);
    }

    private void addLeftPanel() {



        Box box = Box.createVerticalBox();
        box.add(Box.createVerticalGlue());
        box.add(createWipe());
        box.add(createChangerTaille());
        box.add(createInitialiser());
        box.add(Box.createVerticalGlue());


        Box v = Box.createHorizontalBox();
        v.add(Box.createRigidArea(new Dimension(30,30)));
        v.add(box);
        v.add(Box.createRigidArea(new Dimension(30,30)));


        this.add(v, BorderLayout.WEST);
    }

    private Box createWipe() {
        Box box = Box.createHorizontalBox();


        JButton wipe = new JButton("WIPE");
        wipe.addActionListener(actionEvent -> {
            f.wipe();
            //mettre à 0 les statistiques
            printStatistiques();
        });

        box.add(Box.createHorizontalGlue());
        box.add(wipe);
        box.add(Box.createHorizontalGlue());

        Box v = Box.createVerticalBox();
        v.add(Box.createVerticalGlue());
        v.add(Box.createRigidArea(new Dimension(15,15)));
        v.add(box);
        v.add(Box.createRigidArea(new Dimension(15,15)));
        v.add(Box.createVerticalGlue());

        return v;
    }

    private Box createInitialiser() {

        JTextField pMur = new JTextField();
        JTextField pFourmi = new JTextField();
        JTextField pGraine = new JTextField();
        JButton btn = new JButton("Confirmer");
        btn.addActionListener(actionEvent -> {
            int mur = pMur.getText().equals("") ? 0 : Integer.parseInt(pMur.getText());
            int fourmi = pFourmi.getText().equals("") ? 0 : Integer.parseInt(pFourmi.getText());
            int graines = pGraine.getText().equals("") ? 0 : Integer.parseInt(pGraine.getText());

            f.initialiseRandom(mur, fourmi, graines);

            printStatistiques();
        });

        Box box = Box.createVerticalBox();
        box.add(Box.createVerticalGlue());
        box.add(Box.createRigidArea(new Dimension(30,30)));
        box.add(new JLabel("Probabilité d'avoir Mur"));
        box.add(pMur);
        box.add(new JLabel("Probabilité d'avoir Fourmis"));
        box.add(pFourmi);
        box.add(new JLabel("Probabilité d'avoir Graines"));
        box.add(pGraine);
        box.add(btn);
        box.add(Box.createRigidArea(new Dimension(30,30)));
        box.add(Box.createVerticalGlue());

        Box b = Box.createHorizontalBox();
        b.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        b.add(Box.createRigidArea(new Dimension(30,30)));
        b.add(box);
        b.add(Box.createRigidArea(new Dimension(30,30)));

        return b;
    }

    private Box createChangerTaille() {

        SpinnerModel modelL = new SpinnerNumberModel(f.getLargeur(),
                f.minSize, //min
                f.maxSize, //max
                Fourmiliere.sizeCellule);

        SpinnerModel modelH = new SpinnerNumberModel(f.getLargeur(),
                f.minSize, //min
                f.maxSize, //max
                Fourmiliere.sizeCellule);

        JSpinner tailleL = new JSpinner(modelL);
        JSpinner tailleH = new JSpinner(modelH);

        JButton submit = new JButton("Changer Taille");

        //interdire la modification par clavier des spinners
        //alors l'unique manière de changer la valeur du spiner, est par les boutons
        ((JSpinner.DefaultEditor)tailleL.getEditor()).getTextField().setEditable(false);
        ((JSpinner.DefaultEditor)tailleH.getEditor()).getTextField().setEditable(false);

        submit.addActionListener(actionEvent -> f.setSize(
                (Integer) tailleL.getValue(),
                (Integer) tailleH.getValue()
        ));

        Box box = Box.createVerticalBox();
        box.add(new JLabel("Largeur"));
        box.add(tailleL);
        box.add(new JLabel("Hauteur"));
        box.add(tailleH);
        box.add(submit);

        Box b = Box.createHorizontalBox();
        b.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        b.add(Box.createRigidArea(new Dimension(30,30)));
        b.add(box);
        b.add(Box.createRigidArea(new Dimension(30,30)));

        return b;
    }

    private void addQuitButton() {
        Box box =  Box.createHorizontalBox();

        box.add(Box.createHorizontalGlue());
        box.add(new QuitButton());

        this.add(box, BorderLayout.SOUTH);
    }

    private void printStatistiques(){
        nbFourmis.setText(txtLabelFourmis + String.format("%03d", f.getNbFourmis()));
        nbGraines.setText(txtLabelGraines + String.format("%03d", f.getGraines()));
        sizeF.setText(txtLabelSizeF + f.getLargeur() + "x" +f.getHauteur());
        this.pack();
    }

    //class pour savoir quand modifier les textes des divers labels
    private class LabelListener extends MouseAdapter implements ComponentListener{
        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            printStatistiques();
        }

        @Override
        public void mouseDragged(MouseEvent mouseEvent) {
            printStatistiques();
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
            printStatistiques();
        }

        @Override
        public void componentResized(ComponentEvent componentEvent) {
            printStatistiques();
        }

        @Override
        public void componentMoved(ComponentEvent componentEvent) {}

        @Override
        public void componentShown(ComponentEvent componentEvent) {}

        @Override
        public void componentHidden(ComponentEvent componentEvent) {}
    }
    public static void main(String[] args) {
        ControlFrame controlFrame = new ControlFrame();
    }
}
