import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;

public class Aardvark extends JFrame{
	public static final long serialVersionUID=1L;
	private int stage;
	private Sequencer player;
	private JTextField hours, mins, secs;
	private JRadioButton absolute;
	private JButton useCurrent;
	private ImagePanel image;
	private Phrase phraseLabel = new Phrase(new String[] {"user is being an aardvark", "aardvarks are really sexy", "This is even better than using the computer!", "I'm glad I'm locked out!"});
	private String typed = new String();
	private static final String DEFAULT_PASSWORD = "godisanaardvark";
	private Point down;
	private JCheckBox skip;
	private boolean release;
	private Robot robot;
	private String password;
	private boolean control;
	private boolean alt;
	
    public static final String group = "230.108.108.108";
    public static final int port = 1080;
 

	public static void main(String[] args) throws IOException{
		boolean listenMode = (args.length > 0) && (args[0] == "listen");
//		listenMode = true;//DEV ONLY
		if (!listenMode){
			try{
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}catch(Exception ex){}
			String[] names={"Start with screenshot on focus", "Start with screenshot immediately", "Start on timer", "Wait for remote"};
			JRadioButton[] choices=new JRadioButton[names.length];
			ButtonGroup options=new ButtonGroup();
			JCheckBox release = new JCheckBox("Release at end");
			JPanel panel=new JPanel(new GridLayout(7, 1, 5, 5));
			for(int i=0;i<names.length;i++){
				choices[i]=new JRadioButton(names[i]);
				options.add(choices[i]);
				panel.add(choices[i]);
			}
			panel.add(new JLabel("Change backdoor password:"));
			JPasswordField password=new JPasswordField(DEFAULT_PASSWORD);
			password.addFocusListener(new FocusListener(){
				public void focusLost(FocusEvent e){
					JPasswordField source=(JPasswordField)e.getSource();
					if(new String(source.getPassword()).equals("")) source.setText(DEFAULT_PASSWORD);
				}
				public void focusGained(FocusEvent e){
					((JPasswordField)e.getSource()).setText("");
				}
			});
			panel.add(password);
			panel.add(release);
			choices[0].setSelected(true);
			if(JOptionPane.showConfirmDialog(null, panel, "AardvarkX", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("icon.png")))!=JOptionPane.OK_OPTION) System.exit(0);
			else{
				int option=-1;
				for(int i=0;i<choices.length;i++){
					if(choices[i].isSelected()) option=i;
				}
				if (!choices[3].isSelected())
					new Aardvark(option, release.isSelected(), new String(password.getPassword()), false).setVisible(true);
				else
					listenMode=true;
			}
		}//end local mode
		if (listenMode){
			new DiscoveryThread().start();
			ServerSocket serverSocket = null;
			try {
			    serverSocket = new ServerSocket(port);
			} catch (IOException e) {
			    JOptionPane.showMessageDialog(null, "Could not listen on port "+port+". Only one Aardvark at a time!", "Server error", JOptionPane.ERROR_MESSAGE);
			    System.exit(-1);
			}

	        
	        while(true){//always accept network connections, which get their own thread so it doesn't stop the whole program
	        	new RemoteCommunicatorThread(serverSocket.accept()).start();
	        }


		}
	}
	public Aardvark(int option, boolean release, String passwd, boolean musicNow){
		super();
		stage=option;
		musicNow = false;
		this.release = release;
		password=passwd;
		try{
			robot=new Robot();
		}catch(Exception ex){}
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setUndecorated(true);
		setAlwaysOnTop(true);
		setResizable(false);
		setSize(Toolkit.getDefaultToolkit().getScreenSize());
		setLayout(new OverlayLayout(this.getContentPane()));
		if(stage<=0){
			JFrame window=new JFrame();
			window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			window.setResizable(false);
			window.setUndecorated(true);
			window.setAlwaysOnTop(true);
			window.setBackground(Color.white);
			window.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			JLabel label=new JLabel("Waiting for focus change...");
			label.setIcon(new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("loading.gif")));
			window.getContentPane().add(label);
			window.pack();
			window.addFocusListener(new FocusListener(){
				public void focusGained(FocusEvent e){}
				public void focusLost(FocusEvent e){
					if(stage<=0){
						synchronized(e.getSource()){
							e.getSource().notify();
						}
					}else{
						restore();
					}
				}
			});
			window.setVisible(true);
			synchronized(window){
				try{
					window.wait();
				}catch(Exception ex){}
			}
			window.dispose();
		}
		try{
			player=MidiSystem.getSequencer();
			player.open();
			player.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
			player.setSequence(MidiSystem.getSequence(Thread.currentThread().getContextClassLoader().getResource("remix.mid")));
		}catch(Exception ex){}
		if(stage==2){
			JFrame window=new JFrame();
			window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			window.setResizable(false);
			window.setLocation(400, 300);
			JPanel timeInput=new JPanel();
			hours=new JTextField("0", 3);
			timeInput.add(hours);
			timeInput.add(new JLabel("hours (24h)"));
			mins=new JTextField("0", 3);
			timeInput.add(mins);
			timeInput.add(new JLabel("minutes"));
			secs=new JTextField("20", 3);
			timeInput.add(secs);
			timeInput.add(new JLabel("seconds"));
			useCurrent=new JButton("Now");
			useCurrent.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					Calendar now=Calendar.getInstance();
					hours.setText(""+now.get(Calendar.HOUR_OF_DAY));
					mins.setText(""+now.get(Calendar.MINUTE));
					secs.setText(""+now.get(Calendar.SECOND));
					absolute.setSelected(true);
				}
			});
			timeInput.add(useCurrent);
			window.getContentPane().add(timeInput, BorderLayout.NORTH);
			JPanel options=new JPanel();
			options.setLayout(new BoxLayout(options, BoxLayout.Y_AXIS));
			ButtonGroup time=new ButtonGroup();
			JRadioButton relative=new JRadioButton("Start after specified period", true);
			relative.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					hours.setText("0");
					mins.setText("0");
					secs.setText("20");
					secs.grabFocus();
				}
			});
			time.add(relative);
			options.add(relative);
			absolute=new JRadioButton("Start at specified time");
			absolute.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					useCurrent.doClick();
					mins.grabFocus();
				}
			});
			time.add(absolute);
			options.add(absolute);
			skip=new JCheckBox("Start music immediately (Uncheck to wait for click)", true);
			options.add(skip);
			window.getContentPane().add(options, BorderLayout.CENTER);
			JButton start=new JButton("Start");
			start.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					synchronized(e.getSource()){
						e.getSource().notify();
					}
				}
			});
			window.getContentPane().add(start, BorderLayout.SOUTH);
			start.getRootPane().setDefaultButton(start);
			window.pack();
			boolean fail=false;
			do{
				window.setVisible(true);
				secs.grabFocus();
				synchronized(start){
					try{
						start.wait();
					}catch(Exception ex){}
				}
				window.setVisible(false);
				int h=0, m=0, s=0;
				try{
					h=hours.getText().trim().equals("")?0:Integer.parseInt(hours.getText().trim());
					m=mins.getText().trim().equals("")?0:Integer.parseInt(mins.getText().trim());
					s=secs.getText().trim().equals("")?0:Integer.parseInt(secs.getText().trim());
					if(h<0||m<0||s<0) fail=true;
				}catch(Exception ex){
					fail=true;
				}
				if(!fail){
					window.dispose();
					if(absolute.isSelected()){
						Calendar now=Calendar.getInstance();
						h-=now.get(Calendar.HOUR_OF_DAY);
						m-=now.get(Calendar.MINUTE);
						s-=now.get(Calendar.SECOND);
					}
					int seconds=(h*3600)+(m*60)+s;
					try{
						Thread.sleep(seconds<0?0:1000*seconds);
					}catch(Exception ex){}
					stage = 1;
				}
			}while(fail);
		}
		if(stage<=1){
			try{
				image=new ImagePanel(robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())));
				getContentPane().add(image);
			}catch(Exception ex){}
		}
		phraseLabel.setOpaque(false);
		phraseLabel.setFont(new Font("monospaced", Font.BOLD, 25));
		add(phraseLabel);
		if(stage>=3) stage3();
		if (skip != null && skip.isSelected()) musicNow = true;
		if (musicNow) player.start();
		addWindowStateListener(new WindowStateListener(){
			public void windowStateChanged(WindowEvent e){
				restore();
			}
		});
		addComponentListener(new ComponentListener(){
			public void componentHidden(ComponentEvent e){
				((JFrame)e.getSource()).setVisible(true);
			}
			public void componentMoved(ComponentEvent e){
				((JFrame)e.getSource()).setLocation(0, 0);
			}
			public void componentResized(ComponentEvent e){
				((JFrame)e.getSource()).setSize(Toolkit.getDefaultToolkit().getScreenSize());
			}
			public void componentShown(ComponentEvent e){}
		});
		addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent e){}
			public void mouseEntered(MouseEvent e){}
			public void mouseExited(MouseEvent e){}
			public void mousePressed(MouseEvent e){
				down = new Point(e.getX(), e.getY());
			}
			public void mouseReleased(MouseEvent e){
				if (image.failCount >= 100) stage3();
				if(stage<=1){
					if (down != null && Math.abs(Math.sqrt(Math.pow(e.getX() - down.x, 2) + Math.pow(e.getY() - down.y, 2))) > 20){ // looks like a drag
						image.drag(down, new Point (e.getX(), e.getY()));
					}
					else 
						image.fail(e.getX(), e.getY());

					Toolkit.getDefaultToolkit().beep();
					System.out.print('\007');
					if(!player.isRunning()) player.start();
					player.setTempoFactor(0.8f+(image.failCount/40.0f));//speed it up gradually for added anxiety

				}

				down = null;
			}
		});
		control=false;
		control=true;
		addKeyListener(new KeyListener(){
			public void keyPressed(KeyEvent e){
				if(e.getKeyCode()==KeyEvent.VK_WINDOWS){
					robot.keyPress(KeyEvent.VK_Y);
					robot.keyRelease(KeyEvent.VK_Y);
				}
				if(e.getKeyCode()==KeyEvent.VK_CONTROL) control=true;
				if(e.getKeyCode()==KeyEvent.VK_ALT) alt=true;
				if(control&&alt){
					robot.keyPress(KeyEvent.VK_DOWN);
					robot.keyRelease(KeyEvent.VK_DOWN);
				}
			}
			public void keyReleased(KeyEvent e){
				if(e.getKeyCode()==KeyEvent.VK_CONTROL) control=false;
				if(e.getKeyCode()==KeyEvent.VK_ALT) alt=false;
			}
			public void keyTyped(KeyEvent e){
				phraseLabel.type();
				if(Math.random()<0.5) robot.mouseMove((int)(Math.random()*image.getWidth()), (int)(Math.random()*image.getHeight()));
				robot.mousePress(InputEvent.BUTTON1_MASK);
				robot.mouseRelease(InputEvent.BUTTON1_MASK);
				char key = e.getKeyChar();
				if (key == '.' || key == '/')
					typed = "";
				else
					typed += key;
				if (typed.toLowerCase().equals(password)) System.exit(0);
			}
		});
		toFront();
		requestFocus();
	}
	private void stage3(){
		if(stage<3){
			stage = 3;
			Container pane=getContentPane();
			pane.removeAll();
			player.stop();
			try{
				player.setSequence(MidiSystem.getSequence(Thread.currentThread().getContextClassLoader().getResource("sound2.mid")));
				if(release) player.setLoopCount(0);

			}catch(Exception ex){}

			player.start();
			/*for (float t = player.getTempoFactor(); t >= 1.0f; t-=0.042f){//bring the temp back down gradually
				player.setTempoFactor(t);
				try {Thread.sleep(200);} catch (InterruptedException e) {}
			}*/
			float startTempo = player.getTempoFactor();
			float tempo = startTempo;
			while (tempo > 1.0f){
				//System.out.println("Tick:" +player.getTickPosition());
				float perLeft = 1-player.getTickPosition()/17000.0f;
				//System.out.println((perLeft*100)+"% left");
				tempo = perLeft*startTempo +1;
				//System.out.println("Tempo factor:" + tempo);
				player.setTempoFactor(tempo);
				try {
					Thread.sleep(200);
				} catch (InterruptedException ex) {}
			}
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					annoy();
					SwingUtilities.invokeLater(this);
				}
			});
		}
	}
	private void annoy(){
		Color randC = new Color(((float)Math.random()), ((float)Math.random()), (float)Math.random());
		Graphics g= getContentPane().getGraphics();
		g.setColor(randC);
		g.fillRect(0, 0, this.getWidth(), getHeight());
		//setBackground();
		System.out.print('\007');
		try{
			Thread.sleep(20);
		}catch(Exception ex){}
		//System.out.println((System.currentTimeMillis()-annoyStartTime) + " milliseconds past");
		//if (System.currentTimeMillis()-annoyStartTime >= 200000 && release){//three minutes 20 seconds (end of rickroll if coming off max tempo)
		if(!player.isRunning()&&release){
			dispose();
			JOptionPane.showMessageDialog(null, "Thanks for \"using\" AardvarkX!", "You've reached the end!", JOptionPane.PLAIN_MESSAGE, new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("icon.png")));
			System.exit(0);//let them go after three minutes of annoyance
		}
	}
	private void restore(){
		setExtendedState(NORMAL);
		setVisible(true);
		toFront();
		requestFocus();
	}
}
