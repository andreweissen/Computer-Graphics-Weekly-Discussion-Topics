/**
 * FourLights.java
 * Begun 09/15/18
 * @version 2.0
 * @author David J. Eck, Andrew Eissen, UMUC faculty, et al.
 */
package weekfivediscussion;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.gl2.GLUT;
import java.util.ArrayList;
import fourlights.Camera; // Don't forget about this import!

/**
 * The program, such as it is, has gradually grown over the course of its rewrite to include what
 * amounts to a multi-class file program compressed into a single file. Originally, when the author
 * decided to rewrite the file to be a bit more readily understandable and better organized as per
 * the <a href="https://en.wikipedia.org/wiki/Single_responsibility_principle">single responsibility
 * principle</a>, he had intended to divide certain responsibilities between the main class and a
 * set of three helper inner classes. However, as the rewrite continued, this grew to encompass some
 * six total inner classes and a wealth of main class utility methods and helper methods. All
 * things considered, this should probably be divided into several actual class files for enhanced
 * readability, but the Week 5 discussion prompt said to modify the file alone, so modify it the
 * author did.
 * <br />
 * <br />
 * It is important to note that the author imported the necessary <code>fourlights.Camera</code>
 * class used to define the camera itself, an instance of which is stored as a superclass field
 * below. As such, to use this class, be sure to import the file or remove the above import and
 * simply add that class and its contents to a separate file in the same package as this. A copy of
 * the class is located <a href="http://math.hws.edu/graphicsbook/source/jogl/Camera.java">here</a>.
 * <br />
 * <br />
 * <pre>
 * Table of Contents:
 * - Main method                  Line 0073
 * - Constants & class fields     Line 0077
 * - Setters                      Line 0141
 * - Getters                      Line 0263
 * - Utility methods              Line 0373
 * - Listener handlers            Line 0470
 * - Inner classes                Line 0521
 *   - SceneGLJPanel              Line 0536
 *     - Setter                   Line 0548
 *     - Getter                   Line 0560
 *     - Utility methods          Line 0571
 *   - SceneGLEventListener       Line 0832
 *   - LightSource                Line 0959
 *     - Setters                  Line 0994
 *     - Getters                  Line 1066
 *     - Utility methods          Line 1131
 *   - CheckBoxListener           Line 1220
 *   - TimerListener              Line 1247
 *   - TruncatedIcosahedron       Line 1280
 * </pre>
 *
 * @see <a href="http://math.hws.edu/graphicsbook/source/jogl/Camera.java">Camera.java</a>
 * @see javax.swing.JFrame
 * @author David J. Eck, Andrew Eissen, UMUC faculty, et al.
 */
final class FourLights extends JFrame {

  /**
   * The main method simply creates a new <code>FourLights</code> application instance.
   *
   * @param args <code>String[]</code>, the command line arguments
   * @return void
   */
  public static void main(String[] args) {
    final FourLights newApplication = new FourLights();
  }

  // Constants & class fields

  /** Constant defining the width of the entire application window, set to 600 */
  private final static int WINDOW_WIDTH = 600;

  /** Constant defining the height of the entire application window, set to 600 */
  private final static int WINDOW_HEIGHT = 600;

  /** Default <code>Timer</code> interval period, set by default to 30 milliseconds */
  private final static int TIMER_DELAY = 30;

  /** The delay between the end of the <code>constructGUI</code> method and the timer start */
  private final static int INITIAL_DELAY = 500;

  /** This constant sets the scale of the main scene object, the icosahedron, set to 10.0 */
  private final static double OBJECT_SCALE = 10.0;

  /** <code>float</code> array for first color, set to <code>{0.5F, 0, 0, 1}</code> */
  private final static float[] COLOR_1 = {0.5F, 0, 0, 1};

  /** <code>float</code> array for first color ambient, set to <code>{0.1F, 0, 0, 1}</code> */
  private final static float[] COLOR_1_AMBIENT = {0.1F, 0, 0, 1};

  /** <code>float</code> array for second color, set to <code>{0, 0.5F, 0, 1}</code> */
  private final static float[] COLOR_2 = {0, 0.5F, 0, 1};

  /** <code>float</code> array for second color ambient, set to <code>{0, 0.1F, 0, 1}</code> */
  private final static float[] COLOR_2_AMBIENT = {0, 0.1F, 0, 1};

  /** <code>float</code> array for third color, set to <code>{0, 0, 0.5F, 1}</code> */
  private final static float[] COLOR_3 = {0, 0, 0.5F, 1};

  /** <code>float</code> array for third color ambient, set to <code>{0, 0, 0.1F, 1}</code> */
  private final static float[] COLOR_3_AMBIENT = {0, 0, 0.1F, 1};

  /** <code>float</code> array for black lighting, set to <code>{0, 0, 0, 1}</code> */
  private final static float[] BLACK = {0, 0, 0, 1};

  /** <code>float</code> array for face coloring, set to <code>{0.95F, 0.95F, 0.95F, 1}</code> */
  private final static float[] PRIMARY_FACE_COLOR = {0.95F, 0.95F, 0.95F, 1};

  /** <code>float</code> array for grey lighting, set to <code>{0.15F, 0.15F, 0.15F, 1}</code> */
  private final static float[] GLOBAL_AMBIENT = {0.15F, 0.15F, 0.15F, 1};

  /** <code>float</code> array for dim lighting, set to <code>{0.5F, 0.5F, 0.5F, 1}</code> */
  private final static float[] DIM_LIGHTING = {0.5F, 0.5F, 0.5F, 1};

  // Class fields
  private JCheckBox animating, viewpointLight, redLight, greenLight, blueLight, ambientLight;
  private SceneGLJPanel scenePanel;
  private Timer animationTimer;
  private int frameNumber;
  private Camera sceneCamera;
  private GLUT glut;
  private GL2 gl;

  /** Default constructor */
  private FourLights() {
    super("A Lighting Demo");
    this.setFrameNumber(0);
    this.setGlut(new GLUT());
    this.constructGUI();
  }

  // Setters

  /**
   * Setter for <code>FourLights.animating</code>
   *
   * @param animating <code>JCheckBox</code>
   * @return void
   */
  private void setAnimating(JCheckBox animating) {
    this.animating = animating;
  }

  /**
   * Setter for <code>FourLights.viewpointLight</code>
   *
   * @param viewpointLight <code>JCheckBox</code>
   * @return void
   */
  private void setViewpointLight(JCheckBox viewpointLight) {
    this.viewpointLight = viewpointLight;
  }

  /**
   * Setter for <code>FourLights.redLight</code>
   *
   * @param redLight <code>JCheckBox</code>
   * @return void
   */
  private void setRedLight(JCheckBox redLight) {
    this.redLight = redLight;
  }

  /**
   * Setter for <code>FourLights.greenLight</code>
   *
   * @param greenLight <code>JCheckBox</code>
   * @return void
   */
  private void setGreenLight(JCheckBox greenLight) {
    this.greenLight = greenLight;
  }

  /**
   * Setter for <code>FourLights.blueLight</code>
   *
   * @param blueLight <code>JCheckBox</code>
   * @return void
   */
  private void setBlueLight(JCheckBox blueLight) {
    this.blueLight = blueLight;
  }

  /**
   * Setter for <code>FourLights.ambientLight</code>
   *
   * @param ambientLight <code>JCheckBox</code>
   * @return void
   */
  private void setAmbientLight(JCheckBox ambientLight) {
    this.ambientLight = ambientLight;
  }

  /**
   * Setter for <code>FourLights.scenePanel</code>
   *
   * @param scenePanel <code>SceneGLJPanel</code>
   * @return void
   */
  private void setScenePanel(SceneGLJPanel scenePanel) {
    this.scenePanel = scenePanel;
  }

  /**
   * Setter for <code>FourLights.animationTimer</code>
   *
   * @param animationTimer <code>Timer</code>
   * @return void
   */
  private void setAnimationTimer(Timer animationTimer) {
    this.animationTimer = animationTimer;
  }

  /**
   * Setter for <code>FourLights.frameNumber</code>
   *
   * @param frameNumber <code>int</code>
   * @return void
   */
  private void setFrameNumber(int frameNumber) {
    this.frameNumber = frameNumber;
  }

  /**
   * Setter for <code>FourLights.sceneCamera</code>
   *
   * @param sceneCamera <code>Camera</code>
   * @return void
   */
  private void setSceneCamera(Camera sceneCamera) {
    this.sceneCamera = sceneCamera;
  }

  /**
   * Setter for <code>FourLights.glut</code>
   *
   * @param glut <code>GLUT</code>
   * @return void
   */
  private void setGlut(GLUT glut) {
    this.glut = glut;
  }

  /**
   * Setter for <code>FourLights.gl</code>
   *
   * @param gl <code>GL2</code>
   * @return void
   */
  private void setGl(GL2 gl) {
    this.gl = gl;
  }

  // Getters

  /**
   * Getter for <code>FourLights.animating</code>
   *
   * @return animating <code>JCheckBox</code>
   */
  private JCheckBox getAnimating() {
    return this.animating;
  }

  /**
   * Getter for <code>FourLights.viewpointLight</code>
   *
   * @return viewpointLight <code>JCheckBox</code>
   */
  private JCheckBox getViewpointLight() {
    return this.viewpointLight;
  }

  /**
   * Getter for <code>FourLights.redLight</code>
   *
   * @return redLight <code>JCheckBox</code>
   */
  private JCheckBox getRedLight() {
    return this.redLight;
  }

  /**
   * Getter for <code>FourLights.greenLight</code>
   *
   * @return greenLight <code>JCheckBox</code>
   */
  private JCheckBox getGreenLight() {
    return this.greenLight;
  }

  /**
   * Getter for <code>FourLights.blueLight</code>
   *
   * @return blueLight <code>JCheckBox</code>
   */
  private JCheckBox getBlueLight() {
    return this.blueLight;
  }

  /**
   * Getter for <code>FourLights.ambientLight</code>
   *
   * @return ambientLight <code>JCheckBox</code>
   */
  private JCheckBox getAmbientLight() {
    return this.ambientLight;
  }

  /**
   * Getter for <code>FourLights.scenePanel</code>
   *
   * @return scenePanel <code>SceneGLJPanel</code>
   */
  private SceneGLJPanel getScenePanel() {
    return this.scenePanel;
  }

  /**
   * Getter for <code>FourLights.animationTimer</code>
   *
   * @return animationTimer <code>Timer</code>
   */
  private Timer getAnimationTimer() {
    return this.animationTimer;
  }

  /**
   * Getter for <code>FourLights.frameNumber</code>
   *
   * @return frameNumber <code>int</code>
   */
  private int getFrameNumber() {
    return this.frameNumber;
  }

  /**
   * Getter for <code>FourLights.sceneCamera</code>
   *
   * @return sceneCamera <code>Camera</code>
   */
  private Camera getSceneCamera() {
    return this.sceneCamera;
  }

  /**
   * Getter for <code>FourLights.glut</code>
   *
   * @return glut <code>GLUT</code>
   */
  private GLUT getGlut() {
    return this.glut;
  }

  /**
   * Getter for <code>FourLights.gl</code>
   *
   * @return gl <code>GL2</code>
   */
  private GL2 getGl() {
    return this.gl;
  }

  // Utility methods

  /**
   * This functionality is related to the display of the GUI itself to the viewer. Originally, this
   * was pretty hard to read due to its jambled inclusion in the class constructor. To enhance its
   * readability, the author divided the various parts of the code into logical sections and
   * commented accordingly.
   * <br />
   * <br />
   * A few pieces of functionality were modified or removed as needed. For instance, one of the
   * <code>JCheckBox</code> instances related to the display of one of the original template file's
   * objects, the teapot saucer, was removed. Relatedly, a number of layout managers were added to
   * ensure the bottom interface panel retains its shape if the window dimensions were ever to
   * change from their present values.
   *
   * @return void
   */
  private void constructGUI() {

    // Local declarations
    final JPanel mainPanel, bottomPanel, topRow, bottomRow;
    final JCheckBox animateBox, vpLightBox, redLightBox, greenLightBox, blueLightBox, ambientBox;
    final Camera camera;
    final SceneGLJPanel scene;
    final Timer sceneTimer;

    // Define main panels
    mainPanel = new JPanel(new BorderLayout());
    bottomPanel = new JPanel(new GridLayout(2, 1));
    topRow = new JPanel(new GridLayout(1, 3, 5, 5));
    bottomRow = new JPanel(new GridLayout(1, 3, 5, 5));

    // Define GLJPanel scene
    scene = new SceneGLJPanel();
    scene.setPreferredSize(new Dimension(FourLights.WINDOW_WIDTH, FourLights.WINDOW_HEIGHT));
    this.setScenePanel(scene);

    // Define camera
    camera = new Camera();
    camera.lookAt(5,10,30, 0,0,0, 0,1,0);
    camera.setScale(15);
    camera.installTrackball(this.getScenePanel());
    this.setSceneCamera(camera);

    // Define checkboxes
    animateBox = new JCheckBox("Animate", true);
    vpLightBox = new JCheckBox("Viewpoint Light", false);
    redLightBox = new JCheckBox("Red Light", true);
    greenLightBox = new JCheckBox("Green Light", true);
    blueLightBox = new JCheckBox("Blue Light", true);
    ambientBox = new JCheckBox("Global Ambient Light", false);

    // Add listeners
    animateBox.addActionListener(new FourLights.CheckBoxListener());
    vpLightBox.addActionListener(new FourLights.CheckBoxListener());
    redLightBox.addActionListener(new FourLights.CheckBoxListener());
    greenLightBox.addActionListener(new FourLights.CheckBoxListener());
    blueLightBox.addActionListener(new FourLights.CheckBoxListener());
    ambientBox.addActionListener(new FourLights.CheckBoxListener());

    // Set class checkboxes
    this.setAnimating(animateBox);
    this.setViewpointLight(vpLightBox);
    this.setRedLight(redLightBox);
    this.setGreenLight(greenLightBox);
    this.setBlueLight(blueLightBox);
    this.setAmbientLight(ambientBox);

    // Add checkboxes to panels
    topRow.add(animateBox);
    topRow.add(vpLightBox);
    topRow.add(ambientBox);
    bottomRow.add(redLightBox);
    bottomRow.add(greenLightBox);
    bottomRow.add(blueLightBox);

    // Add minipanels to main panels
    bottomPanel.add(topRow);
    bottomPanel.add(bottomRow);
    mainPanel.add(scene, BorderLayout.CENTER);
    mainPanel.add(bottomPanel, BorderLayout.SOUTH);

    // Define JFrame properties
    this.setContentPane(mainPanel);
    this.pack();
    this.setLocation(50, 50);
    this.setResizable(false);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setVisible(true);

    // Define scene animation timer
    sceneTimer = new Timer(FourLights.TIMER_DELAY, new FourLights.TimerListener());
    sceneTimer.setInitialDelay(FourLights.INITIAL_DELAY);
    sceneTimer.start();
    this.setAnimationTimer(sceneTimer);
  }

  // Listener handlers

  /**
   * This method serves as the primary handler of all <code>JCheckBox</code> selects and deselects,
   * called within the body of <code>FourLights.CheckBoxListener#actionPerformed</code> as the sole
   * operation. Basically, if the animation box is clicked, the method checks the state of the
   * animation, switching it to its opposite status depending on its stored value. Otherwise, the
   * scene is repainted, with the rendering of all lights handled deeper down in the logic code.
   *
   * @see FourLights.CheckBoxListener#actionPerformed
   * @param e <code>ActionEvent</code>, passed from <code>FourLights.CheckBoxListener</code>
   * @return void
   */
  private void checkboxHandler(ActionEvent e) {

    // Declarations (cache temp variables)
    final Timer tempTimer;
    final JCheckBox tempAnimating;
    final SceneGLJPanel tempScenePanel;

    // Definitions
    tempTimer = this.getAnimationTimer();
    tempAnimating = this.getAnimating();
    tempScenePanel = this.getScenePanel();

    if (e.getSource() == tempAnimating) {
      if (tempAnimating.isSelected()) {
        tempTimer.start();
      } else {
        tempTimer.stop();
      }
    } else {
      tempScenePanel.repaint();
    }
  }

  /**
   * This method, like that above it, is an example of a method used as the sole action handler of
   * one of the main class's <code>ActionListener</code> inner classes, namely
   * <code>FourLights.TimerListener</code>. This method, called from within the body of the
   * <code>FourLights.TimerListener#actionPerformed</code> method, simply increases the value of the
   * frame counter and repaints the scene accordingly.
   *
   * @see FourLights.TimerListener#actionPerformed
   * @return void
   */
  private void timerHandler() {
    this.setFrameNumber(this.getFrameNumber() + 1);
    this.getScenePanel().repaint();
  }

  // Inner classes

  /**
   * This class, an extending class of the <code>GLJPanel</code> class, defines the scene itself,
   * the instance of which is stored in the superclass. Its utility methods are solely related to
   * the rendering of scene elements, such as the handling of lighting to the drawing and rendering
   * of the central truncated isocahedron object and its octohedron <code>LightSouce</code>
   * elements. This division of code based on the parts played by its functionality is in accordance
   * with the <a href="https://en.wikipedia.org/wiki/Single_responsibility_principle">single
   * responsibility principle</a>, as per the rationale behind the author's decision to rewrite this
   * program in the first place.
   *
   * @see com.jogamp.opengl.awt.GLJPanel
   * @author David J. Eck, Andrew Eissen, UMUC faculty, et al.
   */
  private final class SceneGLJPanel extends GLJPanel {

    // Class field
    private ArrayList<LightSource> lightSourceArrayList;

    /** Default constructor */
    private SceneGLJPanel() {
      super(new GLCapabilities(null));
      this.setLightSourceArrayList(new ArrayList<>());
      this.addGLEventListener(new FourLights.SceneGLEventListener());
    }

    // Setter

    /**
     * Setter for <code>SceneGLJPanel.lightSourceArrayList</code>
     *
     * @param lightSourceArrayList <code>ArrayList</code>
     * @return void
     */
    private void setLightSourceArrayList(ArrayList<LightSource> lightSourceArrayList) {
      this.lightSourceArrayList = lightSourceArrayList;
    }

    // Getter

    /**
     * Getter for <code>SceneGLJPanel.lightSourceArrayList</code>
     *
     * @return lightSourceArrayList <code>ArrayList</code>
     */
    private ArrayList<LightSource> getLightSourceArrayList() {
      return this.lightSourceArrayList;
    }

    // Utility methods

    /**
     * This method creates a number of new <code>LightSource</code> instances on demand,
     * specifically within the body of <code>FourLights.SceneGLEventListener#init</code>. These new
     * initialized objects are created (the constructors of which then call their own related
     * configuration handlers via <code>FourLights.LightSource#configureLightSource</code>) and then
     * shunted into the <code>lightSourceArrayList</code> list for later use in iteration.
     *
     * @see FourLights.SceneGLEventListener#init
     * @see FourLights.LightSource#configureLightSource
     * @return void
     */
    private void constructLightSources() {

      // Declaration
      final ArrayList<LightSource> tempLightSourceArrayList;

      // Definition
      tempLightSourceArrayList = this.getLightSourceArrayList();

      // New red light
      tempLightSourceArrayList.add(new LightSource(FourLights.this.getRedLight(),
        GL2.GL_LIGHT1, FourLights.COLOR_1, FourLights.COLOR_1_AMBIENT, 11, 9, -25));

      // New green light
      tempLightSourceArrayList.add(new LightSource(FourLights.this.getGreenLight(),
        GL2.GL_LIGHT2, FourLights.COLOR_2, FourLights.COLOR_2_AMBIENT, 10, 5, -15));

      // New blue light
      tempLightSourceArrayList.add(new LightSource(FourLights.this.getBlueLight(),
        GL2.GL_LIGHT3, FourLights.COLOR_3, FourLights.COLOR_3_AMBIENT, 9, 7, -5));
    }

    /**
     * This method is a helper method called with every repaint by the primary scene event listener
     * display method, namely <code>FourLights.SceneGLEventListener#display</code>. It handles the
     * changes in y-axis rotation angle placement of the three primary encircling light sources, the
     * newly calculated values of which are then passed to all <code>LightSource</code> instances'
     * <code>FourLights.LightSource#adjustLightSource</code> methods. It also handles the switches
     * on and off of the viewpoint light.
     *
     * @see FourLights.SceneGLEventListener#display
     * @return void
     */
    private void applyLighting() {

      // Declarations (cache temp variables)
      final int tempFrameNumber;
      final GL2 tempGl;
      final JCheckBox tempViewpointLight;
      final ArrayList<LightSource> tempLightSourceArrayList;
      final double[] arrayOfRotationValues;

      // Definitions
      tempFrameNumber = FourLights.this.getFrameNumber();
      tempGl = FourLights.this.getGl();
      tempViewpointLight = FourLights.this.getViewpointLight();
      tempLightSourceArrayList = this.getLightSourceArrayList();
      arrayOfRotationValues = new double[] {
        -tempFrameNumber,
        (tempFrameNumber + 100) * 0.8743,
        (tempFrameNumber - 100) * 1.3057
      };

      // Adds a bit of pseudo-reflection to the lighting balls (helpful when they're switched off)
      tempGl.glColor3d(0.5, 0.5, 0.5);
      tempGl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, FourLights.BLACK, 0);

      // Toggle viewpoint light source
      if (tempViewpointLight.isSelected()) {
          tempGl.glEnable(GL2.GL_LIGHT0);
          tempGl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, FourLights.DIM_LIGHTING, 0);
          tempGl.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, 1);
      } else {
          tempGl.glDisable(GL2.GL_LIGHT0);
      }

      // Redefine light source y-axis rotational angle values (a bit messy)
      for (int i = 0; i < tempLightSourceArrayList.size(); i++) {
        tempLightSourceArrayList.get(i).adjustLightSource(arrayOfRotationValues[i]);
      }

      // Turn off emission color
      tempGl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_EMISSION, FourLights.BLACK, 0);
    }

    /**
     * As with the above lighting method, this method is called from within the primary scene event
     * listener's display method, namely <code>FourLights.SceneGLEventListener#display</code>, with
     * every repaint of the scene. It is responsible for rendering the author's chosen scene object,
     * a <a href="https://en.wikipedia.org/wiki/Truncated_icosahedron">truncated icosahedron</a>,
     * the vertices, face indices, and normals of which were derived from a class provided in the
     * Project 2 template files package, namely <code>Polyhedron</code>. A copy of the class may be
     * found <a href="http://math.hws.edu/graphicsbook/source/jogl/Polyhedron.java">here</a>. They
     * are included in this program in the below inner static class, aptly named
     * <code>FourLights.TruncatedIcosahedron</code>.
     *
     * @see FourLights.TruncatedIcosahedron
     * @see FourLights.SceneGLEventListener#display
     * @return void
     */
    private void drawShape() {

      // Declarations
      final GL2 tempGl;
      final int[][] tempFaces;
      final double[][] tempNormals, tempVertices;

      // Definitions (cache values)
      tempGl = FourLights.this.getGl();
      tempFaces = FourLights.TruncatedIcosahedron.getFaces();
      tempNormals = FourLights.TruncatedIcosahedron.getNormals();
      tempVertices = FourLights.TruncatedIcosahedron.getVertices();

      // Clone new matrix copy
      tempGl.glPushMatrix();

      // Apply default scaling to this matrix
      tempGl.glScaled(FourLights.OBJECT_SCALE, FourLights.OBJECT_SCALE, FourLights.OBJECT_SCALE);

      // Apply translation to matrix using centerpoint coordinates
      tempGl.glTranslated(0, 0, 0);

      // Iterate through the object's faces
      for (int i = 0; i < tempFaces.length; i++) {

        // New matrix copy on top for each face
        tempGl.glPushMatrix();

        // Draw the faces (GL2.GL_TRIANGLE_FAN colors triangle primative slices between vertices)
        this.drawShape(tempGl, FourLights.PRIMARY_FACE_COLOR, tempFaces, tempVertices,
          tempNormals[i], GL2.GL_TRIANGLE_FAN, i);

        // Draw black borders between vertices w/ GL_LINE_LOOP
        this.drawShape(tempGl, FourLights.BLACK, tempFaces, tempVertices, tempNormals[i],
          GL2.GL_LINE_LOOP, i);

        // Remove face matrix copy
        tempGl.glPopMatrix();
      }

      // Delete this master matrix copy/restore to original matrix copy
      tempGl.glPopMatrix();
    }

    /**
     * This method is an overloaded method of that above it, used by that method as an extended
     * helper method that paints the faces of the truncated icosohedron and renders each face's
     * black borders. It basically assembles the vertices as specified in the faces list by the
     * <code>int</code> indices associated with each vertex, setting the colors and normals
     * accordingly. As this function is run twice per loop iteration in the above method, the author
     * decided to simply pass the needed arrays and primatives as parameters rather than make a
     * large number of accessor method invocations for every run of this method.
     * <br />
     * <br />
     * Originally, it was within this method that the author called a separate helper function,
     * namely <code>FourLights.SceneGLJPanel#calculateNormal</code>, to help calculate the normals
     * for each face to ensure that lighting occurred as expected. However, upon realizing that the
     * <code>Polyhedron.java</code> class already listed normals, the author commented out the
     * method invocation in favor of using a faster, preassembled array. However, uncommenting the
     * line and commenting out the usage of the hardcoded array will demonstrate that the method
     * calculates the same normals and behaves the same way as expected.
     *
     * @param paramGl <code>GL2</code> cached copy
     * @param paramColorArray <code>float[]</code>
     * @param paramFaces <code>int[][]</code> cached copy
     * @param paramVertices <code>double[][]</code> cached copy
     * @param paramNormals <code>double[]</code> cached copy
     * @param paramImmediateMode <code>int</code>
     * @param paramCounter <code>int</code>
     * @return void
     */
    private void drawShape(GL2 paramGl, float[] paramColorArray, int[][] paramFaces,
        double[][] paramVertices, double[] paramNormals, int paramImmediateMode, int paramCounter) {

      // Declaration
      int vertexIndex;

      // Set OpenGL float color values for red, green, blue
      paramGl.glColor3f(paramColorArray[0], paramColorArray[1], paramColorArray[2]);

      // Provide mode (GL2.GL_TRIANGLE_FAN suggested for faces)
      paramGl.glBegin(paramImmediateMode);

      // Set face normal prior to vertices definitions
      paramGl.glNormal3dv(paramNormals, 0);

      // Originally, before realizing Polyhedron.java listed normals, these were calculated below
      //this.calculateNormal(paramGl, paramVertices, paramFaces[paramCounter]);

      // Build vertices
      for (int i = 0; i < paramFaces[paramCounter].length; i++) {
        vertexIndex = paramFaces[paramCounter][i];
        paramGl.glVertex3dv(paramVertices[vertexIndex], 0);
      }

      // Complete primitive assembly
      paramGl.glEnd();
    }

    /**
     * This unused helper method was created personally by the author using the OpenGL wiki's
     * <a href="https://www.khronos.org/opengl/wiki/Calculating_a_Surface_Normal">pseudo-code</a>
     * for calculating face normals. This was prior to his realization that the aforementioned
     * template files class <code>Polyhedron.java</code> included the normal values as well as the
     * vertices and faces list in its contents. Regardless, as this class may have some use for
     * other students, the author has seen fit to leave it in just in case.
     *
     * @see <a href="https://www.khronos.org/opengl/wiki/Calculating_a_Surface_Normal">Calculating a
     *      Surface Normal</a>
     * @param paramGl <code>GL2</code> instance, cached from two methods above
     * @param paramVertices <code>double[][]</code> two-dimensional array listing vertices
     * @param paramFaceIndices <code>int[]</code> single line from two-dimensional vertex index face
     *        array listing.
     * @return void
     */
    private void calculateNormal(GL2 paramGl, double[][] paramVertices, int[] paramFaceIndices) {

      // Declarations
      int pointLength, j;
      double normalX, normalY, normalZ;
      final double[][] points;

      // Definition
      points = new double[paramFaceIndices.length][];

      // Add vertices at those face indices to array
      for (int i = 0; i < paramFaceIndices.length; i++) {
        points[i] = paramVertices[paramFaceIndices[i]];
      }

      // More definitions
      pointLength = points.length;
      normalX = normalY = normalZ = 0;

      // Cross products based on pseudocode formulas
      for (int i = 0; i < pointLength; i++) {
        j = (i + 1) % pointLength;
        normalX += (points[i][1] - points[j][1]) * (points[i][2] + points[j][2]);
        normalY += (points[i][2] - points[j][2]) * (points[i][0] + points[j][0]);
        normalZ += (points[i][0] - points[j][0]) * (points[i][1] + points[j][1]);
      }

      // Apply normal
      paramGl.glNormal3d(normalX, normalY, normalZ);
    }
  }

  /**
   * This class serves the primary scene event listener of the program, implementing the required
   * <code>GLEventListener</code> interface. However, only <code>GLEventListener#init</code> and
   * <code>GLEventListener#display</code> were fully implemented due to their importance in
   * initializing the scene and refreshing and updating its contents as required with each change.
   * As per the <a href="https://en.wikipedia.org/wiki/Single_responsibility_principle">single
   * responsibility principle</a>, the code contained herein is only related to refreshing the scene
   * and handling the elements contained therein.
   *
   * @see com.jogamp.opengl.GLEventListener
   * @author David J. Eck, Andrew Eissen, UMUC faculty, et al.
   */
  private final class SceneGLEventListener implements GLEventListener {

    /**
     * One of the four required methods of the <code>GLEventListener</code> interface, this method
     * renders the original scene, defining all the necessary lighting modes and models; begins the
     * initialization of new <code>LightSource</code> objects; and defines the global
     * <code>GL2</code> instance used by all methods in other inner classes in coordination with
     * this one.
     *
     * @see com.jogamp.opengl.GLEventListener#init
     * @param drawable <code>GLAutoDrawable</code>
     * @return void
     */
    @Override
    public void init(GLAutoDrawable drawable) {

      // Declaration
      final GL2 tempGl;
      final SceneGLJPanel tempScenePanel;

      // Definitions
      tempGl = drawable.getGL().getGL2();
      tempScenePanel = FourLights.this.getScenePanel();

      // Set as class GL2 instance
      FourLights.this.setGl(tempGl);

      tempGl.glClearColor(0, 0, 0, 1); // black background
      tempGl.glEnable(GL2.GL_DEPTH_TEST);
      tempGl.glEnable(GL2.GL_LIGHTING);
      tempGl.glEnable(GL2.GL_LIGHT0);
      tempGl.glEnable(GL2.GL_NORMALIZE);
      tempGl.glEnable(GL2.GL_COLOR_MATERIAL);
      tempGl.glLightModeli(GL2.GL_LIGHT_MODEL_LOCAL_VIEWER, 1);
      tempGl.glMateriali(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 32);

      // Initialize new LightSource instances, allowing them to configure individually
      tempScenePanel.constructLightSources();
    }

    /**
     * The other defined method of the four total methods required for implementation of the
     * <code>GLEventListener</code> interface, this method is called every time the scene is
     * refreshed to paint the scene objects anew in accordance with whatever transformations have
     * been applied and whatever check boxes have been clicked. This method's contents were taken
     * from the original version of the file and modified with extracts from the fellow template
     * <code>UnlitCube.java</code> as needed.
     *
     * @see com.jogamp.opengl.GLEventListener#display
     * @param drawable <code>GLAutoDrawable</code>
     * @return void
     */
    @Override
    public void display(GLAutoDrawable drawable) {

      // Declarations
      final GL2 tempGl;
      final Camera tempCamera;
      final SceneGLJPanel tempScenePanel;
      final JCheckBox tempAmbientLight;

      // Definitions
      tempGl = FourLights.this.getGl();
      tempCamera = FourLights.this.getSceneCamera();
      tempScenePanel = FourLights.this.getScenePanel();
      tempAmbientLight = FourLights.this.getAmbientLight();

      // From UnlitCube.java
      tempGl.glClearColor(0, 0, 0, 0);
      tempGl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

      // Apply GL2 instance to Camera instance
      tempCamera.apply(tempGl);

      // Changing lighting of all the LightSource elements per change in their rotations
      tempScenePanel.applyLighting();

      // Handle the selection of global ambient lighting if applied by user
      if (tempAmbientLight.isSelected()) {
          tempGl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, FourLights.GLOBAL_AMBIENT, 0);
      } else {
          tempGl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, FourLights.BLACK, 0);
      }

      tempGl.glColor3d(0.7, 0.7, 0.7);
      tempGl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, FourLights.BLACK, 0);
      tempScenePanel.drawShape();
    }

    /**
     * Noop method required by <code>GLEventListener</code>
     *
     * @see com.jogamp.opengl.GLEventListener#reshape
     * @param drawable <code>GLAutoDrawable</code>
     * @param i <code>int</code>
     * @param i1 <code>int</code>
     * @param i2 <code>int</code>
     * @param i3 <code>int</code>
     * @return void
     */
    @Override
    public void reshape(GLAutoDrawable drawable, int i, int i1, int i2, int i3) {}

    /**
     * Noop method required by <code>GLEventListener</code>
     *
     * @see com.jogamp.opengl.GLEventListener#dispose
     * @param drawable <code>GLAutoDrawable</code>
     * @return void
     */
    @Override
    public void dispose(GLAutoDrawable drawable) {}
  }

  /**
   * This class serves as the primary encapsulating construct used to define and create new rotating
   * light ball units that encircle the main object of the scene, in the case of this program, the
   * truncated icosahedron. Originally, one of the author's previous implementations of this project
   * had the light sources created within the <code>FourLights.SceneGLEventListener#init</code>
   * method using some helper methods. However, the code used grew to the point of becoming
   * difficult to read and stretched across several inner classes, leading the author to consolidate
   * all code related to the light objects in a separate inner class, in accordance with the
   * <a href="https://en.wikipedia.org/wiki/Single_responsibility_principle">single responsibility
   * principle</a>, a paradigm which undergird the author's rewrite at a base level.
   *
   * @author David J. Eck, Andrew Eissen, UMUC faculty, et al.
   */
  private final class LightSource {

    // Class fields
    private JCheckBox checkBox;
    private int lightType;
    private float[] color, colorAmbient;
    private double translateX, translateY, translateZ;

    /**
     * Parameterized constructor
     *
     * @param checkBox <code>JCheckBox</code>
     * @param lightType <code>int</code>
     * @param color <code>float[][]</code>
     * @param colorAmbient <code>float[][]</code>
     * @param translateX <code>double</code>
     * @param translateY <code>double</code>
     * @param translateZ <code>double</code>
     */
    private LightSource(JCheckBox checkBox, int lightType, float[] color, float[] colorAmbient,
        double translateX, double translateY, double translateZ) {

      // Set fields
      this.setCheckBox(checkBox);
      this.setLightType(lightType);
      this.setColor(color);
      this.setColorAmbient(colorAmbient);
      this.setTranslateX(translateX);
      this.setTranslateY(translateY);
      this.setTranslateZ(translateZ);

      // Set individual configuration
      this.configureLightSource();
    }

    // Setters

    /**
     * Setter for <code>LightSource.checkBox</code>
     *
     * @param checkBox <code>JCheckBox</code>
     * @return void
     */
    private void setCheckBox(JCheckBox checkBox) {
      this.checkBox = checkBox;
    }

    /**
     * Setter for <code>LightSource.lightType</code>
     *
     * @param lightType <code>int</code>
     * @return void
     */
    private void setLightType(int lightType) {
      this.lightType = lightType;
    }

    /**
     * Setter for <code>LightSource.color</code>
     *
     * @param color <code>float[]</code>
     * @return void
     */
    private void setColor(float[] color) {
      this.color = color;
    }

    /**
     * Setter for <code>LightSource.colorAmbient</code>
     *
     * @param colorAmbient <code>float[]</code>
     * @return void
     */
    private void setColorAmbient(float[] colorAmbient) {
      this.colorAmbient = colorAmbient;
    }

    /**
     * Setter for <code>LightSource.translateX</code>
     *
     * @param translateX <code>double</code>
     * @return void
     */
    private void setTranslateX(double translateX) {
      this.translateX = translateX;
    }

    /**
     * Setter for <code>LightSource.translateY</code>
     *
     * @param translateY <code>double</code>
     * @return void
     */
    private void setTranslateY(double translateY) {
      this.translateY = translateY;
    }

    /**
     * Setter for <code>LightSource.translateZ</code>
     *
     * @param translateZ <code>double</code>
     * @return void
     */
    private void setTranslateZ(double translateZ) {
      this.translateZ = translateZ;
    }

    // Getters

    /**
     * Getter for <code>LightSource.checkBox</code>
     *
     * @return checkBox <code>JCheckBox</code>
     */
    private JCheckBox getCheckBox() {
      return this.checkBox;
    }

    /**
     * Getter for <code>LightSource.lightType</code>
     *
     * @return lightType <code>int</code>
     */
    private int getLightType() {
      return this.lightType;
    }

    /**
     * Getter for <code>LightSource.color</code>
     *
     * @return color <code>float[]</code>
     */
    private float[] getColor() {
      return this.color;
    }

    /**
     * Getter for <code>LightSource.colorAmbient</code>
     *
     * @return colorAmbient <code>float[]</code>
     */
    private float[] getColorAmbient() {
      return this.colorAmbient;
    }

    /**
     * Getter for <code>LightSource.translateX</code>
     *
     * @return translateX <code>double</code>
     */
    private double getTranslateX() {
      return this.translateX;
    }

    /**
     * Getter for <code>LightSource.translateY</code>
     *
     * @return translateY <code>double</code>
     */
    private double getTranslateY() {
      return this.translateY;
    }

    /**
     * Getter for <code>LightSource.translateZ</code>
     *
     * @return translateZ <code>double</code>
     */
    private double getTranslateZ() {
      return this.translateZ;
    }

    // Utility methods

    /**
     * This method is called once at the end of the constructor to set the properties associated
     * with this lighting object instance. Originally, this code existed in one of the above classes
     * and was being re-invoked with every scene refresh. The creation of a dedicated class for
     * lighting objects helped resolve this issue. In the first version of file, if the author
     * remembers correctly, the code was just copy-pasted for each of the light objects, an approach
     * that inspired the author to rewrite the whole class in its entirety to have it be a bit more
     * readable.
     *
     * @return void
     */
    private void configureLightSource() {

      // Declarations of temp fields
      final GL2 tempGl;
      final int tempLightType;
      final float[] tempColor, tempColorAmbient;

      // Definitions (cached)
      tempGl = FourLights.this.getGl();
      tempLightType = this.getLightType();
      tempColor = this.getColor();
      tempColorAmbient = this.getColorAmbient();

      // Define light models
      tempGl.glLightfv(tempLightType, GL2.GL_AMBIENT, tempColorAmbient, 0);
      tempGl.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, 1);
      tempGl.glLightfv(tempLightType, GL2.GL_DIFFUSE, tempColor, 0);
      tempGl.glLightfv(tempLightType, GL2.GL_SPECULAR, tempColor, 0);
    }

    /**
     * This method is called externally by <code>FourLights.SceneGLJPanel#applyLighting</code> in a
     * <code>for</code> loop for each of the created <code>LightSource</code> elements. It takes a
     * passed <code>rotateY</code> y-axis rotation <code>double</code> parameter and applies that
     * new value to <code>GL2#glRotated</code> to simulate the movement of the lighting ball around
     * the truncated icosahedron.
     *
     * @see FourLights.SceneGLJPanel#applyLighting
     * @param paramRotateY <code>double</code>
     * @return void
     */
    private void adjustLightSource(double paramRotateY) {

      // Declarations
      final GLUT tempGlut;
      final GL2 tempGl;
      final int tempLightType;
      final double tempTranslateX, tempTranslateY, tempTranslateZ;
      final float[] tempColor;
      final JCheckBox tempCheckBox;

      // Definitions
      tempGlut = FourLights.this.getGlut();
      tempGl = FourLights.this.getGl();
      tempLightType = this.getLightType();
      tempTranslateX = this.getTranslateX();
      tempTranslateY = this.getTranslateY();
      tempTranslateZ = this.getTranslateZ();
      tempColor = this.getColor();
      tempCheckBox = this.getCheckBox();

      if (tempCheckBox.isSelected()) {
          tempGl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_EMISSION, tempColor, 0);
          tempGl.glEnable(tempLightType);
      } else {
          tempGl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_EMISSION, FourLights.BLACK, 0);
          tempGl.glDisable(tempLightType);
      }

      tempGl.glPushMatrix();
      tempGl.glRotated(paramRotateY, 0, 1, 0);
      tempGl.glTranslated(tempTranslateX, tempTranslateY, tempTranslateZ);
      tempGl.glLightfv(tempLightType, GL2.GL_POSITION, FourLights.BLACK, 0);
      tempGlut.glutSolidOctahedron(); // originally glutSolidSphere(1.0, 16, 8);
      tempGl.glPopMatrix();
    }
  }

  /**
   * This class is the inner class event listener used to handle the presses or clicks of
   * <code>JCheckBox</code> items. Its sole required interface method, <code>actionPerformed</code>,
   * itself calls the superclass method <code>FourLights.checkboxHandler</code>.
   *
   * @see java.awt.event.ActionListener
   * @author Andrew Eissen
   */
  private final class CheckBoxListener implements ActionListener {

    /**
     * This method is the sole method required of the <code>ActionListener</code> interface, namely
     * <code>java.awt.event.ActionListener#actionPerformed</code>. It simply calls the superclass
     * method <code>FourLights.checkboxHandler</code> when <code>JCheckBox</code> changes are
     * detected.
     *
     * @see FourLights#checkboxHandler
     * @see java.awt.event.ActionListener#actionPerformed
     * @param e <code>ActionEvent</code>
     * @return void
     */
    @Override
    public void actionPerformed(ActionEvent e) {
      FourLights.this.checkboxHandler(e);
    }
  }

  /**
   * This class is the inner class event listener used to handle the passage of <code>Timer</code>
   * interval periods. Its sole required interface method, <code>actionPerformed</code>, itself
   * calls the superclass method <code>FourLights.timerHandler</code>.
   *
   * @see java.awt.event.ActionListener
   * @author Andrew Eissen
   */
  private final class TimerListener implements ActionListener {

    /**
     * This method is the sole method required of the <code>ActionListener</code> interface, namely
     * <code>java.awt.event.ActionListener#actionPerformed</code>. It simply calls the superclass
     * method <code>FourLights.timerHandler</code> when <code>Timer</code> intervals elapse.
     *
     * @see FourLights#timerHandler
     * @see java.awt.event.ActionListener#actionPerformed
     * @param e <code>ActionEvent</code>
     * @return void
     */
    @Override
    public void actionPerformed(ActionEvent e) {
      FourLights.this.timerHandler();
    }
  }

  /**
   * This static class defines the vertices, face vertex indices, and face normals of a standard
   * <a href="https://en.wikipedia.org/wiki/Truncated_icosahedron">truncated icosahedron</a> object,
   * itself composed of 32 hexagonal and pentagonal faces and 60 vertices. The code for this object
   * was borrowed from the Project 2 templates package file entitled <code>Polyhedron.java</code>,
   * which contained the necessary arrays within its contents under the title "Soccer ball." In a
   * deceptive twist, that file also had a static object entitled "truncatedicosahedron" which, upon
   * further examination, was not actually a shape of this form but was rather an
   * <a href="https://en.wikipedia.org/wiki/Icosidodecahedron">icosidodecahedron</a> due to its use
   * of triangles and pentagons as the base shapes rather than hexagons and pentagons.
   *
   * @see <a href="https://en.wikipedia.org/wiki/Truncated_icosahedron">Truncated icosahedron</a>
   * @see <a href="http://math.hws.edu/graphicsbook/source/jogl/Polyhedron.java">Polyhedron.java</a>
   * @author David J. Eck, Andrew Eissen, UMUC faculty, et al.
   */
  private final static class TruncatedIcosahedron {

    /**
     * This static method returns a two-dimensional <code>double</code> array containing polyhedron
     * vertex data.
     *
     * @return <code>double[][]</code>
     */
    private static double[][] getVertices() {
      return new double[][] {
        {-0.666667,-0.745356,0.206011},
        {-0.666667,-0.745356,-0.206011},
        {-0.872678,-0.412023,-0.333333},
        {-1.000000,-0.206011,0.000000},
        {-0.872678,-0.412023,0.333333},
        {0.333333,0.872678,0.412023},
        {0.206011,0.666667,0.745356},
        {-0.206011,0.666667,0.745356},
        {-0.333333,0.872678,0.412023},
        {0.000000,1.000000,0.206011},
        {0.206011,0.666667,-0.745356},
        {0.333333,0.872678,-0.412023},
        {0.000000,1.000000,-0.206011},
        {-0.333333,0.872678,-0.412023},
        {-0.206011,0.666667,-0.745356},
        {0.872678,0.412023,0.333333},
        {0.666667,0.745356,0.206011},
        {0.666667,0.745356,-0.206011},
        {0.872678,0.412023,-0.333333},
        {1.000000,0.206011,0.000000},
        {0.872678,-0.412023,0.333333},
        {1.000000,-0.206011,0.000000},
        {0.872678,-0.412023,-0.333333},
        {0.666667,-0.745356,-0.206011},
        {0.666667,-0.745356,0.206011},
        {0.333333,-0.872678,-0.412023},
        {0.206011,-0.666667,-0.745356},
        {-0.206011,-0.666667,-0.745356},
        {-0.333333,-0.872678,-0.412023},
        {0.000000,-1.000000,-0.206011},
        {0.206011,-0.666667,0.745356},
        {0.333333,-0.872678,0.412023},
        {0.000000,-1.000000,0.206011},
        {-0.333333,-0.872678,0.412023},
        {-0.206011,-0.666667,0.745356},
        {0.412023,0.333333,0.872678},
        {0.745356,0.206011,0.666667},
        {0.745356,-0.206011,0.666667},
        {0.412023,-0.333333,0.872678},
        {0.206011,0.000000,1.000000},
        {-0.206011,0.000000,1.000000},
        {-0.412023,-0.333333,0.872678},
        {-0.745356,-0.206011,0.666667},
        {-0.745356,0.206011,0.666667},
        {-0.412023,0.333333,0.872678},
        {0.745356,-0.206011,-0.666667},
        {0.745356,0.206011,-0.666667},
        {0.412023,0.333333,-0.872678},
        {0.206011,0.000000,-1.000000},
        {0.412023,-0.333333,-0.872678},
        {-0.412023,0.333333,-0.872678},
        {-0.745356,0.206011,-0.666667},
        {-0.745356,-0.206011,-0.666667},
        {-0.412023,-0.333333,-0.872678},
        {-0.206011,0.000000,-1.000000},
        {-0.666667,0.745356,-0.206011},
        {-0.666667,0.745356,0.206011},
        {-0.872678,0.412023,0.333333},
        {-1.000000,0.206011,0.000000},
        {-0.872678,0.412023,-0.333333}
      };
    }

    /**
     * This static method returns a two-dimensional <code>int</code> array containing polyhedron
     * face vertex index data.
     *
     * @return <code>int[][]</code>
     */
    private static int[][] getFaces() {
      return new int[][] {
        {0,1,2,3,4},
        {5,6,7,8,9},
        {10,11,12,13,14},
        {15,16,17,18,19},
        {20,21,22,23,24},
        {25,26,27,28,29},
        {30,31,32,33,34},
        {35,36,37,38,39},
        {40,41,42,43,44},
        {45,46,47,48,49},
        {50,51,52,53,54},
        {55,56,57,58,59},
        {15,36,35,6,5,16},
        {20,37,36,15,19,21},
        {30,38,37,20,24,31},
        {40,39,38,30,34,41},
        {39,40,44,7,6,35},
        {45,22,21,19,18,46},
        {10,47,46,18,17,11},
        {11,17,16,5,9,12},
        {55,13,12,9,8,56},
        {50,14,13,55,59,51},
        {54,48,47,10,14,50},
        {49,26,25,23,22,45},
        {31,24,23,25,29,32},
        {0,33,32,29,28,1},
        {3,58,57,43,42,4},
        {56,8,7,44,43,57},
        {52,2,1,28,27,53},
        {53,27,26,49,48,54},
        {4,42,41,34,33,0},
        {2,52,51,59,58,3}
      };
    }

    /**
     * This static method returns a two-dimensional <code>double</code> array containing polyhedron
     * face normals data.
     *
     * @return <code>double[][]</code>
     */
    private static double[][] getNormals() {
      return new double[][] {
        {0.850651,0.525731,-0.000000},
          {-0.000000,-0.850651,-0.525731},
          {-0.000000,-0.850651,0.525731},
          {-0.850651,-0.525731,-0.000000},
          {-0.850651,0.525731,0.000000},
          {0.000000,0.850651,0.525731},
          {-0.000000,0.850651,-0.525731},
          {-0.525731,-0.000000,-0.850651},
          {0.525731,0.000000,-0.850651},
          {-0.525731,0.000000,0.850651},
          {0.525731,0.000000,0.850651},
          {0.850651,-0.525731,0.000000},
          {-0.577350,-0.577350,-0.577350},
          {-0.934172,0.000000,-0.356822},
          {-0.577350,0.577350,-0.577350},
          {0.000000,0.356822,-0.934172},
          {0.000000,-0.356822,-0.934172},
          {-0.934172,0.000000,0.356822},
          {-0.577350,-0.577350,0.577350},
          {-0.356822,-0.934172,0.000000},
          {0.356822,-0.934172,0.000000},
          {0.577350,-0.577350,0.577350},
          {0.000000,-0.356822,0.934172},
          {-0.577350,0.577350,0.577350},
          {-0.356822,0.934172,0.000000},
          {0.356822,0.934172,-0.000000},
          {0.934172,0.000000,-0.356822},
          {0.577350,-0.577350,-0.577350},
          {0.577350,0.577350,0.577350},
          {-0.000000,0.356822,0.934172},
          {0.577350,0.577350,-0.577350},
          {0.934172,-0.000000,0.356822}
      };
    }
  }
}