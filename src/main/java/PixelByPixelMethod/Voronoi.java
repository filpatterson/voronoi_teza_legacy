package PixelByPixelMethod;

import javax.swing.JFrame;

public class Voronoi extends JFrame {
    public static void main(String[] args) {
        //  set initial parameters for PixelByPixelMethod.Voronoi Diagram
        VoronoiBrute voronoiBrute = new VoronoiBrute("PixelByPixelMethod.Voronoi Diagram", 100, 1000);

        //  randomize interest points colors and coordinates
        voronoiBrute.interestPointsRandomize();

        //  find and calculate locusts for interest points pixel-by-pixel
        voronoiBrute.voronoiLocustsIdentification(true);

        //  make displayable image
        voronoiBrute.setVisible(true);

        //  draw diagram
        voronoiBrute.draw();
    }
}
