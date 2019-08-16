package projet;

import java.util.Random;
import javax.swing.JFrame;

// Game of Life
public class GameOfLife {

    int[][] mat; // matrice (carrée) des états
    int taille = 50; // demi-taille de la matrice
    int nbThreads = 4; // nombre de threads utilisés (un pour chaque quart)
    Thread Ids[] = new Thread[nbThreads]; // liste des threads
    Object[] locks = new Object[nbThreads]; // liste des verrous
    Panneau pan; // panneau d'affichage de la matrice
    boolean live = false; // vaut true si le système est vivant donc évolue, false sinon
    int pause = 100; // temps de pause entre deux générations successives (utilisé pour Thread.sleep(pause))

    // constructeur
    // selon l'entier donné en argument, le modèle de départ est différent
    GameOfLife(int num) {
        mat = new int[2 * taille][2 * taille]; // création de la matrice d'états
        Random r = new Random();
        switch (num) {
            case 1: // matrice entièrement vivante
                for (int i = 0; i < mat.length; i++) {
                    for (int j = 0; j < mat.length; j++) {
                        mat[i][j] = 1;
                    }
                }
                break;
            case 2: // matrice entièrement morte
                for (int i = 0; i < mat.length; i++) {
                    for (int j = 0; j < mat.length; j++) {
                        mat[i][j] = 0;
                    }
                }
                break;
            case 3: // seules les cellules situées verticalement au milieu de la matrice sont vivants
                for (int i = 0; i < mat.length; i++) {
                    for (int j = 0; j < mat.length; j++) {
                        if (j == taille) {
                            mat[i][j] = 1;
                        } else {
                            mat[i][j] = 0;
                        }
                    }
                }
                break;
            default: // matrice aléatoire
                for (int i = 0; i < mat.length; i++) {
                    for (int j = 0; j < mat.length; j++) {
                        mat[i][j] = r.nextInt(2);
                    }
                }
        }

        // création des verrous
        for (int i = 0; i < nbThreads; i++) {
            locks[i] = new Object();
        }
        pan = new Panneau(this); // création du panneau d'affichage
        live = true; // le système devient vivant
    }

    // la fonction suivante change l'état de la cellule d'indices (i,j) selon les règles du Game of Life
    public void nouvelEtat(int i, int j) {
        int vivants = 0;
        for (int k = -1; k < 2; k++) {
            for (int l = -1; l < 2; l++) {
                if (k != 0 && l != 0 && (i + k) < mat.length && (i + k) >= 0 && (j + l) < mat.length && (j + l) >= 0) {
                    vivants += mat[i + k][j + l];
                }
            }
        }
        if (vivants >= 2 && vivants <= 3) {
            mat[i][j] = 1;
        } else {
            mat[i][j] = 0;
        }
    }

    // création des Runnables pour chaque quart de la matrice d'états
    // création du Runnable pour la quart 0 (cf. rapport)
    Runnable createRunnable0() {
        Runnable aRunnable = new Runnable() {
            @Override
            public void run() {
                do {
                    // mise à jour des états des cellules non "partagées"
                    for (int i = 0; i < mat.length / 2 - 1; i++) {
                        for (int j = 0; j < mat.length / 2 - 1; j++) {
                            nouvelEtat(i, j);
                        }
                    }
                    // pour mettre à jour les cellules partagées, on vérouille
                    synchronized (locks[0]) {
                        for (int k = 0; k < mat.length / 2 - 1; k++) {
                            nouvelEtat(k, mat.length / 2 - 1);
                            nouvelEtat(mat.length / 2 - 1, k);
                        }
                        nouvelEtat(mat.length / 2 - 1, mat.length / 2 - 1);
                        pan.repaint(); // on met à jour le panneau d'affichage pour l'effet visuel
                    }
                    try {
                        Thread.sleep(pause); // on marque le temps de pause
                    } catch (InterruptedException ex) {
                    }
                } while (live);
            }
        };
        return aRunnable;
    }

    // IDEM pour les quarts 1,2 et 3
    Runnable createRunnable1() {
        Runnable aRunnable = new Runnable() {
            @Override
            public void run() {
                do {
                    for (int i = 0; i < mat.length / 2 - 1; i++) {
                        for (int j = mat.length / 2 + 1; j < mat.length; j++) {
                            nouvelEtat(i, j);
                        }
                    }
                    synchronized (locks[1]) {
                        for (int k = 0; k < mat.length / 2 - 1; k++) {
                            nouvelEtat(k, mat.length / 2 - 1);
                            nouvelEtat(mat.length / 2 - 1, mat.length / 2 + 1 + k);
                        }
                        nouvelEtat(mat.length / 2 - 1, mat.length / 2);
                        pan.repaint();
                    }
                    try {
                        Thread.sleep(pause);
                    } catch (InterruptedException ex) {
                    }
                } while (live);
            }
        };
        return aRunnable;
    }

    Runnable createRunnable2() {
        Runnable aRunnable = new Runnable() {
            @Override
            public void run() {
                do {
                    for (int i = mat.length / 2 + 1; i < mat.length; i++) {
                        for (int j = 0; j < mat.length / 2 - 1; j++) {
                            nouvelEtat(i, j);
                        }
                    }
                    synchronized (locks[2]) {
                        for (int k = 0; k < mat.length / 2 - 1; k++) {
                            nouvelEtat(mat.length / 2, k);
                            nouvelEtat(mat.length / 2 + 1 + k, mat.length / 2 - 1);
                        }
                        nouvelEtat(mat.length / 2, mat.length / 2 - 1);
                        pan.repaint();
                    }
                    try {
                        Thread.sleep(pause);
                    } catch (InterruptedException ex) {
                    }
                } while (live);
            }

        };
        return aRunnable;
    }

    Runnable createRunnable3() {
        Runnable aRunnable = new Runnable() {
            @Override
            public void run() {
                do {
                    for (int i = mat.length / 2 + 1; i < mat.length; i++) {
                        for (int j = mat.length / 2 + 1; j < mat.length; j++) {
                            nouvelEtat(i, j);
                        }
                    }
                    synchronized (locks[3]) {
                        for (int k = 0; k < mat.length / 2 - 1; k++) {
                            nouvelEtat(mat.length / 2 + 1 + k, mat.length / 2);
                            nouvelEtat(mat.length / 2, mat.length / 2 + 1 + k);
                        }
                        nouvelEtat(mat.length / 2, mat.length / 2);
                        pan.repaint();
                    }
                    try {
                        Thread.sleep(pause);
                    } catch (InterruptedException ex) {
                    }
                } while (live);
            }
        };
        return aRunnable;
    }

    // méthode main
    public static void main(String[] args) {

        GameOfLife game = new GameOfLife(3);

        JFrame f = new JFrame("Game of Life"); // on crée la fenêtre principale

        f.add(game.pan); // on ajoute le panneau d'affichage dans la fenêtre principale

        // on arrange la fenêtre, on la rend visible, ...
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
        f.setResizable(false);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // on initialise et on démarre les threads
        game.Ids[0] = new Thread(game.createRunnable0());
        game.Ids[1] = new Thread(game.createRunnable1());
        game.Ids[2] = new Thread(game.createRunnable2());
        game.Ids[3] = new Thread(game.createRunnable3());

        game.Ids[0].start();
        game.Ids[1].start();
        game.Ids[2].start();
        game.Ids[3].start();

    }

}
