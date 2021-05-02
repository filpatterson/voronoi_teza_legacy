package HalfPlaneIntersectionOld;

//  class for storing application settings
public class ParametersOld {
    //  displayable/analyzable area size in X and Y
    public static short xLimit = 700;
    public static short yLimit = 700;

    //  corners of the displayable/analyzable area
    public static final PointOld topLeftCorner = new PointOld(0, 0);
    public static final PointOld topRightCorner = new PointOld(xLimit, 0);
    public static final PointOld bottomLeftCorner = new PointOld(0, yLimit);
    public static final PointOld bottomRightCorner = new PointOld(xLimit, yLimit);
}
