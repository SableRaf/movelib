# MoveLib

A [Processing](http://processing.org/) interface for the [PS Move Controller](http://en.wikipedia.org/wiki/PlayStation_Move) based on the [PS Move API](http://thp.io/2010/psmove/).

## About

Movelib provides a convenient wrapper for the [PS Move API](http://thp.io/2010/psmove/), giving access to the Move Controller's orientation sensors, buttons, actuators (RGB led sphere, vibration motor) and more, as well as camera based 3D tracking. Up to 7 controllers can be connected on a single machine.

The lib will eventually add support for sending and receiving the data from the controllers via OSC. An earlier pde version supporting osc can be found [here](https://github.com/SableRaf/MoveP5).

## Download

* [MoveLib.zip v1.0beta1](https://raw.github.com/SableRaf/movelib/master/download/MoveLib.zip)

## Installation

Unzip and put the extracted *MoveLib* folder into the libraries folder of your Processing sketch folder. Reference and examples are included in the *MoveLib* folder.

## Pairing

Pairing of the PS Move Controllers is done via the [psmovepair](https://raw.github.com/SableRaf/movelib/master/tools/psmovepair) script you will find in the *tools* folder. Plug your controller to the computer with a mini-USB cable and run the script. Repeat for other controllers if necessary. The script will get the sensor calibration information of the controller and allow it to be recognized as a bluetooth peripheral. If you later use the controller with your console or another computer, you will need to the pairing again.

*Note: Pairing currently works on Mac OS and Linux only.*

## Sphere tracking

The PSeye camera does not have a MacOS driver. Here is the calibration process for the internal camera on Mac computers. Launch the sketch and put the sphere in front of the camera so that it touches the lense. The LEDs will light in white for a few seconds. Then move the controller further from the lense as it starts to blink in a solid color. It tracking should start after a few seconds.

*Note: The iSight/Facetime camera support is still very much a hack and only supports up to two simultaneous controllers.*

## Snapshot

![Snapshots](https://raw.github.com/SableRaf/movelib/master/reference/capture.png)

## Examples

* [Hello](https://raw.github.com/SableRaf/movelib/master/MoveLib/examples/Hello/Hello.pde)
* [setLeds](https://raw.github.com/SableRaf/movelib/master/MoveLib/examples/setLeds/setLeds.pde)
* [setRumble](https://raw.github.com/SableRaf/movelib/master/MoveLib/examples/setRumble/setRumble.pde)
* [orientation](https://raw.github.com/SableRaf/movelib/master/MoveLib/examples/orientation/orientation.pde)
* [tracker_image](https://raw.github.com/SableRaf/movelib/master/MoveLib/examples/tracker_image/tracker_image.pde)
* Check the *examples* folder for more...

## Tested

System:

* OSX

Processing Version:

* 2.0b7
* 2.0b8

## Dependencies

None.

## Support

For problems concerning the core API, please ask to the PSMove [mailing list](https://lists.ims.tuwien.ac.at/mailman/listinfo/psmove).

For bugs in Movelib itself, please file an issue [here](https://github.com/SableRaf/movelib/issues).

## Known issues

* The magnetometer calibration is not implemented yet. The error message saying “Magnetometer in [MAC address of your controller] not yet calibrated.” is normal.
* MoveManager.startTracking() can't be called in setup() because of a Processing bug. See this post: https://github.com/processing/processing/issues/1735. A temporary solution is shown in the [tracker_image](https://raw.github.com/SableRaf/movelib/master/MoveLib/examples/tracker_image/tracker_image.pde) example.

## License

* The library is Open Source Software released under the [GNU General Public License](https://raw.github.com/SableRaf/movelib/master/reference/LICENSE.txt). It's developed by [Raphaël de Courville](https://vimeo.com/sableraf/).
* The PS Move API is released under the [Simplified BSD-style license](https://raw.github.com/thp/psmoveapi/master/COPYING). It's developed by Thomas Perl <[m@thp.io](mailto:m@thp.io)>.

*This README file was last updated on 2013-04-26 by Raphaël de Courville.*