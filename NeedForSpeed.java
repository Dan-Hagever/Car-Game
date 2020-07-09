package edu.cg;

import java.awt.Component;
import java.util.List;

import javax.swing.JOptionPane;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES1;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;

import edu.cg.algebra.Point;
import edu.cg.algebra.Vec;
import edu.cg.models.BoundingSphere;
import edu.cg.models.Track;
import edu.cg.models.TrackSegment;
import edu.cg.models.Car.F1Car;
import edu.cg.models.Car.Specification;

/**
 * An OpenGL 3D Game.
 *
 */
public class NeedForSpeed implements GLEventListener {
	private GameState gameState = null; // Tracks the car movement and orientation
	private F1Car car = null; // The F1 car we want to render
	private Vec carCameraTranslation = null; // The accumulated translation that should be applied on the car, camera
												// and light sources
	private Track gameTrack = null; // The game track we want to render
	private FPSAnimator ani; // This object is responsible to redraw the model with a constant FPS
	private Component glPanel; // The canvas we draw on.
	private boolean isModelInitialized = false; // Whether model.init() was called.
	private boolean isDayMode = true; // Indicates whether the lighting mode is day/night.
	private boolean isBirdseyeView = false; // Indicates whether the camera is looking from above on the scene or
											// looking
	// towards the car direction.

	private Point INITIAL_CAR_POSITION = new Point(0.0, 0.15, -7.0);	//Car initial position
	private double scaleFactor = 4.0;
	public static final float VIEW_ANGLE = 60f;
	private Point thirdPersonCameraInitialPosition = new Point(0.0, 1.8, 0.0);	//Camera initial position
	private Point birdsEyeCameraInitialPosition = new Point(INITIAL_CAR_POSITION.x, 50.0, INITIAL_CAR_POSITION.z - 25f);	//Different camera settings
	private float[] carLightRight = {(float) (Specification.F_BUMPER_DEPTH * depthFactorBumper / 2.0
			+ Specification.F_BUMPER_WINGS_DEPTH / 2.0), 0.1f, (float) (-Specification.F_LENGTH / 2.0 -
			Specification.C_BASE_LENGTH / 2.0 - Specification.F_HOOD_LENGTH / 2.0 - Specification.TIRE_RADIUS), 1.0f};
	private float[] carLightLeft = {(float) (-Specification.F_BUMPER_DEPTH * depthFactorBumper / 2.0
			- Specification.F_BUMPER_WINGS_DEPTH/ 2.0), 0.1f, (float) (-Specification.F_LENGTH / 2.0 -
			Specification.C_BASE_LENGTH / 2.0 - Specification.F_HOOD_LENGTH / 2.0 - Specification.TIRE_RADIUS), 1.0f};
	public static final double depthFactorBumper = 1.75;
	private float[] carLight = {1.0f, 1.0f, 1.0f, 1.0f};	//Light colors



	public NeedForSpeed(Component glPanel) {
		this.glPanel = glPanel;
		gameState = new GameState();
		gameTrack = new Track();
		carCameraTranslation = new Vec(0.0);
		car = new F1Car();
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		if (!isModelInitialized) {
			initModel(gl);
		}
		if (isDayMode) {
			// TODO: Setup background when day mode is on
			gl.glClearColor(0.52f, 0.824f, 1.0f, 1.0f);
		} else {
			// TODO: Setup background when night mode is on
			gl.glClearColor(0.0F, 0.0F, 0.32F, 1.0F);
		}
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		// TODO: This is the flow in which we render the scene.
		// Step (1) Update the accumulated translation that needs to be
		// applied on the car, camera and light sources.
		updateCarCameraTranslation(gl);
		// Step (2) Position the camera and setup its orientation
		setupCamera(gl);
		// Step (3) setup the lights.
		setupLights(gl);
		// Step (4) render the car.
		renderCar(gl);
		// Step (5) render the track.
		renderTrack(gl);
		// Step (6) check collision. Note this has nothing to do with OpenGL.
		if (checkCollision()) {
			JOptionPane.showMessageDialog(this.glPanel, "Game is Over");
			this.gameState.resetGameState();
			this.carCameraTranslation = new Vec(0.0);
		}
	}

	/**
	 * @return Checks if the car intersects the one of the boxes on the track.
	 */
	private boolean checkCollision() {
		// TODO: Implement this function to check if the car collides into one of the boxes.
		// You can get the bounding spheres of the track by invoking:
		// List<BoundingSphere> trackBoundingSpheres = gameTrack.getBoundingSpheres();
		List<BoundingSphere> carSpheres = car.getBoundingSpheres();
		for (BoundingSphere carSphere : carSpheres) {
			double radius = carSphere.getRadius();
			carSphere.setRadius(radius * scaleFactor);
			carSphere.translateCenter(INITIAL_CAR_POSITION.x + carCameraTranslation.x, INITIAL_CAR_POSITION.y + carCameraTranslation.y, INITIAL_CAR_POSITION.z + carCameraTranslation.z);
		}

		List<BoundingSphere> trackSpheres = gameTrack.getBoundingSpheres();
		boolean isCollide = false;
		for (BoundingSphere trackSphere : trackSpheres) {
			isCollide = trackSphere.checkIntersection(carSpheres.get(0)) &&
					trackSphere.checkIntersection(carSpheres.get(1)) ||
					trackSphere.checkIntersection(carSpheres.get(2)) ||
					trackSphere.checkIntersection(carSpheres.get(3));
			if (isCollide) break;
		}

		return isCollide;
	}


	private void updateCarCameraTranslation(GL2 gl) {
		// Update the car and camera translation values (not the ModelView-Matrix).
		// - Always keep track of the car offset relative to the starting
		// point.
		// - Change the track segments here.
		Vec ret = gameState.getNextTranslation();
		carCameraTranslation = carCameraTranslation.add(ret);
		double dx = Math.max(carCameraTranslation.x, -TrackSegment.ASPHALT_TEXTURE_DEPTH / 2.0 - 2);
		carCameraTranslation.x = (float) Math.min(dx, TrackSegment.ASPHALT_TEXTURE_DEPTH / 2.0 + 2);
		if (Math.abs(carCameraTranslation.z) >= TrackSegment.TRACK_LENGTH + 10.0) {
			carCameraTranslation.z = -(float) (Math.abs(carCameraTranslation.z) % TrackSegment.TRACK_LENGTH);
			gameTrack.changeTrack(gl);
		}
	}


private void setupCamera(GL2 gl) {
	GLU glu = new GLU();
	Point cameraPositionByMode, upVector;
	float v4 = 0, v5 = 0;

	if (this.isBirdseyeView) {
		cameraPositionByMode = birdsEyeCameraInitialPosition;
		v4 = -1.f;
		upVector = new Point (0, 0, -1);
	} else {
		cameraPositionByMode = thirdPersonCameraInitialPosition;
		v5 = -1.f;
		upVector = new Point(0, 1, 0);
	}

	glu.gluLookAt(cameraPositionByMode.x + this.carCameraTranslation.x,
			cameraPositionByMode.y + this.carCameraTranslation.y,
			cameraPositionByMode.z + this.carCameraTranslation.z,
			cameraPositionByMode.x + this.carCameraTranslation.x,
			cameraPositionByMode.y + this.carCameraTranslation.y + v4,
			cameraPositionByMode.z + this.carCameraTranslation.z + v5,
			upVector.x, upVector.y, upVector.z);
}

private void setupLights(GL2 gl) {
	if (isDayMode) {
		gl.glDisable(GLLightingFunc.GL_LIGHT0);
		gl.glDisable(GLLightingFunc.GL_LIGHT1);
		initiateModeDay(gl, GLLightingFunc.GL_LIGHT0);
	} else {
		gl.glDisable(GLLightingFunc.GL_LIGHT0);
		initiateModeNight(gl);
	}
}

	private void initiateModeDay(GL2 gl, int light) {
		float[] sunOneDirectLight = {1.0f, 1.0f, 1.0f, 1.0f};
		float[] factors = new float[]{0.5f, 0.5f, 0.5f, 1.0f};
		float[] sunPosition = { 0.0f, (float) Math.sqrt(2), (float) Math.sqrt(2), 0.0f };
		gl.glLightfv(light, GLLightingFunc.GL_AMBIENT,factors , 0);
		gl.glLightfv(light, GLLightingFunc.GL_SPECULAR, sunOneDirectLight, 0);
		gl.glLightfv(light, GLLightingFunc.GL_DIFFUSE, sunOneDirectLight, 0);
		gl.glLightfv(light, GLLightingFunc.GL_POSITION, sunPosition, 0);
		gl.glEnable(light);
	}

	private void initiateModeNight(GL2 gl) {
		gl.glLightModelfv(GL2ES1.GL_LIGHT_MODEL_AMBIENT, new float[]{ 0.25f, 0.25f, 0.3f, 1.0f }, 0);
		gl.glPushMatrix();
		gl.glTranslated(INITIAL_CAR_POSITION.x + (double)(this.carCameraTranslation.x),
				INITIAL_CAR_POSITION.y + (double)(this.carCameraTranslation.y),
				INITIAL_CAR_POSITION.z + (double)(this.carCameraTranslation.z));
		gl.glRotated(-gameState.getCarRotation(), 0.0, 1.0, 0.0);
		gl.glScaled(scaleFactor, scaleFactor, scaleFactor);
		//		set up car lights
		gl.glLightfv(GLLightingFunc.GL_LIGHT0,GLLightingFunc.GL_DIFFUSE, carLight, 0);
		gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_SPECULAR, carLight, 0);
		gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_POSITION, carLightRight, 0);
		gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_SPOT_DIRECTION, new float[]{0.0f, 0.0f, -1.0f}, 0);
		gl.glLightf(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_SPOT_CUTOFF, 70.0f);
		gl.glEnable(GLLightingFunc.GL_LIGHT0);
		gl.glLightfv(GLLightingFunc.GL_LIGHT1, GLLightingFunc.GL_DIFFUSE, carLight, 0);
		gl.glLightfv(GLLightingFunc.GL_LIGHT1, GLLightingFunc.GL_SPECULAR, carLight, 0);
		gl.glLightfv(GLLightingFunc.GL_LIGHT1, GLLightingFunc.GL_POSITION, carLightLeft, 0);
		gl.glLightfv(GLLightingFunc.GL_LIGHT1, GLLightingFunc.GL_SPOT_DIRECTION, new float[]{0.0f, 0.0f, -1.0f}, 0);
		gl.glLightf(GLLightingFunc.GL_LIGHT1, GLLightingFunc.GL_SPOT_CUTOFF, 70.0f);
		gl.glEnable(GLLightingFunc.GL_LIGHT1);

		gl.glPopMatrix();
	}

	private void renderTrack(GL2 gl) {
		// * Note: the track is not translated. It should be fixed.
		gl.glPushMatrix();
		gameTrack.render(gl);
		gl.glPopMatrix();
	}

	private void renderCar(GL2 gl) {
		// TODO: Render the car.
		// * Remember: the car position should be the initial position + the accumulated translation.
		//             This will simulate the car movement.
		// * Remember: the car was modeled locally, you may need to rotate/scale and translate the car appropriately.
		// * Recommendation: it is recommended to define fields (such as car initial position) that can be used during rendering.
		final double rotationCar = this.gameState.getCarRotation();
		gl.glPushMatrix();
		gl.glTranslated(INITIAL_CAR_POSITION.x + (double)(this.carCameraTranslation.x),
				INITIAL_CAR_POSITION.y + (double)(this.carCameraTranslation.y),
				INITIAL_CAR_POSITION.z + (double)(this.carCameraTranslation.z));
		gl.glRotated(-rotationCar, 0.0, 1.0, 0.0);
		gl.glRotated(90.0, 0.0, 1.0, 0.0);
		gl.glScaled(scaleFactor, scaleFactor, scaleFactor);
		car.render(gl);
		gl.glPopMatrix();
	}

	public GameState getGameState() {
		return gameState;
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		// Initialize display callback timer
		ani = new FPSAnimator(30, true);
		ani.add(drawable);
		glPanel.repaint();

		initModel(gl);
		ani.start();
	}

	public void initModel(GL2 gl) {
		gl.glCullFace(GL2.GL_BACK);
		gl.glEnable(GL2.GL_CULL_FACE);

		gl.glEnable(GL2.GL_NORMALIZE);
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glEnable(GL2.GL_LIGHTING);
		gl.glEnable(GL2.GL_SMOOTH);

		car.init(gl);
		gameTrack.init(gl);
		isModelInitialized = true;
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		// TODO Setup the projection matrix here.
		GL2 gl = drawable.getGL().getGL2();
		gl.glMatrixMode(gl.GL_PROJECTION);
		gl.glLoadIdentity();
		double ratio = width / (double)(height);
		GLU glu = new GLU();
		glu.gluPerspective(VIEW_ANGLE, ratio, 2.0, 500.0);
	}

	/**
	 * Start redrawing the scene with 30 FPS
	 */
	public void startAnimation() {
		if (!ani.isAnimating())
			ani.start();
	}

	/**
	 * Stop redrawing the scene with 30 FPS
	 */
	public void stopAnimation() {
		if (ani.isAnimating())
			ani.stop();
	}

	public void toggleNightMode() {
		isDayMode = !isDayMode;
	}

	public void changeViewMode() {
		isBirdseyeView = !isBirdseyeView;
	}

}
