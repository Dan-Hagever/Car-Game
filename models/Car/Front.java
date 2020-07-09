package edu.cg.models.Car;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.jogamp.opengl.GL2;
import edu.cg.algebra.Point;
import edu.cg.models.BoundingSphere;
import edu.cg.models.IIntersectable;
import edu.cg.models.IRenderable;

public class Front implements IRenderable, IIntersectable {
	private FrontHood hood = new FrontHood();
	private PairOfWheels wheels = new PairOfWheels();
	private FrontBumber frontBumber = new FrontBumber();

	@Override
	public void render(GL2 gl) {
		// the car.
		gl.glPushMatrix();
		// Render hood - Use Red Material.
		gl.glTranslated(-Specification.F_LENGTH / 2.0 + Specification.F_HOOD_LENGTH / 2.0, 0, 0);
		hood.render(gl);
		// Render the wheels.
		gl.glTranslated(Specification.F_HOOD_LENGTH / 2.0 - 1.25 * Specification.TIRE_RADIUS,
				0.5 * Specification.TIRE_RADIUS, 0);
		wheels.render(gl);
		// Render the front bumber.
		gl.glTranslated(Specification.F_HOOD_LENGTH / 2.0, 0, 0);
		frontBumber.render(gl);
		gl.glPopMatrix();
	}

	@Override
	public void init(GL2 gl) {
	}

	@Override
	public List<BoundingSphere> getBoundingSpheres() {
		// s1
		// where:
		// s1 - sphere bounding the car front

		//initiate res
		LinkedList<BoundingSphere> res = new LinkedList<BoundingSphere>();

		double x = Specification.F_LENGTH * 0.5;
		double y = Specification.F_BUMPER_WINGS_HEIGHT_1 + Specification.TIRE_RADIUS * 0.5;
		y = y * 0.5;
		double z = Specification.F_BUMPER_DEPTH + Specification.F_BUMPER_WINGS_DEPTH * 2.0;
		z = z * 0.5;

		Point sphereBoundCenter = new Point(0, y, 0);
		double radius = Math.sqrt(Math.pow(x, 2.0) + Math.pow(y, 2.0) + Math.pow(z, 2.0));

		//creating the bounding sphere according to the center
		BoundingSphere s1 = new BoundingSphere(radius, sphereBoundCenter);
		s1.setSphereColore3d(250, 0, 0);

		res.add(s1);

		return res;
	}

	@Override
	public String toString() {
		return "CarFront";
	}

	@Override
	public void destroy(GL2 gl) {

	}
}
