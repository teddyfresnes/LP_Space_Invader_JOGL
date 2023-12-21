package gl;

import com.jogamp.opengl.GL2;

import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;

public class Ship {
	private MainSI mainSI;
    private float posX, posY;
    private boolean leftKeyPressed = false;
    private boolean rightKeyPressed = false;
    private boolean spaceKeyPressed = false;
    

    public Ship(MainSI mainSI) {
        this.mainSI = mainSI;

        // ajout d'un KeyAdapter pour gérer les événements clavier
        mainSI.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e.getKeyCode());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                handleKeyRelease(e.getKeyCode());
            }
        });
        mainSI.setFocusable(true);
    }

    public float getPosX() {
        return posX;
    }

    public float getPosY() {
        return posY;
    }

    public boolean isSpaceKeyPressed() {
        return spaceKeyPressed;
    }
    
    public void setLeftKeyPressed(boolean leftKeyPressed) {
        this.leftKeyPressed = leftKeyPressed;
    }

    public void setRightKeyPressed(boolean rightKeyPressed) {
        this.rightKeyPressed = rightKeyPressed;
    }

    public void setSpaceKeyPressed(boolean spaceKeyPressed) {
        this.spaceKeyPressed = spaceKeyPressed;
    }

    public void reset() {
        posX = 0.0f;
        posY = 0.0f;
        leftKeyPressed = false;
        rightKeyPressed = false;
        spaceKeyPressed = false;
    }

    private void handleKeyPress(int keyCode) {
        // event si les touches ont été appuyées
        switch (keyCode) {
            case KeyEvent.VK_LEFT:
                leftKeyPressed = true;
                break;
            case KeyEvent.VK_RIGHT:
                rightKeyPressed = true;
                break;
            case KeyEvent.VK_SPACE:
                spaceKeyPressed = true;
                break;
        }
    }

    private void handleKeyRelease(int keyCode) {
        // event a la fin des touches (relachement des touches)
        switch (keyCode) {
            case KeyEvent.VK_LEFT:
                leftKeyPressed = false;
                break;
            case KeyEvent.VK_RIGHT:
                rightKeyPressed = false;
                break;
            case KeyEvent.VK_SPACE:
                spaceKeyPressed = false;
                break;
        }
    }

    public void draw(GL2 gl) {
        // dessin du vaisseau
        gl.glColor3f(1.0f, 1.0f, 1.0f); // white rgb

        // on place le vaisseau en bas
        float shipHeight = 0.2f; // hauteur vaisseau
        posY = -0.94f + shipHeight / 2; // posY en bas de la fenetre

        // square
        float shipBodyHeight = 0.01f;  // height of the spaceship body
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex2f(posX - 0.15f, posY - 0.05f);
        gl.glVertex2f(posX + 0.15f, posY - 0.05f);
        gl.glVertex2f(posX + 0.15f, posY + shipBodyHeight);
        gl.glVertex2f(posX - 0.15f, posY + shipBodyHeight);
        gl.glEnd();

        // square top  // height of the cabin
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex2f(posX - 0.1f, posY + shipBodyHeight);
        gl.glVertex2f(posX + 0.1f, posY + shipBodyHeight);
        gl.glVertex2f(posX + 0.1f, posY + shipBodyHeight + 0.05f);
        gl.glVertex2f(posX - 0.1f, posY + shipBodyHeight + 0.05f);
        gl.glEnd();

        // aile gauche
        gl.glBegin(GL2.GL_TRIANGLES);
        gl.glVertex2f(posX - 0.15f, posY + shipBodyHeight);
        gl.glVertex2f(posX - 0.23f, posY + shipBodyHeight);
        gl.glVertex2f(posX - 0.23f, posY + shipBodyHeight - 0.08f);
        gl.glEnd();

        // aile droite
        gl.glBegin(GL2.GL_TRIANGLES);
        gl.glVertex2f(posX + 0.15f, posY + shipBodyHeight);
        gl.glVertex2f(posX + 0.23f, posY + shipBodyHeight);
        gl.glVertex2f(posX + 0.23f, posY + shipBodyHeight - 0.08f);
        gl.glEnd();

        // decoration
        gl.glColor3f(0.6f, 0.6f, 1.0f);  // light blue for windows
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex2f(posX - 0.05f, posY + shipBodyHeight + 0.01f);
        gl.glVertex2f(posX + 0.05f, posY + shipBodyHeight + 0.01f);
        gl.glVertex2f(posX + 0.05f, posY + shipBodyHeight + 0.05f - 0.01f);
        gl.glVertex2f(posX - 0.05f, posY + shipBodyHeight + 0.05f - 0.01f);
        gl.glEnd();

        // deplacement vaisseau
        float moveSpeed = 0.02f;
        if (leftKeyPressed) {
            posX -= moveSpeed;
        }
        if (rightKeyPressed) {
            posX += moveSpeed;
        }
        // collisions murs
        float shipWidth = 0.30f; // largeur ship
        float leftLimit = -1.0f + shipWidth / 2; // limite gauche
        float rightLimit = 1.0f - shipWidth / 2; // limite droite
        posX = Math.max(leftLimit, Math.min(rightLimit, posX));
    }
}
