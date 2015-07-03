package lambdacloud.examples;

import static symjava.math.SymMath.log;
import static symjava.math.SymMath.random;
import static symjava.math.SymMath.sin;
import static symjava.math.SymMath.sqrt;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JPanel;

import lambdacloud.core.CloudConfig;
import lambdacloud.core.CloudFunc;
import lambdacloud.core.CloudSD;
import lambdacloud.core.lang.LCBuilder;
import lambdacloud.core.lang.LCDouble;
import lambdacloud.core.lang.LCIf;
import lambdacloud.core.lang.LCInt;
import lambdacloud.core.lang.LCLoop;
import lambdacloud.core.lang.LCReturn;
import lambdacloud.core.lang.LCVar;
import lambdacloud.test.CompileUtils;
import symjava.bytecode.BytecodeFunc;
import symjava.domains.Domain;
import symjava.domains.Domain2D;
import symjava.relational.Ge;
import symjava.relational.Le;
import symjava.relational.Lt;
import symjava.symbolic.Expr;
import symjava.symbolic.Integrate;

public class ExampleMonteCarlo {
	
	/**
	 * Monte Carlo integration on two annuluses.
	 * 
	 * Domain: { (x,y) | a^2 <= (x-1/2)^2 + (y-1/2)^2 <= b^2 or c^2 <= (x-1/2)^2 + (y-1/2)^2 <= d^2 },
	 * Integrand: sin(sqrt(log(x+y+1))),
	 * where a,b,c,d=0.13,0.25,0.38,0.5
	 * 
	 */
	public static void MonteCarloTwoAnnulusImp1(int N, String configFile, boolean isAsync) {
		CloudConfig config = CloudConfig.instance(configFile);
		LCBuilder task = new LCBuilder(config);
		
		LCVar x = task.declareDouble("x"); 
		LCVar y = task.declareDouble("y");
		LCVar a = task.declareDouble("a");
		LCVar b = task.declareDouble("b");
		LCVar c = task.declareDouble("c");
		LCVar d = task.declareDouble("d");
		
		LCInt i = task.declareInt("i");
		LCVar sum = task.declareDouble("sum");
		LCVar counter = task.declareInt("counter");
		
		LCLoop loop = task.For(i.assign(0), 
				Lt.apply(i, N), i.inc());    // for(i=0; i<N; i++) {
		
		loop.appendBody(x.assign(random())); // x = random(); //0.0~1.0
		loop.appendBody(y.assign(random())); // y = random(); //0.0~1.0
		
		Expr eq = (x-0.5)*(x-0.5) + (y-0.5)*(y-0.5);
		Expr domain = (                      // if( a^2 <= (x-1/2)^2 + (y-1/2)^2 <= b^2 or c^2 <= (x-1/2)^2 + (y-1/2)^2 <= d^2 ) {
				Ge.apply(eq, a*a) & Le.apply(eq, b*b) ) | 
				( Ge.apply(eq, c*c) & Le.apply(eq, d*d) 
				);
		LCIf ifBranch = new LCIf(domain);
		ifBranch.appendTrue(sum.assign(sum + sin(sqrt(log(x+y+1))))); // sum = sum + sin(sqrt(log(x+y+1))))
		ifBranch.appendTrue(counter.assign(counter+1));               // counter = counter + 1
		// } //end if
		loop.appendBody(ifBranch);
		//} end for

		double squareArea = 1.0;
		Expr area = (counter/N)*squareArea; // area of domain
		task.Return((sum/counter)*area); 
		
		CloudSD params = new CloudSD(config,"params").init(new double[]{0.13,0.25,0.38,0.5});
		CloudSD[] result = new CloudSD[config.getTotalNumClients()];
		
		long start, end, totalTime;
		long start2, end2, applyTime;
		start = System.currentTimeMillis();
		start2 = System.currentTimeMillis();
		for(int j=0; j<config.getTotalNumClients(); j++) {
			config.useClient(config.getClientByIndex(j));
			
			CloudFunc func = task.build(new LCVar[]{a,b,c,d});
			func.isAsyncApply(isAsync);
			result[j] = new CloudSD(config, "result"+j).resize(1);
			
			func.apply(result[j], params);
		}
		end2 = System.currentTimeMillis();
		applyTime = end2 - start2;
		
		double rltSum = 0.0;
		for(int j=0; j<config.getTotalNumClients(); j++) {
			config.useClient(config.getClientByIndex(j));
			result[j].fetchToLocal();
			double rlt = result[j].getData(0);
			rltSum += rlt;
			System.out.println(rlt);
		}
		end = System.currentTimeMillis();
		totalTime = end-start;
		System.out.println("apply time="+applyTime+" getDataTime="+(totalTime-applyTime)+" totalTime="+totalTime);
		
		System.out.println("final result="+rltSum/config.getTotalNumClients());
	}
	
	public static void MonteCarloTowAnnulusVerifiy() {
		double xMin=0, xMax=1, xStep=0.0001;
		double yMin=0, yMax=1, yStep=0.0001;
		double sum = 0.0;
		double a=0.13, b=0.25, c=0.38, d=0.5;
		for(double x=xMin; x<=xMax; x+=xStep) {
			for(double y=yMin; y<=yMax; y+=yStep) {
				double disk = (x-0.5)*(x-0.5) + (y-0.5)*(y-0.5);
				if((a*a <= disk && disk <= b*b) || 
				   (c*c <= disk && disk <= d*d )
				  ) {
					sum += Math.sin(Math.sqrt(Math.log(x+y+1)))*xStep*yStep;
				}
			}
		}
		System.out.println("verify="+sum);
	}
	
	
	/**
	 * TODO: Implement bytecodeGen() for class Integrate
	 * Monte Carlo integration on an annulus.
	 * 
	 * I = \int_{\Omega} sin(sqrt(log(x+y+1))) dxdy
	 * where 
	 * \Omega= { (x,y), where
	 *             a^2 <= (x-1/2)^2 + (y-1/2)^2 <= b^2 or
	 *             c^2 <= (x-1/2)^2 + (y-1/2)^2 <= d^2 
	 *         }
	 * we choose 
	 * a=0.13, b=0.25, c=0.38, d=0.5
	 */
	public static void MonteCarloTwoAnnulusImp2() {
		LCBuilder task = new LCBuilder("server");
		LCVar x = task.declareDouble("x"); 
		LCVar y = task.declareDouble("y");
		LCVar a = task.declareDouble("a");
		LCVar b = task.declareDouble("b");
		LCVar c = task.declareDouble("c");
		LCVar d = task.declareDouble("d");
		
		Expr eq = (x-0.5)*(x-0.5) + (y-0.5)*(y-0.5);
		Domain omega = new Domain2D("\\Omega", x, y)
			.setConstraint(
					( Ge.apply(eq, a*a) & Le.apply(eq, b*b)) |
					( Ge.apply(eq, c*c) & Le.apply(eq, d*d) ))
			.setBound(x, 0, 1)
			.setBound(y, 0, 1);
		
		Expr I = Integrate.apply(sin(sqrt(log(x+y+1)) ), omega);
		System.out.println(I);
		
		CloudFunc mc = new CloudFunc("MonteCarlo1", new LCVar[]{a, b, c, d}, I);
		CloudSD result = new CloudSD("result");
		CloudSD inputParams = new CloudSD("params");
		inputParams.init(new double[]{0.13, 0.25, 0.38, 0.5});
		mc.apply(result, inputParams);
		
		result.fetchToLocal();
		System.out.println(result.getData(0));
	}
	
	public static class MyFrame extends JFrame {
		private static final long serialVersionUID = 1L;
		public MyFrame() {
			LCVar x = LCVar.getDouble("x"); 
			LCVar y = LCVar.getDouble("y");
			LCVar a = LCVar.getDouble("a");
			LCVar b = LCVar.getDouble("b");
			LCVar c = LCVar.getDouble("c");
			LCVar d = LCVar.getDouble("d");
			Expr eq = (x-0.5)*(x-0.5) + (y-0.5)*(y-0.5);
			Expr domain = (                      // if( a^2 <= (x-1/2)^2 + (y-1/2)^2 <= b^2 or c^2 <= (x-1/2)^2 + (y-1/2)^2 <= d^2 ) {
					Ge.apply(eq, a*a) & Le.apply(eq, b*b) ) | 
					( Ge.apply(eq, c*c) & Le.apply(eq, d*d) 
					);
			System.out.println(domain);
			
			setTitle("Mento Carlo Demo");
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			
			//LCVar ret = new LCDouble("ret");
			//ret.assign(domain);
			add(new MyPanel(CompileUtils.compile(new LCReturn(domain+0.00001), new LCVar[]{x,y,a,b,c,d})));
			
			setSize(350, 350);
			setLocationRelativeTo(null);
		}
		
		public static class MyPanel extends JPanel {
			private static final long serialVersionUID = 1L;
			BytecodeFunc func;
			public MyPanel(BytecodeFunc func) {
				this.func = func;
			}
			private void doDrawing(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				g2d.setStroke(new BasicStroke(3));
//				Dimension size = getSize();
//				Insets insets = getInsets();
//				int w = size.width - insets.left - insets.right;
//				int h = size.height - insets.top - insets.bottom;
				
				for (int i = 0; i < 10000; i++) {
					double x = Math.random();
					double y = Math.random();
					double flag = func.apply(x,y,0.13,0.25,0.38,0.5);
					System.out.println(flag);
					if(flag > 0.5) {
						g2d.setColor(Color.red);
					} else {
						g2d.setColor(Color.blue);
					}
					int xx = (int)(320*x);
					int yy = (int)(320*y);
					g2d.drawLine(xx, yy, xx, yy);
				}
			}
	
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				doDrawing(g);
			}
		}
	}
	
	public static void main(String[] args) {
		System.out.println("Current working dir="+System.getProperty("user.dir"));
		int N = 1000000;
		String configFile = "job_google.conf";
		boolean isAsync = false;
		if(args.length == 3) {
			N = Integer.valueOf(args[0]);
			configFile = args[1];
			isAsync = Boolean.valueOf(args[2]);
		}
		MonteCarloTwoAnnulusImp1(N, configFile, isAsync);
		//MonteCarloTwoAnnulusImp2();
		MonteCarloTowAnnulusVerifiy();
		
//		MyFrame frame = new MyFrame();
//		frame.setVisible(true);
	}

}
