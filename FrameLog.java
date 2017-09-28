//GUI
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class FrameLog extends MyFrame{
	private FileRead fileRead;
	private JTextArea log;
	private JScrollPane scroller;
	public FrameLog(){
		super("Auction Server - Log",
			640,
			480,
			false,true
		);//passing arguments to MyFrame constructor)

		init();

		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);
		this.pack(); //JFrame pack();
	}

	private void init(){
		fileRead=new FileRead("log.csv");
		log=new JTextArea(fileRead.getContent());
		//log.setWrapStyleWord(true);
		log.setFont(new Font(
						"Tahoma", //font face
						0, //style (plain=0, bold-1)
						18 //font size
					)
		);
		scroller=new JScrollPane(log);
		this.add(scroller);
	}
}