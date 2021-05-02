package PixelByPixelMethod;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class VoronoiBrute extends JFrame {
    //  constants showing how many points of interest there are and size of segment for analysis
    private final int interestPointsValue;
    private final int imageSize;

    //  reference to the image
    private final BufferedImage image;

    //  interest points X and Y coordinates
    private final int[] interestPointsX;
    private final int[] interestPointsY;

    //  interest points colors
    private final int[] interestPointsColors;

    /**
     * constructor
     * @param name name of the figure
     * @param interestPointsValue how many interest points there are
     * @param imageSize size of section for PixelByPixelMethod.Voronoi Diagram
     */
    public VoronoiBrute(String name, int interestPointsValue, int imageSize) {
        //  set name of the graph
        super(name);

        //  amount of interest points
        this.interestPointsValue = interestPointsValue;

        //  set size of image
        this.imageSize = imageSize;

        //  set window size and position relatively to the screen
        setBounds(0, 0, this.imageSize, this.imageSize);
        //  set how program should be finished
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        //  constructor of the image
        this.image = new BufferedImage(this.imageSize, this.imageSize, BufferedImage.TYPE_INT_RGB);

        //  set arrays for storing X and Y coordinates of interest points
        this.interestPointsX = new int[interestPointsValue];
        this.interestPointsY = new int[interestPointsValue];
        //  set array of colors for interest points
        this.interestPointsColors = new int[interestPointsValue];
    }

    /**
     * randomizes coordinates of interest points and sets random colors to those points
     */
    public void interestPointsRandomize() {
        //  set random generator
        Random rand = new Random();

        //  iterate through all points of interest of the field
        for (int i = 0; i < this.interestPointsValue; i++) {
            //  generate random position for the interest point on X-axis
            this.interestPointsX[i] = rand.nextInt(this.imageSize);

            //  generate random position for the interest point on Y-axis
            this.interestPointsY[i] = rand.nextInt(this.imageSize);

            //  generate random color for the interest point
            this.interestPointsColors[i] = rand.nextInt(16777215);
        }
    }

    /**
     * generate locusts for all interest points (find nearest points) using colorization via pixel-by-pixel calculation
     * @param isManhattanRequired true if manhattan distance calculation is required, false if euclidean
     */
    public void voronoiLocustsIdentification(boolean isManhattanRequired) {
        long startTime = System.currentTimeMillis();

        //  iterate through all "pixels" of the image
        for (int currentPointX = 0; currentPointX < this.imageSize; currentPointX++)
            for (int currentPointY = 0; currentPointY < this.imageSize; currentPointY++) {
                int closestInterestPointIndex = 0;

                //  iterate through all cells of the image
                for (byte currentInteresPoint = 0; currentInteresPoint < this.interestPointsValue; currentInteresPoint++)
                    //  choose distance calculation method between Manhattan algorithm and Euclidean
                    if(isManhattanRequired) {
                        if (
                                manhattanDistance2D(
                                        this.interestPointsX[currentInteresPoint], currentPointX,
                                        this.interestPointsY[currentInteresPoint], currentPointY
                                ) < manhattanDistance2D(
                                        this.interestPointsX[closestInterestPointIndex], currentPointX,
                                        this.interestPointsY[closestInterestPointIndex], currentPointY)
                        ) {
                            closestInterestPointIndex = currentInteresPoint;
                        }
                    } else if (
                            euclideanDistance2D(
                                    this.interestPointsX[currentInteresPoint], currentPointX,
                                    this.interestPointsY[currentInteresPoint], currentPointY
                            ) < euclideanDistance2D(
                                    this.interestPointsX[closestInterestPointIndex], currentPointX,
                                    this.interestPointsY[closestInterestPointIndex], currentPointY)
                    ) {
                        closestInterestPointIndex = currentInteresPoint;
                    }
                //  apply color of the cell to current "pixel"
                this.image.setRGB(currentPointX, currentPointY, interestPointsColors[closestInterestPointIndex]);
            }

        long endTime = System.currentTimeMillis();
        System.out.println("Execution time is " + (endTime - startTime) + " ms.");
    }

    /**
     * Paint image on the screen using graphics by setting offset and observer that will handle displayable elements
     */
    public void draw() {
        //  apply graphics of the image with all parameters
        Graphics2D graphics2D = image.createGraphics();

        //  set background color to be black
        graphics2D.setColor(Color.BLACK);

        //  color all areas matched conform parameters
        for (int i = 0; i < this.interestPointsValue; i++) {
            graphics2D.fill(new Ellipse2D.Double(this.interestPointsX[i] - 2.5, this.interestPointsY[i] - 2.5, 5, 5));
        }

        //  try to create image
        try {
            ImageIO.write(image, "png", new File("voronoi.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * find distance in two-dimensional space using Euclidean algorithm
     * @param x1 position on X-axis of the first point
     * @param x2 position on X-axis of the second point
     * @param y1 position on Y-axis of the first point
     * @param y2 position on Y-axis of the second point
     * @return distance between two points on the two-dimensional array using Euclidian algorithm
     */
    public static double euclideanDistance2D(int x1, int x2, int y1, int y2) {
        //  euclidean distance calculation between two points in two-dimensional space
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    /**
     * find distance in two-dimensional space using Manhattan algorithm
     * @param x1 first point X coordinate
     * @param x2 second point X coordinate
     * @param y1 first point Y coordinate
     * @param y2 second point Y coordinate
     * @return distance between two points on two-dimensional space
     */
    public static double manhattanDistance2D(int x1, int x2, int y1, int y2) {
        //  manhattan algorithm for estimating two-points distance in two-dimensional space
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    /**
     *  Paint image on the screen using graphics by setting offset and observer that will handle displayable elements
     * IMPORTANT NOTE: This is required method used by JFrame module
     * @param graphics graphics module
     */
    public void paint(Graphics graphics) {
        graphics.drawImage(image, 0, 0, this);
    }
}
