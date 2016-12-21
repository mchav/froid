# froid
A library for using the Frege programming language in Android development.

## Building froid

Run `compile` and then zip the class files into a jar. I'll use a more involved/appropriate and automated process with time.

## Building a project
Meant for use with Android Studio.
Download jar from release and place in your project's `libs` folder. 
Copy the `project.afterEvaluate` section from [here](https://github.com/mchav/GeoQuiz-Frege/blob/master/app/build.gradle) and place it at the bottom of your app's `build.gradle`.

In general Frege on android requires a version of the Frege library without `run8` so dex can parse the dependency. You can download said version [here](https://github.com/mchav/GeoQuiz-Frege/blob/master/app/libs/frege-3.24.100.1-jdk7.jar?raw=true) and place it in `libs` as well.

Create a 'frege' folder in you application's `src` directory and copy the diretory strucutre of the Java file (i.e it should look like `src/frege/com/example/application`) and create all your Frege files there.

## Usage

Simple Activity

```
module io.github.mchav.fregeandroid.FregeActivity where

import froid.app.Activity
import froid.content.Context
import froid.widget.TextView

native module type Activity {}

onCreate :: MutableIO Activity -> IO ()
onCreate !this = do
	context <- this.getApplicationContext
	tv <- TextView.new context
	tv.setText "Hello, Android - Love, Frege"
	this.setContentView tv
```

## Example

You can find a more involved example [here](https://github.com/mchav/GeoQuiz-Frege)

## Contributing

A lot of what there is to do is create the bindings for the other types in `android` and `android.support`. Any PRs of this nature are welcome.
