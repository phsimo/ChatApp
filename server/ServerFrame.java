package server;
import javax.swing.*;

import java.awt.*;


/*
 * Class that creates a frame for the server, which contains the list of the connected clients
 */
public class ServerFrame extends JFrame {
	
	Container contentPane;
	JPanel panel;

	public ServerFrame() {
		super();
		contentPane= getContentPane();
		contentPane.setLayout(new BorderLayout());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		JLabel label = new JLabel("List of Clients");
		label.setHorizontalAlignment(JLabel.CENTER);
		contentPane.add(label, BorderLayout.NORTH);
		setSize(400, 600);
		setVisible(true);
	}
		
	public void update(Object[] usernameList) { //updates the list of clients when a clients joins or leaves the server
		JList list = new JList(usernameList);
		if (panel==null) 
			panel = new JPanel();
		else {
				panel.removeAll();
		}
		panel.setLayout(new BorderLayout());
		panel.add(list);
		panel.revalidate();
		contentPane.add(panel);
		setVisible(true);
	}
	
	
	

}
