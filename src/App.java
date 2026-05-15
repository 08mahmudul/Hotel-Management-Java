import javax.swing.*;

public class App {

    public static void main(String[] args) {

        JFrame frame = new JFrame("My Swing App");

        JLabel label = new JLabel("Hello Hasan!");
        label.setBounds(120, 80, 200, 30);

        JButton button = new JButton("Click");
        button.setBounds(120, 130, 120, 40);

        frame.add(label);
        frame.add(button);

        frame.setLayout(null);
        frame.setSize(400, 300);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}