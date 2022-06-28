import java.awt.*;
import java.awt.event.*;

import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.util.Random;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;


public class wordle extends Game  {

    private ClientSideConnection csc;
    private JTextArea chatbox;
    private String message;
    protected  boolean yourTurn = false;
    Timer t;
    private Point initialPointOfLetter;
    private Point initialPointOfMouse;
    private int labelX;    private int labelY;
    private JLabel time;
    private JLabel letterCopy;
    private String wordleWord;
    private double startTime;
    private double endTime;
    private boolean isPlayed = false;
    private boolean isWon = false;
    private int greenCounter = 0;
    private int yellowCounter = 0;
    private char userChar;
    private List<Integer> letterStatistics = new ArrayList<>();
    private List<String> wordleWordsList;
    private List<String> wordleList = new ArrayList<>();
    private List<String> absoluteWord;
    private List<Double> scoresList = new ArrayList<>();
    private String user1_word = "";
    private String user2_word = "";
    private int wordCounter1 = 0;
    private int wordCounter2 = 0;
    private boolean is_2player_game = false;
    private boolean is_online_game = false;
    private JLabel info;
    private JLabel[][] word_player1;
    private JLabel[][] word_player2;
    private JPanel jpCenter;
    private double highestScore;
    private int playerID;
    private JPanel jpTrial;
    private JLabel[][] letters;
    private final String[][] alphabet = { {"Q","W","E","R","T","Y","U","I","O","P"},
            {"A","S","D","F","G","H","J","K","L",""},
            {"Z","X","C","V","B","N","M", "Enter", "Delete",""} };
    public wordle() throws FileNotFoundException {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(Color.BLUE);
        setLayout(new BorderLayout());
        textImporter();
        System.out.println(wordleWord);

        icon_Region();
        showTime();
        number_of_players();
        //confetti();


        try {
            winCounter();
        } catch (IOException e) {
            System.out.println("wincounter hatası");
        }
        try {
            gameCounter();
        } catch (IOException e) {
        }
        try {
            letterStatisticsFunction();
        } catch (IOException e) {
        }


//        try {
//            File myObj = new File("highScores.txt");
//            Scanner myReader = new Scanner(myObj);
//            Double data = myReader.nextDouble();
//            highestScore = data;
//            System.out.println(data);
//
//        } catch (NoSuchElementException e) {
//            System.out.println("Score error");
//        } catch (FileNotFoundException e){
//            System.out.println("File error");
//        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        wordle.class.getResourceAsStream("highScores.txt"),
                        StandardCharsets.ISO_8859_1))) {
            Double score;
            score = Double.valueOf(reader.readLine());
            highestScore = score;

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchElementException e) {
            System.out.println("Score error");
        }
        catch (NullPointerException e){
            System.out.println("Score error");
        }

    }
    public double scoreCalculator(double startTime,double endTime,int greenCounter,int wordCounter1){
        double score = (greenCounter / (((endTime - startTime) / 80000) + (wordCounter1))) + (0.5 * yellowCounter) + 5 * greenCounter;
        BigDecimal bd = new BigDecimal(score);
        bd = bd.round(new MathContext(3));
        return bd.doubleValue();
    }
    public void textImporter() throws FileNotFoundException {
//        Scanner sc = new Scanner(new File("wordlewords.txt"));
//
//        while (sc.hasNextLine()) {
//            wordleList.add(sc.nextLine().toUpperCase());
//        }
//        sc.close();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        wordle.class.getResourceAsStream("wordlewords.txt"),
                        StandardCharsets.ISO_8859_1))) {
            String word;
            while ((word = reader.readLine()) != null) {
                wordleList.add(word.toUpperCase());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Random rand = new Random();
        wordleWord = wordleList.get(rand.nextInt(wordleList.size()));
        wordleWordsList = Arrays.asList(wordleWord.split(""));
        absoluteWord = wordleWordsList;

    }
    public void play(JLabel[][] word_player,int wordCounter,String user_word) throws IOException {
        letterStatisticsFunction();
        isPlayed = true;
        winCounter();
        wordleWordsList = new ArrayList<String>(absoluteWord);
        user_word = user_word.toUpperCase();
        if(!checker(user_word)){
            JOptionPane.showMessageDialog(null,
                    user_word + " is not in our database :(, please enter another word", "Warning"
                    , JOptionPane.INFORMATION_MESSAGE);
            for (int i = 0; i<5;i++){
                word_player[wordCounter][i].setText("");
            }
        }
        else{
            String[] userWordsArray = user_word.split("");
            for (int i = 0;i<5;i++){
                word_player[wordCounter][i].setBackground(Color.gray);
            }
            for (int i = 0; i < 5; i++) {
                word_player[wordCounter][i].setText(userWordsArray[i]);
                userChar = userWordsArray[i].charAt(0);
                letterStatisticsFunction();
                if (wordleWordsList.contains(userWordsArray[i])) {
                    int index = wordleWordsList.indexOf(userWordsArray[i]);
                    if (userWordsArray[i].equals(wordleWordsList.get(i))) {
                        word_player[wordCounter][i].setBackground(Color.green);
                        greenCounter++;
                        wordleWordsList.set(i, "");
                    } else {
                        if(!wordleWordsList.get(index).equals(userWordsArray[index])){
                            wordleWordsList.set(index,"");
                            yellowCounter++;
                            word_player[wordCounter][i].setBackground(Color.yellow);
                        }
                    }
                }
            }
            for(int i = 0; i < 5; i++){
                for(int j = 0; j < 3; j++){
                    for(int k = 0; k<10;k++){
                        if(letters[j][k].getText().equals(word_player[wordCounter][i].getText())){
                            if((letters[j][k].getBackground() == Color.white || letters[j][k].getBackground() == Color.gray) ){
                                letters[j][k].setBackground(word_player[wordCounter][i].getBackground());
                            }
                            else if(letters[j][k].getBackground() == Color.yellow &&
                                    word_player[wordCounter][i].getBackground() == Color.green){
                                letters[j][k].setBackground(word_player[wordCounter][i].getBackground());
                            }
                        }
                    }
                }
            }

            wordCounter++;
            if (user_word.equals(wordleWord) && !is_2player_game) {
                endTime = System.currentTimeMillis();
                confetti();
                if(scoreCalculator(startTime,endTime,greenCounter,wordCounter1)>=highestScore){
                    JOptionPane.showMessageDialog(null,
                            "<html> Congratulations! You break the high score!" + "<br/><FONT COLOR=green>Highest score was: " + highestScore + "<br/><FONT COLOR=red> Your score is: " + scoreCalculator(startTime,endTime,greenCounter,wordCounter1), "Congratulations"
                            , JOptionPane.INFORMATION_MESSAGE);
                }
                else {
                    JOptionPane.showMessageDialog(null,
                            "You win! Your score is: " + scoreCalculator(startTime, endTime, greenCounter, wordCounter1) + "\n" + "Highest Score Was: " + highestScore + " ;)", "Congratulations"
                            , JOptionPane.INFORMATION_MESSAGE);
                }
                ScoreWriter();
                isWon = true;
                winCounter();
                gameCounter();

                System.exit(0);

            }
            else if(user_word.equals(wordleWord) && !is_online_game){
                if(wordCounter1 == wordCounter2){
                    confetti();
                    JOptionPane.showMessageDialog(null,
                            "Player1 wins!", "Congrats"
                            , JOptionPane.INFORMATION_MESSAGE);
                    isWon = true;
                    winCounter();
                    gameCounter();
                    System.exit(0);
                }
                else{
                    confetti();
                    JOptionPane.showMessageDialog(null,
                            "Player2 wins!", "Congrats"
                            , JOptionPane.INFORMATION_MESSAGE);
                    isWon = true;
                    winCounter();
                    gameCounter();
                    System.exit(0);
                }
            }
            if(is_online_game && yourTurn && user_word.equals(wordleWord)){
                confetti();
                JOptionPane.showMessageDialog(null,
                        "You win! Your opponent lost" ,  "Congrats"
                        , JOptionPane.INFORMATION_MESSAGE);
                isWon = true;
                winCounter();
                gameCounter();
                System.exit(0);
            }
            else if(is_online_game && !yourTurn && user_word.equals(wordleWord)){
                if(playerID < 3){
                    JOptionPane.showMessageDialog(null,
                            "You lost! Your opponent wins" ,  ":("
                            , JOptionPane.INFORMATION_MESSAGE);
                    gameCounter();
                }
                else{
                    if (wordCounter1 == wordCounter2){
                        JOptionPane.showMessageDialog(null,
                                "Player1 wins" ,  "..."
                                , JOptionPane.INFORMATION_MESSAGE);
                    }
                    else{
                        JOptionPane.showMessageDialog(null,
                                "Player2 wins" ,  "..."
                                , JOptionPane.INFORMATION_MESSAGE);
                    }
                }


                System.exit(0);
            }
            if(!is_2player_game){
                wordCounter1++;
            }
            else if(!is_online_game){
                if(wordCounter1 == wordCounter2){
                    wordCounter1++;
                }
                else{
                    wordCounter2++;
                }
            }

        }
        if(wordCounter == 5 && !user_word.equals(wordleWord) && !is_2player_game) {
            JOptionPane.showMessageDialog(null,
                    "You lost!, The word was " + wordleWord, ":("
                    , JOptionPane.INFORMATION_MESSAGE);
            gameCounter();
            System.exit(0);
        }
        else if(wordCounter == 5 && !user_word.equals(wordleWord) && !is_online_game){
            if(wordCounter1 > wordCounter2){
                JOptionPane.showMessageDialog(null,
                        "Player1 lost!", ":("
                        , JOptionPane.INFORMATION_MESSAGE);
            }
            else{
                JOptionPane.showMessageDialog(null,
                        "Player2 lost too!, The word was " + wordleWord, ":("
                        , JOptionPane.INFORMATION_MESSAGE);
                gameCounter();
                System.exit(0);
            }
        }
        if (wordCounter == 5 && !user_word.equals(wordleWord) && is_online_game && yourTurn){
            JOptionPane.showMessageDialog(null,
                    "You lost!", ":("
                    , JOptionPane.INFORMATION_MESSAGE);
            if(wordCounter1+1 == 5 && wordCounter2 == 5){
                JOptionPane.showMessageDialog(null,
                        "The word was " + wordleWord, ":("
                        , JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
            }
        }
        else if(wordCounter == 5 && !user_word.equals(wordleWord) && is_online_game){
            if(playerID < 3){
                JOptionPane.showMessageDialog(null,
                        "Your opponent lost!", ":("
                        , JOptionPane.INFORMATION_MESSAGE);
                if(wordCounter1 == 5 && wordCounter2+1 == 5){
                    JOptionPane.showMessageDialog(null,
                            "The word was " + wordleWord, ":("
                            , JOptionPane.INFORMATION_MESSAGE);
                    System.exit(0);
                }
            }
            else{
                if(wordCounter1 == 5 && wordCounter2+1 == 5){
                    JOptionPane.showMessageDialog(null,
                            "The word was " + wordleWord, "..."
                            , JOptionPane.INFORMATION_MESSAGE);
                    System.exit(0);
                }
            }
        }



        if(!is_2player_game){
            user1_word = "";
        }
        else if(!is_online_game){
            if(wordCounter1 == wordCounter2){
                user1_word = "";
            }
            else{
                user2_word = "";
            }
        }
    }
    public void guessField_Keyboard() {
        jpTrial = new JPanel();
        jpTrial.setLayout(null);

        letters = new JLabel[3][10];

        Border border = BorderFactory.createLineBorder(Color.black, 1);

        for (int i = 0; i < letters.length; i++) {
            for (int j = 0; j < alphabet[i].length; j++) {
                letters[i][j] = new JLabel(alphabet[i][j], SwingConstants.CENTER);
                letters[i][j].setBorder(border);
                if(!alphabet[i][j].equals("Enter") && !alphabet[i][j].equals("Delete") && !alphabet[i][j].equals("")){
                    letters[i][j].addMouseListener(new MouseListener() {
                        @Override
                        public void mouseClicked(MouseEvent e) {

                        }

                        @Override
                        public void mousePressed(MouseEvent e) {
                            JLabel label = (JLabel) e.getSource();
                            label.setSize(40,30);

                        }

                        @Override
                        public void mouseReleased(MouseEvent e) {
                            JLabel label = (JLabel) e.getSource();
                            label.setSize(35,25);
                            if(user1_word.length() < 5){
                                word_player1[wordCounter1][user1_word.length()].setText(label.getText());
                                user1_word += label.getText();
                            }
                        }

                        @Override
                        public void mouseEntered(MouseEvent e) {

                        }

                        @Override
                        public void mouseExited(MouseEvent e) {

                        }
                    });
                }
                else if (alphabet[i][j].equals("Enter")){
                    letters[i][j].addMouseListener(new MouseListener() {
                        @Override
                        public void mouseClicked(MouseEvent e) {

                        }

                        @Override
                        public void mousePressed(MouseEvent e) {
                            JLabel label = (JLabel) e.getSource();
                            label.setSize(60,35);
                            label.setBackground(Color.lightGray);
                        }

                        @Override
                        public void mouseReleased(MouseEvent e) {
                            if(user1_word.length() == 5){
                                try {
                                    play(word_player1,wordCounter1,user1_word);
                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                            JLabel label = (JLabel) e.getSource();
                            label.setSize(50,25);
                            label.setBackground(Color.white);
                        }

                        @Override
                        public void mouseEntered(MouseEvent e) {

                        }

                        @Override
                        public void mouseExited(MouseEvent e) {

                        }
                    });
                }
                else if(alphabet[i][j].equals("Delete")){
                    letters[i][j].addMouseListener(new MouseListener() {
                        @Override
                        public void mouseClicked(MouseEvent e) {

                        }

                        @Override
                        public void mousePressed(MouseEvent e) {
                            JLabel label = (JLabel) e.getSource();
                            label.setSize(60,35);
                            label.setBackground(Color.lightGray);
                        }

                        @Override
                        public void mouseReleased(MouseEvent e) {
                            if(user1_word.length()>0){
                                word_player1[wordCounter1][user1_word.length()-1].setText("");
                                user1_word = user1_word.substring(0, user1_word.length()-1);
                            }
                            JLabel label = (JLabel) e.getSource();
                            label.setSize(50,25);
                            label.setBackground(Color.white);
                        }

                        @Override
                        public void mouseEntered(MouseEvent e) {

                        }

                        @Override
                        public void mouseExited(MouseEvent e) {

                        }
                    });
                }

                letters[i][j].setOpaque(true);
                letters[i][j].setBackground(Color.white);
                if(i == 1) {
                    letters[i][j].setBounds(j*35+30, i*25+315, 35, 25);
                }
                else {
                    letters[i][j].setBounds(j*35+15, i*25+315, 35, 25);
                }
                if(alphabet[i][j].equalsIgnoreCase("Enter" )) {
                    letters[i][j].setBounds(j*35+15, i*25+315, 50, 25);
                }
                else if ( alphabet[i][j].equalsIgnoreCase("Delete")) {
                    letters[i][j].setBounds(j*35+30, i*25+315, 50, 25);
                }
                if(alphabet[i][j].equals("")){
                    letters[i][j].setBorder(null);
                    letters[i][j].setOpaque(false);
                    letters[i][j].setBounds(j*35+60, i*25+5, 50, 25);
                }
                jpTrial.add(letters[i][j]);
            }

        }


        word_player1 = new JLabel[5][5];

        border = BorderFactory.createLineBorder(Color.black, 2);
        for (int i = 0; i < word_player1.length; i++) {
            for (int j = 0; j < 5; j++) {
                word_player1[i][j] = new JLabel("", SwingConstants.CENTER);
                word_player1[i][j].setBorder(border);
                word_player1[i][j].setOpaque(true);
                word_player1[i][j].setBackground(Color.white);
                word_player1[i][j].setBounds(75*j+7, 60*i, 70, 50);
                jpTrial.add(word_player1[i][j]);
            }
        }
        jpTrial.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {


            }

            @Override
            public void keyPressed(KeyEvent e) {
                String key = "";
                if(e.getKeyCode()>=65 && e.getKeyCode()<=90 && user1_word.length() < 5){
                    key += e.getKeyChar();
                    user1_word += e.getKeyChar();
                    key = key.toUpperCase();
                    word_player1[wordCounter1][user1_word.length()-1].setText(key);
                }
                else if(user1_word.length()==5 && e.getKeyCode()==10){
                    try {
                        play(word_player1,wordCounter1,user1_word);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                else if(e.getKeyCode() == 8 && user1_word.length()>0){
                    word_player1[wordCounter1][user1_word.length()-1].setText("");
                    user1_word = user1_word.substring(0, user1_word.length()-1);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        jpTrial.requestFocusInWindow();
        jpTrial.setBackground(Color.white);
        add(jpTrial,BorderLayout.CENTER);
    }
    public void guessField_DnD()  {
        jpTrial = new JPanel();
        jpTrial.setLayout(null);


        letters = new JLabel[3][10];

        Border border = BorderFactory.createLineBorder(Color.black, 1);

        for (int i = 0; i < letters.length; i++) {
            for (int j = 0; j < alphabet[i].length; j++) {
                letters[i][j] = new JLabel(alphabet[i][j], SwingConstants.CENTER);
                letters[i][j].setBorder(border);
                if(!alphabet[i][j].equals("Enter") && !alphabet[i][j].equals("Delete") && !alphabet[i][j].equals("")){
                    letters[i][j].addMouseListener(new MouseListener() {
                        @Override
                        public void mouseClicked(MouseEvent e) {

                        }

                        @Override
                        public void mousePressed(MouseEvent e) {
                            JLabel label = (JLabel) e.getSource();

                            letterCopy = new JLabel(label.getText(),SwingConstants.CENTER);
                            letterCopy.setBackground(((JLabel) e.getSource()).getBackground());
                            Border border = BorderFactory.createLineBorder(Color.black, 1);
                            letterCopy.setBorder(border);
                            letterCopy.setOpaque(true);
                            letterCopy.setBounds(label.getLocation().x,label.getLocation().y,35,25);
                            jpTrial.add(letterCopy);

                            initialPointOfLetter = label.getLocation();
                            initialPointOfMouse = e.getLocationOnScreen();
                        }

                        @Override
                        public void mouseReleased(MouseEvent e) {
                            JLabel label = (JLabel) e.getSource();
                            label.setLocation(initialPointOfLetter);
                            if(user1_word.length() < 5){
                                if(labelX >= word_player1[wordCounter1][user1_word.length()].getLocation().x &&
                                        labelX <= word_player1[wordCounter1][user1_word.length()].getLocation().x + 35 &&
                                        labelY >= word_player1[wordCounter1][user1_word.length()].getLocation().y &&
                                        labelY <= word_player1[wordCounter1][user1_word.length()].getLocation().y + 25){

                                    word_player1[wordCounter1][user1_word.length()].setText(((JLabel) e.getSource()).getText());
                                    user1_word += word_player1[wordCounter1][user1_word.length()].getText();
                                }
                            }
                            jpTrial.remove(letterCopy);
                        }

                        @Override
                        public void mouseEntered(MouseEvent e) {

                        }

                        @Override
                        public void mouseExited(MouseEvent e) {

                        }
                    });
                    letters[i][j].addMouseMotionListener(new MouseMotionListener() {
                        @Override
                        public void mouseDragged(MouseEvent e) {
                            JLabel label = (JLabel) e.getSource();

                            Point currentMouseLocation = e.getLocationOnScreen();

                            int deltaX = currentMouseLocation.x - initialPointOfMouse.x;
                            int deltaY = currentMouseLocation.y - initialPointOfMouse.y;

                            labelX = initialPointOfLetter.x + deltaX;
                            labelY = initialPointOfLetter.y + deltaY;

                            label.setLocation(labelX, labelY);
                        }

                        @Override
                        public void mouseMoved(MouseEvent e) {

                        }
                    });
                }
                else if (alphabet[i][j].equals("Enter")){
                    letters[i][j].addMouseListener(new MouseListener() {
                        @Override
                        public void mouseClicked(MouseEvent e) {

                        }

                        @Override
                        public void mousePressed(MouseEvent e) {
                            JLabel label = (JLabel) e.getSource();
                            label.setSize(60,35);
                            label.setBackground(Color.lightGray);
                        }

                        @Override
                        public void mouseReleased(MouseEvent e) {
                            if(user1_word.length() == 5){
                                try {
                                    play(word_player1,wordCounter1,user1_word);
                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                            JLabel label = (JLabel) e.getSource();
                            label.setSize(50,25);
                            label.setBackground(Color.white);
                        }

                        @Override
                        public void mouseEntered(MouseEvent e) {

                        }

                        @Override
                        public void mouseExited(MouseEvent e) {

                        }
                    });
                }
                else if(alphabet[i][j].equals("Delete")){
                    letters[i][j].addMouseListener(new MouseListener() {
                        @Override
                        public void mouseClicked(MouseEvent e) {

                        }

                        @Override
                        public void mousePressed(MouseEvent e) {
                            JLabel label = (JLabel) e.getSource();
                            label.setSize(60,35);
                            label.setBackground(Color.lightGray);
                        }

                        @Override
                        public void mouseReleased(MouseEvent e) {
                            if(user1_word.length()>0){
                                word_player1[wordCounter1][user1_word.length()-1].setText("");
                                user1_word = user1_word.substring(0, user1_word.length()-1);
                            }
                            JLabel label = (JLabel) e.getSource();
                            label.setSize(50,25);
                            label.setBackground(Color.white);
                        }

                        @Override
                        public void mouseEntered(MouseEvent e) {

                        }

                        @Override
                        public void mouseExited(MouseEvent e) {

                        }
                    });
                }

                letters[i][j].setOpaque(true);
                letters[i][j].setBackground(Color.white);
                if(i == 1) {
                    letters[i][j].setBounds(j*35+30, i*25+315, 35, 25);
                }
                else {
                    letters[i][j].setBounds(j*35+15, i*25+315, 35, 25);
                }
                if(alphabet[i][j].equalsIgnoreCase("Enter" )) {
                    letters[i][j].setBounds(j*35+15, i*25+315, 50, 25);
                }
                else if ( alphabet[i][j].equalsIgnoreCase("Delete")) {
                    letters[i][j].setBounds(j*35+30, i*25+315, 50, 25);
                }
                if(alphabet[i][j].equals("")){
                    letters[i][j].setBorder(null);
                    letters[i][j].setOpaque(false);
                    letters[i][j].setBounds(j*35+60, i*25+5, 50, 25);
                }
                jpTrial.add(letters[i][j]);
            }

        }

        word_player1 = new JLabel[5][5];

        border = BorderFactory.createLineBorder(Color.black, 2);
        for (int i = 0; i < word_player1.length; i++) {
            for (int j = 0; j < 5; j++) {
                word_player1[i][j] = new JLabel("", SwingConstants.CENTER);
                word_player1[i][j].setBorder(border);
                word_player1[i][j].setOpaque(true);
                word_player1[i][j].setBackground(Color.white);
                word_player1[i][j].setBounds(75*j+7, 60*i, 70, 50);
                jpTrial.add(word_player1[i][j]);
            }
        }


        jpTrial.requestFocusInWindow();
        jpTrial.setBackground(Color.white);
        add(jpTrial,BorderLayout.CENTER);
    }
    public void guessField_2players() {
        jpTrial = new JPanel();
        jpTrial.setLayout(null);

        letters = new JLabel[3][10];

        Border border = BorderFactory.createLineBorder(Color.black, 1);

        for (int i = 0; i < letters.length; i++) {
            for (int j = 0; j < alphabet[i].length; j++) {
                letters[i][j] = new JLabel(alphabet[i][j], SwingConstants.CENTER);
                letters[i][j].setBorder(border);
                if(!alphabet[i][j].equals("Enter") && !alphabet[i][j].equals("Delete") && !alphabet[i][j].equals("")){
                    letters[i][j].addMouseListener(new MouseListener() {
                        @Override
                        public void mouseClicked(MouseEvent e) {

                        }

                        @Override
                        public void mousePressed(MouseEvent e) {
                            JLabel label = (JLabel) e.getSource();
                            label.setSize(40,30);

                        }

                        @Override
                        public void mouseReleased(MouseEvent e) {
                            JLabel label = (JLabel) e.getSource();
                            label.setSize(35,25);

                            if(wordCounter1 == wordCounter2 && user1_word.length() < 5){
                                word_player1[wordCounter1][user1_word.length()].setText(label.getText());
                                user1_word += label.getText();
                            }
                            else if(wordCounter1 > wordCounter2 && user2_word.length() < 5){
                                word_player2[wordCounter2][user2_word.length()].setText(label.getText());
                                user2_word += label.getText();
                            }
                        }

                        @Override
                        public void mouseEntered(MouseEvent e) {

                        }

                        @Override
                        public void mouseExited(MouseEvent e) {

                        }
                    });
                }
                else if (alphabet[i][j].equals("Enter")){
                    letters[i][j].addMouseListener(new MouseListener() {
                        @Override
                        public void mouseClicked(MouseEvent e) {

                        }

                        @Override
                        public void mousePressed(MouseEvent e) {
                            JLabel label = (JLabel) e.getSource();
                            label.setSize(60,35);
                            label.setBackground(Color.lightGray);
                        }

                        @Override
                        public void mouseReleased(MouseEvent e) {
                            if(wordCounter1 == wordCounter2 && user1_word.length() == 5){
                                try {
                                    play(word_player1, wordCounter1,user1_word);
                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                            else if(wordCounter1 > wordCounter2 && user2_word.length() == 5){
                                try {
                                    play(word_player2, wordCounter2,user2_word);
                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                            JLabel label = (JLabel) e.getSource();
                            label.setSize(50,25);
                            label.setBackground(Color.white);
                        }

                        @Override
                        public void mouseEntered(MouseEvent e) {

                        }

                        @Override
                        public void mouseExited(MouseEvent e) {

                        }
                    });
                }
                else if(alphabet[i][j].equals("Delete")){
                    letters[i][j].addMouseListener(new MouseListener() {
                        @Override
                        public void mouseClicked(MouseEvent e) {

                        }

                        @Override
                        public void mousePressed(MouseEvent e) {
                            JLabel label = (JLabel) e.getSource();
                            label.setSize(60,35);
                            label.setBackground(Color.lightGray);
                        }

                        @Override
                        public void mouseReleased(MouseEvent e) {
                            if(wordCounter1 == wordCounter2 && user1_word.length()>0){
                                word_player1[wordCounter1][user1_word.length()-1].setText("");
                                user1_word = user1_word.substring(0, user1_word.length()-1);
                            }
                            else if(wordCounter1 > wordCounter2 && user2_word.length()>0){
                                word_player2[wordCounter2][user2_word.length()-1].setText("");
                                user2_word = user2_word.substring(0, user2_word.length()-1);
                            }
                            JLabel label = (JLabel) e.getSource();
                            label.setSize(50,25);
                            label.setBackground(Color.white);
                        }

                        @Override
                        public void mouseEntered(MouseEvent e) {

                        }

                        @Override
                        public void mouseExited(MouseEvent e) {

                        }
                    });
                }

                letters[i][j].setOpaque(true);
                letters[i][j].setBackground(Color.white);
                if(i == 1) {
                    letters[i][j].setBounds(j*35+230, i*25+315, 35, 25);
                }
                else {
                    letters[i][j].setBounds(j*35+215, i*25+315, 35, 25);
                }
                if(alphabet[i][j].equalsIgnoreCase("Enter" )) {
                    letters[i][j].setBounds(j*35+215, i*25+315, 50, 25);
                }
                else if ( alphabet[i][j].equalsIgnoreCase("Delete")) {
                    letters[i][j].setBounds(j*35+230, i*25+315, 50, 25);
                }
                if(alphabet[i][j].equals("")){
                    letters[i][j].setBorder(null);
                    letters[i][j].setOpaque(false);
                    letters[i][j].setBounds(j*35+260, i*25+5, 50, 25);
                }
                jpTrial.add(letters[i][j]);
            }

        }


        word_player1 = new JLabel[5][5];

        border = BorderFactory.createLineBorder(Color.black, 2);
        for (int i = 0; i < word_player1.length; i++) {
            for (int j = 0; j < 5; j++) {
                word_player1[i][j] = new JLabel("", SwingConstants.CENTER);
                word_player1[i][j].setBorder(border);
                word_player1[i][j].setOpaque(true);
                word_player1[i][j].setBackground(Color.white);
                word_player1[i][j].setBounds(75*j+7, 60*i, 70, 50);
                jpTrial.add(word_player1[i][j]);
            }
        }
        jpTrial.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {


            }

            @Override
            public void keyPressed(KeyEvent e) {
                String key = "";
                if(e.getKeyCode()>=65 && e.getKeyCode()<=90 && user1_word.length() < 5 && wordCounter1 == wordCounter2){
                    key += e.getKeyChar();
                    user1_word += e.getKeyChar();
                    key = key.toUpperCase();
                    word_player1[wordCounter1][user1_word.length()-1].setText(key);
                }
                else if(user1_word.length()==5 && e.getKeyCode()==10 && wordCounter1 == wordCounter2){
                    try {
                        play(word_player1,wordCounter1,user1_word);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                else if(e.getKeyCode() == 8 && user1_word.length()>0 && wordCounter1 == wordCounter2){
                    word_player1[wordCounter1][user1_word.length()-1].setText("");
                    user1_word = user1_word.substring(0, user1_word.length()-1);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });


        word_player2 = new JLabel[5][5];

        for (int i = 0; i < word_player2.length; i++) {
            for (int j = 0; j < 5; j++) {
                word_player2[i][j] = new JLabel("", SwingConstants.CENTER);
                word_player2[i][j].setBorder(border);
                word_player2[i][j].setOpaque(true);
                word_player2[i][j].setBackground(Color.white);
                word_player2[i][j].setBounds(75*j+407, 60*i, 70, 50);
                jpTrial.add(word_player2[i][j]);
            }
        }
        jpTrial.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {


            }

            @Override
            public void keyPressed(KeyEvent e) {
                String key = "";
                if(e.getKeyCode()>=65 && e.getKeyCode()<=90 && user2_word.length() < 5 && wordCounter1 > wordCounter2){
                    key += e.getKeyChar();
                    user2_word += e.getKeyChar();
                    key = key.toUpperCase();
                    word_player2[wordCounter2][user2_word.length()-1].setText(key);
                }
                else if(user2_word.length()==5 && e.getKeyCode()==10 && wordCounter1 > wordCounter2){
                    try {
                        play(word_player2,wordCounter2,user2_word);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                else if(e.getKeyCode() == 8 && user2_word.length()>0 && wordCounter1 > wordCounter2){
                    word_player2[wordCounter2][user2_word.length()-1].setText("");
                    user2_word = user2_word.substring(0, user2_word.length()-1);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        jpTrial.requestFocusInWindow();
        jpTrial.setBackground(Color.white);
        add(jpTrial,BorderLayout.CENTER);
    }
    public void guessField_online() throws IOException {
        connectServer();
        startReceivingOpponentsWord();
        jpTrial = new JPanel();
        jpTrial.setLayout(null);
        jpTrial.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                jpTrial.requestFocusInWindow();
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        chatbox = new JTextArea();
        JScrollPane scroll = new JScrollPane(chatbox);
        chatbox.setEditable(false);

        scroll.setBounds(790,0,190,350);
        jpTrial.add(scroll);

        JTextField chat = new JTextField();
        chat.setBounds(700,355,200,30);
        jpTrial.add(chat);

        JButton sendButton = new JButton("Send");
        sendButton.setBounds(910,355,70,30);
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                message = "*";
                message += chat.getText();
                csc.send(message);
                chatbox.setText(chatbox.getText()+"\n"+"You: "+chat.getText());
                chat.setText("");
                System.out.println(message);
            }
        });
        jpTrial.add(sendButton);

        letters = new JLabel[3][10];

        Border border = BorderFactory.createLineBorder(Color.black, 1);

        for (int i = 0; i < letters.length; i++) {
            for (int j = 0; j < alphabet[i].length; j++) {
                letters[i][j] = new JLabel(alphabet[i][j], SwingConstants.CENTER);
                letters[i][j].setBorder(border);

                letters[i][j].setOpaque(true);
                letters[i][j].setBackground(Color.white);
                if(i == 1) {
                    letters[i][j].setBounds(j*35+230, i*25+315, 35, 25);
                }
                else {
                    letters[i][j].setBounds(j*35+215, i*25+315, 35, 25);
                }
                if(alphabet[i][j].equalsIgnoreCase("Enter" )) {
                    letters[i][j].setBounds(j*35+215, i*25+315, 50, 25);
                }
                else if ( alphabet[i][j].equalsIgnoreCase("Delete")) {
                    letters[i][j].setBounds(j*35+230, i*25+315, 50, 25);
                }
                if(alphabet[i][j].equals("")){
                    letters[i][j].setBorder(null);
                    letters[i][j].setOpaque(false);
                    letters[i][j].setBounds(j*35+260, i*25+5, 50, 25);
                }
                jpTrial.add(letters[i][j]);
            }

        }


        word_player1 = new JLabel[5][5];

        border = BorderFactory.createLineBorder(Color.black, 2);
        for (int i = 0; i < word_player1.length; i++) {
            for (int j = 0; j < 5; j++) {
                word_player1[i][j] = new JLabel("", SwingConstants.CENTER);
                word_player1[i][j].setBorder(border);
                word_player1[i][j].setOpaque(true);
                word_player1[i][j].setBackground(Color.white);
                word_player1[i][j].setBounds(75*j+7, 60*i, 70, 50);
                jpTrial.add(word_player1[i][j]);
            }
        }
        jpTrial.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {


            }
            @Override
            public void keyPressed(KeyEvent e) {
                String key = "";
                if(e.getKeyCode()>=65 && e.getKeyCode()<=90 && user1_word.length() < 5  && yourTurn){
                    key += e.getKeyChar();
                    user1_word += e.getKeyChar();
                    key = key.toUpperCase();
                    word_player1[wordCounter1][user1_word.length()-1].setText(key);
                }
                else if(user1_word.length()==5 && e.getKeyCode()==10 && yourTurn && checker(user1_word.toUpperCase())){
                    try {
                        csc.send(user1_word.toUpperCase());

                        play(word_player1,wordCounter1,user1_word);
                        yourTurn = false;
                        info.setText("You are Player"+ playerID +" Opponent's Turn!");
                        wordCounter1++;
                        user1_word = "";
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                else if(e.getKeyCode() == 8 && user1_word.length()>0 && yourTurn){
                    word_player1[wordCounter1][user1_word.length()-1].setText("");
                    user1_word = user1_word.substring(0, user1_word.length()-1);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });


        word_player2 = new JLabel[5][5];

        for (int i = 0; i < word_player2.length; i++) {
            for (int j = 0; j < 5; j++) {
                word_player2[i][j] = new JLabel("", SwingConstants.CENTER);
                word_player2[i][j].setBorder(border);
                word_player2[i][j].setOpaque(true);
                word_player2[i][j].setBackground(Color.white);
                word_player2[i][j].setBounds(75*j+407, 60*i, 70, 50);
                jpTrial.add(word_player2[i][j]);
            }
        }

        info = new JLabel();
        info.setBounds(5,315,200,60);
        if (playerID == 1){
            info.setText("You are Player"+ playerID +" Your Turn!");
        }
        else if(playerID == 2){
            info.setText("You are Player"+ playerID + " Opponent Turn!");
        }
        else{
            info.setText("You are Observer" + (playerID-2));
        }
        jpTrial.add(info);



        jpTrial.requestFocusInWindow();
        jpTrial.setBackground(Color.white);
        add(jpTrial,BorderLayout.CENTER);
    }
    public void icon_Region() {

        JPanel jpIcon = new JPanel();



        ImageIcon image = new ImageIcon(getClass().getClassLoader().getResource("i.png"));
        JLabel icon = new JLabel(image);
        jpIcon.add(icon);


        jpIcon.setBackground(Color.white);
        add(jpIcon,BorderLayout.NORTH);
    }
    public void center_Region() {
        jpCenter = new JPanel();
        jpCenter.setLayout(null);
        JLabel how_to_play = new JLabel("Please Select How to Play!", SwingConstants.CENTER);
        how_to_play.setBounds(100,100,200,20);
        jpCenter.add(how_to_play);

        JButton b1 = new JButton("Keyboard");
        b1.setBounds(90,130,100,30);
        // b1 is for keyboard & b2 is for DnD
        JButton b2 = new JButton("Drag and Drop");
        b2.setBounds(200,130,120,30);

        jpCenter.add(b1);
        jpCenter.add(b2);
        jpCenter.setBackground(Color.white);
        b1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                remove(jpCenter);
                guessField_Keyboard();
                jpTrial.requestFocusInWindow();
                revalidate();
                repaint();
                startTime = System.currentTimeMillis();

            }
        });
        b2.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                remove(jpCenter);
                guessField_DnD();
                jpTrial.requestFocusInWindow();
                revalidate();
                repaint();
                startTime = System.currentTimeMillis();

            }
        });

        jpCenter.setBackground(Color.white);
        add(jpCenter, BorderLayout.CENTER);
    }
    public void number_of_players(){
        JPanel jpPlayers = new JPanel();
        jpPlayers.setLayout(null);
        JLabel game_mode = new JLabel("Please Select Game Mode!", SwingConstants.CENTER);
        JLabel game_mode2 = new JLabel("Score is only available in single mode.", SwingConstants.CENTER);
        game_mode.setBounds(100,90,200,20);
        game_mode2.setBounds(75,110,250,20);
        jpPlayers.add(game_mode);
        jpPlayers.add(game_mode2);

        JButton p1 = new JButton("1 Player");
        p1.setBounds(90,130,100,30);
        JButton p2 = new JButton("2 Players");
        p2.setBounds(200,130,100,30);
        JButton p3 = new JButton("Online");
        p3.setBounds(150,170,100,30);
        JButton stat = new JButton("Game Statistics");
        stat.setBounds(100,350,200,40);
        jpPlayers.add(stat);

        jpPlayers.add(p1);
        p1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                remove(jpPlayers);
                center_Region();
                revalidate();
                repaint();
            }
        });
        stat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame S = new JFrame("Stats");
                double temp = 0;
                double temp2 = 0;
                try {
                    temp2 = winCounter();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                try {
                    temp = gameCounter();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                BigDecimal AI = new BigDecimal(temp2*100/temp);
                AI = AI.round(new MathContext(4));

                JLabel announce = new JLabel("<html>Highest score is: " + highestScore +"<br/>" + " Games played so far: "+ temp + "<br/> Games won so far: "+ temp2+ "<br/><FONT COLOR=green> Win percentage: "+ AI +"<br/> <FONT size=5> <FONT COLOR=red>LETTER USAGE STATS</html>"  );
                announce.setBounds(100,100,100,200);



                List<String> columns = new ArrayList<String>();
                List<String[]> values = new ArrayList<String[]>();
                columns.add("Letters");
                columns.add("Values");
                for (int i = 0; i < 26; i++) {
                    values.add(new String[]{String.valueOf((char) (i + 65)), String.valueOf(letterStatistics.get(i))});
                }
                TableModel tableModel = new DefaultTableModel(values.toArray(new Object[][]{}), columns.toArray());
                JTable table = new JTable(tableModel);
                S.setLayout(new BorderLayout());
                S.add(announce,BorderLayout.NORTH);
                S.add(new JScrollPane(table), BorderLayout.CENTER);
                S.setSize(200, 500);
                S.setLocationRelativeTo(null);
                S.setVisible(true);

            }
        });
        p2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                remove(jpPlayers);
                setSize(800,600);
                setLocationRelativeTo(null);
                guessField_2players();
                is_2player_game = true;
                jpTrial.requestFocusInWindow();
                revalidate();
                repaint();
            }
        });
        jpPlayers.add(p2);

        p3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                remove(jpPlayers);
                setSize(1000,600);
                setLocationRelativeTo(null);
                try {
                    guessField_online();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                wordleWordsList = Arrays.asList(wordleWord.split(""));
                absoluteWord = wordleWordsList;
                is_2player_game = true;
                is_online_game = true;
                jpTrial.requestFocusInWindow();
                revalidate();
                repaint();
            }
        });
        jpPlayers.add(p3);
        jpPlayers.setBackground(Color.white);
        add(jpPlayers, BorderLayout.CENTER);

    }
    public boolean checker(String user_word) {
        for (String s : wordleList) {
            if (s.equals(user_word)) {
                return true;
            }

        }
        return false;
    }
    public void showTime(){
        JPanel jpTime = new JPanel();
        time = new JLabel();
        time.setHorizontalAlignment(JLabel.CENTER);
        time.setFont(UIManager.getFont("Label.font").deriveFont(Font.ITALIC, 15f));
        time.setText(DateFormat.getDateTimeInstance().format(new Date()));
        time.setOpaque(true);
        jpTime.add(time);


        Timer timer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                time.setText(DateFormat.getDateTimeInstance().format(new Date()));
            }
        });
        timer.setRepeats(true);
        timer.setCoalesce(true);
        timer.setInitialDelay(0);
        timer.start();

        add(jpTime, BorderLayout.SOUTH);
    }
    public void ScoreWriter() throws IOException {

        //RandomAccessFile highscoresfile = new RandomAccessFile(new File("src\\highScores.txt"), "rw");
//        highscoresfile.seek(highscoresfile.length());
//        highscoresfile.writeBytes(String.valueOf((Double)scoreCalculator(startTime,endTime,greenCounter,wordCounter1)));
//        highscoresfile.writeBytes("\n");
//        Scanner scoreReader = new Scanner(new File("src\\highScores.txt"));
//        while (scoreReader.hasNextDouble()) {
//            scoresList.add(scoreReader.nextDouble());
//        }
//        scoreReader.close();

//        PrintStream out = new PrintStream(new FileOutputStream("highScores.txt"));
//        out.println("45");

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        wordle.class.getResourceAsStream("highScores.txt"),
                        StandardCharsets.ISO_8859_1))) {
            Double score;
            while ((score = Double.valueOf(reader.readLine())) != null) {
                scoresList.add(score);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }catch (NullPointerException e){

        }catch (NumberFormatException e){

        }
        scoresList.add(scoreCalculator(startTime,endTime,greenCounter,wordCounter1));
        Collections.sort(scoresList);
        Collections.reverse(scoresList);
        //highscoresfile.seek(0);

        try (PrintWriter writer = new PrintWriter(new FileWriter("src//highScores.txt"))) {
            for (Double score : scoresList) {
                writer.println(score);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
    public void letterStatisticsFunction() throws IOException {
        //RandomAccessFile letterStatistic = new RandomAccessFile(new File("letterStats.txt"), "rw");
//        Scanner letterStats = new Scanner(new File("letterStats.txt"));
//        while (letterStats.hasNextInt()) {
//            letterStatistics.add(letterStats.nextInt());
//        }
//        letterStats.close();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        wordle.class.getResourceAsStream("letterStats.txt"),
                        StandardCharsets.ISO_8859_1))) {
            Integer count;
            while ((count = Integer.valueOf(reader.readLine())) != null) {
               letterStatistics.add(count);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }catch (NumberFormatException e){

        }
        for (int i = 0; i < 26; i++) {
            if(i==((int)userChar)-65) {
                letterStatistics.set(i,letterStatistics.get(i)+1);
            }
        }

        try (BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(
                                new FileOutputStream("src//letterStats.txt"),
                                StandardCharsets.ISO_8859_1))) {
            for (int i=0; i<26; i++) {
                writer.write(letterStatistics.get(i).toString());
                writer.write("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        /* letterStatistic.seek(0);
        for (int i = 0; i < 26; i++) {
            letterStatistic.writeBytes(valueOf(letterStatistics.get(i)));
            letterStatistic.writeBytes("\n");
        }
        letterStatistic.close();

         */
    }
    public int gameCounter() throws IOException {
       // RandomAccessFile gameCount = new RandomAccessFile(new File("gameCount.txt"), "rw");
//        Scanner gameCounts = new Scanner(new File("gameCount.txt"));
        int gameCounter = 0;
//        try{ gameCounter = gameCounts.nextInt();}
//        catch (NoSuchElementException e){
//            gameCount.seek(0);
//            gameCount.writeBytes(String.valueOf(0));
//        }
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        wordle.class.getResourceAsStream("gameCount.txt"),
                        StandardCharsets.ISO_8859_1))) {
            gameCounter = Integer.valueOf(reader.readLine());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (isPlayed) {
            gameCounter++;
        }
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream("src//gameCount.txt"),
                        StandardCharsets.ISO_8859_1))) {
            writer.write(String.valueOf(gameCounter));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        /*gameCounts.close();
        gameCount.seek(0);
        gameCount.writeBytes(valueOf(gameCounter));
        gameCount.close();

         */
        return gameCounter;


    }
    public int winCounter() throws IOException {
        //RandomAccessFile winGames = new RandomAccessFile(new File("winCount.txt"), "rw");
//        Scanner winGame = new Scanner(new File("winCount.txt"));
        int winCounter = 0;
//        try{winCounter = winGame.nextInt();}
//        catch (NoSuchElementException e){
//            winGames.seek(0);
//            winGames.writeBytes(String.valueOf(0));
//        }
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        wordle.class.getResourceAsStream("winCount.txt"),
                        StandardCharsets.ISO_8859_1))) {
            winCounter = Integer.parseInt(reader.readLine());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(isWon) {
            winCounter++;
        }
        try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("src//winCount.txt"), StandardCharsets.ISO_8859_1))) {
            writer.write(String.valueOf(winCounter));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        /*
        winGame.close();
        winGames.seek(0);
        winGames.writeBytes(valueOf(winCounter));
        winGames.close();

         */
        return winCounter;


    }
    public void confetti(){
        JPanel jpConfetti = new confetti();
        jpConfetti.setBackground(Color.white);

        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                repaint();

            }
        };
        t = new Timer(500,al);
        t.start();
        remove(jpTrial);
        add(jpConfetti,BorderLayout.CENTER);
        repaint();
    }
    public void startReceivingOpponentsWord(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        csc.receive();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();
    }
    public void connectServer(){
        csc = new ClientSideConnection();
    }
    public class ClientSideConnection {

        private DataInputStream dataIn;
        private DataOutputStream dataOut;

        public ClientSideConnection(){
            System.out.println("Client");
            try{
                Socket socket = new Socket("localhost", 51734);
                dataIn = new DataInputStream(socket.getInputStream());
                dataOut = new DataOutputStream((socket.getOutputStream()));
                playerID=dataIn.readInt();
                if(playerID == 1){
                    yourTurn = true;
                }
                else{
                    yourTurn = false;
                }
                if(playerID < 3){
                    System.out.println("Connected as Player Number "+playerID);
                }
                else{
                    System.out.println("Connected as Observer Number "+(playerID-2));
                }
                wordleWord = "";
                wordleWord = dataIn.readUTF();
                System.out.println(wordleWord);
            }catch (IOException ex){
                System.out.println("IOException from CSC constructor.");
            }
        }

        public void send(String userWord){ // to send message or user word
            try{
                dataOut.writeUTF(userWord);
                dataOut.flush();
            }catch (IOException ex){

            }
        }
        public void receive() throws IOException { // to receive message or user word
            List<String> templist;
            String tempstr = dataIn.readUTF();
            templist = Arrays.asList(tempstr.split(""));
            if(Objects.equals(templist.get(0), "*")){
                message = tempstr.substring(1);
                chatbox.setText(chatbox.getText()+"\n"+message);
            }
            else{
                if (playerID < 3) {
                    try {
                        user2_word = tempstr;
                        System.out.println("opponentWord " + user2_word);
                        String[] opponentWordsArray = user2_word.split("");
                        for (int i = 0; i < 5; i++) {
                            word_player2[wordCounter2][i].setText(opponentWordsArray[i]);
                        }
                        play(word_player2, wordCounter2, user2_word);
                        yourTurn = true;
                        info.setText("You are Player"+ playerID +" Your Turn!");
                        wordCounter2++;

                    } catch (IOException ex) {}
                    user2_word = "";
                }
                else {
                    if (wordCounter1 == wordCounter2) {
                        try {
                            user1_word = tempstr;
                            System.out.println("opponentWord " + user1_word);
                            String[] opponentWordsArray = user1_word.split("");
                            for (int i = 0; i < 5; i++) {
                                word_player1[wordCounter1][i].setText(opponentWordsArray[i]);
                            }
                            play(word_player1, wordCounter1, user1_word);
                            wordCounter1++;
                        } catch (IOException ex) {
                        }
                        user1_word = "";
                    } else {
                        try {
                            user2_word = tempstr;
                            System.out.println("opponentWord " + user2_word);
                            String[] opponentWordsArray = user2_word.split("");
                            for (int i = 0; i < 5; i++) {
                                word_player2[wordCounter2][i].setText(opponentWordsArray[i]);
                            }
                            play(word_player2, wordCounter2, user2_word);
                            wordCounter2++;

                        } catch (IOException ex) {
                        }
                        user2_word = "";
                    }
                }
            }
        }
    }
}