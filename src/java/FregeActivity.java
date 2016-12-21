package froid.app;

import android.app.Activity;

import java.lang.reflect.Method;

public class FregeActivity extends Activity {

	// reflection methods
	private Object invokeStaticActivityMethod(String methodName, Object[] args, String signature) {
		Method fregeMethod = null;
		try {
			fregeMethod = this.getClass().getDeclaredMethod(methodName, FregeActivity.class);
		} catch (NoSuchMethodException nsm) {
			System.err.println("Method " + methodName + " is not defined. Make sure your onCreate function is defined as " + signature);
			return null;
		}

		Object invokedMethod = null;

		try {
			invokedMethod = fregeMethod.invoke(null, args);
		} catch (Exception e) { // none of the invocation exceptions should happen
			System.err.println("Failed to call " + methodName);
			return null;	
		}
		return invokedMethod;
	}

	private void run(Object invokedMethod) {
		if (invokedMethod == null) return;
		
		final frege.run7.Func.U<Object,Short> res = frege.run.RunTM.<frege.run7.Func.U<Object,Short>>cast(
				invokedMethod).call();
		frege.prelude.PreludeBase.TST.run(res).call();
	}

	@Override
	public void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Object invokedOnCreate = invokeStaticActivityMethod("onCreate", new Object[] {this}, "onCreate :: MutableIO Activity -> IO ()");
		run(invokedOnCreate);	
	}

	@Override
	public void onPause() {
		super.onPause();
		Object invokedOnPause = invokeStaticActivityMethod("onPause", null, "onPause :: IO ()");
		run(invokedOnPause);
	}
}