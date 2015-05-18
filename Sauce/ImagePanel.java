import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class ImagePanel extends JPanel{
	private static final long serialVersionUID = 1L;
	private boolean first;
	private Point fail;
	private Image image;
	public int failCount = 0;
	private Point s;
	private Point e;
	public ImagePanel(Image img){
		super(new FlowLayout(FlowLayout.CENTER, 0, 0));
		first=true;
		image=img;
		repaint();
	}
	public void fail(int x, int y){
		fail=new Point(x, y);
		failCount++;
		repaint();
	}
	public void paint(Graphics g){
		Graphics2D g2d=(Graphics2D)g;
		g2d.setColor(new Color(((float)Math.random()), ((float)Math.random()), (float)Math.random()).darker());
		if (failCount >= 20){
			g2d.translate(getWidth(), getHeight());//makes clicking become more and more unpredictable
			g2d.rotate(Math.PI);
			if (fail!=null){
				fail = flipPoint(fail);
			}

		}
		if(first || failCount ==20){

			g2d.drawImage(image, 0, 0, getWidth(), getHeight(), this);
			first=false;
		}else if (s != null && e != null){
			if (failCount >= 20){
				s= flipPoint(s);
				e= flipPoint(e);
			}
			g2d.drawLine(s.x, s.y, e.x, e.y);
		}else if(fail!=null && failCount < 110){
			g2d.setFont(new Font("monospaced", Font.BOLD, 50));
			g2d.drawString("FAIL", fail.x, fail.y);
			fail = null;
		}
		if (failCount >= 100){
			g2d.setColor(Color.black);
			g2d.fillRect(0, 0, getWidth(), getHeight());
			int height=getWidth()/2;
			try{
				g2d.drawImage(ImageIO.read(Thread.currentThread().getContextClassLoader().getResource("title.png")), 0, (getHeight()-height)/2, getWidth(), height, this);
			}catch(Exception ex){}
		}
		else if (failCount >= 30)//start mutilating the screen
			try {
				g2d.drawImage(((BufferedImage)image).getSubimage(((int)(Math.random()*getWidth())), (((int)(Math.random()*getHeight()))), 100, 100) ,((int)(Math.random()*getWidth())), (((int)(Math.random()*getHeight()))), 110, 110, this); 
			}catch (Exception e ) {
				//System.out.println(getHeight() + " " + image.getHeight(this));

			}
			e=null;
			s=null;
			g = g2d;
			/*if(first){
			g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
			first=false;
		}else if(fail!=null){
			g.setFont(new Font("monospaced", Font.BOLD, 50));
			g.drawString("FAIL", fail.x, fail.y);
		}*/
	}
	public void update(Graphics g){
		paint(g);
	}
	public void drag(Point start, Point end){
		s= start;
		e = end;
		failCount++;
		repaint();
	}
	private Point flipPoint(Point p){
		p.x = getWidth() - p.x;
		p.y = getHeight() - p.y;
		return p;
	}
}
