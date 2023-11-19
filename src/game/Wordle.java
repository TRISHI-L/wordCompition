package game;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.awt.event.KeyEvent.VK_SPACE;

/**
 * {@code Wordle} is the game window.
 * It includes 6*5 blocks for users to guess words, and also displays some prompt message to players.
 */
public class Wordle extends JFrame {
    /**
     * 跟踪初始化次数
     */
	private int initNum=1;
	Wordle activeWordle;
    /**
     * Store characters or strings of characters for player "O" and player "S" in the game.
     */
	public static List<Character> playStrO=new ArrayList<Character>();
	public static List<Character> playStrS=new ArrayList<Character>();
    private  Boolean flag=true;
    
    public static String switchFlag="O";//O  表示第一个用户S代表第二个用户
    /**
     * A TxtReader object to read words in words.txt.
     */
    private static TxtReader tro,trs;
    /**
     * An arraylist of letterBlock to modifies style and content of blocks.
     */
    private static ArrayList<LetterBlock> tFList,sFList;
    /**
     * Number of letters in a row.
     */
    private static final int LEN_COLUMN=5;
    /**
     * Number of attempts in a round
     */
    private static final int LEN_ROW=6;
    /**
     * The width of the entire window.
     */
    private final int WINDOW_WIDTH=580;
    /**
     * The height of the entire window.
     */
    private static final int WINDOW_HEIGHT=870;
    /**
     * The height of the message textField.
     */
    private static final int MESSAGE_HEIGHT=40;
    /**
     * The distance between topMessageField and top border.
     */
    private final static int TOP_FIELD_TOP_MARGIN=20;
    /**
     * A textField at the top of window, displays specifications of input to players.
     */
    private MessageTextField topMessageField=new MessageTextField("Press A Letter to Input",
                                                                        0,TOP_FIELD_TOP_MARGIN,
                                                                        WINDOW_WIDTH,MESSAGE_HEIGHT);
    /**
     * A textField at the bottom of window, displays game statusO to players.
     */
    private MessageTextField bottomMessageField=new MessageTextField("Press Enter to Check",
                        0, Wordle.BLOCKS_TOP_MARGIN+ Wordle.CONTENT_HEIGHT+TOP_FIELD_TOP_MARGIN,
                            WINDOW_WIDTH,MESSAGE_HEIGHT);
    private MessageTextField playerField;
    
    /**
     * The distance between blocks and top border.
     */
    private static final int BLOCKS_TOP_MARGIN=70;
    /**
     * The distance between blocks and left border.
     */
    private final int LEFT_MARGIN=50;
    /**
     * The width of 6*5 blocks.
     */
    private final int CONTENT_WIDTH=WINDOW_WIDTH-LEFT_MARGIN*2;
    /**
     * The height of 6*5 blocks.
     */
    private static final int CONTENT_HEIGHT=WINDOW_HEIGHT-BLOCKS_TOP_MARGIN*2-TOP_FIELD_TOP_MARGIN-MESSAGE_HEIGHT*3;
    /**
     * The width and height of each letter block.
     */
    private final int BLOCK_SIZE=70;
    /**
     * The padding between 2 adjacent letter blocks in message textField in x direction.
     */
    private final int BLOCK_X_PADDING=(CONTENT_WIDTH-BLOCK_SIZE*LEN_COLUMN)/4;
    /**
     * The padding between 2 adjacent letter blocks in message textField in y direction.
     */
    private final int BLOCK_Y_PADDING=(CONTENT_HEIGHT-BLOCK_SIZE*LEN_ROW)/5;
    /**
     * {@code statusO==0} means this letter is not checked.
     */
    private final int LETTER_NOT_CHECK=0;
    /**
     * A 2-dimensional array that stores input of players.
     */
    private static char[][] contentO=new char[LEN_ROW][LEN_COLUMN];
    private static char[][] contentS=new char[LEN_ROW][LEN_COLUMN];
    /**
     * A 2-dimensional array that stores status of each letter blocks. (0: Not checked, 1: yellow, 2: green, -1: grey)
     */
    public static int[][] statusO=new int[LEN_ROW][LEN_COLUMN];
    public static int[][] statusS=new int[LEN_ROW][LEN_COLUMN];
    /**
     * A pointer indicates current input position of player_O.
     */
    public static int pointer_Ro=0,pointer_Rs=0;
    /**
     * A pointer indicates current input position of player_S.
     */
    public static int pointer_Co=0,pointer_Cs=0;
    /**
     * A regex expression to judge whether input character is a letter.
     */
    private final String REGEX="[A-Z]+";
    /**
     * The time the game started.
     */
    //public final static long START_TIME=System.currentTimeMillis();

    /**
     * This method initializes a Wordle Window.
     */
    public Wordle() throws IOException {
    	activeWordle=this;
        this.init();
        for(int i=0;i<LEN_ROW;i++){ //playerO字母块初始化
            for(int j=0;j<LEN_COLUMN;j++){
                int COLUMN_LIMIT = 1;
                LetterBlock textField=new LetterBlock(COLUMN_LIMIT,
                        "",
                        LEFT_MARGIN+j*(BLOCK_X_PADDING+BLOCK_SIZE),
                        BLOCKS_TOP_MARGIN+i*(BLOCK_Y_PADDING+BLOCK_SIZE),
                        BLOCK_SIZE,
                        BLOCK_SIZE,
                        LETTER_NOT_CHECK);
                this.add(textField); //将字母块添加到Wordle窗口
                tFList.add(textField);
            }

        }
        for(int i=0;i<LEN_ROW;i++){ //playerS字母块初始化
            for(int j=0;j<LEN_COLUMN;j++){
                int COLUMN_LIMIT = 1;
                LetterBlock textField=new LetterBlock(COLUMN_LIMIT,
                        "",
                        LEFT_MARGIN+j*(BLOCK_X_PADDING+BLOCK_SIZE),
                        BLOCKS_TOP_MARGIN+i*(BLOCK_Y_PADDING+BLOCK_SIZE),
                        BLOCK_SIZE,
                        BLOCK_SIZE,
                        LETTER_NOT_CHECK);
                this.add(textField);
                sFList.add(textField);
            }

        }
        this.add(topMessageField);
        this.add(bottomMessageField);
        this.add(playerField);
        refreshTextField();
        //Get players' inputs.
        this.addKeyListener(new KeyAdapter(){
            @Override
            public void keyTyped(KeyEvent e) {
                //玩家1 input
                if("O".equals(switchFlag)) {
                    if (pointer_Ro < 6) { //检查是否还有输入的机会
                        if (e.getKeyChar() != KeyEvent.VK_SPACE //确保不是空格、退格、回车
                                && e.getKeyChar() != KeyEvent.VK_BACK_SPACE
                                && e.getKeyChar() != KeyEvent.VK_ENTER  && flag) {
                            String s = "" + Character.toUpperCase(e.getKeyChar());
                            //输入非字母字符
                            if (!s.matches(REGEX)) topMessageField.setText("Input Is not a Letter!");
                            else {
                                //键入一个字母
                                flag=false;
                                topMessageField.setText("Press A Letter to Input");
                                if(tro.answer.contains(s)) { //字母在单词中出现
                                    //遍历玩家 "O" 已猜测的字母列表 playStrO
                                	for(int k = 0; k < playStrO.size(); k++) {
                                        //遍历答案单词 tro.answer 中的每个字符
                                		for(int m = 0; m < tro.answer.length(); m++) {
                                			if(playStrO.get(k) == tro.answer.charAt(m)) {
                                                //更新字母块显示
                                				contentO[pointer_Ro][m] = (char) Character.toUpperCase(tro.answer.charAt(m));
                                			}
                                		}
                                	}
                                    for(int k=0; k < tro.answer.length(); k++) {
                                        if(s.equalsIgnoreCase(tro.answer.charAt(k) + "")) {
                                            contentO[pointer_Ro][k] = (char) Character.toUpperCase(tro.answer.charAt(k));
                                        }
                                    }
                                    playStrO.add(s.charAt(0));
                                    
                                } else { //字母不在单词中
                                    topMessageField.setText("The Letter Not In Word");
                                    Reward.heartO--;
                                }
                                /**
                                 * 更新玩家 "O" 的游戏进度信息并在界面上显示
                                 */
                                String temp="-----";
                                for(int k = 0; k < playStrO.size(); k++) {
                                    for(int m = 0; m < tro.answer.length(); m++) {
                                        if(playStrO.get(k) == tro.answer.charAt(m)) {
                                            if(m+1 < tro.answer.length())//是否存在下一字符，确保在更新 temp 字符串时不会越界
                                                temp = temp.substring(0,m) + playStrO.get(k)
                                                        + temp.substring(m + 1);
                                            else
                                                temp = temp.substring(0,m) + playStrO.get(k);
                                        }
                                    }
                                }
                                Reward.playInfoO="玩家1当前进度："+temp+",\n剩余血量:"+Reward.heartO;
                                playerField.setText(Reward.playInfoO);
                                refreshTextField();

                            }
                        }
                    }
                } else if("S".equals(switchFlag)){
                    //玩家2 input
                    if(pointer_Rs<6){
                        if(e.getKeyChar() != KeyEvent.VK_SPACE
                                && e.getKeyChar() != KeyEvent.VK_BACK_SPACE
                                && e.getKeyChar() != KeyEvent.VK_ENTER){
                            String s = ""+Character.toUpperCase(e.getKeyChar());
                            if(!s.matches(REGEX))topMessageField.setText("Input Is not a Letter!");
                            else{
                                flag=false;
                                topMessageField.setText("Press A Letter to Input");
                                if(tro.answer.contains(s)) {
                                	for(int k = 0; k < playStrS.size(); k++) {
                                		for(int m = 0; m < tro.answer.length(); m++) {
                                			if(playStrS.get(k) == tro.answer.charAt(m)) {
                                				contentS[pointer_Rs][m] = (char) Character.toUpperCase(tro.answer.charAt(m));
                                			}
                                		}
                                	}
                                    for(int k = 0; k < tro.answer.length(); k++) {
                                        if(s.equalsIgnoreCase(tro.answer.charAt(k)+"")) {
                                            contentS[pointer_Rs][k] = (char) Character.toUpperCase(tro.answer.charAt(k));
                                        }
                                    }
                                    playStrS.add(s.charAt(0));

                                }else {
                                    topMessageField.setText("The Letter Not In Word");
                                    Reward.heartS--;
                                }
                                String temp = "-----";
                                for(int k = 0; k < playStrS.size(); k++) {
                                	for(int m = 0; m < trs.answer.length(); m++) {
                            			if(playStrS.get(k) == tro.answer.charAt(m)) {
                            				if(m+1 < tro.answer.length())
	                        					temp = temp.substring(0,m) + playStrS.get(k)
                                                        + temp.substring(m + 1);
	                        				else
	                        					temp = temp.substring(0,m) + playStrS.get(k);
                            			}
                            		}
                            	}
                                
                                Reward.playInfoS="玩家2当前进度："+temp+",\n剩余血量:"+Reward.heartS;
                                playerField.setText(Reward.playInfoS);
                                refreshTextField();
                            }
                        }
                    }
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if ("O".equals(switchFlag)) {
                    if (pointer_Ro < 6) {
                        if (e.getKeyCode() == VK_SPACE) {
                            if (!flag) {
                                try {
                                    //获取玩家本次猜测进度
                                    String guess = "" + contentO[pointer_Ro][0] +
                                            contentO[pointer_Ro][1] +
                                            contentO[pointer_Ro][2] +
                                            contentO[pointer_Ro][3] +
                                            contentO[pointer_Ro][4];

                                    //和答案比较
                                    boolean flag = tro.Compare(guess);
                                    refreshTextField();
                                    if (pointer_Ro == 6) {
                                        if (flag) {
                                        	Reward.winFlagO++;
                                        	if(Reward.winFlagO >= Reward.winNum) {
                                        		JOptionPane.showConfirmDialog(null,
                                                        "玩家1取得最终的胜利，将退出游戏",
                                            	        "确认",JOptionPane.DEFAULT_OPTION);
                                        		System.exit(0);
                                        	}else if(Reward.winFlagO + Reward.winFlagS >= Reward.gameNum){
                                        		JOptionPane.showConfirmDialog(null,
                                                        "平局，将退出游戏",
                                            	        "确认",JOptionPane.DEFAULT_OPTION);
                                        		System.exit(0);
                                        	}else {
                                        		topMessageField.setText("Congratulations!");
                                                bottomMessageField.setText("You win! Answer Is " + guess);
                                                new Result(Wordle.this, "Game Clear!", true, true);
                                                return ;
                                        	}
                                        } else {
                                            topMessageField.setText("Game Over");
                                            bottomMessageField.setText("Oops! Answer Is " + TxtReader.answer);
                                            new Result(Wordle.this, "Game Over!", true, false);
                                            return ;
                                        }
                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                                flag=true;
                            }
                            Wordle.switchFlag="S";
                        }
                        refreshTextField();
                        activeWordle.setVisible(false);
                        //显示一个确认对话框
                        int flag=JOptionPane.showConfirmDialog(null,
                                Reward.playInfoO + "\n玩家1   玩家2 比分：\n  " +
                                        Reward.winFlagO + "  ：  "+Reward.winFlagS,
                    	        "确认",JOptionPane.DEFAULT_OPTION);
                        if("".equals(Reward.playInfoS)) { //若玩家2的进度信息尚未初始化
                        	Reward.playInfoS="玩家2当前进度：-----,剩余血量:" + Reward.heartS;
                        }
                        playerField.setText(Reward.playInfoS);
                        activeWordle.setVisible(true);
                    }
                } else if ("S".equals(switchFlag)) {
                    if (pointer_Rs < 6) {
                        if (e.getKeyCode() == VK_SPACE) {
                            if (!flag)  {
                                try {
                                    String guess = "" + contentS[pointer_Rs][0] +
                                            contentS[pointer_Rs][1] +
                                            contentS[pointer_Rs][2] +
                                            contentS[pointer_Rs][3] +
                                            contentS[pointer_Rs][4];
                                    boolean flag = trs.Compare(guess);
                                    refreshTextField();
                                    if (pointer_Rs == 6) {
                                        if (flag) {
                                        	Reward.winFlagS++;
                                        	if(Reward.winFlagS>=Reward.winNum) {
                                        		JOptionPane.showConfirmDialog(null,
                                                        "玩家2取得最终的胜利，将退出游戏",
                                            	        "确认",JOptionPane.DEFAULT_OPTION);
                                        		System.exit(0);
                                        	}else if(Reward.winFlagO+Reward.winFlagS >= Reward.gameNum){
                                        		JOptionPane.showConfirmDialog(null, "平局，将退出游戏",
                                            	        "确认",JOptionPane.DEFAULT_OPTION);
                                        		System.exit(0);
                                        	}else {
                                        		topMessageField.setText("Congratulations!");
                                                bottomMessageField.setText("You win! Answer Is " + guess);
                                                new Result(Wordle.this, "Game Clear!", true, true);
                                                return ;
                                        	}
                                        } else {
                                            topMessageField.setText("Game Over");
                                            bottomMessageField.setText("Oops! Answer Is " + TxtReader.answer);
                                            new Result(Wordle.this, "Game Over!", true, false);
                                            return ;
                                        }
                                    }

                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                                flag=true;
                            }

                            Wordle.switchFlag="O";
                        }
                        refreshTextField();
                        activeWordle.setVisible(false);
                        int flag=JOptionPane.showConfirmDialog(null,
                                Reward.playInfoS +
                                        "\n玩家1   玩家2 比分：\n  " + Reward.winFlagO+"  ：  " + Reward.winFlagS,
                    	        "确认", JOptionPane.DEFAULT_OPTION);
                        playerField.setText(Reward.playInfoO);
                        activeWordle.setVisible(true);
                    }
                }
            }
        });
        this.setTitle("Wordle Game");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(WINDOW_WIDTH,WINDOW_HEIGHT);
        this.setLocationRelativeTo(null);
        this.setLayout(null);
        this.setVisible(true);
    }

    /**
     *  This method is to update the contentO and style of letter blocks.
     */
    private void refreshTextField(){
        if("O".equals(switchFlag)) {
        	if(sFList.size() > 0 && tFList.size() > 0) {//将玩家1的字母块设置为不可见
        	    for(int k = 0; k < 30; k++) {
        		    sFList.get(k).setVisible(false);
        		    tFList.get(k).setVisible(true);
        	    }
            }
        	if(initNum == 1 && playStrO.size() > 0) {
        	    for(int k = 0; k < tro.answer.length(); k++) {
                    if(playStrO.get(0)==(tro.answer.charAt(k))) {
                    contentO[pointer_Ro][k] = (char) Character.toUpperCase(tro.answer.charAt(k));
                    }
                }
                initNum++;
        	}
            int i = 0, j = 0;
            for (LetterBlock tf : tFList) {
                if (i < 6) {
                    if (contentO[i][j] != '0') { //如果 contentO[i][j] 不等于 '0'，表示当前位置上有一个有效字符（玩家的猜测字母），
                        // 则将该字符设置为 LetterBlock 的文本，以便玩家在界面上看到
                        tf.setText("" + contentO[i][j]);

                    } else {//如果 contentO[i][j] 等于 '0'，表示当前位置为空，
                        // 这意味着玩家还没有在该位置猜测字母，所以将文本字段的内容设置为空字符串，即清空该位置。
                        tf.setText("");
                    }
                }
                tf.setStatus(statusO[i][j]);
                //通过 tf.setStatus(statusO[i][j])，将文本字段的状态设置为与 statusO 二维数组中相应位置相对应的值
                if (j < 4) j += 1;
                else { //到达了当前行的最后一个位置
                    i += 1;//移动到下一行的第一个位置
                    j = 0;//将 j 重置为0
                }
            }
        } else if("S".equals(switchFlag)) {
        	if(sFList.size() > 0 && tFList.size() > 0) {
        	    for(int k = 0; k < 30; k++) {
        		    tFList.get(k).setVisible(false);
        		    sFList.get(k).setVisible(true);
        	    }
            }
        	if(initNum == 1 && playStrS.size() > 0) {
        	    for(int k = 0; k < tro.answer.length(); k++) {
                    if(playStrS.get(0) == (tro.answer.charAt(k))) {
                    contentS[pointer_Rs][k] = (char) Character.toUpperCase(tro.answer.charAt(k));
                    }
                }
                initNum++;
            }
            int i = 0, j = 0;
            for (LetterBlock tf : sFList) {
                if (i < 6) {
                    if (contentS[i][j] != '0') { //如果 contentS[i][j] 不等于 '0'，表示当前位置上有一个有效字符（玩家的猜测字母），
                        // 则将该字符设置为 LetterBlock 的文本，以便玩家在界面上看到
                        tf.setText("" + contentS[i][j]);
                    } else {//如果 contentO[i][j] 等于 '0'，表示当前位置为空，
                        // 这意味着玩家还没有在该位置猜测字母，所以将文本字段的内容设置为空字符串，即清空该位置。
                        tf.setText("");
                    }
                }
                tf.setStatus(statusS[i][j]);
                //通过 tf.setStatus(statusS[i][j])，将文本字段的状态设置为与 statusO 二维数组中相应位置相对应的值
                if (j < 4) j += 1;
                else {
                    i += 1;
                    j = 0;
                }
            }
        }
    }
    private void init() throws IOException {
        tFList = new ArrayList<>();
        sFList = new ArrayList<>();
        //choose a word as answer
        tro = new TxtReader();
        //tro.answer="SSSHS";
        pointer_Ro = 0;
        pointer_Co = 0;
        for(int i = 0; i < LEN_ROW; i++){
            for(int j = 0; j < LEN_COLUMN; j++){
                contentO[i][j] = '0';
                statusO[i][j] = 0;
            }
        }
        Reward.heartO = 5;
        Reward.heartS = 5;
        Reward.playInfoO = "玩家1当前进度：-----,剩余血量:"+Reward.heartO;
        Reward.playInfoS = "玩家2当前进度：-----,剩余血量:"+Reward.heartS;
        Wordle.playStrO = new ArrayList<>();
        Wordle.playStrS = new ArrayList<>();
        Wordle.switchFlag = "O";
        trs = tro;
        
        System.out.println("Reward.winFlagO   "+Reward.winFlagO);
        System.out.println("Reward.winFlagS   "+Reward.winFlagS);
        if(Reward.winFlagO < Reward.winFlagS) {//玩家1落后玩家2
        	if(Reward.rewardChoose() == 1) {//获得提示char功能
        		Reward.rewardChar(tro.answer, "O");
        		
        	}else {
        		Reward.rewardBai("O");
        	}
        } else if(Reward.winFlagO != Reward.winFlagS) {
        	System.out.println("0000000000000000000");
        	if(Reward.rewardChoose() == 1) {
        		Reward.rewardChar(tro.answer, "S");
        	}else {
        		Reward.rewardBai("S");
        	}
        }
        
        
        String temp = "-----";
        if(initNum >= 1 && playStrO.size() > 0) {
            for(int k = 0; k < playStrO.size(); k++) {
        	    for(int m = 0;m < tro.answer.length(); m++) {
    			    if(playStrO.get(k) == tro.answer.charAt(m)) {
    				    if(m+1 < tro.answer.length())
    					    temp = temp.substring(0,m)+playStrO.get(k)+temp.substring(m+1);
    				    else
    					    temp = temp.substring(0,m)+playStrO.get(k);
    			    }
    		    }
    	    }
        }
        playerField = new MessageTextField("玩家1当前进度："+temp+",剩余血量:"+Reward.heartO,
                0, Wordle.BLOCKS_TOP_MARGIN+ Wordle.CONTENT_HEIGHT+TOP_FIELD_TOP_MARGIN*2+MESSAGE_HEIGHT,
                WINDOW_WIDTH,MESSAGE_HEIGHT);
        
        pointer_Rs = 0;
        pointer_Cs = 0;
        for(int i = 0; i < LEN_ROW; i++){
            for(int j = 0;j < LEN_COLUMN; j++){
                contentS[i][j] = '0';
                statusS[i][j] = 0;
            }
        }
        refreshTextField();
        topMessageField.setText("Press A Letter to Input");
        bottomMessageField.setText("Press Enter to Check");
    }
}