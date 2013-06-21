# MoveLib

A [Processing](http://processing.org/) interface for the [PS Move Controller](http://en.wikipedia.org/wiki/PlayStation_Move) based on Thomas Perl's [PS Move API](http://thp.io/2010/psmove/).

## About

Movelib provides a convenient wrapper for the [PS Move API](http://thp.io/2010/psmove/), giving access to the Move Controller's orientation sensors, buttons, actuators (RGB led sphere, vibration motor) and more, as well as camera based 3D tracking. Up to 7 controllers can be connected on a single machine.

The lib will eventually add support for sending and receiving the data from the controllers via OSC. An earlier pde version supporting osc can be found [here](https://github.com/SableRaf/MoveP5).

## Download

* [MoveLib.zip v0.1b1](http://s176381904.onlinehome.fr/processing/MoveLib/download/MoveLib.zip)

## Installation

Unzip and put the extracted *MoveLib* folder into the libraries folder of your Processing sketch folder. Reference and examples are included in the *MoveLib* folder.

## Pairing

Pairing of the PS Move Controllers is done via the [psmovepair](https://raw.github.com/SableRaf/movelib/master/MoveLib/tools/psmovepair) script you will find in the *tools* folder. Plug your controller to the computer with a mini-USB cable and run the script (Alternatively, you can drag and drop the file into an open Terminal window and press <enter> in case you want to see what the script does). Repeat for other controllers if necessary. The script will get the sensor calibration information of the controller and save it in a "Users/yourName/.psmoveapi" folder as well as allow it to be recognized as a bluetooth peripheral. If you later use the controller with your console or another computer, then want to connect it with your computer again, you will need to go through the pairing again.

*Note: Pairing currently works on Mac OS and Linux only.*

## Magnetometer calibration

Values from the magnetometers in the controller are dependent on the local magnetic field (how nerdy is that, right?). The "magnetometer_calibration" tool can be used to calibrate the magnetometer output ranges for a given environment. This data will then be used for initializing the magnetometer calibration in future runs of the program until it is reset or re-calibrated. Orientation tracking will work without this step but you should use it if accurate orientation tracking matters in your application.


## Sphere tracking

The PSeye camera does not have a MacOS driver. Here is the calibration process for the internal camera on Mac computers. Launch the sketch and put the sphere in front of the camera so that it touches the lense. The LEDs will light in white for a few seconds. Then move the controller further from the lense as it starts to blink in a solid color. Tracking should start after a few seconds.

*Note: The iSight/Facetime camera support is still very much a hack and only supports up to two simultaneous controllers.*

## Snapshot

![Snapshots](https://raw.github.com/SableRaf/movelib/master/ressources/capture.png)

## Examples

* [Hello](https://github.com/SableRaf/movelib/tree/master/MoveLib/examples/Hello/Hello.pde)
* [setLeds](https://github.com/SableRaf/movelib/tree/master/MoveLib/examples/setLeds/setLeds.pde)
* [setRumble](https://github.com/SableRaf/movelib/tree/master/MoveLib/examples/setRumble/setRumble.pde)
* [orientation](https://github.com/SableRaf/movelib/tree/master/MoveLib/examples/orientation/orientation.pde)
* [tracker_image](https://github.com/SableRaf/movelib/tree/master/MoveLib/examples/tracker_image/tracker_image.pde)
* Check the *examples* folder for more...

## Tested

System:

* OSX, Linux

Processing Version:

* 2.0b8

## Dependencies

None.

## Support

For problems concerning the core API, please ask to the PSMove [mailing list](https://lists.ims.tuwien.ac.at/mailman/listinfo/psmove).

For bugs in Movelib itself, please file an issue [here](https://github.com/SableRaf/movelib/issues).

## Known issues

* The magnetometer calibration is not implemented yet. The error message saying “Magnetometer in [MAC address of your controller] not yet calibrated.” is normal.
* MoveManager.startTracking() can't be called in setup() because of a Processing bug. See this post: https://github.com/processing/processing/issues/1735. A temporary solution is shown in the [tracker_image](https://github.com/SableRaf/movelib/tree/master/MoveLib/examples/tracker_image/tracker_image.pde) example.
* The camera tracking sometimes fail to start properly and the image then flips like crazy. I'm working on it.

## License

* The library is Open Source Software released under the [GNU General Public License](https://raw.github.com/SableRaf/movelib/master/reference/LICENSE.txt). It's developed by [Raphaël de Courville](https://vimeo.com/sableraf/).
* The PS Move API is released under the [Simplified BSD-style license](https://raw.github.com/thp/psmoveapi/master/COPYING). It's developed by Thomas Perl <[m@thp.io](mailto:m@thp.io)>.

*This README file was last updated on 2013-05-04 by Raphaël de Courville.*