package symjava.bytecode;

import java.util.ArrayList;
import java.util.List;

public class VecFuncs implements BytecodeBatchFunc {
	public static class FuncInfo {
		BytecodeBatchFunc func;
		int outPos;
		public FuncInfo(BytecodeBatchFunc func, int outPos) {
			this.func = func;
			this.outPos = outPos;
		}
	}
	List<FuncInfo> funInfo = new ArrayList<FuncInfo>();
	
	public void addFunc(BytecodeBatchFunc func, int outPos) {
		funInfo.add(new FuncInfo(func, outPos));
	}
	
	@Override
	public void apply(double[] outAry, int outPos, double... args) {
		for(FuncInfo fInfo : funInfo) {
			fInfo.func.apply(outAry, outPos+fInfo.outPos, args);
		}
	}
}
