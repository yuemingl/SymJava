package io.lambdacloud.symjava.symbolic.utils;

import io.lambdacloud.symjava.bytecode.BytecodeFunc;
import com.sun.org.apache.bcel.internal.generic.ClassGen;

public class FuncClassLoader<T> extends ClassLoader {
	
	public FuncClassLoader() {
		//super(Thread.currentThread().getContextClassLoader());
		//https://blogs.oracle.com/sundararajan/understanding-java-class-loading
		//super(java.lang.ClassLoader.getSystemClassLoader());
		super(BytecodeFunc.class.getClassLoader());
	}
//	
//	public FuncClassLoader(ClassLoader parent) {
//		super(parent);
//	}
	
	/**
	 * Return an instance from a ClassGen object 
	 *
	 */
	@SuppressWarnings("unchecked")
	public T newInstance(ClassGen cg) {
        byte[] bytes  = cg.getJavaClass().getBytes();
        Class<T> cl = null;
        cl = (Class<T>) defineClass(cg.getJavaClass().getClassName(), bytes, 0, bytes.length);
		try {
			return cl.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
}
