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
import java.util.ArrayList;
import java.util.List;

public class MainSI_old extends GLCanvas implements GLEventListener {
    private float posX, posY, posZ;
    private boolean leftKeyPressed = false;
    private boolean rightKeyPressed = false;
    private boolean spaceKeyPressed = false;
    
    private List<Enemy> enemies;
    private float enemySpeed = 0.002f;
    private int direction = 1; // 1 pour droite, -1 pour gauche
    private float enemyMoveDown = 0.1f; // distance de descente a chaque collision des ennemis
    
    private List<Bullet> bullets;
    private float bulletSpeed = 0.03f;
    private long lastBulletTime = 0;
    private long bulletInterval = 500; // intervalle tir en ms


    public MainSI_old() {
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
        
        initializeEnemies();
        
        bullets = new ArrayList<>();
    }
    
    
    private void initializeEnemies() {
        enemies = new ArrayList<>();
        float enemySpacingX = 0.12f; // margin largeur
        float enemySpacingY = 0.12f;  // margin hauteur
        float startY = 0.85f;         // spawn vertical ennemi (-0.8f pas mal)
        float startX = -0.4f;        // spawn horizontal ennemi (-0.6f la moitié)

        for (int row = 0; row < 4; row++) {
            float y = startY - row * enemySpacingY;

            for (float x = startX; x <= startX + 1.2; x += enemySpacingX) {
                enemies.add(new Enemy(x, y));
            }
        }
    }
    
    

    public static void main(String[] args) {
        GLCanvas canvas = new MainSI_old();
        canvas.setPreferredSize(new Dimension(800, 600));

        final JFrame frame = new JFrame();
        frame.getContentPane().add(canvas);
        frame.setTitle("Spaysse 1vadeur");
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        Animator animator = new Animator(canvas);
        animator.start();
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
        
        drawShip(gl);
        drawEnemies(gl);
        moveEnemies();
        drawBullets(gl);
        moveBullets();
        
        checkCollisions();
        
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

    private void handleKeyPress(int keyCode) {
    	// event si les touches ont été appuyés
    	long currentTime = System.currentTimeMillis();
        switch (keyCode)
        {
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
        switch (keyCode)
        {
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
    
    
    
    
    
    private void drawShip(GL2 gl) {
        // dessin du vaisseau 
        gl.glColor3f(1.0f, 1.0f, 1.0f); // white rgb
        
        // on place le vaisseau en bas
        float shipHeight = 0.2f; // hauteur vaisseau
        posY = -0.94f + shipHeight / 2; // posY en bas de la fenetre
        
        // corps du vaisseau
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex2f(posX - 0.15f, posY - 0.05f);
        gl.glVertex2f(posX + 0.15f, posY - 0.05f);
        gl.glVertex2f(posX + 0.15f, posY + 0.0f);
        gl.glVertex2f(posX - 0.15f, posY + 0.0f);
        gl.glEnd();
        
        // deplacement vaisseau
        float moveSpeed = 0.02f;
        if (leftKeyPressed)
        {
            posX -= moveSpeed;
        }
        if (rightKeyPressed)
        {
            posX += moveSpeed;
        }
        // collisions murs
        float shipWidth = 0.30f; // largeur ship
        float leftLimit = -1.0f + shipWidth / 2; // limite gauche
        float rightLimit = 1.0f - shipWidth / 2; // limite droite
        posX = Math.max(leftLimit, Math.min(rightLimit, posX));
    }
    
    private void drawEnemies(GL2 gl) {
        gl.glColor3f(1.0f, 0.0f, 0.0f); // red

        for (Enemy enemy : enemies) {
            float enemySize = 0.05f; // enemy size

            gl.glBegin(GL2.GL_QUADS);
            gl.glVertex2f(enemy.getX() - enemySize, enemy.getY() - enemySize);
            gl.glVertex2f(enemy.getX() + enemySize, enemy.getY() - enemySize);
            gl.glVertex2f(enemy.getX() + enemySize, enemy.getY() + enemySize);
            gl.glVertex2f(enemy.getX() - enemySize, enemy.getY() + enemySize);
            gl.glEnd();
        }
    }
    
    private void drawBullets(GL2 gl) {
        gl.glColor3f(0.0f, 1.0f, 0.0f); // green for bullets

        for (Bullet bullet : bullets) {
            gl.glBegin(GL2.GL_QUADS);
            gl.glVertex2f(bullet.getX() - 0.01f, bullet.getY() - 0.01f);
            gl.glVertex2f(bullet.getX() + 0.01f, bullet.getY() - 0.01f);
            gl.glVertex2f(bullet.getX() + 0.01f, bullet.getY() + 0.01f);
            gl.glVertex2f(bullet.getX() - 0.01f, bullet.getY() + 0.01f);
            gl.glEnd();
        }
    }
    
    private void moveEnemies() {
        boolean shouldMoveDown = false;

        // deplacement horintzal
        for (Enemy enemy : enemies) {
            enemy.setX(enemy.getX() + direction * enemySpeed);
        }

        // condition pour deplacer en bas lorsque l'ennemi tape le mur
        for (Enemy enemy : enemies) {
            // check avec une marge (taille ennemi environ 0.10)
            if (enemy.getX() > 0.95f - enemySpeed || enemy.getX() < -0.95f + enemySpeed) {
                shouldMoveDown = true;
                break; // bord atteint, on breaak
            }
        }

        // deplacement en bas
        if (shouldMoveDown) {
        	enemySpeed += 0.0015f; // augmentation de la vitesse
            for (Enemy enemy : enemies) {
                enemy.setY(enemy.getY() - enemyMoveDown);
            }

            direction *= -1; // changement de direction deplacement horizontal
        }
    }
    
    private void moveBullets() {
        // move les balles vers le haut
        for (Bullet bullet : bullets) {
            bullet.setY(bullet.getY() + bulletSpeed);
        }

        // remove les balles en haut de la fenetre
        bullets.removeIf(bullet -> bullet.getY() > 1.0f);
        
        // tir à la touche espace
        if (spaceKeyPressed) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastBulletTime > bulletInterval) {
                bullets.add(new Bullet(posX, posY + 0.1f));
                lastBulletTime = currentTime;
            }
        }
    }
    
    
    
    
    
    private void checkCollisions() {
        List<Bullet> bulletsToRemove = new ArrayList<>();
        List<Enemy> enemiesToRemove = new ArrayList<>();

        for (Bullet bullet : bullets) {
            for (Enemy enemy : enemies) {
                if (checkCollision(bullet, enemy)) {
                    bulletsToRemove.add(bullet);
                    enemiesToRemove.add(enemy);
                }
            }
        }

        bullets.removeAll(bulletsToRemove);
        enemies.removeAll(enemiesToRemove);
        
        // check win or lose
        if (enemies.isEmpty()) {
            displayMessage("Vous avez gagné");
        }
        for (Enemy enemy : enemies) {
            if (enemy.getY() < -0.9f) {
                displayMessage("Les ennemis ont gagnés");
            }
        }
    }
    
    private boolean checkCollision(Bullet bullet, Enemy enemy) {
        float bulletSize = 0.02f;
        float enemySize = 0.05f;
        // on check si les zones de balles/ennemis se chevauchent
        return (
            bullet.getX() + bulletSize > enemy.getX() - enemySize &&
            bullet.getX() - bulletSize < enemy.getX() + enemySize &&
            bullet.getY() + bulletSize > enemy.getY() - enemySize &&
            bullet.getY() - bulletSize < enemy.getY() + enemySize
        );
    }
    
    private void displayMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Fin de la partie", JOptionPane.INFORMATION_MESSAGE);
        resetGame();
    }
    
    private void resetGame() {
        initializeEnemies();
        bullets.clear();
        posX = 0.0f;
        posY = 0.0f;
        enemySpeed = 0.002f;
        direction = 1;
        enemyMoveDown = 0.1f;
        lastBulletTime = 0;
        leftKeyPressed = false;
        rightKeyPressed = false;
        spaceKeyPressed = false;
    }
    
    
    
    
    private static class Enemy {
        private float x;
        private float y;

        public Enemy(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public void setX(float x) {
            this.x = x;
        }

        public void setY(float y) {
            this.y = y;
        }
    }
    
    
    private static class Bullet {
        private float x;
        private float y;

        public Bullet(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }
    }
}
