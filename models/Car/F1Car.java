package edu.cg.models.Car;

import java.util.LinkedList;
import java.util.List;

import com.jogamp.opengl.*;

import edu.cg.algebra.Point;
import edu.cg.models.BoundingSphere;
import edu.cg.models.IIntersectable;
import edu.cg.models.IRenderable;

/**
 * A F1 Racing Car.
 *
 */
public class F1Car implements IRenderable, IIntersectable {
	// Remember to include a ReadMe file specifying what you implemented.
	Center carCenter = new Center();
	Back carBack = new Back();
	Front carFront = new Front();

	@Override
	public void render(GL2 gl) {
		carCenter.render(gl);
		gl.glPushMatrix();
		gl.glTranslated(-Specification.B_LENGTH / 2.0 - Specification.C_BASE_LENGTH / 2.0, 0.0, 0.0);
		carBack.render(gl);
		gl.glPopMatrix();
		gl.glPushMatrix();
		gl.glTranslated(Specification.F_LENGTH / 2.0 + Specification.C_BASE_LENGTH / 2.0, 0.0, 0.0);
		carFront.render(gl);
		gl.glPopMatrix();

	}

	@Override
	public String toString() {
		return "F1Car";
	}

	@Override
	public void init(GL2 gl) {

	}

	@Override
	public List<BoundingSphere> getBoundingSpheres() {
		// s1 -> s2 -> s3 -> s4
		// where:
		// s1 - sphere bounding the whole car
		double radius = (Specification.F_LENGTH + Specification.B_LENGTH + Specification.C_LENGTH) * 0.5;

		double y = Specification.B_HEIGHT * 0.5;
		Point sphereBoundCenter = new Point(0, y, 0);
		BoundingSphere s1 = new BoundingSphere(radius, sphereBoundCenter);

		// s2 - sphere bounding the car front
		BoundingSphere s2 = carFront.getBoundingSpheres().get(0);
		s2.translateCenter(Specification.F_LENGTH * 0.5 + Specification.C_BASE_LENGTH * 0.5, 0 , 0);

		// s3 - sphere bounding the car center
		BoundingSphere s3 = carCenter.getBoundingSpheres().get(0);

		// s4 - sphere bounding the car back
		//
		BoundingSphere s4 = carBack.getBoundingSpheres().get(0);
		s4.translateCenter(-Specification.B_LENGTH * 0.5 - Specification.C_BASE_LENGTH * 0.5, 0 , 0);

		LinkedList<BoundingSphere> res = new LinkedList<BoundingSphere>();
		res.add(s1);
		res.add(s2);
		res.add(s3);
		res.add(s4);
		return res;
	}

	@Override
	public void destroy(GL2 gl) {

	}
}
