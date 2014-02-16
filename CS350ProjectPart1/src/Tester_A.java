import java.awt.Color;

import acg.architecture.view.loader.LayoutBundle;


public class Tester_A {
	public static void main(String [] args)
	{
		GlyphLoader g = new GlyphLoader("test_loader.txt");
		LayoutBundle temp = g.load();
		System.out.println(temp);
		System.out.println(Color.decode("#abcdef"));
		
	}
}
