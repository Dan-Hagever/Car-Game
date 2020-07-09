package edu.cg.models.Car;

import java.util.LinkedList;
import java.util.List;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import edu.cg.algebra.Point;
import edu.cg.models.BoundingSphere;
import edu.cg.models.IIntersectable;
import edu.cg.models.IRenderable;
import edu.cg.models.SkewedBox;

public class Back implements IRenderable, IIntersectable {
	private SkewedBox baseBox = new SkewedBox(Specification.B_BASE_LENGTH, Specification.B_BASE_HEIGHT,
			Specification.B_BASE_HEIGHT, Specification.B_BASE_DEPTH, Specification.B_BASE_DEPTH);
	private SkewedBox backBox = new SkewedBox(Specification.B_LENGTH, Specification.B_HEIGHT_1,
			Specification.B_HEIGHT_2, Specification.B_DEPTH_1, Specification.B_DEPTH_2);
	private PairOfWheels wheels = new PairOfWheels();
	private Spolier spoiler = new Spolier();

	@Override
	public void render(GL2 gl) {
		gl.glPushMatrix();
		Materials.SetBlackMetalMaterial(gl);
		gl.glTranslated(Specification.B_LENGTH / 2.0 - Specification.B_BASE_LENGTH / 2.0, 0.0, 0.0);
		baseBox.render(gl);
		Materials.SetRedMetalMaterial(gl);
		gl.glTranslated(-1.0 * (Specification.B_LENGTH / 2.0 - Specification.B_BASE_LENGTH / 2.0),
				Specification.B_BASE_HEIGHT, 0.0);
		backBox.render(gl);
		gl.glPopMatrix();
		gl.glPushMatrix();
		gl.glTranslated(-Specification.B_LENGTH / 2.0 + Specification.TIRE_RADIUS, 0.5 * Specification.TIRE_RADIUS,
				0.0);
		wheels.render(gl);
		gl.glPopMatrix();
		gl.glPushMatrix();
		gl.glTranslated(-Specification.B_LENGTH / 2.0 + 0.5 * Specification.S_LENGTH,
				0.5 * (Specification.B_HEIGHT_1 + Specification.B_HEIGHT_2), 0.0);
		spoiler.render(gl);
		gl.glPopMatrix();

//		//***New Feature!!!***
//		//Rendering the exhaust
//		GLU glu = new GLU();
//		Materials.SetBlackMetalMaterial(gl);
//		gl.glPushMatrix();
//		gl.glTranslated(0, -0.07, 0);
//		gl.glRotated(90.0,1,0,0);
//		gl.glRotated(-90.0,0,1,0);
//		GLUquadric quad = glu.gluNewQuadric();
//
//		//Rendering the first pipe
//		gl.glPushMatrix();
//		gl.glTranslated(-0.05, 0.03, 0.2);
//		glu.gluCylinder(quad, 0.028,0.02,
//				0.13,10,10);
//		gl.glPopMatrix();
//
//		//Rendering the second pipe
//		gl.glPushMatrix();
//		gl.glTranslated(-0.05, -0.03, 0.2);
//		glu.gluCylinder(quad, 0.028,0.02,
//				0.13,10,10);
//		gl.glPopMatrix();
//		gl.glPopMatrix();
//		glu.gluDeleteQuadric(quad);
//		gl.glPopMatrix();
	}

	@Override
	public void init(GL2 gl) {

	}

	@Override
	public List<BoundingSphere> getBoundingSpheres() {
		// s1
		// where:
		// s1 - sphere bounding the car front
		LinkedList<BoundingSphere> res = new LinkedList<BoundingSphere>();

		double x = Specification.B_LENGTH * 0.5;
		double y = Specification.B_HEIGHT * 0.5;
		double z = Specification.B_DEPTH_2 * 0.5;
		Point sphereBoundCenter = new Point(0, y,0);
		double radius = Math.sqrt(Math.pow(x, 2.0) + Math.pow(y, 2.0) + Math.pow(z, 2.0));

		//creating the bounding sphere according to the center
		BoundingSphere s1 = new BoundingSphere(radius, sphereBoundCenter);
		s1.setSphereColore3d(0, 0, 255);
		res.add(s1);

		return res;
	}

	@Override
	public void destroy(GL2 gl) {

	}

}
