package projet;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;

// panneau d'affichage de la matrice d'états du Game of Life
public class Panneau extends JPanel {

    GameOfLife gol; // Game of Life associé
    int referenceCote = 600; // taille de référence pour la panneau 
    int coteCellule; // côté d'une cellule dans le panneau
    int cotePanneau; // côté du panneau

    Panneau(GameOfLife game) {
        super();

        gol = game;
        coteCellule = referenceCote / gol.mat.length; // on détermine le côté d'une cellule à partir de la taille de réference
        cotePanneau = coteCellule * (gol.mat.length - 2); // on en déduit le côté du panneau

        this.setPreferredSize(new Dimension(cotePanneau, cotePanneau)); // on dimensionne le panneau
        this.setLayout(null);
    }

    @Override
    public void paintComponent(Graphics g) {

        // Les cellules vivantes sont coloriées en rouge, les cellules mortes sont coloriées en blanc
       
        for (int i = 0; i < gol.mat.length; i++) {
            for (int j = 0; j < gol.mat.length; j++) {
                if (gol.mat[i][j] == 1) {
                    g.setColor(Color.red);
                    g.fillRect(i * coteCellule + coteCellule / 4, j * coteCellule + coteCellule / 4, coteCellule / 2, coteCellule / 2);
                } else {
                    g.setColor(Color.white);
                    g.fillRect(i * coteCellule + coteCellule / 4, j * coteCellule + coteCellule / 4, coteCellule / 2, coteCellule / 2);
                }
            }

        }

    }

}
