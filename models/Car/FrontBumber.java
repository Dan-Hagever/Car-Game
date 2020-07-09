package edu.cg.models.Car;

import com.jogamp.opengl.GL2;

import edu.cg.algebra.Point;
import edu.cg.models.BoundingSphere;
import edu.cg.models.IRenderable;
import edu.cg.models.SkewedBox;

public class FrontBumber implements IRenderable {
	SkewedBox bumper = new SkewedBox(Specification.F_BUMPER_LENGTH + 0.15,
			Specification.F_BUMPER_HEIGHT_1, Specification.F_BUMPER_HEIGHT_2, Specification.F_BUMPER_DEPTH , Specification.F_BUMPER_DEPTH );
	SkewedBox rightWings = new SkewedBox(Specification.F_BUMPER_LENGTH + 0.14,
			Specification.F_BUMPER_WINGS_HEIGHT_1, Specification.F_BUMPER_WINGS_HEIGHT_2 ,
			Specification.F_BUMPER_WINGS_DEPTH, Specification.F_BUMPER_WINGS_DEPTH);
	SkewedBox leftWings = new SkewedBox(Specification.F_BUMPER_LENGTH + 0.14,
			Specification.F_BUMPER_WINGS_HEIGHT_1, Specification.F_BUMPER_WINGS_HEIGHT_2 ,
			Specification.F_BUMPER_WINGS_DEPTH, Specification.F_BUMPER_WINGS_DEPTH);
	BoundingSphere attractiveLights = new BoundingSphere(0.025, new Point(0.0));

	@Override
	public void render(GL2 gl) {
		// Remember the dimensions of the bumper, this is important when you
		// combine the bumper with the hood.

		// render the bumper
		Materials.SetBlackMetalMaterial(gl);
		bumper.render(gl);
		// render wing
		Materials.SetRedMetalMaterial(gl);
		gl.glPushMatrix();

		// Render Right Wing
		gl.glTranslated(0, 0,
				Specification.F_BUMPER_DEPTH * 0.75
						- Specification.F_BUMPER_WINGS_DEPTH/ 2.0);
		rightWings.render(gl);
		attractiveLights.setCenter(new Point(0));
		attractiveLights.translateCenter(0, 0.05, 0);
		attractiveLights.setSphereColore3d(1, 0, 0);
		attractiveLights.render(gl);
		gl.glPopMatrix();

		//Render Left Wing
		Materials.SetRedMetalMaterial(gl);
		gl.glPushMatrix();
		gl.glTranslated(0, 0,
				-Specification.F_BUMPER_DEPTH * 0.75
						+ Specification.F_BUMPER_WINGS_DEPTH / 2.0);
		leftWings.render(gl);
		attractiveLights.setCenter(new Point(0.0));
		attractiveLights.translateCenter(0, 0.05, 0);
		attractiveLights.setSphereColore3d(1, 0, 0);
		attractiveLights.render(gl);
		gl.glPopMatrix();
	}

	@Override
	public void init(GL2 gl) {
	}

	@Override
	public String toString() {
		return "FrontBumper";
	}

	@Override
	public void destroy(GL2 gl) {

	}

}
