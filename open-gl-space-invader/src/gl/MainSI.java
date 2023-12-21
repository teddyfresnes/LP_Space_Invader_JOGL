package gl;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;

public class MainSI extends GLCanvas implements GLEventListener {
    private Ship ship;
    private GameController gameController;

    public MainSI() {
        this.addGLEventListener(this);
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e.getKeyCode());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                handleKeyRelease(e.getKeyCode());
            }
        });
        this.setFocusable(true);

        initializeGame();
    }

    private void initializeGame() {
        ship = new Ship(this);
        gameController = new GameController(ship);
        gameController.initializeEnemies();
    }

    private void handleKeyPress(int keyCode) {
        // event si les touches ont été appuyés
        long currentTime = System.currentTimeMillis();
        switch (keyCode) {
            case KeyEvent.VK_LEFT:
                ship.setLeftKeyPressed(true);
                break;
            case KeyEvent.VK_RIGHT:
                ship.setRightKeyPressed(true);
                break;
            case KeyEvent.VK_SPACE:
                ship.setSpaceKeyPressed(true);
                break;
        }
    }

    private void handleKeyRelease(int keyCode) {
        // event a la fin des touches (relâchement des touches)
        switch (keyCode) {
            case KeyEvent.VK_LEFT:
                ship.setLeftKeyPressed(false);
                break;
            case KeyEvent.VK_RIGHT:
                ship.setRightKeyPressed(false);
                break;
            case KeyEvent.VK_SPACE:
                ship.setSpaceKeyPressed(false);
                break;
        }
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        // initialisations OpenGL
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        ship.draw(gl);
        gameController.draw(gl);

        gl.glFlush();
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        // ajustements en cas de redimensionnement de la fenêtre
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        // libération des ressources OpenGL
    }

    public static void main(String[] args) {
        GLCanvas canvas = new MainSI();
        canvas.setPreferredSize(new Dimension(800, 600));

        final JFrame frame = new JFrame();
        frame.getContentPane().add(canvas);
        frame.setTitle("Spaysse 1vadeur");
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Ajout d'une boucle de rendu simple
        Animator animator = new Animator(canvas);
        animator.start();
    }
}
