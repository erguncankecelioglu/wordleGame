import javax.swing.*;
import java.io.IOException;

public abstract class Game extends JFrame {
    protected abstract void play(JLabel[][] word_player,int wordCounter,String user_word)throws IOException;
    protected abstract double scoreCalculator(double startTime,double endTime,int greenCounter,int wordCounter1);
}
