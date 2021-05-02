package HalfPlaneIntersectionOld;

import java.awt.geom.Line2D;
import java.util.ArrayList;

/**
 *  Custom class defining line with specification of two endpoints of this line. Supports definition of first-degree
 * polynomial describing shape of line, definition of the perpendicular, distance estimation by Euclidean and Manhattan
 * algorithms, has "exact" and "epsilon-error-based" algorithms of finding point in line. Supports conversion into
 * Line2D from Swing
 */
public class LineOld extends Line2D.Float {
    //  ending points of the line
    private PointOld firstPointOld;
    private PointOld secondPointOld;

    //  coefficients for equation of the line in the format: y = mx + b
    private float m;
    private float b;

    //  perpendicular of the line
    private LineOld perpendicular;

    /**
     * Constructor, creates line basing on given points
     * @param firstPointOld first point
     * @param secondPointOld second point
     */
    public LineOld(PointOld firstPointOld, PointOld secondPointOld) {
        this.firstPointOld = firstPointOld;
        this.secondPointOld = secondPointOld;
    }

    /**
     * Constructor, creates line basing on given coordinates on each axis of two points
     * @param x1 first point x coordinate
     * @param y1 first point y coordinate
     * @param x2 second point x coordinate
     * @param y2 second point y coordinate
     */
    public LineOld(float x1, float y1, float x2, float y2) {
        this.firstPointOld = new PointOld(x1, y1);
        this.secondPointOld = new PointOld(x2, y2);
    }

    /**
     * Find line length in Euclidean algorithm
     * @return length in Euclidean algorithm
     */
    public double lineDistanceEuclidean(){
        return Math.sqrt(Math.pow((firstPointOld.getX() - secondPointOld.getX()), 2) + Math.pow((firstPointOld.getY() - secondPointOld.getY()), 2));
    }

    /**
     * Find line length in Manhattan algorithm
     * @return length in Manhattan algorithm
     */
    public float lineDistanceManhattan(){
        return (float) (Math.abs(firstPointOld.getX() - secondPointOld.getX()) + Math.abs(firstPointOld.getY() - secondPointOld.getY()));
    }

    /**
     * Find middle point of the line
     * @return middle point of the line
     */
    public PointOld middleOfLine(){
        float x = (float) ((firstPointOld.getX() + secondPointOld.getX()) / 2);
        float y = (float) ((firstPointOld.getY() + secondPointOld.getY()) / 2);
        return new PointOld(x, y);
    }

    /**
     * Find line angle comparing with X-axis in radians
     * @return line angle in radians
     */
    public double angleOfLineInRadians(){
        return Math.atan2(secondPointOld.getY() - firstPointOld.getY(), secondPointOld.getX() - firstPointOld.getX());
    }

    /**
     *  Find perpendicular of the line using differential approach (find line angle, perpendicular angle, iterate in
     * both directions via angular differentials). Primitive approach.
     * @return perpendicular line
     */
    public LineOld getPerpendicularByAngleIteration(){
        //  find angle of perpendicular: get line angle and turn it by 90 deg
        double perpendicularAngleInRadians = angleOfLineInRadians() + Math.PI/2;

        //  get middle of the line
        PointOld middle = middleOfLine();

        //  set coordinates of perpendicular points to be the center of line
        double perpendicularFirstPointX = middle.getX();
        double perpendicularFirstPointY = middle.getY();
        double perpendicularSecondPointX = middle.getX();
        double perpendicularSecondPointY = middle.getY();

        //  calculate differentials for each axis that will be applied for finding perpendicular endpoints
        double difX = Math.cos(perpendicularAngleInRadians);
        double difY = Math.sin(perpendicularAngleInRadians);

        //  iterate in one direction until reaching axis limits
        while((perpendicularFirstPointX > 0 && perpendicularFirstPointX < ParametersOld.xLimit) &&
                (perpendicularFirstPointY > 0 && perpendicularFirstPointY < ParametersOld.yLimit)){
            perpendicularFirstPointX += difX;
            perpendicularFirstPointY += difY;
        }

        //  iterate in another direction until reaching axis limits
        while((perpendicularSecondPointX > 0 && perpendicularSecondPointX < ParametersOld.xLimit) &&
                (perpendicularSecondPointY > 0 && perpendicularSecondPointY < ParametersOld.yLimit)){
            perpendicularSecondPointX -= difX;
            perpendicularSecondPointY -= difY;
        }

        //  set reference to perpendicular in current line to estimated perpendicular
        this.perpendicular = new LineOld((int) perpendicularFirstPointX, (int) perpendicularFirstPointY,
                (int) perpendicularSecondPointX, (int) perpendicularSecondPointY);

        //  return perpendicular line
        return perpendicular;
    }

    /**
     * Finds perpendicular of the line basing on first degree polynomial calculations.
     * @return perpendicular of the line limited by area
     */
    public LineOld getPerpendicularByEquation() {
        //  if line is horizontal, then get vertical perpendicular
        if (firstPointOld.getY() == secondPointOld.getY()) {
            PointOld middle = middleOfLine();
            return new LineOld(
                    new PointOld((float) middle.getX(), 0),
                    new PointOld((float) middle.getX(), ParametersOld.yLimit)
            );
        }

        //  if line is vertical, then get horizontal perpendicular
        else if (firstPointOld.getX() == secondPointOld.getX()) {
            PointOld middle = middleOfLine();
            return new LineOld(
                    new PointOld(0, (float) middle.getY()),
                    new PointOld(ParametersOld.xLimit, (float) middle.getY())
            );
        }

        //  storage for perpendicular endpoints
        ArrayList<PointOld> perpendicularEndpoints = new ArrayList<>();

        if (this.m == 0 && this.b == 0) {
            //  find coefficient of the line, defining its "angle"
            this.m = (float) ((secondPointOld.getY() - firstPointOld.getY()) /
                    (secondPointOld.getX() - firstPointOld.getX()));
            this.b = (float) (secondPointOld.getY() - this.m * secondPointOld.getX());
        }

        //  find equation of the perpendicular
        double perpM = -1 / this.m;
        PointOld middle = middleOfLine();
        double perpB = middle.getY() - perpM * middle.getX();

        //  check if there is perpendicular endpoint on the left border
        double yValueAtXBorder = perpM * 0 + perpB;
        if (yValueAtXBorder <= ParametersOld.yLimit && yValueAtXBorder >= 0) {
            perpendicularEndpoints.add(new PointOld(0, (int) yValueAtXBorder));
        }

        //  check if it is on the right border
        yValueAtXBorder = perpM * ParametersOld.xLimit + perpB;
        if (yValueAtXBorder <= ParametersOld.yLimit && yValueAtXBorder >= 0) {
            perpendicularEndpoints.add(new PointOld(ParametersOld.xLimit, (int) yValueAtXBorder));
        }

        //  check if it is on the top border
        double xValueAtYBorder = 0;
        if (perpendicularEndpoints.size() < 2) {
            xValueAtYBorder = (0 - perpB) / perpM;
            if (xValueAtYBorder <= ParametersOld.xLimit && xValueAtYBorder >= 0)
                perpendicularEndpoints.add(new PointOld((int) xValueAtYBorder, 0));
        }

        //  check if it is on the bottom border
        if (perpendicularEndpoints.size() < 2) {
            xValueAtYBorder = (ParametersOld.yLimit - perpB) / perpM;
            if (xValueAtYBorder <= ParametersOld.xLimit && xValueAtYBorder >= 0)
                perpendicularEndpoints.add(new PointOld((int) xValueAtYBorder, ParametersOld.yLimit));
        }

        if (perpendicularEndpoints.size() == 0) {
            System.out.println("somethings wrong!");
        }

        //  return perpendicular line
        return new LineOld(perpendicularEndpoints.get(0), perpendicularEndpoints.get(1));
    }

    /**
     * Find out if current line contains point using first degree polynomial check: y = mx + b
     * @param pointOld point presence of which must be checked
     * @return true if point is on the line, false if not
     */
    public boolean containsByEquation(PointOld pointOld) {
        //  if line is vertical or horizontal -> find if it contains the point
        if (innerHorizontalAndVerticalContainsCheck(pointOld))
            return true;

        //  if m and b coefficients have not been calculated -> calculate them
        if (this.m == 0 && this.b == 0) {
            this.m = (float) ((secondPointOld.getY() - firstPointOld.getY()) / (secondPointOld.getX() - firstPointOld.getX()));
            this.b = (float) (secondPointOld.getY() - this.m * secondPointOld.getX());
        }

        // check if point is on the line
        PointOld checkPointOld = new PointOld((float) pointOld.getX(), (float) (this.m * pointOld.getX() + this.b));
        return checkPointOld.isEqual(pointOld);
    }

    /**
     * Checks presence of point on the line if the line is horizontal or vertical
     * @param pointOld point presence of which is requested to check
     * @return true if point is present on the line, false if not
     */
    private boolean innerHorizontalAndVerticalContainsCheck(PointOld pointOld) {
        //  check if line is vertical and if point has the same X coordinate
        if (this.getFirstPointOld().getX() == this.getSecondPointOld().getX() && this.getSecondPointOld().getX() == pointOld.getX())
            //  find which end of line is lower, which end is higher, and if point is between ends
            if(this.getFirstPointOld().getY() < this.getSecondPointOld().getY())
                return pointOld.getY() > this.getFirstPointOld().getY() || pointOld.getY() < this.getSecondPointOld().getY();
            else
                return pointOld.getY() < this.getFirstPointOld().getY() || pointOld.getY() > this.getSecondPointOld().getY();

            //  check if line is horizontal and if point has the same Y coordinate
        else if (this.getFirstPointOld().getY() == this.getSecondPointOld().getY() && this.getFirstPointOld().getY() == pointOld.getY())
            //  find which end of line if left, which end is right, and if point is between ends
            if (this.getFirstPointOld().getX() < this.getSecondPointOld().getX())
                return pointOld.getX() > this.getFirstPointOld().getX() || pointOld.getX() < this.getSecondPointOld().getX();
            else
                return pointOld.getX() < this.getFirstPointOld().getX() || pointOld.getX() > this.getSecondPointOld().getX();

        //  presence is not detected, line may be not horizontal or vertical, more precise check is required
        return false;
    }

    //  getters

    public PointOld getFirstPointOld() {
        return firstPointOld;
    }

    public PointOld getSecondPointOld() {
        return secondPointOld;
    }

    public void setFirstPointOld(PointOld firstPointOld) {
        this.firstPointOld = firstPointOld;
    }

    public void setSecondPointOld(PointOld secondPointOld) {
        this.secondPointOld = secondPointOld;
    }

    @Override
    public String toString() {
        return "Line{" +
                "firstPoint=" + firstPointOld +
                ", secondPoint=" + secondPointOld +
                '}';
    }
}
