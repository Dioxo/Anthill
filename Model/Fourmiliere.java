package jeuDesFourmis.Model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Classe de gestion de la fourmiliere
 *
 * @author abergey
 * @author abrunet
 * correction largeur-hauteur
 * correction boucle infinie si fourmi bloquée
 * @version 1.2
 */


public class Fourmiliere extends JPanel {

    private int largeur, hauteur;

    // la liste des fourmis de la fourmiliere.
    // Attention : la position X,Y d'une fourmi doit correspondre à un booleen true
    // dans le tableau fourmis
    private List<Fourmi> lesFourmis;

    // Tableaux contenant les murs, les fourmis et les graines.
    // Attention : pour un terrain [1..hauteur]x[1..largeur], ces tableaux
    // sont indicés de [0..hauteur+1][0..largeur+1], cela permet de simplifier
    // certains traitements en ne traitant pas le cas particulier des bordures.
    private boolean murs[][];
    private boolean fourmis[][];
    private int qteGraines[][];

    private static final int QMAX = 4;

    public static int sizeCellule = 5;

    //thread pour arreter et commencer simulation
    private SimulationTrigger simulationTrigger;

    //la fenetre de loupe
    private Loupe loupe;

    public final int minSize = 200;
    public final int maxSize = 600;

    //Colors de graines
    public static Color graine_1 = new Color(252, 185, 185);
    public static Color graine_2 = new Color(250, 127, 128);
    public static Color graine_3 = new Color(250, 90, 89);
    public static Color graine_4 = new Color(250, 66, 59);

    /**
     * Crée une fourmiliere de largeur l et de hauteur h.
     *
     * @param l largeur
     * @param h hauteur
     */
    public Fourmiliere(int l, int h) {
        largeur = l;
        hauteur = h;

        this.setPreferredSize(new Dimension(l, h));
        this.setMinimumSize(new Dimension(200, 200));
        this.setMaximumSize(new Dimension(600, 600));
        this.setFocusable(true);

        //initialiser parametres
        simulationTrigger = new SimulationTrigger();
        this.lesFourmis = new LinkedList<Fourmi>();
        fourmis = new boolean[hauteur / sizeCellule][largeur / sizeCellule];
        murs = new boolean[hauteur / sizeCellule][largeur / sizeCellule];
        qteGraines = new int[hauteur / sizeCellule][largeur / sizeCellule];

        assignerMursExternes();

       this.loupe = new Loupe(murs, fourmis, qteGraines, lesFourmis);

        //ajouter mes evenements listeners
        MouseKeyEvents mouseKeyEvents = new MouseKeyEvents();
        this.addMouseListener(mouseKeyEvents);
        this.addMouseMotionListener(mouseKeyEvents);
        this.addKeyListener(mouseKeyEvents);
        this.addMouseWheelListener(mouseKeyEvents);
        this.addComponentListener(new ResizeFourmiliere());
    }


    /**
     * Methode pour assignres les murs externes à la fourmiliere
     */
    private void assignerMursExternes() {
        for (int y = 0; y < hauteur; y += sizeCellule)
            for (int x = 0; x < largeur; x += sizeCellule)
                setMur(x, y, (y == 0) || (y == hauteur - sizeCellule) || (x == 0) || (x == largeur - sizeCellule));
    }

    /**
     * Montrer ou cacher la fenetre Loupe
     */
    public void showHideLoupe() {
        loupe.changeVisible();
    }

    /**
     * Effacer l'interieur du terrain, sauf les murs de bordure ( murs externes)
     */
    public void wipe() {

        //arreter si on n'a pas la confirmation pour continuer
        if (confirmChanges())
            return;

        //on peut effacer uniquement quand la simulation est en pause
        if (simulationTrigger.isPaused()) {
            for (int x = 0; x < murs[0].length; x++)
                for (int y = 0; y < murs.length; y++) {
                    //mettre tous les valeurs a false ou 0
                    murs[y][x] = false;
                    fourmis[y][x] = false;
                    qteGraines[y][x] = 0;
                    //vider les fourmis crées
                    lesFourmis.clear();
                }

            //laisser seulement les bordures
            assignerMursExternes();
            this.repaint();
        } else {
            //show message
            showErrorModifDialog();
        }
    }

    private boolean confirmChanges() {
        return JOptionPane.showConfirmDialog(
                null,
                "Vous voulez continuer?",
                "Confirmer",
                JOptionPane.YES_NO_OPTION) != 0;
    }

    /**
     * MessageDialog pour afficher si l'utilisateur essaie de modifier pendant que la simulation est en cours
     */
    private void showErrorModifDialog() {
        JOptionPane.showMessageDialog(
                null,
                "Vous ne pouvez pas modifier le terraint pendant que la simulation est en cours",
                "alert",
                JOptionPane.ERROR_MESSAGE);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        paintMurs(graphics);
        paintGraines(graphics);
        paintFourmis(graphics);
    }

    private void paintGraines(Graphics graphics) {
        graphics.translate(0, 0);

        for (int y = 0; y < hauteur; y += sizeCellule)
            for (int x = 0; x < largeur; x += sizeCellule) {
                int qte = getQteGraines(x, y);
                if (qte > 0) {
                    switch (qte) {
                        case 1:
                            graphics.setColor(graine_1);
                            break;
                        case 2:
                            graphics.setColor(graine_2);
                            break;
                        case 3:
                            graphics.setColor(graine_3);
                            break;
                        case 4:
                            graphics.setColor(graine_4);
                            break;
                    }
                    graphics.fillRect(x, y, sizeCellule, sizeCellule);
                }
            }
    }

    private void paintFourmis(Graphics graphics) {
        graphics.translate(0, 0);

        for (Fourmi fourmi : lesFourmis) {
            if (fourmi.porte())
                graphics.setColor(Color.BLUE);
            else
                graphics.setColor(Color.GREEN);

            graphics.fillRect(
                    fourmi.getX(),
                    fourmi.getY(),
                    sizeCellule,
                    sizeCellule);
        }
    }

    private void paintMurs(Graphics graphics) {
        graphics.translate(0, 0);
        graphics.setColor(Color.BLACK);

        for (int y = 0; y < hauteur; y += sizeCellule)
            for (int x = 0; x < largeur; x += sizeCellule)
                if (getMur(x, y))
                    graphics.fillRect(x, y, sizeCellule, sizeCellule);
    }

    /**
     * Retourne la largeur de la fourmiliere
     *
     * @return la hauteur
     */
    public int getLargeur() {
        return largeur;
    }

    /**
     * Retourne la hauteur de la fourmiliere
     *
     * @return la hauteur
     */
    public int getHauteur() {
        return hauteur;
    }

    public int getNbFourmis() {
        return lesFourmis.size();
    }

    public int getGraines() {
        int cmpt = 0;

        for (int x = 0; x < largeur; x += sizeCellule)
            for (int y = 0; y < hauteur; y += sizeCellule)
                cmpt += getQteGraines(x, y);

        return cmpt;
    }

    /**
     * Presence d'un mur au point  (x,y) du terrain
     *
     * @param x coordonnée
     * @param y abcisse
     * @return vrai si il y a un mur
     */
    public boolean getMur(int x, int y) {
        assert (x >= 0 && x < largeur &&
                y > 0 && y < hauteur);
        return murs[y / sizeCellule][x / sizeCellule];
    }

    /**
     * Positionne un mur en au point (x,y) du terrain
     *
     * @param x coordonnée
     * @param y abciss'e
     * @param m vrai si l'on veut poser un mur, faux sinon
     */
    public void setMur(int x, int y, boolean m) {
        assert (x >= 0 && x < largeur &&
                y > 0 && y < hauteur);

        murs[y / sizeCellule][x / sizeCellule] = m;
    }

    /**
     * Presence  d'une fourmi au point (x,y) du terrain
     *
     * @param x coordonnee
     * @param y abcisse
     * @return vrai si il y a une fourmi
     */
    public boolean contientFourmi(int x, int y) {
        assert (x >= 0 && x < largeur &&
                y > 0 && y < hauteur);

        return fourmis[y / sizeCellule][x / sizeCellule];
    }

    /**
     * Positionne un Fourmi en au point (x,y) du terrain
     *
     * @param x coordonnée
     * @param y abciss'e
     * @param m vrai si l'on veut poser un fourmi, faux sinon
     */
    private void setFourmi(int x, int y, boolean m) {
        assert (x >= 0 && x < largeur &&
                y > 0 && y < hauteur);
        fourmis[y / sizeCellule][x / sizeCellule] = m;
    }


    /**
     * Ajoute (ou remplace) une fourmi non chargée au point (x,y) du terrain
     *
     * @param x coordonnee
     * @param y abcisse
     */
    public void ajouteFourmi(int x, int y) {
        assert (x >= 0 && x < largeur &&
                y > 0 && y < hauteur);

        //positions en multiple de sizeCellule
        x -=  x % sizeCellule;
        y -= y % sizeCellule;

        if (!contientFourmi(x, y) && !getMur(x, y)) {
            Fourmi f = new Fourmi(x, y, false);
            fourmis[y / sizeCellule][x / sizeCellule] = true;
            lesFourmis.add(f);
        }
    }

    /**
     * Retourne la quantité de graine au point (x,y) du terrain
     *
     * @param x coordonnnee
     * @param y abcisse
     * @return la quantité de graine
     */
    public int getQteGraines(int x, int y) {
        assert (x >= 0 && x < largeur &&
                y > 0 && y < hauteur);

        return this.qteGraines[y / sizeCellule][x / sizeCellule];
    }

    /**
     * Positionne des graines au point (x,y) du terrain
     *
     * @param x   coordonnee
     * @param y   abcisse
     * @param qte le nombre de graines que l'on souhaite poser. Si qte !E [0..QMAX] rien n'est effectué
     */
    public void setQteGraines(int x, int y, int qte) {
        //assert (qte >=0 && qte <=QMAX);
        if (qte < 0 || qte > QMAX || getMur(x, y)) {
            return;
        }
        this.qteGraines[y / sizeCellule][x / sizeCellule] = qte;

    }

    /**
     * Compte les graines du point (x,y) et des cellules voisines
     * Les voisines s'entendent au sens de 8-connexité.
     * On ne compte pas les graines sur les murs)
     *
     * @param x coordonnee
     * @param y abcisse
     * @return le nombre de graines
     */
    private int compteGrainesVoisines(int x, int y) {
        assert (x > 0 && x < largeur && y > 0 && y < hauteur);
        int nb = 0;
        for (int vx = -1; vx < 2; vx++)
            for (int vy = -1; vy < 2; vy++)
                if (!getMur(x + vx, y + vy))
                    nb = nb + getQteGraines(x + vx, y + vy);
        return nb;
    }

    /**
     * Evolution d'une étape de la fourmilière
     * Pour chaque fourmi f de la foumilière.
     * 1) si il y a une(ou des) graines sur la case, et que
     * la fourmi ne porte rien :
     * on choisit aléatoirement de charger ou non une graine,
     * en fonction du nombre de graines autour.
     * 2) f se deplace aléatoirement d'une case (en évitant les murs)
     * 3) si f est chargée et qu'il reste de la place pour une graine,
     * on choisit aléatoirement de poser ou non  la graine,
     * en fonction du nombre de graines autour.
     */
    public void evolue() {
        for (Fourmi f : lesFourmis) {
            int posX = f.getX();
            int posY = f.getY();
            // la fourmi f prend ?
            if (!f.porte() && getQteGraines(f.getX(), f.getY()) > 0) {
                if (Math.random() < Fourmi.probaPrend(compteGrainesVoisines(posX, posY))) {
                    f.prend();
                    setQteGraines(posX, posY, getQteGraines(f.getX(), f.getY()) - 1);
                }
            }
            // la fourmi f se déplace.
            int deltaX = posX;
            int deltaY = posY;

            boolean walked = false;
            for (int i = 0; i < 100; i++) {
                //reinitialiser la position de la fourmi si elle n'a pas sortie de la boucle
                deltaX = posX;
                deltaY = posY;

                int tirage = (int) (Math.random() * 7.99999999);
                switch (tirage) {
                    case 0:
                        deltaX -= sizeCellule;
                        deltaY -= sizeCellule;
                        break;
                    case 1:
                        deltaY -= sizeCellule;
                        break;
                    case 2:
                        deltaX += sizeCellule;
                        deltaY -= sizeCellule;
                        break;
                    case 3:
                        deltaX -= sizeCellule;
                        break;
                    case 4:
                        deltaX += sizeCellule;
                        break;
                    case 5:
                        deltaX -= sizeCellule;
                        deltaY += sizeCellule;
                        break;
                    case 6:
                        deltaY += sizeCellule;
                        break;
                    case 7:
                        deltaX += sizeCellule;
                        deltaY += sizeCellule;
                        break;
                }

                //si la fourmi sort du tableau ou touche les murs
                if (deltaX < sizeCellule || deltaX > largeur - sizeCellule
                        || deltaY < sizeCellule || deltaY > hauteur - sizeCellule) {
                    continue;
                }

                if (!getMur(deltaX, deltaY) && !contientFourmi(deltaX, deltaY)) {
                    //on peut bouger la fourmi, alors sortie du boucle
                    walked = true;
                    break;
                }
            }

            //si la fourmi a changé de lieu
            if (walked) {
                setFourmi(posX, posY, false);
                setFourmi(deltaX, deltaY, true);

                f.setX(deltaX);
                f.setY(deltaY);

                // la fourmi pose ?
                if (f.porte() && getQteGraines(deltaX, deltaY) < QMAX) {
                    if (Math.random() < Fourmi.probaPose(compteGrainesVoisines(deltaX, deltaY))) {
                        f.pose();
                        setQteGraines(deltaX, deltaY, getQteGraines(deltaX, deltaY) + 1);
                    }
                }
            }


        }
    }

    public void initialiseRandom(int lMur, int lFourmis, int lGraines) {
        if (!simulationTrigger.isPaused()) {
            showErrorModifDialog();
            return;
        }

        //si les probabilites ne sont pas 0
        if (lMur == 0 && lFourmis == 0 && lGraines == 0)
            return;

        //arreter si on n'a pas la confirmation pour continuer
        if (confirmChanges())
            return;

        //initialiser un nouveau terrain
        viderAvecBordures();

        //definir les limites
        //lMur => [0, lMur]
        //PFourmi => ]lMur, lFourmis]
        lFourmis += lMur;
        //lGraines => ]lFourmis, 100
        lGraines += lFourmis;


        //créer nouveau terrain
        for (int y = sizeCellule; y < hauteur; y += sizeCellule)
            for (int x = sizeCellule; x < largeur; x += sizeCellule) {
                int tirage = (int) (Math.random() * 100);

                if (tirage <= lMur) {
                    //créer un mur
                    setMur(x, y, true);
                } else if (tirage <= lFourmis) {
                    //créer fourmi
                    ajouteFourmi(x, y);
                } else if (tirage <= lGraines) {
                    //graines
                    setQteGraines(x, y,
                            (int) (Math.random() * 4));
                }

            }

        this.repaint();
    }

    private void viderAvecBordures() {
        for (int x = 0; x < murs[0].length; x++)
            for (int y = 0; y < murs.length; y++) {
                //mettre tous les valeurs a false ou 0
                murs[y][x] = false;
                fourmis[y][x] = false;
                qteGraines[y][x] = 0;
                //vider les fourmis crées
            }

        lesFourmis.clear();
        //laisser seulement les bordures
        assignerMursExternes();
    }

    @Override
    public void setSize(int l, int h) {
        //confirmer modifications
        if (confirmChanges()) {
            return;
        }
        if (!simulationTrigger.isPaused()) {
            showErrorModifDialog();
            return;
        }

        super.setSize(l, h);
        this.setPreferredSize(new Dimension(l, h));
    }

    private class MouseKeyEvents extends MouseAdapter implements KeyListener {
        private int keyPressed;

        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            //reprendre le focus si le panel ne l'avait pas deja
            Fourmiliere.this.grabFocus();

            //on peut ajouter uniquement si la simulation est en pause
            if (simulationTrigger.isPaused())
                addElementToFourmiliere(mouseEvent.getPoint());
            else
                showErrorModifDialog();
        }

        @Override
        public void mouseEntered(MouseEvent mouseEvent) {
            loupe.showLoupe(true);
        }

        @Override
        public void mouseExited(MouseEvent mouseEvent) {
            loupe.showLoupe(false);
        }

        @Override
        public void mouseMoved(MouseEvent mouseEvent) {
            loupe.paintLoupePoint(mouseEvent.getPoint());
        }

        @Override
        public void mouseDragged(MouseEvent mouseEvent) {
            if (simulationTrigger.isPaused())
                addElementToFourmiliere(mouseEvent.getPoint());
            else
                showErrorModifDialog();
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
            int qte = getQteGraines(mouseWheelEvent.getX(), mouseWheelEvent.getY());
            int delta = qte - mouseWheelEvent.getWheelRotation();

            //si la nouvelle quantité entre dans les paremetres[0,QMAX]
            // et la simulation est en pause,
            // alors ajouter graine
            if (delta >= 0 && delta <= QMAX && simulationTrigger.isPaused()) {
                setQteGraines(
                        mouseWheelEvent.getX(),
                        mouseWheelEvent.getY(),
                        delta
                );

                Fourmiliere.this.repaint();
                loupe.paintLoupe();
            } else {
                if (!simulationTrigger.isPaused()) {
                    showErrorModifDialog();
                }
            }
        }

        private void addElementToFourmiliere(Point mousePoint) {
            //reprendre le focus si le panel ne l'avait pas deja
            Fourmiliere.this.grabFocus();

            if (isInsideTerrain(mousePoint)) {
                int x = mousePoint.x;
                int y = mousePoint.y;

                //si touche Shift pressed, alors fourmi
                if (keyPressed == KeyEvent.VK_SHIFT) {
                    //creer une fourmi s'il n'y a pas de mur et s'il n'y a pas une fourmi
                    if (!getMur(x, y) && !contientFourmi(x, y)) {
                        Fourmiliere.this.ajouteFourmi(x, y);
                    }
                } else {
                    //creer mur
                    setMur(x, y, !getMur(x, y) && !contientFourmi(x, y));
                }

                //dessiner le nouveau element ajouté
                Fourmiliere.this.repaint();
                loupe.paintLoupe();
            }

        }

        /**
         * @param p point P donné par la souris
         * @return si la point p est dans le terrain que l'utilisateur peut modifier (non bordures)
         */
        private boolean isInsideTerrain(Point p) {
            return p.x > sizeCellule && p.x < Fourmiliere.this.largeur - sizeCellule
                    && p.y > sizeCellule && p.y < Fourmiliere.this.hauteur - sizeCellule;
        }

        @Override
        public void keyTyped(KeyEvent keyEvent) {
        }

        @Override
        public void keyPressed(KeyEvent keyEvent) {
            keyPressed = keyEvent.getKeyCode();
        }

        @Override
        public void keyReleased(KeyEvent keyEvent) {
            //arreter de dessiner des fourmis
            keyPressed = 0;
        }
    }

    private class SimulationTrigger extends Thread {

        private volatile boolean paused;
        private final Object pauseLock;

        public SimulationTrigger() {
            this.paused = true;
            this.pauseLock = new Object();
        }

        @Override
        public void run() {
            paused = false;
            while (true) {

                //section critique pour pause et continuer
                synchronized (pauseLock) {
                    if (paused) {
                        try {
                            pauseLock.wait();
                        } catch (InterruptedException ex) {
                            break;
                        }
                    }
                }

                try {
                    //evoluer et repaint de la fourmiliere
                    Fourmiliere.this.evolue();
                    Fourmiliere.this.repaint();

                    //paint loupe aussi
                    loupe.paintLoupe();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

        public boolean isPaused() {
            return paused;
        }
    }

    public void launchSimulation() {
        //si c'est la premiere fois qu'on lance la simulation, start thread
        if (simulationTrigger.getState() == Thread.State.NEW) {
            simulationTrigger.start();
        } else {
            //sinon, continuer simulation
            synchronized (simulationTrigger.pauseLock) {
                simulationTrigger.paused = false;
                simulationTrigger.pauseLock.notify();
            }
        }
    }

    public void pauseSimulation() {
        simulationTrigger.paused = true;
    }

    private class ResizeFourmiliere extends ComponentAdapter {

        @Override
        public void componentResized(ComponentEvent componentEvent) {
            hauteur = Fourmiliere.this.getHeight();
            largeur = Fourmiliere.this.getWidth();

            //reinitialiser valeurs à 0
            fourmis = new boolean[hauteur / sizeCellule][largeur / sizeCellule];
            murs = new boolean[hauteur / sizeCellule][largeur / sizeCellule];
            qteGraines = new int[hauteur / sizeCellule][largeur / sizeCellule];
            lesFourmis.clear();

            //reassigner les variables à la loupe, car loupe contient les vieilles references
            loupe.setMurs(murs);
            loupe.setFourmis(fourmis);
            loupe.setQteGraines(qteGraines);
            loupe.setLesFourmis(lesFourmis);

            assignerMursExternes();

            Fourmiliere.this.repaint();
        }
    }

}
