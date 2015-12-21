package interfaz;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by angel on 21/12/2015.
 */
public final class mainFrame {

    private static JFrame padre;
    private static JComboBox<String> comboMapas;
    private static BufferedImage[] mapas;
    private static JLabel preview;
    private static JTextPane consola;

    private mainFrame() {}

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
        comboMapas = new JComboBox<>(maps_name);
        comboMapas.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                preview.setIcon(new ImageIcon(mapas[comboMapas.getSelectedIndex()]));
                padre.pack();
            }
        });
        panel_Izq.add(comboMapas, BorderLayout.NORTH);

        preview = new JLabel(new ImageIcon(mapas[comboMapas.getSelectedIndex()]));
        panel_Izq.add(preview, BorderLayout.CENTER);


        JPanel panelInf = new JPanel();
        panelInf.setLayout(new BoxLayout(panelInf,BoxLayout.Y_AXIS));

        JPanel panelBotonera = new JPanel(new FlowLayout(FlowLayout.LEADING));
        JButton select = new JButton("make QuadTree");
        select.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        panelBotonera.add(select);
        panelInf.add(panelBotonera);

        consola = new JTextPane();
        consola.setText("Select image for making the QuadTree...");
        JScrollPane scroll = new JScrollPane(consola);
        panelInf.add(consola);

        panel_Izq.add(panelInf, BorderLayout.SOUTH);
        content_panel.add(panel_Izq, BorderLayout.WEST);

        principal.setContentPane(content_panel);
        return principal;
    }

    private static BufferedImage scaleImage (BufferedImage originalImg, int height) {
        float scaleFactor = height / originalImg.getHeight();
        int nuevaWidth = Math.round(originalImg.getWidth() * scaleFactor);
        int nuevaHeight = Math.round(originalImg.getHeight() * scaleFactor);
        BufferedImage vuelta = new BufferedImage(nuevaWidth, nuevaHeight, BufferedImage.TYPE_BYTE_GRAY);

        Graphics2D g2 = vuelta.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(originalImg, 0, 0, nuevaWidth, nuevaHeight, null);

        return vuelta;
    }

    private static String[] loadImages() {
        File[] images = new File("imgs/").listFiles();
        String[] names = new String[images.length];
        mapas = new BufferedImage[images.length];

        for (int i = 0; i < images.length; i++) {
            try {
                mapas[i] = scaleImage(ImageIO.read(images[i]), 400);
                names[i] = images[i].getName();
            } catch (IOException e) {
                // TODO: controlar el error
            }
        }
        return names;
    }
}
