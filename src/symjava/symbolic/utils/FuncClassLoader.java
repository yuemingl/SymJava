package symjava.symbolic.utils;

import com.sun.org.apache.bcel.internal.generic.ClassGen;

import symjava.bytecode.BytecodeFunc;

public class FuncClassLoader<T> extends ClassLoader {
	
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
