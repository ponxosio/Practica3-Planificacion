package logica;

import org.jgrapht.Graph;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedPseudograph;

import java.awt.image.BufferedImage;

/**
 * Created by angel on 21/12/2015.
 */
public class Image2Graph {

    WeightedPseudograph<Nodo, DefaultWeightedEdge> grafo;

    public Image2Graph() {
        grafo = new WeightedPseudograph<>(DefaultWeightedEdge.class);
    }

    public void loadQuadTreeImg (BufferedImage image) {

    }

    private Boolean[][] image2Logic(BufferedImage img) {
        //TODO:
        return null;
    }


}
