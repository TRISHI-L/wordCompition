package game;

import java.util.Random;

import javax.swing.JOptionPane;

/**
 * Reward 类用于管理游戏中的奖励和玩家生命值；
 * 还维护了游戏的相关统计信息，如游戏回合数、胜利回合数和玩家的生命值。
 */
public class Reward {

	public static int gameNum=3;
	public static int winNum=2;
	public static int heartO=5,heartS=5;
	public static String playInfoO="",playInfoS="";
	public static int winFlagO=0,winFlagS=0;

	/**
	 * 随机选择一个技能
	 * @return 1 获得一个字母
	 */
	public static int rewardChoose() {
		int num=new Random().nextInt(100);
		if(num%2==0) {
			return -1;
		}else {
			return 1;
		}
		
	}

	/**
	 * 增加生命值
	 * @param player 玩家标识，"O" 表示玩家1，"S" 表示玩家2
	 */
	public static void rewardBai(String player) {
		int num=new Random().nextInt(100);
		if(player.equals("O")) {
				Reward.heartO++;
				JOptionPane.showConfirmDialog(null, "玩家1获得1技能，血量加1",
            	        "确认",JOptionPane.DEFAULT_OPTION);
		}else {
				Reward.heartS++;
				JOptionPane.showConfirmDialog(null, "玩家2获得1技能，血量加1",
            	        "确认",JOptionPane.DEFAULT_OPTION);
			
		}
	}

	/**
	 * 获得一个字母的技能，并将字母添加到相应玩家的列表中
	 * @param word
	 * @param player
	 */
	public static void rewardChar(String word,String player) {
		int data=new Random().nextInt(100);
		if(player.equals("O")) {
			Wordle.playStrO.add(word.charAt(data%5));
			JOptionPane.showConfirmDialog(null, "玩家1获得1技能，获得一个字符:  "+Wordle.playStrO.get(0),
        	        "确认",JOptionPane.DEFAULT_OPTION);
		}else {
			Wordle.playStrS.add(word.charAt(data%5));
			//Wordle.playStrO.add(word.charAt(data%5));
			JOptionPane.showConfirmDialog(null, "玩家2获得1技能，获得一个字符:  "+Wordle.playStrS.get(0),
        	        "确认",JOptionPane.DEFAULT_OPTION);
		}
	}
}
