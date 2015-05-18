import javax.swing.JLabel;

public class Phrase extends JLabel{
	private static final long serialVersionUID = 1L;
	private String[] phrases;
	private String cur;
	private int pos;
	private String text;
	public Phrase (String [] phrases){
		this.phrases = phrases;
		text = new String();
		reset();
	}
	public void type(){
		if (pos >= cur.length())
			reset();
		text += cur.charAt(pos++);
		setText("<html>"+text+"</html>");
	}
	private void reset(){
		cur = phrases[(int) (Math.random()*phrases.length)];
		cur = cur.replace("user", System.getProperty("user.name"));
		pos = 0;
		text+="<br>";
		setText("<html>"+text+"</html>");
	}
}
