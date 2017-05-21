package interfaz;

import datastructures.Nodo;
import logica.Image2Graph;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by angel on 21/12/2015.
 */
public final class mainFrame {

    private static JFrame padre;
    private static JComboBox<String> comboMapas;
    private static BufferedImage[] mapas;
    private static BufferedImage actual;
    private static Nodo n1;
    private static Nodo n2;
    private static Point p1;
    private static Point p2;
    private static ArrayList<Nodo> camino;
    private static JLabel preview;
    private static JButton select;
    private static JTextPane consola;
    private static Image2Graph logic;
    private static boolean cursorActivado;

    private mainFrame() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                padre = crearGUI();
                padre.pack();
                padre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                padre.setVisible(true);
            }
        });
    }

    private static JFrame crearGUI() {
        JFrame principal = new JFrame("Planificacion");
        JPanel content_panel = new JPanel();

        JPanel panel_Izq = new JPanel();
        panel_Izq.setLayout(new BorderLayout());

        String[] maps_name = loadImages();
        cursorActivado = false;
        logic = new Image2Graph();
        comboMapas = new JComboBox<>(maps_name);
        comboMapas.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                n1 = null;
                n2 = null;
                p1 = null;
                p2 = null;
                camino = null;
                actual = null;
                select.setEnabled(true);
                cursorActivado = false;
                preview.setIcon(new ImageIcon(mapas[comboMapas.getSelectedIndex()]));
                padre.pack();
            }
        });
        panel_Izq.add(comboMapas, BorderLayout.NORTH);

        preview = new JLabel(new ImageIcon(mapas[comboMapas.getSelectedIndex()]));
        preview.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (cursorActivado) {
                    super.mouseClicked(e);
                    Point puntoClick = e.getPoint();
                    writeConsole("punto x: " + puntoClick.x + ", punto y:" + puntoClick.y);

                    if (camino != null) {
                        limpiarCamino(camino);
                    }

                    if (SwingUtilities.isLeftMouseButton(e)) {
                        trataEventoRaton(puntoClick, true);
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        trataEventoRaton(puntoClick, false);
                    } else if (SwingUtilities.isMiddleMouseButton(e)) {
                        tratarEventoDebug(puntoClick);
                    }

                    if (n1 != null && n2 != null) {
                        if (n1 == n2) {
                            Graphics2D g = actual.createGraphics();
                            g.setPaint(Color.BLUE);
                            g.draw(new Line2D.Double(p1.x, p1.y, p2.x, p2.y));
                        } else {

                            writeConsole("Calculando camino...");
                            camino = logic.getCamino(n1, n2);
                            if (camino != null) {
                                pintarCamino(camino);
                            } else {
                                writeConsole("Error buscando camino");
                            }
                        }
                    }

                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            preview.repaint();
                        }
                    });
                }
            }
        });
        panel_Izq.add(preview, BorderLayout.CENTER);


        JPanel panelInf = new JPanel();
        panelInf.setLayout(new BoxLayout(panelInf, BoxLayout.Y_AXIS));

        JPanel panelBotonera = new JPanel(new FlowLayout(FlowLayout.LEADING));
        select = new JButton("make QuadTree");
        select.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                select.setEnabled(false);
                actual = logic.loadQuadTreeImg(mapas[comboMapas.getSelectedIndex()]);
                preview.setIcon(new ImageIcon(actual));
                cursorActivado = true;
            }
        });
        panelBotonera.add(select);
        panelInf.add(panelBotonera);

        consola = new JTextPane();
        consola.setText("Select image for making the QuadTree...");
        JScrollPane scroll = new JScrollPane(consola);
        scroll.setPreferredSize(new Dimension(500, 100));
        panelInf.add(scroll);

        panel_Izq.add(panelInf, BorderLayout.SOUTH);
        content_panel.add(panel_Izq, BorderLayout.WEST);

        principal.setContentPane(content_panel);
        return principal;
    }

    private static void tratarEventoDebug(Point puntoClick) {
        Graphics2D g = actual.createGraphics();

        Nodo nodo = logic.getNodoPoint(puntoClick);
        ArrayList<Nodo> vecinos = nodo.getTodosVecinosDireccion(Nodo.Orientacion.S);

        Nodo algo = nodo.getEqualAdajecntNeighbout(Nodo.Orientacion.S);
        int height = algo.getInferiorDer().y - algo.getSuperiorIzq().y;
        int width = algo.getInferiorDer().x - algo.getSuperiorIzq().x;
        g.setPaint(Color.RED);
        g.draw(new Rectangle2D.Double(algo.getSuperiorIzq().x, algo.getSuperiorIzq().y, width, height));

        for (Nodo n: vecinos) {
            height = n.getInferiorDer().y - n.getSuperiorIzq().y;
            width = n.getInferiorDer().x - n.getSuperiorIzq().x;
            g.setPaint(Color.BLUE);
            g.draw(new Rectangle2D.Double(n.getSuperiorIzq().x, n.getSuperiorIzq().y, width, height));
        }
    }

    private static void limpiarCamino(ArrayList<Nodo> camino) {
        Graphics2D g = actual.createGraphics();

        for (Nodo n : camino) {
            int height = n.getInferiorDer().y - n.getSuperiorIzq().y;
            int width = n.getInferiorDer().x - n.getSuperiorIzq().x;
            g.setPaint(Color.WHITE);
            g.fill(new Rectangle2D.Double(n.getSuperiorIzq().x, n.getSuperiorIzq().y, width, height));
            g.setPaint(Color.GREEN);
            g.draw(new Rectangle2D.Double(n.getSuperiorIzq().x, n.getSuperiorIzq().y, width, height));
        }
    }

    private static void pintarCamino(ArrayList<Nodo> camino) {
        Point ant = p1;
        Graphics2D g = actual.createGraphics();
        Stroke backup = g.getStroke();
        g.setStroke(new BasicStroke(5));

        for (Nodo n : camino) {
            Point centro = n.centro();

            g.setPaint(Color.BLUE);
            g.draw(new Line2D.Double(ant.x, ant.y, centro.x, centro.y));
            ant = centro;
        }

        g.draw(new Line2D.Double(ant.x, ant.y, p2.x, p2.y));
        g.setStroke(backup);
    }


    private static BufferedImage scaleImage(BufferedImage originalImg, int height) {
        float scaleFactor = height / originalImg.getHeight();
        int nuevaWidth = Math.round(originalImg.getWidth() * scaleFactor);
        int nuevaHeight = Math.round(originalImg.getHeight() * scaleFactor);
        BufferedImage vuelta = new BufferedImage(nuevaWidth, nuevaHeight, BufferedImage.TYPE_BYTE_GRAY);
        //BufferedImage vuelta = new BufferedImage( originalImg.getWidth(),  originalImg.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

        Graphics2D g2 = vuelta.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(originalImg, 0, 0, nuevaWidth, nuevaHeight, null);
        //g2.drawImage(originalImg, 0, 0, originalImg.getWidth(), originalImg.getHeight(), null);

        return vuelta;
    }

    private static String[] loadImages() {
        File[] images = new File("imgs/").listFiles();
        String[] names = new String[images.length];
        mapas = new BufferedImage[images.length];

        for (int i = 0; i < images.length; i++) {
            try {
                mapas[i] = scaleImage(ImageIO.read(images[i]), 500);
                names[i] = images[i].getName();
            } catch (IOException e) {
                // TODO: controlar el error
            }
        }
        return names;
    }

    private static void writeConsole(String text) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                consola.setText(consola.getText() + text + "\n");
            }
        });
    }

    private static void trataEventoRaton(Point puntoClick, boolean derecho) {
        if (derecho) {
            Graphics2D g = actual.createGraphics();
            if (n1 != null) {
                int height = n1.getInferiorDer().y - n1.getSuperiorIzq().y;
                int width = n1.getInferiorDer().x - n1.getSuperiorIzq().x;
                g.setPaint(Color.WHITE);
                g.fill(new Rectangle2D.Double(n1.getSuperiorIzq().x, n1.getSuperiorIzq().y, width, height));
                g.setPaint(Color.GREEN);
                g.draw(new Rectangle2D.Double(n1.getSuperiorIzq().x, n1.getSuperiorIzq().y, width, height));
            }

            Nodo aux = logic.getNodoPoint(puntoClick);
            if (aux == null) {
                writeConsole("punto fuera de la imagen");
            } else if (aux.isOcupado()) {
                writeConsole("punto ocupado");
            } else {
                n1 = aux;
                p1 = puntoClick;
                g.setPaint(Color.BLUE);
                g.fill(new Rectangle2D.Double(puntoClick.x, puntoClick.y, 4, 4));
                if (p2 != null) {
                    g.setPaint(Color.RED);
                    g.fill(new Rectangle2D.Double(p2.x, p2.y, 4, 4));
                }
            }
        } else {
            Graphics2D g = actual.createGraphics();
            if (n2 != null) {
                int height = n2.getInferiorDer().y - n2.getSuperiorIzq().y;
                int width = n2.getInferiorDer().x - n2.getSuperiorIzq().x;
                g.setPaint(Color.WHITE);
                g.fill(new Rectangle2D.Double(n2.getSuperiorIzq().x, n2.getSuperiorIzq().y, width, height));
                g.setPaint(Color.GREEN);
                g.draw(new Rectangle2D.Double(n2.getSuperiorIzq().x, n2.getSuperiorIzq().y, width, height));
            }
            Nodo aux = logic.getNodoPoint(puntoClick);
            if (aux == null) {
                writeConsole("punto fuera de la imagen");
            } else if (aux.isOcupado()) {
                writeConsole("punto ocupado");
            } else {
                n2 = aux;
                p2 = puntoClick;
                g.setPaint(Color.RED);
                g.fill(new Rectangle2D.Double(puntoClick.x, puntoClick.y, 4, 4));
                if (p1 != null) {
                    g.setPaint(Color.BLUE);
                    g.fill(new Rectangle2D.Double(p1.x, p1.y, 4, 4));
                }
            }
        }
    }
}
