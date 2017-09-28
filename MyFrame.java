/**
	Author	: Chandima B Samarasinghe
	Date 	: 29th Aug 2017
**/
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class MyFrame extends JFrame implements ActionListener{
	public void actionPerformed(ActionEvent event){}
	public MyFrame(String title,int width,int height){
		this(title,width,height,true,true);
	}
	public MyFrame(String title,int width,int height,boolean isResizable,boolean isVisible){
		setTitle(title);	
		setResizable(isResizable);
		setSize(width,height);
		setPreferredSize(new Dimension(width,height));
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation( //center screen
			dim.width/2-this.getSize().width/2,
			dim.height/2-this.getSize().height/2
		);
		setVisible(isVisible);
	}
}