# froid
A library for using the Frege programming language in Android development.

## Usage and examples

To get setup froid read the instructions on the [froid Wiki](https://github.com/mchav/froid/wiki).

Simple Activity
---------------
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

You can find a more involved example [here](https://github.com/mchav/GeoQuiz-Frege). More will be available soon.


## Building froid

Run `compile` and then zip the class files into a jar. I'll use a more involved/appropriate and automated process with time. The Frege compiler will attempt to compile some classes before their dependenices. Recompile until there are no errors. I will look into defining the compile order later.

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
