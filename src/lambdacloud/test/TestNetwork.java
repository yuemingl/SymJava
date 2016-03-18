package lambdacloud.test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import lambdacloud.core.CloudConfig;
import lambdacloud.core.CloudSD;

public class TestNetwork {

	public static void main(String[] args) {
		OutputStream output;
		try {
			output = new FileOutputStream("/dev/null");
			PrintStream nullOut = new PrintStream(output);
			System.setErr(nullOut);		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int N = 100000;
		if(args.length >= 1) {
			N = Integer.parseInt(args[0]);
		}
		CloudConfig.setGlobalConfig("job1.conf");
		for(int i=0; i<CloudConfig.getGlobalConfig().getNumClients(); i++) {
			test(i, N);
		}
	}
	public static void test(int serverIndex, int N) {
		CloudConfig config = CloudConfig.getGlobalConfig();
		config.setCurrentClient(config.getClientByIndex(serverIndex));
		System.out.println("------------------------------------Using sever:"+config.getCurrentClient().host);
		
		double[] data = new double[N];
		for(int i=0; i<data.length; i++)
			data[i] = i;
		
		long start1, start2;
		long end1, end2;
		CloudSD warmup = new CloudSD("test_network_warmup").init(new double[10]);
		warmup.push();
		
		start1 = System.currentTimeMillis();
		CloudSD var = new CloudSD("test_network_var").init(data);
		for(int i=0; i<3; i++)
			var.push();
		end1 = System.currentTimeMillis();
		
		start2 = System.currentTimeMillis();
		CloudSD var2 = new CloudSD("test_network_var");
		for(int i=0; i<3; i++)
			var2.fetch();
		end2 = System.currentTimeMillis();
		
		double sec1 = (end1-start1)/1000.0;
		double sec2 = (end2-start2)/1000.0;
		System.out.println(String.format("Upload %.2f MB/s (number=%d, time=%.2f)", (12+N*8)*3/sec1/1000000.0, N, sec1));
		System.out.println(String.format("Download: %.2f MB/s (number=%d, time=%.2f)", (12+N*8)*3/sec2/1000000.0, N, sec2));
		
//			if(var2.isOnCloud()) {
//				for(double d : var2.getData()) {
//					System.out.println(d);
//				}
//			}
	}
}
