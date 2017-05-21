package logica;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.Arrays;
import datastructures.Nodo;
import datastructures.QuadTree;

/**
 * Created by angel on 21/12/2015.
 */
public class Image2Graph {

    private static final  int MASCARA = 3;
    private QuadTree<Nodo> tree;

    public Image2Graph() {

    }

    public BufferedImage loadQuadTreeImg (BufferedImage image) {
        Boolean[][] logicImg = image2Logic(image);

        BufferedImage vuelta = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = vuelta.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(image, null, 0, 0);

        Nodo root = crearGrafoQuadTree(logicImg, new Point(0, 0), new Point(image.getWidth(), image.getHeight()), null);
        tree = new QuadTree<>(root);
        paintCellDivision(root, g2);
        return vuelta;
    }

    public Nodo getNodoPoint (Point p) {
        Nodo vuelta = null;
        if (tree != null) {
            vuelta = tree.getNodoPoint(p);
        }
        return vuelta;
    }

    public ArrayList<Nodo> getCamino (Nodo inicio, Nodo fin) {
        ArrayList<Nodo> vuelta = null;
        if (tree != null) {
            vuelta = tree.aEstrella(inicio, fin);
        }
        return vuelta;
    }

    private void paintCellDivision (Nodo root, Graphics2D g2) {
        int height = root.getInferiorDer().y - root.getSuperiorIzq().y;
        int width = root.getInferiorDer().x - root.getSuperiorIzq().x;

        if (root.isOcupado()) {
            g2.setPaint(Color.BLACK);
        } else {
            g2.setPaint(Color.WHITE);
        }
        g2.fill(new Rectangle2D.Double(root.getSuperiorIzq().x, root.getSuperiorIzq().y, width, height));
        g2.setPaint(Color.GREEN);
        g2.draw(new Rectangle2D.Double(root.getSuperiorIzq().x, root.getSuperiorIzq().y, width, height));

        if (root.isMixto()) {
            paintCellDivision(root.getHijo(Nodo.Cuadrante.NW), g2);
            paintCellDivision(root.getHijo(Nodo.Cuadrante.NE), g2);
            paintCellDivision(root.getHijo(Nodo.Cuadrante.SW), g2);
            paintCellDivision(root.getHijo(Nodo.Cuadrante.SE), g2);
        }
    }

    private void paintLogic (Boolean[][] logic, Graphics2D g2) {
        for (int i = 0; i < logic.length; i++) {
            for (int j=0; j < logic[0].length; j++) {
                if (logic[i][j]) {
                    g2.setPaint(Color.BLACK);
                } else {
                    g2.setPaint(Color.WHITE);
                }
                g2.fill(new Rectangle2D.Double(j, i, 1, 1));
            }
        }
    }

    private Nodo crearGrafoQuadTree(Boolean[][] logicImg, Point supIzq, Point infDer, Nodo padre) {
        int height = infDer.y - supIzq.y;
        int width = infDer.x - supIzq.x;
        Nodo vuelta;

        int tipo = isMatrixEquals(grabSubmatrix(logicImg, supIzq.x, supIzq.y, width, height));
        if (tipo != Nodo.MIXTO) {
            vuelta = new Nodo(supIzq, infDer, tipo, padre);
        } else {
            int nHeight = height/2;
            int nWidth = width/2;
            vuelta = new Nodo(supIzq, infDer, Nodo.MIXTO, padre);

            Point centro = new Point(supIzq.x + nWidth, supIzq.y + nHeight);
            Point centroN = new Point(supIzq.x + nWidth, supIzq.y);
            Point centroS = new Point(supIzq.x + nWidth, supIzq.y + height);
            Point centroE = new Point(supIzq.x + width, supIzq.y + nHeight);
            Point centroW = new Point(supIzq.x , supIzq.y + nHeight);

            //Region NW
            Nodo nw = crearGrafoQuadTree(logicImg, supIzq, centro, vuelta);
            vuelta.setNw(nw);
            //Region NE
            Nodo ne = crearGrafoQuadTree(logicImg, centroN, centroE, vuelta);
            vuelta.setNe(ne);
            //Region SW
            Nodo sw = crearGrafoQuadTree(logicImg, centroW, centroS, vuelta);
            vuelta.setSw(sw);
            //Region SE
            Nodo se = crearGrafoQuadTree(logicImg, centro, infDer, vuelta);
            vuelta.setSe(se);
        }
        return vuelta;
    }

    private int isMatrixEquals(Boolean[][] logicImg) {
        int vuelta;
        boolean igual = true;
        Boolean contraste = maskType(logicImg, 0, 0);

        for (int i = 0; i < logicImg.length - MASCARA && igual; i += MASCARA) {
            for (int j= 0; j < logicImg[0].length - MASCARA && igual; j += MASCARA) {
                igual = (maskType(logicImg,i , j) == contraste);
            }
        }

        if (!igual) {
            vuelta = Nodo.MIXTO;
        } else {
            if (contraste) {
                vuelta = Nodo.OCUPADO;
            } else {
                vuelta = Nodo.VACIO;
            }
        }
        return vuelta;
    }

    private boolean maskType(Boolean[][] logicImg, int x, int y) {
        boolean vuelta = false;
        int ocupados = 0;
        int libres = 0;

        for (int i = 0; i < MASCARA && (i + x) < logicImg.length ; i++) {
            for (int j = 0; j < MASCARA && (j + y) < logicImg[0].length; j++) {
                if (logicImg[x + i][y + j]) {
                    ocupados ++;
                } else {
                    libres ++;
                }
            }
        }

        if (ocupados > libres) {
            vuelta = true;
        }
        return vuelta;
    }

    private Boolean[][] grabSubmatrix (Boolean[][] imgLogic, int x, int y, int widht, int heigth) {
        Boolean[][] alto = Arrays.copyOfRange(imgLogic, y, y + heigth);

        for(int i = 0; i < alto.length; i ++) {
            alto[i] = Arrays.copyOfRange(alto[i], x, x + widht);
        }
        return alto;
    }

    /**
     * Transforms a gray scale image, into a logic one (true if a pixel is occupied, false if it is free). The bytes
     * bigger than 128 will be considered free (white ones) and the ones smaller (black) occupied.
     * @param img picture to transform
     * @return logic matriz with the same size as the picture.
     */
    private Boolean[][] image2Logic(BufferedImage img) {
        int width = img.getWidth();
        Boolean[][] vuelta = new Boolean[img.getHeight()][width];
        final byte[] pixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();

        for (int i = 0 ; i < pixels.length; i++) {
            Boolean ocupado = false;
            if (Byte.toUnsignedInt(pixels[i]) < 128) {
                ocupado = true;
            }
            vuelta[i / width][i % width] = ocupado;
        }

        return vuelta;
    }


}
