package io.lambdacloud.symjava.bytecode;

import io.lambdacloud.core.CloudFunc.FUNC_TYPE;

public class IR {
	public FUNC_TYPE type;
	public int outAryLen;
	public int numArgs;
	
	public String name;
	public byte[] bytes;

}
