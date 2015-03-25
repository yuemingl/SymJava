package symjava.examples;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

public class TestLibsvm {

	public static double evaluate(double[] features, svm_model model) {
		svm_node[] nodes = new svm_node[features.length - 1];
		for (int i = 1; i < features.length; i++) {
			svm_node node = new svm_node();
			node.index = i;
			node.value = features[i];

			nodes[i - 1] = node;
		}

		int totalClasses = 2;
		int[] labels = new int[totalClasses];
		svm.svm_get_labels(model, labels);

		double[] prob_estimates = new double[totalClasses];
		double v = svm.svm_predict_probability(model, nodes, prob_estimates);

		if(features[0]!=v) {
			System.out.print("Actual >>>" + features[0] + " " + v + "<<< Prediction");

			
		for (int i = 0; i < totalClasses; i++) {
			System.out.print("(" + labels[i] + ":" + prob_estimates[i] + ")");
		}
		System.out.println();
		}
		return v;
	}

	private static svm_model svmTrain(double[][] train) {
		svm_problem prob = new svm_problem();
		int dataCount = train.length;
		prob.y = new double[dataCount];
		prob.l = dataCount;
		prob.x = new svm_node[dataCount][];

		for (int i = 0; i < dataCount; i++) {
			double[] features = train[i];
			prob.x[i] = new svm_node[features.length - 1];
			for (int j = 1; j < features.length; j++) {
				svm_node node = new svm_node();
				node.index = j;
				node.value = features[j];
				prob.x[i][j - 1] = node;
			}
			prob.y[i] = features[0];
		}

		svm_parameter param = new svm_parameter();
		param.probability = 1;
		param.gamma = 0.5;
		param.nu = 0.5;
		param.C = 1;
		param.svm_type = svm_parameter.C_SVC;
		param.kernel_type = svm_parameter.LINEAR;
		param.cache_size = 20000;
		param.eps = 0.001;

		svm_model model = svm.svm_train(prob, param);

		return model;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double[][] train = new double[1000][];
		double[][] test = new double[10][];

		for (int i = 0; i < train.length / 2; i++) { // 50% positive
			double x = 0.5 + 0.5 * Math.random();
			double y = 0.5 + 0.5 * Math.random();
			double[] vals = { 1, x, y };
			train[i] = vals;
		}
		for (int i = train.length / 2; i < train.length; i++) { // 50% negative
			double x = 0.5 * Math.random();
			double y = 0.5 * Math.random();
			double[] vals = { 0, x, y };
			train[i] = vals;
		}
		for (int i = 0; i < test.length; i++) {
			double x = Math.random();
			double y = Math.random();
			double feature = 0.0;
			if(x + y > Math.sqrt(2)/2)
				feature = 1.0;
			double[] vals = { feature, x, y };
			test[i] = vals;
		}

		svm_model model = svmTrain(train);
		for (int i = 0; i < test.length; i++)
			evaluate(test[i], model);
	}

}
