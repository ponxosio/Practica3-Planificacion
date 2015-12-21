package logica;

import java.awt.geom.Point2D;

/**
 * Created by angel on 21/12/2015.
 */
public class Nodo {

    Point2D SuperiorIzq;
    Point2D InferiorDer;

    public Nodo(Point2D superiorIzq, Point2D inferiorDer) {
        SuperiorIzq = superiorIzq;
        InferiorDer = inferiorDer;
    }

    public Point2D getSuperiorIzq() {
        return SuperiorIzq;
    }

    public Point2D getInferiorDer() {
        return InferiorDer;
    }
}
