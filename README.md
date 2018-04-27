# froid

<img width="100" height="100" align="middle" src="froid.png"/>

A library for using the Frege programming language in Android development.

## Usage and examples

To get/setup froid read the instructions on the [froid Wiki](https://github.com/mchav/froid/wiki). To write a simple application from scratch follow [this step-by-step tutorial](https://github.com/mchav/froid/wiki/Tutorial).

Simple Activity
---------------
```frege
module io.github.mchav.fregeandroid.FregeActivity where

import froid.app.Activity
import froid.content.Context
import froid.os.Bundle
import froid.widget.TextView

native module type Activity where {}

onCreate :: Activity -> Maybe Bundle -> IO ()
onCreate this bundle = do
	tv <- TextView.new this
	tv.setText "Hello, Android - Love, Frege"
	this.setContentView tv
```

## Example

You can find a more involved example [here](https://github.com/mchav/GeoQuiz-Frege). More will be available soon.


## Building froid

Run `compile` and then `package`.

## Contributing

A lot of what there is to do is create the bindings for the other types in `android`. For classes such as adapters/fragments read [this](http://mchav.github.io/functional-inheritance-in-android/) to learn about the design philosophy for subclassing.  Any PRs of this nature are welcome.
