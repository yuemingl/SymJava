package symjava.examples;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import symjava.matrix.ExprVector;
import symjava.relational.Eq;
import symjava.symbolic.*;
import symjava.symbolic.utils.AddList;
import symjava.symbolic.utils.Utils;
import static symjava.math.SymMath.*;
import static symjava.symbolic.Symbol.*;

/**
 * 
 http://stackoverflow.com/questions/1072097/pointers-to-some-good-svm-tutorial
 * 
 * The standard recommendation for a tutorial in SVMs is A Tutorial on Support
 * Vector Machines for Pattern Recognition by Christopher Burges. Another good
 * place to learn about SVMs is the Machine Learning Course at Stanford (SVMs
 * are covered in lectures 6-8). Both these are quite theoretical and heavy on
 * the maths.
 * 
 * As for source code; SVMLight, libsvm and TinySVM are all open-source, but the
 * code is not very easy to follow. I haven't looked at each of them very
 * closely, but the source for TinySVM is probably the is easiest to understand.
 * There is also a pseudo-code implementation of the SMO algorithm in this
 * paper.
 * 
 * 
 */
public class SVM extends JFrame {
	private static final long serialVersionUID = 1L;
	public SVM(double[][] data, double[] line) {
		setTitle("SymJava SVM Demo");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		add(new MyPanel(data, line));
		setSize(350, 350);
		setLocationRelativeTo(null);
	}

	public static class MyPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		double[][] data;
		double[] line;
		public MyPanel(double[][] data, double[] line) {
			this.data = data;
			this.line = line;
		}
		private void doDrawing(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.setStroke(new BasicStroke(5));
			Dimension size = getSize();
			Insets insets = getInsets();
			int w = size.width - insets.left - insets.right;
			int h = size.height - insets.top - insets.bottom;
			g2d.setColor(Color.blue);
			for (int i = 0; i < data.length/2; i++) {
				int x = (int)(data[i][0]*w);
				int y = (int)(data[i][1]*h);
				y = h - y;
				//System.out.println(x+", "+y);
				g2d.drawLine(x, y, x, y);
			}
			g2d.setColor(Color.red);
			for (int i = data.length/2; i < data.length; i++) {
				int x = (int)(data[i][0]*w);
				int y = (int)(data[i][1]*h);
				y = h - y;
				//System.out.println(x+", "+y);
				g2d.drawLine(x, y, x, y);
			}
			
			//line ax+by+c=0 => y=(-c-ax)/b
			int N = 1000;
			double a = line[0];
			double b = line[1];
			double c = line[2];
			System.out.println("Distance to (0,0)="+c/Math.sqrt(a*a+b*b));
			double step = 1.0/N;
			for(double xx=0.0; xx<1.0; xx+=step) {
				double yy = (-c-a*xx)/b;
				int x = (int)(xx*w);
				int y = (int)(yy*h);
				y = h - y;
				//System.out.println(x+", "+y);
				g2d.drawLine(x, y, x, y);
				
			}
			//System.out.println(a+","+b+","+c);
			
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			doDrawing(g);
		}
	}

	public static void main(String[] args) {
//		 double[][] data = {
//				 {0.5, 0.2, -1},
//				 {0.5, 0.5, 1}
//		 };
//		 double[] sol  = solve(data, new double[]{0.0,0.0,0.0});

		//?
		//http://axon.cs.byu.edu/Dan/478/misc/SVM.example.pdf
//		 double[][] data = {
//		 { 3.0, 1.0, 1},
//		 { 3.0, -1.0, 1},
//		 { 6.0, 1.0, 1},
//		 { 6.0, -1.0, 1},
//		 { 1.0, 0.0, -1},
//		 { 0.0, 1.0, -1},
//		 { 0.0, -1.0, -1},
//		 {-1.0, 0.0, -1}
//		 };
//		 // w=(1,0) b=-2
//		double[] sol = solve(data, new double[]{0.1,0.0,-1});

		double[][] data = new double[16][3];
		for (int i = 0; i < data.length/2; i++) {
			data[i][0] = Math.random();
			data[i][1] = (0.9 - data[i][0])*Math.random();
			data[i][2] = 1.0;
		}
		for (int i = data.length/2; i < data.length; i++) {
			data[i][0] = Math.random();
			data[i][1] = (1.1-data[i][0])+(data[i][0]-0.1)*Math.random();
			data[i][2] = -1.0;
		}
		double[] init = new double[] { -1.0, -1.0, 0.1 };
		double[] sol = solve(data, init);
		
		
		SVM svm = new SVM(data, sol);
		svm.setVisible(true);
		
//		SwingUtilities.invokeLater(new Runnable() {
//			@Override
//			public void run() {
//				SVM svm = new SVM();
//				svm.setVisible(true);
//			}
//		});

	}

public static double[] solve(double[][] data, double[] init) {
	int nFeatures = data[0].length - 1;
	ExprVector w = new ExprVector("w", 1, nFeatures);
	
	Expr sumf = 0.5*dot(w, w);
	System.out.println(sumf);
	
	ExprVector lmd = new ExprVector("\\lambda", 1, data.length);
	ExprVector c = new ExprVector("c", 1, data.length); //slack variable
	AddList addList = new AddList();
	for (int i = 0; i < data.length; i++) {
		double[] xi = data[i];
		double yi = data[i][2];
		addList.add(lmd.get(i) * (yi * (dot(xi, w) + b) - 1 - c.get(i)*c.get(i)));
	}
	Expr sumg = addList.toExpr();
	System.out.println(sumg);
	
	Expr L = sumf - sumg;
	Expr[] freeVars = Utils.joinArrays(w.getData(), new Expr[]{b}, 	lmd.getData(), c.getData());
	
	Eq eq = new Eq(L, C0, freeVars);
	double[] guess = new double[freeVars.length];
	System.arraycopy(init, 0, guess, 0, init.length);
	
	NewtonOptimization.solve(eq, guess, 10000, 1e-6, false);
	return guess;
}

}
