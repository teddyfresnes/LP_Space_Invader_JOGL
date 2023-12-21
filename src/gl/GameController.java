package gl;

import com.jogamp.opengl.GL2;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class GameController {
    private List<Enemy> enemies;
    private float enemySpeed = 0.002f;
    private int direction = 1; // 1 pour droite, -1 pour gauche
    private float enemyMoveDown = 0.1f; // distance de descente à chaque collision des ennemis

    private List<Bullet> bullets;
    private float bulletSpeed = 0.03f;
    private long lastBulletTime = 0;
    private long bulletInterval = 500; // intervalle tir en ms

    private Ship ship;

    public GameController(Ship ship) {
        this.ship = ship;
        bullets = new ArrayList<>();
    }

    public void initializeEnemies() {
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

    public void draw(GL2 gl) {
        drawEnemies(gl);
        moveEnemies();
        drawBullets(gl);
        moveBullets();
        checkCollisions(gl);
    }

    private void drawEnemies(GL2 gl) {
        gl.glColor3f(1.0f, 0.0f, 0.0f); // red

        for (Enemy enemy : enemies) {
            float enemySize = 0.05f; // enemy size
            float eyeSize = 0.01f;  // size eyes

            gl.glBegin(GL2.GL_QUADS);
            gl.glVertex2f(enemy.getX() - enemySize, enemy.getY() - enemySize);
            gl.glVertex2f(enemy.getX() + enemySize, enemy.getY() - enemySize);
            gl.glVertex2f(enemy.getX() + enemySize - 0.01f, enemy.getY() + enemySize);
            gl.glVertex2f(enemy.getX() - enemySize + 0.01f, enemy.getY() + enemySize);
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

        // deplacement horizontal
        for (Enemy enemy : enemies) {
            enemy.setX(enemy.getX() + direction * enemySpeed);
        }

        // condition pour deplacer en bas lorsque l'ennemi tape le mur
        for (Enemy enemy : enemies) {
            // check avec une marge (taille ennemi environ 0.10)
            if (enemy.getX() > 0.95f - enemySpeed || enemy.getX() < -0.95f + enemySpeed) {
                shouldMoveDown = true;
                break; // bord atteint, on break
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
        if (ship != null && ship.isSpaceKeyPressed()) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastBulletTime > bulletInterval) {
                bullets.add(new Bullet(ship.getPosX(), ship.getPosY() + 0.1f));
                lastBulletTime = currentTime;
            }
        }
    }

    private void checkCollisions(GL2 gl) {
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
            displayMessage(gl, "Vous avez gagné");
        }
        for (Enemy enemy : enemies) {
            if (enemy.getY() < -0.9f) {
                displayMessage(gl, "Les ennemis ont gagné");
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

    private void displayMessage(GL2 gl, String message) {
        JOptionPane.showMessageDialog(null, message, "Fin de la partie", JOptionPane.INFORMATION_MESSAGE);
        ship.reset();
        initializeEnemies();
        bullets.clear();
        ship.reset();
        direction = 1;
        enemyMoveDown = 0.1f;
        lastBulletTime = 0;
        enemySpeed = 0.002f;
    }
}
