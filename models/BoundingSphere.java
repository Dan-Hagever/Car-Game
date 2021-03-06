package edu.cg.models;

import com.jogamp.opengl.GL2;

import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import edu.cg.algebra.Point;

public class BoundingSphere implements IRenderable {
	private double radius = 0.0;
	private Point center;
	private double color[];

	public BoundingSphere(double radius, Point center) {
		color = new double[3];
		this.setRadius(radius);
		this.setCenter(new Point(center.x, center.y, center.z));
	}

	public void setSphereColore3d(double r, double g, double b) {
		this.color[0] = r;
		this.color[1] = g;
		this.color[2] = b;
	}

	/**
	 * Given a sphere s - check if this sphere and the given sphere intersect.
	 * 
	 * @return true if the spheres intersects, and false otherwise
	 */
	public boolean checkIntersection(BoundingSphere s) {
		boolean intersection = center.dist(s.center) <= radius + s.radius;
		return intersection;
	}

	public void translateCenter(double dx, double dy, double dz) {
		this.setCenter(new Point(center.x + dx, center.y + dy, center.z + dz));
	}

	@Override
	public void render(GL2 gl) {
		gl.glPushMatrix();

		// define sphere to the center point
		gl.glTranslated(center.x, center.y ,center.z);
		// define color of the sphere
		gl.glColor3d(color[0], color[1], color[2]);
		GLU glu = new GLU();
		GLUquadric q = glu.gluNewQuadric();
		// render sphere
		glu.gluSphere(q, radius, 10, 10);
		glu.gluDeleteQuadric(q);
		gl.glPopMatrix();
	}

	@Override
	public void init(GL2 gl) {
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public Point getCenter() {
		return center;
	}

	public void setCenter(Point center) {
		this.center = center;
	}

	@Override
	public void destroy(GL2 gl) {

	}

}
