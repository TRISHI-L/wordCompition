package game;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

/**
 * {@code TxtReader} 用于读取 words.txt 文件，选择一个单词。
 */
public class TxtReader {
    /**
     * A String choose from words.txt as answer.
     */
    public static String answer;
    /**
     * {@code status==2} means this letter is checked, and should be green.
     */
    private final int LETTER_GREEN=2;

    /**
      * read words dictionary file, and choose a word as answer.
      */
    public TxtReader() throws IOException {
        Random r=new Random();
        int line=r.nextInt(2315);
        System.out.print("line "+line+": ");
        BufferedReader br=new BufferedReader(new FileReader("words.txt"));
        int cnt=0;
        while(cnt<=line){
            answer=br.readLine();
            cnt++;
        }
        br.close();
        System.out.println(answer);
    }

    /**
     * 将玩家的猜测与答案进行比较,修改status[][]
     *
     * @param guess 玩家输入的单词
     * @return 如果猜测正确，返回 true；否则返回 false。
     */
    public boolean Compare(String guess){
        if("O".equals(Wordle.switchFlag)){
            if(guess.equals(answer)){
                //game clear!
                for(int i=0;i<5;i++) Wordle.statusO[Wordle.pointer_Ro][i]=2;
                Wordle.pointer_Ro=6;
                Wordle.pointer_Co=0;
                return true;
            }
            else{ //猜测与答案不完全匹配
                for(int i=0;i<5;i++) {
                    if(answer.charAt(i)==guess.charAt(i)){
                        Wordle.statusO[Wordle.pointer_Ro][i]=LETTER_GREEN;
                    }
                }
                Wordle.pointer_Co=0;
                Wordle.pointer_Ro+=1;
                return false;
            }
        }else if("S".equals(Wordle.switchFlag)){
            if(guess.equals(answer)){
                //game clear!
                for(int i=0;i<5;i++) Wordle.statusS[Wordle.pointer_Rs][i]=2;
                Wordle.pointer_Rs=6;
                Wordle.pointer_Cs=0;
                return true;
            }
            else{
                for(int i=0;i<5;i++) {
                    if(answer.charAt(i)==guess.charAt(i)){
                        Wordle.statusS[Wordle.pointer_Rs][i]=LETTER_GREEN;
                    }
                }
                Wordle.pointer_Cs=0;
                Wordle.pointer_Rs+=1;
                return false;
            }
        }
        return false;
    }
}