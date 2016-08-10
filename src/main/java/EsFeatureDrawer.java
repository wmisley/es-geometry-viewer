import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class EsFeatureDrawer implements EsFeatureDrawable {
    public static final Logger logger = LogManager.getLogger(EsFeatureDrawer.class);
    private Graphics2D g = null;
    private CoordinateConverter cc = null;

    public EsFeatureDrawer(Graphics2D graphics2D, int width, int height) {
        g = graphics2D;
        cc = new CoordinateConverter(width, height);
    }

    public void drawFeature(HashMap map) {
        String type = (String) map.get("type");

        if (type.equalsIgnoreCase("point")) {
            drawPoint(map);
        } else if (type.equalsIgnoreCase("polygon")) {
            drawPolygon(map);
        } else {
            logger.error("Unsupported feature type {} detected", type);
        }
    }

    private void drawPoint(double x, double y) {
        g.setPaint( Color.red );
        Point p = cc.toScreenPoint(x, y);
        g.fillOval(p.x - 2, p.y + 2, 4, 4);
    }

    private void drawPoint(HashMap map) {
        ArrayList list = (ArrayList) map.get("coordinates");
        double x = (Double) list.get(0);
        double y = (Double) list.get(1);

        drawPoint(x, y);

        logger.info("{},{}", x, y);
    }

    private void drawPolygon(HashMap map) {
        logger.info(map);

        ArrayList rings = (ArrayList) map.get("coordinates");
        ArrayList outterRing = (ArrayList) rings.get(0);
        drawPolygonOutterRing(outterRing);

        /*
        for (int i = 1; i < list.size(); i++) {
            ArrayList holes = (ArrayList) rings.get(i);
        }
        */
    }

    private void drawPolygonOutterRing(ArrayList outterRing) {
        g.setPaint( Color.red );

        Polygon poly = new Polygon();

        for (Object coord : outterRing) {
            ArrayList vertex = (ArrayList) coord;

            double x = (Double) vertex.get(0);
            double y = (Double) vertex.get(1);
            Point p = cc.toScreenPoint(x, y);
            poly.addPoint(p.x, p.y);
            g.draw(poly);
        }
    }
}
