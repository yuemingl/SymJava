package symjava.bytecode;

import static edu.uta.futureye.function.FMath.*;

import java.util.HashMap;
import java.util.Map;

import edu.uta.futureye.bytecode.CompiledFunc;
import edu.uta.futureye.core.Element;
import edu.uta.futureye.core.Node;
import edu.uta.futureye.function.basic.FX;
import edu.uta.futureye.function.intf.MathFunc;
import edu.uta.futureye.function.operator.FOIntegrate;
import edu.uta.futureye.lib.shapefun.SFLinearLocal2D;
import edu.uta.futureye.lib.shapefun.SFLinearLocal2DRS;
import edu.uta.futureye.util.container.NodeList;

public class BytecodeFuncImpFEM implements BytecodeFunc {

		public static double eps = 1e-5;
		static double[] triW = {
			0.06296959,
		    0.06619708,
		    0.06296959,
		    0.06619708,
		    0.06296959,
		    0.06619708,
		    0.11250000
		};
		static double[] triR = {
		        0.10128651,
		        0.47014206,
		        0.79742699,
		        0.47014206,
		        0.10128651,
		        0.05971587,
		        0.33333333
		    };
		static double[] triS = {
				0.10128651,
				0.05971587,
				0.10128651,
				0.47014206,
				0.79742699,
				0.47014206,
				0.33333333
			};
		
		public static void check(String info, double d1, double d2) {
			if(Math.abs(d1-d2) < eps) {
				System.out.println("pass");
			} else {
				System.out.println("!!!FAIL!!!   "+d1+"!="+d2+" "+info);
			}
		}
		public static double intOnTriangleRefElement(CompiledFunc integrand, double[] params, int paramPos, int order) {
			double rlt = 0.0;
			if(order == 2) {
				params[paramPos] = 0.333333333333333;
				params[paramPos+1] = 0.333333333333333;
				params[paramPos+2] = 0.333333333333333;
				rlt = 0.5*integrand.apply(params);
			} else if(order == 3) {
				params[paramPos] = 0.5; params[paramPos+1] = 0.5; params[paramPos+2] = 0.0; 
				double pv1 = integrand.apply(params);
				params[paramPos] = 0.0; params[paramPos+1] = 0.5; params[paramPos+2] = 0.5; 
				double pv2 = integrand.apply(params);
				params[paramPos] = 0.5; params[paramPos+1] = 0.0; params[paramPos+2] = 0.5; 
				double pv3 = integrand.apply(params);
				rlt = 0.5*0.333333333333333*(pv1 + pv2 + pv3);
			} else if(order == 4) {
				double w123 = 25.0/48.0;
				double w4 = -27.0/48.0;
				
				params[paramPos] = 0.6; params[paramPos+1] = 0.2; params[paramPos+2] = 0.2; 
				double pv1 = integrand.apply(params);
				params[paramPos] = 0.2; params[paramPos+1] = 0.6; params[paramPos+2] = 0.2; 
				double pv2 = integrand.apply(params);
				params[paramPos] = 0.2; params[paramPos+1] = 0.2; params[paramPos+2] = 0.6; 
				double pv3 = integrand.apply(params);
				params[paramPos] = 0.333333333333333; params[paramPos+1] = 0.333333333333333; params[paramPos+2] = 0.333333333333333; 
				double pv4 = 0.5*integrand.apply(params);
				
				rlt = 0.5*w123*(pv1 + pv2 + pv3) + w4*pv4;
			} else if(order == 5) {
				for(int i=0;i<7;i++) {
					params[paramPos]   = triR[i]; 
					params[paramPos+1] = triS[i]; 
					params[paramPos+2] = 1.0-triR[i]-triS[i];
					rlt += triW[i]*integrand.apply(params);
				}
			}
			return rlt;
		}

		public static void shapeFuncTest1() {
			SFLinearLocal2D[] shapeFun = new SFLinearLocal2D[3];
			shapeFun[0] = new SFLinearLocal2D(1);
			shapeFun[1] = new SFLinearLocal2D(2);
			shapeFun[2] = new SFLinearLocal2D(3);
			System.out.println(shapeFun[0]);
			System.out.println(shapeFun[1]);
			System.out.println(shapeFun[2]);
		}
		public static void shapeFuncTest2() {
			SFLinearLocal2DRS[] shapeFun = new SFLinearLocal2DRS[3];
			shapeFun[0] = new SFLinearLocal2DRS(1);
			shapeFun[1] = new SFLinearLocal2DRS(2);
			shapeFun[2] = new SFLinearLocal2DRS(3);
			System.out.println(shapeFun[0]);
			System.out.println(shapeFun[1]);
			System.out.println(shapeFun[2]);
		}

		
		public static void testIntegration() {
			NodeList nodes = new NodeList();
			/**
			 * |\
			 * | \
			 * ----
			 */
			nodes.add(new Node(1, 0.0,0.0));
			nodes.add(new Node(2, 0.2,0.0));
			nodes.add(new Node(3, 0.0,0.2));
			Element e = new Element(nodes);
			//Construct a function with coordinate of points as parameters
			String[] argsOrder = new String[]{"x1","x2","x3","y1","y2","y3","r","s","t"};
			FX x1 = new FX("x1");
			FX x2 = new FX("x2");
			FX x3 = new FX("x3");
			FX y1 = new FX("y1");
			FX y2 = new FX("y2");
			FX y3 = new FX("y3");
			MathFunc fx = x1*r + x2*s + x3*t;
			MathFunc fy = y1*r + y2*s + y3*t;
			MathFunc f = fx + fy;
			CompiledFunc cf = f.compile(argsOrder);
			double[] params = new double[9];
			double[] coords = e.getNodeCoords();
			System.arraycopy(coords, 0, params, 0, coords.length);
			
			MathFunc fx2 = coords[0]*r + coords[1]*s + coords[2]*t;
			MathFunc fy2 = coords[3]*r + coords[4]*s + coords[5]*t;
			MathFunc f2 = fx2 + fy2;
			
			for(int order=2; order<=5; order++) {
				check("CompiledFunc: order="+order, intOnTriangleRefElement(cf, params, coords.length, order),0.06666666);
				check("MathFunc: order="+order, FOIntegrate.intOnTriangleRefElement(f2, order),0.06666666);
			}
		}
		
		public static void testLaplace() {
			NodeList nodes = new NodeList();
			/**
			 * |\
			 * | \
			 * ----
			 */
			nodes.add(new Node(1, 1.0,1.0));
			nodes.add(new Node(2, 2.0,1.0));
			nodes.add(new Node(3, 1.0,2.0));
			Element e = new Element(nodes);

			e.updateJacobin();
			MathFunc jac = e.getJacobin();
			System.out.println(jac.compile().apply());
			System.out.println(jac.apply());
			
			SFLinearLocal2DRS[] sf = new SFLinearLocal2DRS[3];
			sf[0] = new SFLinearLocal2DRS(1);
			sf[1] = new SFLinearLocal2DRS(2);
			sf[2] = new SFLinearLocal2DRS(3);
			for(int i=0; i<3; i++)
				sf[i].assignElement(e);

			//Construct a function with the coordinate of points in an element as parameters
			String[] argsOrder = new String[]{"x1","x2","x3","y1","y2","y3","r","s","t"};
			FX x1 = new FX("x1");
			FX x2 = new FX("x2");
			FX x3 = new FX("x3");
			FX y1 = new FX("y1");
			FX y2 = new FX("y2");
			FX y3 = new FX("y3");
			MathFunc fx = x1*r + x2*s + x3*t;
			MathFunc fy = y1*r + y2*s + y3*t;
			
//			MathFunc ff = fx*fy;
//			MathFunc f = sin(PI*x)*sin(PI*y)*pow(E, -0.2); 
			MathFunc f = 4*pow(x,4)*pow(y,4);
//			MathFunc f = 4*(x*x*x*x + y*y*y*y);
			
			Map<String, MathFunc> map = new HashMap<String, MathFunc>();
			map.put("x", fx);
			map.put("y", fy);
			MathFunc ff = f.compose(map);
			
			MathFunc[][] lhs = new MathFunc[3][3];
			MathFunc[] rhs = new MathFunc[3];
			for(int j=0; j<3; j++) {
				MathFunc v = sf[j];
				for(int i=0; i<3; i++) {
					MathFunc u = sf[i];
					//lhs[j][i] = (u.diff("x")*v.diff("x")+u.diff("y")*v.diff("y") + u*v)*jac;
					lhs[j][i] = (grad(u,"x","y").dot(grad(v,"x","y")) + u*v)*jac;
				}
				rhs[j] = v*ff*jac;
				rhs[j].setName("RHS"+j);
			}
			
			CompiledFunc[][] clhs = new CompiledFunc[3][3];
			CompiledFunc[] crhs = new CompiledFunc[3];
			for(int j=0; j<3; j++) {
				for(int i=0; i<3; i++) {
					clhs[j][i] = lhs[j][i].compile(argsOrder);
				}
				crhs[j] = rhs[j].compile(argsOrder);
			}
			
			double[][] A = new double[3][3];
			double[] b = new double[3];
			double[] params = new double[9];
			double[] coords = e.getNodeCoords();
			System.arraycopy(coords, 0, params, 0, coords.length);

			long start = System.currentTimeMillis();
			for(int k=0; k<1000000; k++) {
				for(int j=0; j<3; j++) {
					for(int i=0; i<3; i++) {
						A[j][i] += intOnTriangleRefElement(clhs[j][i], params, coords.length, 3);
					}
					b[j] += intOnTriangleRefElement(crhs[j], params, coords.length, 3);
				}
			}
			System.out.println(System.currentTimeMillis()-start);
			
			for(int j=0; j<3; j++) {
				for(int i=0; i<3; i++) {
					System.out.print(A[j][i]+" ");
				}
				System.out.println();
			}
			for(int j=0; j<3; j++) {
				System.out.println(b[j]);
			}
			/*
			 * 
			 * 1.4999999999999984 0.0 -1.4999999999999984 
				0.0 0.9999999999999989 -0.9999999999999989 
				-1.4999999999999984 -0.9999999999999989 2.4999999999999973 
				0.8749999999999991
				0.8749999999999991
				-1.7499999999999982
				Correct: (add setArgIdx() in FComposite)
				1.0833333333333321 -0.45833333333333287 -0.45833333333333287 
				-0.45833333333333287 0.5833333333333327 0.04166666666666662 
				-0.45833333333333287 0.04166666666666662 0.5833333333333327 
				0.24999999999999972
				0.31249999999999967
				0.31249999999999967			
			 */
		}

		public static void main(String[] args) {
			shapeFuncTest1();
			shapeFuncTest2();
			testIntegration();
			testLaplace();
		}
		
		@Override
		public double apply(double... args) {
			testLaplace();
			return 0;
		}	
}
