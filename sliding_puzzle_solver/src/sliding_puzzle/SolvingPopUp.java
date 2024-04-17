package sliding_puzzle;

import javax.swing.*;

public class SolvingPopUp extends JFrame{
    private JPanel Background;
    private JTextArea label;

    public SolvingPopUp() {
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setContentPane(Background);
        this.pack();
        setLocationRelativeTo(null); // Centers JFrame
    }

    public void showText(String text) {
        label.setText(text);
        label.setVisible(true);
    }
}