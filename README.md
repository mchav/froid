# froid

<img width="450" height="450" src="froid.png"/>

A library for using the Frege programming language in Android development.

## Building froid

Run `compile` and then zip the class files into a jar. I'll use a more involved/appropriate and automated process with time.

## Building a project
Meant for use with Android Studio.
Download jar from release and place in your project's `libs` folder. 
Copy the `project.afterEvaluate` section from [here](https://github.com/mchav/GeoQuiz-Frege/blob/master/app/build.gradle) and place it at the bottom of your app's `build.gradle`.

In general Frege on android requires a version of the Frege library without `run8` so dex can parse the dependency. You can download said version [here](https://github.com/mchav/GeoQuiz-Frege/blob/master/app/libs/frege-3.24.100.1-jdk7.jar?raw=true) and place it in `libs` as well.

Create a 'frege' folder in you application's `src` directory and copy the diretory strucutre of the Java file (i.e it should look like `src/frege/com/example/application`) and create all your Frege files there.

To create a minified jar add the following [proguard rules](https://github.com/mchav/GeoQuiz-Frege/blob/master/app/proguard-rules.pro) to your project.

## Usage

Simple Activity

```
module io.github.mchav.fregeandroid.FregeActivity where

import froid.app.Activity
import froid.content.Context
import froid.widget.TextView

native module type Activity where {}

onCreate :: MutableIO Activity -> Maybe (MutableIO Bundle) -> IO ()
onCreate this bundle = do
	context <- this.getApplicationContext
	tv <- TextView.new context
	tv.setText "Hello, Android - Love, Frege"
	this.setContentView tv
```

## Example

You can find a more involved example [here](https://github.com/mchav/GeoQuiz-Frege)

## Contributing

A lot of what there is to do is create the bindings for the other types in `android`. For classes such as adapters/fragments read [this](http://mchav.github.io/functional-inheritance-in-android/) to learn about the design philosophy for subclassing.  Any PRs of this nature are welcome.


## Implemented

* Activities
* Animation
* View
* Some basic widgets
* Intents
* Logging

## Needed

* TBA