package io.lambdacloud.examples;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JPanel;

import io.lambdacloud.core.CloudConfig;
import io.lambdacloud.core.CloudFunc;
import io.lambdacloud.core.CloudSD;
import io.lambdacloud.core.lang.LCReturn;
import io.lambdacloud.core.lang.LCVar;
import io.lambdacloud.examples.ExampleMonteCarlo.MyFrame;
import io.lambdacloud.examples.ExampleMonteCarlo.MyFrame.MyPanel;
import io.lambdacloud.test.CompileUtils;
import io.lambdacloud.symjava.bytecode.BytecodeFunc;
import io.lambdacloud.symjava.bytecode.BytecodeFuncImpFEM;
import io.lambdacloud.symjava.relational.Ge;
import io.lambdacloud.symjava.relational.Le;
import io.lambdacloud.symjava.symbolic.Expr;

public class ExampleFEM {
	
	public static class MyFrame extends JFrame {
		private static final long serialVersionUID = 1L;
		public MyFrame() {
			setTitle("Finite Element Assembly Demo - Random Mesh");
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			double[] data = new double[50];
			for(int i=0; i<data.length; i++) {
				data[i] = Math.random()*420;
			}
			add(new MyPanel(data));
			setSize(550, 550);
			setLocationRelativeTo(null);
		}
		
		public static class MyPanel extends JPanel {
			private static final long serialVersionUID = 1L;
			double[] data;
			public MyPanel(double[] data) {
				this.data = data;
			}
			private void doDrawing(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				g2d.setStroke(new BasicStroke(2));
				Dimension size = getSize();
				Insets insets = getInsets();
				int w = size.width - insets.left - insets.right;
				int h = size.height - insets.top - insets.bottom;
				System.out.println(w+", "+h);
				for (int i = 0; i < data.length; i++) {
					int x1 = (int)data[i];
					g2d.drawLine(x1, 0, x1, h);
					g2d.drawLine(0, x1, w, x1);
				}
			}
	
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				doDrawing(g);
			}
		}
	}
	
	public static void main(String[] args) {
		FEMImp1(args);
		
//		MyFrame frame = new MyFrame();
//		frame.setVisible(true);
	}
	
	public static void FEMImp1(String[] args) {
		System.out.println("Current working dir="+System.getProperty("user.dir"));
		
		String configFile = "job_google.conf";
		int nData = 1000;
		int nEle = 100000;
		boolean isAsync = false;
		if(args.length < 3) {
			System.out.println("args: configFile nData nEle isAsync");
		} else {
			if(args.length >= 3) {
				configFile = args[0];
				nData = Integer.valueOf(args[1]);
				nEle = Integer.valueOf(args[2]);
			}
			if(args.length == 4) {
				isAsync = Boolean.valueOf(args[3]);
			}
		}
		
		CloudConfig config = CloudConfig.setGlobalConfig(configFile);
		
		double[] data = new double[nData];
		for(int i=0; i<data.length; i++)
			data[i] = i;
		data[0] = nEle;
		
		CloudSD input = new CloudSD("input").init(data);
		CloudSD output = new CloudSD("output").resize(1);
		
		long start, end, totalTime;
		long start2, end2, applyTime;
		start = System.currentTimeMillis();
		start2 = System.currentTimeMillis();
		for(int i=0; i<CloudConfig.getGlobalConfig().getNumClients(); i++) {
			config.setCurrentClient(config.getClientByIndex(i));
			CloudFunc f = new CloudFunc(BytecodeFuncImpFEM.class);
			//f.useCloudConfig(config); //No need to set config. We use global configuration here
			
			f.isAsyncApply(isAsync);
			f.apply(output, input);
		}
		end2 = System.currentTimeMillis();
		applyTime = end2 - start2;

		for(int i=0; i<CloudConfig.getGlobalConfig().getNumClients(); i++) {
			if(output.fetch()) {
				for(double d : output.getData()) {
					System.out.println("output="+d);
				}
			}
		}
		end = System.currentTimeMillis();
		totalTime = end-start;
		System.out.println("apply time="+applyTime+" getDataTime="+(totalTime-applyTime)+" totalTime="+totalTime);
	}
}
