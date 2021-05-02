package HalfPlaneIntersectionOld;

import java.awt.geom.Point2D;

/**
 * Custom class that stores location of the point on 2-dimensional space, supports convertion to Point2D from Swing
 */
public class PointOld extends Point2D.Float {
    /**
     * Constructor, simple point with X and Y coordinates
     * @param x X-axis coordinate
     * @param y Y-axis coordinate
     */
    public PointOld(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Find if coordinates match coordinates of the point
     * @param x X-axis coordinate to check
     * @param y Y-axis coordinate to check
     * @return True if point has the same coordinates as transmitted ones, False if not
     */
    public boolean isEqual(float x, float y) {
        return this.x == x && this.y == y;
    }

    /**
     * Find if coordinates of this point match coordinates of another point
     * @param anotherPointOld another point
     * @return True if points have the same coordinates, False if not
     */
    public boolean isEqual(PointOld anotherPointOld) {
        return this.x == anotherPointOld.getX() && this.y == anotherPointOld.getY();
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
