package froid.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import frege.run7.Func;
import frege.run7.Lazy;
import frege.run7.Thunk;
import frege.run.Kind;
import frege.run.RunTM;
import frege.runtime.Meta;
import frege.runtime.Phantom.RealWorld;
import frege.prelude.PreludeBase;

import java.lang.reflect.Method;

public class FregeActivity extends Activity {
    
    Func.U<RealWorld, Short> onPauseLambda = null;
    Func.U<PreludeBase.TMaybe<Bundle>, Func.U<RealWorld, Short>> onSavedInstanceStateLambda = null;
    Func.U<Integer, Func.U<Integer, Func.U<PreludeBase.TMaybe<Intent>, Func.U<RealWorld, Short>>>> onActivityResultLambda = null;

    // executes io action given as parameter
    public void setOnPause(Func.U<RealWorld, Short> lambda) {
        this.onPauseLambda = lambda;
    }

    public void setOnSavedInstanceState(Func.U<PreludeBase.TMaybe<Bundle>, Func.U<RealWorld, Short>> lambda) {
        this.onSavedInstanceStateLambda = lambda;
    }

    public void setOnActivityResult(Func.U<Integer, Func.U<Integer, Func.U<PreludeBase.TMaybe<Intent>, Func.U<RealWorld, Short>>>> onActivityResultLambda) {
        this.onActivityResultLambda = onActivityResultLambda;
    }

    // reflection methods
    private Object invokeStaticActivityMethod(String methodName, Object[] args, String signature) {
        Method fregeMethod = null;
        try {
            fregeMethod = this.getClass().getDeclaredMethod(methodName, FregeActivity.class, Lazy.class);
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
        PreludeBase.TMaybe<Bundle> sis = savedInstanceState == null ?
         PreludeBase.TMaybe.DNothing.<Bundle>mk(): 
         PreludeBase.TMaybe.DJust.<Bundle>mk(Thunk.<Bundle>lazy(savedInstanceState));
        Object invokedOnCreate = invokeStaticActivityMethod("onCreate", new Object[] {this, sis}, "onCreate :: MutableIO Activity -> IO ()");
        run(invokedOnCreate);   
    }

    @Override
    public void onPause() {
        super.onPause();
        run(onPauseLambda);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        PreludeBase.TMaybe<Bundle> sis = savedInstanceState == null ?
         PreludeBase.TMaybe.DNothing.<Bundle>mk(): 
         PreludeBase.TMaybe.DJust.<Bundle>mk(Thunk.<Bundle>lazy(savedInstanceState));
        run(onSavedInstanceStateLambda.apply(Thunk.<PreludeBase.TMaybe<Bundle>>lazy(sis)).call());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Func.U<Integer, Func.U<PreludeBase.TMaybe<Intent>, Func.U<RealWorld, Short>>> first = 
            RunTM.<Func.U<Integer, Func.U<PreludeBase.TMaybe<Intent>, Func.U<RealWorld, Short>>>>cast(
                onActivityResultLambda.apply(Thunk.<Integer>lazy(requestCode)).call());
        Func.U<PreludeBase.TMaybe<Intent>, Func.U<RealWorld, Short>> second = 
            RunTM.<Func.U<PreludeBase.TMaybe<Intent>, Func.U<RealWorld, Short>>>cast(
                first.apply(Thunk.<Integer>lazy(resultCode)).call());

        PreludeBase.TMaybe<Intent> d = data == null ?
         PreludeBase.TMaybe.DNothing.<Intent>mk(): 
         PreludeBase.TMaybe.DJust.<Intent>mk(Thunk.<Intent>lazy(data));

        run(second.apply(Thunk.<PreludeBase.TMaybe<Intent>>lazy(d)).call());
    }
}