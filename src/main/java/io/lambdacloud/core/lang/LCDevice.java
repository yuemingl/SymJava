package io.lambdacloud.core.lang;

public class LCDevice {
	public String name;
	public LCDevice(int indexNum) {
		this.name = String.valueOf(indexNum);
	}
	
	public LCDevice(String name) {
		this.name = name;
	}
}
