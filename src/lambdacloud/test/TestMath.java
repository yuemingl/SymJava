package lambdacloud.test;

import java.util.HashMap;
import java.util.Map;

import lambdacloud.core.CloudConfig;
import lambdacloud.core.CloudSD;
import lambdacloud.core.Session;
import lambdacloud.core.graph.GraphBuilder;
import lambdacloud.core.graph.Node;
import symjava.symbolic.Matrix;
import static symjava.math.SymMath.*;

public class TestMath {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Matrix A = new Matrix("A",4,4);
		Map<String, double[]> dict = new HashMap<String, double[]>();
		/*
		1 2 3 4
		1 2 1 3
		1 2 2 1
		2 3 1 4
		*/
		//matrix stored in cloumnwise
		dict.put(A.toString(), new double[]{1,1,1,2,2,2,2,3,3,1,2,1,4,3,1,4});
		
		CloudConfig.setGlobalTarget("job_local.conf");
		//Node n = GraphBuilder.build(A);
		Node n = GraphBuilder.build(sin(A));
		Session sess1 = new Session();
		CloudSD rlt = sess1.runVec(n, dict);
		System.out.println("------------");
		for(double d : rlt.getData())
			System.out.println(d);
	}

}
