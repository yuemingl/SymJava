package symjava.symbolic.utils;

import com.sun.org.apache.bcel.internal.generic.ClassGen;

import symjava.bytecode.BytecodeFunc;

public class FuncClassLoader extends ClassLoader {
	
	/**
	 * Return an instance from a ClassGen object 
	 *
	 */
	public BytecodeFunc newInstance(ClassGen cg) {
        byte[] bytes  = cg.getJavaClass().getBytes();
        Class<?> cl = null;
        cl = defineClass(cg.getJavaClass().getClassName(), bytes, 0, bytes.length);
		try {
			return (BytecodeFunc)cl.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
}
