

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.util.*;
import javax.imageio.*;
import javax.swing.*;

public class HeliLove extends JPanel implements KeyListener {

    JFrame j = new JFrame();
    Graphics g = null;
    Hero hero = new Hero();
    ArrayList<Cloud> clouds = new ArrayList<>();
    ArrayList<Jet> jets = new ArrayList<>();
    ArrayList<Tree> trees = new ArrayList<>();
    ArrayList<Bullet> bullets = new ArrayList<>();
    ArrayList<Explode> exs = new ArrayList<>();
    Random rand = new Random();
    int bulletsCount = 0;
    BufferedImage fieldImg1 = null;
    BufferedImage fieldImg2 = null;
    BufferedImage fieldImg3 = null;
    int thex = 0;
    
    class Explode {
        int x, y;
    }

    class Tree {
        int x, y;
    }
    
    class Bullet {
        int x, y;
    }

    class Hero {
        int x, y;
        int crash, kills;
        ArrayList<Shot> shots = new ArrayList<>();
        
        class Shot {
            int x, y;
        }
        
        void fire() {
            Shot shot = new Shot();
            shot.x = this.x + 110;
            shot.y = this.y + 50;
            shots.add(shot);
        }
        
        void fireTheShot() {
            drawFire();
        }
        
        void drawFire() {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true) {
                        for(int i=0; i<shots.size(); i++) {
                            synchronized(this) {
                                g.setColor(new Color(150, 150, 255));
                                g.fillRect(shots.get(i).x, shots.get(i).y, 60, 20);
                            }
                            shots.get(i).x += 50;
                            synchronized(this) {
                                g.setColor(Color.gray);
                                g.fillRect(shots.get(i).x, shots.get(i).y, 60, 20);
                            }
                            for(int k=0; k<jets.size(); k++)
                                if(shots.get(i).x < jets.get(k).x + 100 && shots.get(i).x > jets.get(k).x &&
                                        shots.get(i).y < jets.get(k).y + 100 && shots.get(i).y > jets.get(k).y)
                                {
                                    Explode ex = new Explode();
                                    ex.x = shots.get(i).x;
                                    ex.y = shots.get(i).y;
                                    exs.add(ex);

                                    hero.kills ++;

                                    j.setTitle("contracted: " + hero.crash + ", corona viruses captured: " + hero.kills);

                                    synchronized(this) {
                                        g.setColor(new Color(150, 150, 255));
                                        g.fillRect(jets.get(k).x, jets.get(k).y, 100, 100);
                                    }

                                    jets.remove(jets.get(k));
                                }
                        }
                        try {
                            Thread.sleep(100);
                        } catch(Exception e) {}
                    }
                }
            });
            
            t.start();
        }
    }
    
    class Jet {
        int x, y;
    }

    class Grass {
        int x, y;
        Color color;
    }

    class Cloud {
        int x, y;
    }

    public HeliLove() {
        
        super();
        
        setGUI();
        try {
            setHero();
        } catch(Exception e) {
            e.printStackTrace();
        }
        drawScreen();

        hero.fireTheShot();
        
        try {
            explode();
    
            drawBullets();
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        Thread t = new Thread() {
            public void run() {
                while(true) {
                    HeliLove.this.repaint();
                    j.setTitle("contracted: " + hero.crash + ", corona viruses captured: " + hero.kills + ", vaccines: " + bulletsCount);
                    drawField();
                    drawTrees();
                    try {
                        Thread.sleep(1200);
                    } catch(Exception e) {}
                }
            }
        };
        
        t.start();
        
        j.addKeyListener(this);
    }

    void drawBullets() throws Exception {
        Image img = ImageIO.read(getClass().getResourceAsStream("bullet.png"));

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    for(int i=0; i<bullets.size(); i++) {
                        
                        try {
                            g.setColor(new Color(150, 150, 255));
                            g.fillRect(bullets.get(i).x, bullets.get(i).y, 90, 90);
                            
                            bullets.get(i).x -= 1;
                        } catch(Exception e) {}

                        g.drawImage(img, bullets.get(i).x, bullets.get(i).y, 90, 90, null);
                        for(int k=0; k<100; k++)
                            for(int l=0; l<100; l++)
                                try {
                                    if(bullets.get(i).x + 90 > hero.x + k && bullets.get(i).x < hero.x + k &&
                                            bullets.get(i).y + 90 > hero.y + l && bullets.get(i).y < hero.y + l)
                                    {
                                        bulletsCount ++;

                                        bullets.remove(bullets.get(i));
                                    }
                                } catch(Exception e) {}
                        
                        try {
                            if(bullets.get(i).x < -100)
                                bullets.remove(bullets.get(i));
                        } catch(Exception e) {}
                    }
                    if(bullets.size() == 0) {
                        for(int i=0; i<14; i++) {
                            Bullet bullet = new Bullet();
                            bullet.x = 1200 + rand.nextInt(1150);
                            bullet.y = rand.nextInt(550);
                            bullets.add(bullet);
                        }
                    }
//                    try {
//                        Thread.sleep(150);
//                    } catch(Exception e) {}
                }
            }
        });
           
        t.start();
    }
    
    void explode() throws Exception {
        Image img = ImageIO.read(getClass().getResourceAsStream("explode.gif"));

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList ar = new ArrayList();
                while(true) {
                    for(int i=0; i<exs.size(); i++) {
                        g.drawImage(img, exs.get(i).x-50, exs.get(i).y-120, 200, 200, null);
                        ar.add(exs.get(i));
                        exs.remove(exs.get(i));
                    }
                    try {
                        Thread.sleep(150);
                    } catch(Exception e) {}
                    for(int i=0; i<ar.size(); i++) {
                        g.setColor(new Color(150, 150, 255));
                        g.fillRect(((Explode)ar.get(i)).x-50, ((Explode)ar.get(i)).y-120, 200, 200);
                    }
                    ar.clear();
                }
            }
        });
           
        t.start();
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_UP) {
            synchronized(this) {
                g.setColor(new Color(150, 150, 255));
                g.fillRect(hero.x, hero.y, 200, 130);
            }
            if(hero.y > 12)
                hero.y -= 12;
        }
        else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
            synchronized(this) {
                g.setColor(new Color(150, 150, 255));
                g.fillRect(hero.x, hero.y, 200, 130);
            }
            if(hero.y < 471)
                hero.y += 12;
        }
        else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
            synchronized(this) {
                g.setColor(new Color(150, 150, 255));
                g.fillRect(hero.x, hero.y, 200, 130);
            }
            if(hero.x > 20)
                hero.x -= 12;
        }
        else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
            synchronized(this) {
                g.setColor(new Color(150, 150, 255));
                g.fillRect(hero.x, hero.y, 200, 130);
            }
            if(hero.x < 1180)
                hero.x += 12;
        }
        else if(e.getKeyCode() == KeyEvent.VK_PERIOD) {
            if(bulletsCount > 0) {
                hero.fire();
                bulletsCount --;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    void setHero() throws Exception {
        
        hero.x = 100;
        hero.y = 200;
        
        hero.kills = 0;
        
        hero.crash = 0;
        
        j.setTitle("contracted: " + hero.crash + ", corona viruses captured: " + hero.kills);
        
        javax.swing.ImageIcon iFb = new javax.swing.ImageIcon(this.getClass().getResource("heli.gif"));
        Image img = iFb.getImage();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    g.drawImage(img, hero.x, hero.y, 150, 130, null);
                    try {
                        Thread.sleep(0);
                    } catch(Exception e) {}
                }
            }
        });
        
        t.start();
        
    }
    
    public void paint(Graphics g) {
        super.paintComponent(g);

        setDoubleBuffered(true);

        g.setColor(new Color(150, 150, 255));
        g.fillRect(0, 0, 1200, 800);

        g.drawImage(fieldImg2, thex + 1200, 600, 1200, 200, null);
        g.drawImage(fieldImg1, thex, 600, 1200, 200, null);

        for(int i=0; i<trees.size(); i++) {
            synchronized(this) {
                try {
                    int v = rand.nextInt(2);
                    if(v == 0)
                        g.setColor(Color.PINK);
                    else
                        g.setColor(Color.red);
                    g.fillOval(trees.get(i).x, trees.get(i).y, 20, 20);
                    g.setColor(new Color(200,100,50));
                    g.fillRect(trees.get(i).x, trees.get(i).y+20, 20, 30);
                } catch(Exception e) {}
            }

            try {
                if(trees.get(i).x < 0) {
                    trees.remove(trees.get(i));
                }
            } catch(Exception e) {}
        }
    }

    void setGUI() {
        j.setLayout(null);
        
        j.setResizable(false);
        
        j.setMaximumSize(new Dimension(1600, 1000));

        j.setBounds(0, 0, 1600, 1000);

        setBounds(40, 40, 1200, 800);
        
        JPanel pp = new JPanel();
        
        pp.setLayout(null);
        
        pp.setBackground(Color.green);

        pp.setBounds(j.getBounds());

        pp.add(this);
        
        j.add(pp);

        j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        j.setVisible(true);

        setGraphics();
    }
    
    void setGraphics() {
        g = this.getGraphics();
    }
    
    void drawScreen() {
        try {
            drawClouds();
            drawJets();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    void drawFire() {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                hero.fireTheShot();
            }
        });
        
        t.start();
    }

    void drawTrees() {

        if(trees.size() == 0) {
            for(int i=0; i<10; i++) {
                Tree cloud = new Tree();
                cloud.x = 1200 + rand.nextInt(1000);
                cloud.y = 600;
                trees.add(cloud);
            }
        }
        for(int i=0; i<trees.size(); i++) {

            trees.get(i).x -= 100;
        }

        for(int i=0; i<trees.size(); i++) {
            synchronized(this) {
                try {
                    g.setColor(Color.PINK);
                    g.fillOval(trees.get(i).x, trees.get(i).y, 20, 20);
                    g.setColor(new Color(200,100,50));
                    g.fillRect(trees.get(i).x, trees.get(i).y+20, 20, 30);
                } catch(Exception e) {}
            }

            try {
                if(trees.get(i).x < 0) {
                    trees.remove(trees.get(i));
                }
            } catch(Exception e) {}
        }
    }

    private void drawField() {
        if(fieldImg1 == null) {
            try {
                fieldImg1 = ImageIO.read(getClass().getResourceAsStream("grass.png"));
            } catch(Exception e) {
                
            }
        }
        if(fieldImg2 == null) {
            try {
                fieldImg2 = ImageIO.read(getClass().getResourceAsStream("grass.png"));

                // Flip the image horizontally
                AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
                tx.translate(-fieldImg2.getWidth(null), 0);
                AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
                //fieldImg2 = op.filter(fieldImg2, null);
            } catch(Exception e) {
                
            }
        }
        
        thex-=100;

        g.drawImage(fieldImg2, thex + 1200, 600, 1200, 200, null);
        g.drawImage(fieldImg1, thex, 600, 1200, 200, null);

        if(thex <= -1200) {
            thex = 0;
        }
    }

    void drawJets() throws Exception {
        Image img = ImageIO.read(getClass().getResourceAsStream("bomber.png"));

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    if(jets.size() == 0) {
                        for(int i=0; i<6; i++) {
                            Jet cloud = new Jet();
                            cloud.x = 1200 + rand.nextInt(1000);
                            cloud.y = rand.nextInt(499);
                            jets.add(cloud);
                        }
                    }
                    for(int i=0; i<jets.size(); i++) {
                        synchronized(this) {
                            g.setColor(new Color(150, 150, 255));
                            g.fillRect(jets.get(i).x, jets.get(i).y, 100, 100);
                        }

                        try {
                            jets.get(i).x -= 30;
                        } catch(Exception e) {}
                    }
                    for(int i=0; i<jets.size(); i++) {
                        g.drawImage(img, jets.get(i).x, jets.get(i).y, 100, 100, null);
                        
                        for(int k=0; k<100; k++)
                            for(int l=0; l<100; l++)
                                try {
                                    if(jets.get(i).x + k < hero.x + 100 && jets.get(i).x + k > hero.x &&
                                            jets.get(i).y + l < hero.y + 100 && jets.get(i).y + l > hero.y)
                                    {
                                        synchronized(this) {
                                            g.setColor(new Color(150, 150, 255));
                                            g.fillRect(jets.get(i).x, jets.get(i).y, 100, 100);
                                        }

                                        jets.remove(jets.get(i));

                                        hero.crash ++;
                                        
                                        bulletsCount --;

                                        j.setTitle("contracted: " + hero.crash + ", corona viruses captured: " + hero.kills);
                                    }
                                } catch(Exception e) {}
                        try {
                            if(jets.get(i).x < -100) {
                                jets.remove(jets.get(i));
                            }
                        } catch(Exception e) {
                        }
                    }
                    try {
                        Thread.sleep(100);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();
    }

    void drawClouds() throws Exception {
        Image img = ImageIO.read(getClass().getResourceAsStream("cloud.png"));

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    if(clouds.size() == 0) {
                        for(int i=0; i<10; i++) {
                            Cloud cloud = new Cloud();
                            cloud.x = 1200 + rand.nextInt(1000);
                            cloud.y = rand.nextInt(400);
                            clouds.add(cloud);
                        }
                    }
                    for(int i=0; i<clouds.size(); i++) {
                        synchronized(this) {
                            g.setColor(new Color(150, 150, 255));
                            g.fillRect(clouds.get(i).x, clouds.get(i).y, 200, 100);
                        }

                        clouds.get(i).x -= 10;
                    }
                    for(int i=0; i<clouds.size(); i++) {
                        g.drawImage(img, clouds.get(i).x, clouds.get(i).y, 200, 100, null);
                        
                        if(clouds.get(i).x < -100) {
                            clouds.remove(clouds.get(i));
                        }
                    }
                    try {
                        Thread.sleep(100);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();
    }
    
    void drawGrass() throws Exception {

        Image img = ImageIO.read(getClass().getResourceAsStream("grass.png"));

        g.drawImage(img, 0, 600, 1200, 200, null);

        drawTrees();
//        g.setColor(new Color(50, 150, 0));
//        g.fillRect(0, 600, 1200, 200);
    }

    public static void main(String[] args) {
        new HeliLove();
    }    
}