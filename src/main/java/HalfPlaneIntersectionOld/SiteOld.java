package HalfPlaneIntersectionOld;

import java.awt.*;

import java.awt.geom.Area;
import java.util.ArrayList;


/**
 *  Class with specification of either service point, or point of interest. Extends Point class and has specifications
 * of color for further graphical representation and locus that describes shape of area, where all points are closer to
 * this site than to any another one (locus). Supports locus estimation via perpendicular method, has several approaches
 * for finding locus.
 */
public class SiteOld extends PointOld {
    //  color of the site that will be applied for drawing PixelByPixelMethod.Voronoi diagram by coloring locus
    private Color color;

    //  locus - area each point of which is closer to this site than to any another one
    private Area locus;

    //  borders of the reviewed area, common for all sites
    private static final ArrayList<LineOld> borders = new ArrayList<>();
    private static final Rectangle screenArea;
    static {
        //  form borders of the sector in clockwise direction
        borders.add(new LineOld(ParametersOld.topLeftCorner, ParametersOld.topRightCorner));     //  top border
        borders.add(new LineOld(ParametersOld.topRightCorner, ParametersOld.bottomRightCorner));   //  right border
        borders.add(new LineOld(ParametersOld.bottomRightCorner, ParametersOld.bottomLeftCorner));   //  bottom border
        borders.add(new LineOld(ParametersOld.bottomLeftCorner, ParametersOld.topLeftCorner));     //  left border
        screenArea = new Rectangle(0, 0, ParametersOld.xLimit, ParametersOld.yLimit);
    }

    /**
     * Constructor, create site with coordinates, no color attached
     * @param x X-axis coordinate
     * @param y Y-axis coordinate
     */
    public SiteOld(float x, float y) {
        super(x, y);
    }

    /**
     * Constructor, create site with coordinates and color specification
     * @param x X-axis coordinate
     * @param y Y-axis coordinate
     * @param color Color that will be used for coloring locus of this site
     */
    public SiteOld(float x, float y, Color color) {
        super(x, y);
        this.color = color;
    }

    /**
     * Find locus area for this site
     * @param siteOlds array of all sites presented on this sector
     */
    public void findLocus(ArrayList<SiteOld> siteOlds) {
        locus = new Area(screenArea);

        //  iterate through each site
        for (SiteOld anotherSiteOld : siteOlds) {
            //  if current site is the same as this one
            if (!anotherSiteOld.equals(this)) {
                locus.intersect(findHalfPlane(new LineOld(this, anotherSiteOld).getPerpendicularByEquation(), true));
            }
        }
    }

    /**
     * Find half plane of this site using perpendicular estimated with another site
     * @param perpendicular perpendicular that was calculated between this site and another one
     * @param useCustomSecondHalfEstimation true if use of custom algorithm is required, false if required area negation
     * @return Half plane area containing this site
     */
    private Area findHalfPlane(LineOld perpendicular, boolean useCustomSecondHalfEstimation) {
        //  iterate through sector borders in clockwise direction
        ArrayList<PointOld> halfplaneCorners = findCornersOfHalfplane(borders, perpendicular, false);

        //  form polygon out of estimated corners
        Polygon halfPlane = new Polygon();
        for (PointOld corner : halfplaneCorners) {
            halfPlane.addPoint((int) corner.getX(), (int) corner.getY());
        }

        //  if half plane contains site then return this area
        if (halfPlane.contains(this.getX(), this.getY())) {
            return new Area(halfPlane);
        }
        //  if half plane does not have site then return another half plane from this sector
        else {
            if (useCustomSecondHalfEstimation) {
                //  use algorithm of finding corners of the halfplane with specified flag, defining which half plane is
                // required and reset original halfplane
                halfplaneCorners = findCornersOfHalfplane(borders, perpendicular, true);
                halfPlane.reset();
                for (PointOld corner : halfplaneCorners) {
                    halfPlane.addPoint((int) corner.getX(), (int) corner.getY());
                }

                return new Area(halfPlane);
            } else {
                //  take area of sector and subtract from it area of the half plane that does not have site
                Area siteArea = new Area(new Rectangle(0, 0, ParametersOld.xLimit, ParametersOld.yLimit));
                siteArea.subtract(new Area(halfPlane));
                return siteArea;
            }
        }
    }

    /**
     * finds all corners of the halfplane by iterating through area borders (requires setting borders in clockwise direction)
     * @param borders borders of the area defined in clockwise direction
     * @param perpendicular perpendicular of the line
     * @return list of points defining halfplane corners
     */
    private ArrayList<PointOld> findCornersOfHalfplane(ArrayList<LineOld> borders, LineOld perpendicular, boolean isSecondOneRequired) {
        //  flag that will detect if half plane was completely found
        int perpendicularPointsMet = 0;

        //  half plane corners array
        ArrayList<PointOld> halfplaneCorners = new ArrayList<>();

        if (!isSecondOneRequired) {
            //  iterate through sector borders (works with either clockwise or anti-clockwise direction)
            for (LineOld border : borders) {
                //  if any of the perpendicular is met -> append it to half plane corners -> show this to the flag
                if (border.containsByEquation(perpendicular.getFirstPointOld())) {
                    perpendicularPointsMet++;
                    halfplaneCorners.add(perpendicular.getFirstPointOld());
                } else if (border.containsByEquation(perpendicular.getSecondPointOld())) {
                    perpendicularPointsMet++;
                    halfplaneCorners.add(perpendicular.getSecondPointOld());
                }

                //  if both ends of perpendicular was checked -> half plane is found
                if (perpendicularPointsMet == 2) {
                    break;
                }

                    //  if corner is a part of half plane -> append it to the half plane corners list
                else if (perpendicularPointsMet == 1) {
                    halfplaneCorners.add(border.getSecondPointOld());
                }
            }
        } else {
            for (LineOld border : borders) {
                //  if corner is a part of half plane -> append it to the half plane corners list
                if (perpendicularPointsMet == 0 || perpendicularPointsMet == 2) {
                    halfplaneCorners.add(border.getFirstPointOld());
                }

                //  if any of the perpendicular is met -> append it to half plane corners -> show this to the flag
                if (border.containsByEquation(perpendicular.getFirstPointOld())) {
                    perpendicularPointsMet++;
                    halfplaneCorners.add(perpendicular.getFirstPointOld());
                } else if (border.containsByEquation(perpendicular.getSecondPointOld())) {
                    perpendicularPointsMet++;
                    halfplaneCorners.add(perpendicular.getSecondPointOld());
                }
            }
        }

        return halfplaneCorners;
    }

    //  getters

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Area getLocus() {
        return locus;
    }

    @Override
    public String toString() {
        return "Site{" +
                "color=" + color +
                ", " + super.toString() +
                '}';
    }
}
