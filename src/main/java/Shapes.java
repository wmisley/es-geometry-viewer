import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

// Demonstrating some Java2D shapes

public class Shapes extends JFrame {
    public static final Logger logger = LogManager.getLogger(Shapes.class);
    private int mapWidth = 1000;
    private int mapHeight = 500;

    public Shapes() {
        super( "Drawing 2D shapes" );

        //setSize( 425, 160 );
        setSize(mapWidth, mapHeight);
        show();
    }

    private void drawImage(Graphics2D g2d) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File("/Users/will4769/IdeaProjects/es-geometry-viewer/src/main/resources/earth.jpg"));
        } catch (IOException e) {
        }

        g2d.drawImage(img, 0, 0, null);
    }

    private void drawGrid(Graphics2D g2d) {
        g2d.setPaint( Color.green );

        CoordinateConverter cc = new CoordinateConverter(mapWidth, mapHeight);
        Point pt1 = cc.toScreenPoint(0.0, -180.0);
        Point pt2 = cc.toScreenPoint(0.0, 180.0);
        Point pt3 = cc.toScreenPoint(180.0, 0.0);
        Point pt4 = cc.toScreenPoint(-180.0, 0);

        g2d.draw( new Line2D.Double( pt1.x, pt1.y, pt2.x, pt2.y ) );
        g2d.draw( new Line2D.Double( pt3.x, pt3.y, pt4.x, pt4.y ) );
    }

    public void paint( Graphics g ) {
        EsClient esClient = new EsClient();

        try {
            Graphics2D g2d = ( Graphics2D ) g;
            drawImage(g2d);

            EsFeatureDrawer render = new EsFeatureDrawer(g2d, mapWidth, mapHeight);
            //esClient.connect("eshost", 9300, "building_p");
            esClient.connect("eshost", 9300, "built_up_area_s");
            esClient.matchAllQuery(render);
        } catch (Exception ex) {
            logger.error("", ex);
        } finally {
            esClient.disconnect();
        }
    }

    public void paint_( Graphics g )
    {
        // create 2D by casting g to Graphics2D
        Graphics2D g2d = ( Graphics2D ) g;

        // draw 2D ellipse filled with a blue-yellow gradient
        g2d.setPaint( new GradientPaint( 5, 30, // x1, y1
                Color.blue, // initial Color
                35, 100, // x2, y2
                Color.yellow, // end Color
                true ) ); // cyclic
        g2d.fill( new Ellipse2D.Double( 5, 30, 65, 100 ) );

        // draw 2D rectangle in red
        g2d.setPaint( Color.red );
        g2d.setStroke( new BasicStroke( 10.0f ) );
        g2d.draw( new Rectangle2D.Double( 80, 30, 65, 100 ) );

        // draw 2D rounded rectangle with a buffered background
        BufferedImage buffImage = new BufferedImage( 10, 10, BufferedImage.TYPE_INT_RGB );

        Graphics2D gg = buffImage.createGraphics();
        gg.setColor( Color.yellow ); // draw in yellow
        gg.fillRect( 0, 0, 10, 10 ); // draw a filled rectangle
        gg.setColor( Color.black ); // draw in black
        gg.drawRect( 1, 1, 6, 6 ); // draw a rectangle
        gg.setColor( Color.blue ); // draw in blue
        gg.fillRect( 1, 1, 3, 3 ); // draw a filled rectangle
        gg.setColor( Color.red ); // draw in red
        gg.fillRect( 4, 4, 3, 3 ); // draw a filled rectangle

        // paint buffImage onto the JFrame
        g2d.setPaint( new TexturePaint( buffImage, new Rectangle( 10, 10 ) ) );
        g2d.fill( new RoundRectangle2D.Double( 155, 30, 75, 100, 50, 50 ) );

        // draw 2D pie-shaped arc in white
        g2d.setPaint( Color.white );
        g2d.setStroke( new BasicStroke( 6.0f ) );
        g2d.draw( new Arc2D.Double( 240, 30, 75, 100, 0, 270, Arc2D.PIE ) );

        // draw 2D lines in green and yellow
        g2d.setPaint( Color.green );
        g2d.draw( new Line2D.Double( 395, 30, 320, 150 ) );

        float dashes[] = { 10 };

        g2d.setPaint( Color.yellow );
        g2d.setStroke( new BasicStroke( 4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, dashes, 0 ) );
        g2d.draw( new Line2D.Double( 320, 30, 395, 150 ) );
    }

    public static void main( String args[] )
    {
        Shapes app = new Shapes();

        app.addWindowListener( new WindowAdapter()
        {
            public void windowClosing( WindowEvent e )
            {
                System.exit( 0 );
            }
        });
    }
}
