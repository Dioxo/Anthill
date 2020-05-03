package jeuDesFourmis.Model;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Loupe extends JFrame {
    private boolean show;

    private boolean[][] murs;
    private boolean[][] fourmis;
    private int[][] qteGraines;
    private List<Fourmi> lesFourmis;

    private Point mousePoint;

    public Loupe(boolean[][] murs,boolean[][] fourmis,int[][] qteGraines,List<Fourmi> lesFourmis) {
        this.setMinimumSize(new Dimension(330, 330));
        this.setPreferredSize(new Dimension(330, 330));
        this.setMaximumSize(new Dimension(330, 330));

        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setUndecorated(true);
        //par defaut, ne pas montrer la loupe
        this.show = false;

        this.murs = murs;
        this.fourmis = fourmis;
        this.qteGraines = qteGraines;
        this.lesFourmis = lesFourmis;

        mousePoint = new Point();
        this.pack();
    }

    public void setMurs(boolean[][] murs) {
        this.murs = murs;
    }

    public void setFourmis(boolean[][] fourmis) {
        this.fourmis = fourmis;
    }

    public void setQteGraines(int[][] qteGraines) {
        this.qteGraines = qteGraines;
    }

    public void setLesFourmis(List<Fourmi> lesFourmis) {
        this.lesFourmis = lesFourmis;
    }

    public void changeVisible(){
        //montrer ou pas la fenetre
        show = !show;
    }

    public void showLoupe(boolean b) {
        this.setVisible(b && show);
    }

    @Override
    public void paint(Graphics graphics) {
        super.paint(graphics);

        for (int x = 0 ; x <  11; x++)
            for (int y = 0 ; y < 11; y++) {

                //position du tableau à montrer selon la position de la souris
                int posX = mousePoint.x + x;
                int posY = mousePoint.y + y;

                if (murs[posY][posX]) {
                    graphics.setColor(Color.BLACK);
                    graphics.fillRect(x * 30, y * 30, 30, 30);
                }

                if (qteGraines[posY][posX] > 0) {
                    switch (qteGraines[posY][posX]) {
                        case 1:
                            graphics.setColor(Fourmiliere.graine_1);
                            break;
                        case 2:
                            graphics.setColor(Fourmiliere.graine_2);
                            break;
                        case 3:
                            graphics.setColor(Fourmiliere.graine_3);
                            break;
                        case 4:
                            graphics.setColor(Fourmiliere.graine_4);
                            break;
                    }
                    graphics.fillRect(x * 30, y * 30, 30, 30);
                }

                if (fourmis[posY][posX]) {
                    //trouver la fourmi dans cette position pour voir si elle possede ou pas des graines
                    for (Fourmi fourmi : lesFourmis){
                        if (fourmi.getX() == posX * Fourmiliere.sizeCellule
                        && fourmi.getY() == posY * Fourmiliere.sizeCellule){

                            if (fourmi.porte())
                                graphics.setColor(Color.BLUE);
                            else
                                graphics.setColor(Color.GREEN);

                            graphics.fillRect(x * 30, y * 30, 30, 30);
                        }
                    }
                }

            }

    }

    public void paintLoupe(){
        if (this.isVisible())
            this.repaint();
    }

    public void paintLoupePoint(Point p){
        if (this.isVisible()){
            //avoir coordonnées (x,y)  => dans le tableau[][]
            p.x = p.x / Fourmiliere.sizeCellule;
            p.y = p.y / Fourmiliere.sizeCellule;

            //rectangle qui va avoir les coordonnées à afficher dans la loupe selon la souris
            int sizeCellule = 11;
            Rectangle rectangle = new Rectangle(
                    p.x - 5, //bouger 5 cases à gauche
                    p.y - 5, //bouger 5 cases en haut
                    sizeCellule,
                    sizeCellule);

            int largeur = murs[0].length;
            int hauteur = murs.length;

            //savoir les coordonnés dans les points critiques (bordures)

            //si le rectangle depasse une des bordures, mettre les bordures comme coordonnée max/min

            if (rectangle.x < 0) {
                rectangle.x = 0;
            }

            if (rectangle.x + rectangle.width > largeur)
                rectangle.x = largeur - rectangle.width;

            if (rectangle.y < 0)
                rectangle.y = 0;

            if (rectangle.y + rectangle.height > hauteur)
                rectangle.y = hauteur - rectangle.height;

            //fin pour calculer les coordonéés à montrer dans la loupe

            this.mousePoint.x = rectangle.x;
            this.mousePoint.y = rectangle.y;

            this.repaint();
        }
    }

}
