import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class confetti extends JPanel {
    static int y = 0;

    @Override
    public void setBackground(Color bg) {
        super.setBackground(Color.white);
    }

    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2D = (Graphics2D) g;

        ImageIcon celebIconPng = new ImageIcon(getClass().getClassLoader().getResource("celebrateWinner.png"));
        //Image celebIconPng = new ImageIcon("celebrateWinner.png").getImage();
       // g2D.drawImage(celebIconPng, getWidth()/2-50, 120, null);
        g2D.drawImage(celebIconPng.getImage(), getWidth()/2-50, 120, null);
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2D.rotate(270);

        for(int i = 0;i<15;i++){
            for(int j = 0;j<6;j++){
                g.setColor(new Color(new Random().nextInt(256),new Random().nextInt(256),new Random().nextInt(256)));
                g.fillRect(new Random().nextInt()%getWidth(),y-j*8,5,25);

                g.setColor(new Color(new Random().nextInt(256),new Random().nextInt(256),new Random().nextInt(256)));
                g.fillOval(new Random().nextInt()%getWidth(),y-j*30,10,10);

                g.setColor(new Color(new Random().nextInt(256),new Random().nextInt(256),new Random().nextInt(256)));
                g.fillRect(new Random().nextInt()%getWidth(),y-j*48,5,25);
            }
            y += 4;
        }

    }
}
