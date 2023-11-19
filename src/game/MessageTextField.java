package game;

import javax.swing.*;
import java.awt.*;

/**
 * {@code MessageTextField} defines the style and content of top and bottom message textField.
 */
public class MessageTextField extends JTextField {
    /**
     * This method initializes a {@code MessageTextField} textField.
     */
    public MessageTextField(String content, int x, int y, int width, int height){
        super.setText(content);
        this.setFocusable(false);
        this.setFont(new Font("New Times Roman", Font.BOLD,30));
        this.setHorizontalAlignment(JTextField.CENTER);
        this.setOpaque(false);
        this.setBorder(null);
        this.setBounds(x,y, width,height);
    }
}
