import java.awt.*;

public class CoordinateConverter {
    private int canvasWidth = 0;
    private int canvasHeight = 0;

    public CoordinateConverter(int cWidth, int cHeight) {
        canvasWidth = cWidth;
        canvasHeight = cHeight;
    }

    public Point toScreenPoint(double lon, double lat) {
        Point point = new Point();
        point.x = (int) ((lon + 180.0) * ( canvasWidth  / 360.0));
        point.y = (int) (((lat * -1.0) + 90.0) * (canvasHeight / 180.0));

        return point;
    }
}
