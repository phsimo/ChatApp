package client;

/*
 * Class that creates a message frame where the user chats with another client connected to the server
 */
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class MessageFrame extends JFrame {
	public static final String NEWLINE="850075"; //Regex that replaces the '\n' charcter
	MyActionListenerHandler actionHandler = new MyActionListenerHandler();
	
	JTextArea writingArea;
	JTextArea readingArea;
	String user;
	Client client;
	JButton button;
	
	public MessageFrame(Client client,String user) {
		super("Conversation with "+user);
		this.client=client;
		this.user=user;
		init();
	}
	
	/*
	 * Initializes the message frame
	 */
	public void init() {
		Container contentPane = getContentPane();
		setSize(350, 350);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		JPanel messagePanel=new JPanel();
		readingArea= new JTextArea(15,15);
		readingArea.setText("");
		readingArea.setEditable(false);
		JScrollPane scroll= new JScrollPane(readingArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		writingArea=new JTextArea(5,20);
		JScrollPane writingScroll= new JScrollPane(writingArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		button= new JButton("Send");
		button.addActionListener(actionHandler);
		setLayout(new BorderLayout());
		contentPane.add(scroll, BorderLayout.CENTER);
		messagePanel.add(writingScroll);
		messagePanel.add(button);
		contentPane.add(messagePanel,BorderLayout.SOUTH);
		setVisible(true);
	}
	
	/*
	 * Action listener that sends the message of the user to the server in XML format
	 */
	class MyActionListenerHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String message=writingArea.getText();
			writingArea.setText(null); // clears the writing text area
			if (!(message.equals(""))) {
				 readingArea.append(client.getUsername()+": "+message+"\n"); //puts the message of the user in the reading area
				 XMLProcessing process= new XMLProcessing(client);
				 try {
					 process.createXML(user, message.trim().replaceAll("\n", NEWLINE)); //substitutes the newline with the regex and calls the method to convert the message into XML
				 }catch (Exception a) {
					 a.printStackTrace();
				 }
			}
		}
	}
	
	/*
	 * Puts the message sent by the other client through the server in the reading area
	 */
	public void insertMessage(String user, String message) {
			String s=user+": "+message.replaceAll(NEWLINE, "\n")+"\n";
			readingArea.append(s);
		
	}
	
	/*
	 * Prints a message when the client the user is talking to is disconnected from the server
	 */
	public void blockFrame(String user) {
		readingArea.append(user+" IS NOT ONLINE ANYMORE. CONVERSION IS OVER.");
		writingArea.setEditable(false); // the user can't send anymore messages
		button.setEnabled(false);
	}

}
