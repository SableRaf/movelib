# MoveLib (beta)

*Note: Movelib is considered beta and may not work consistently on every machine. Please report bugs and submit feature requests on the [issues](https://github.com/SableRaf/movelib/issues) page.*

A [Processing](http://processing.org/) interface for the [PS Move Controller](http://en.wikipedia.org/wiki/PlayStation_Move) based on Thomas Perl's [PS Move API](http://thp.io/2010/psmove/).

## About

Movelib provides a convenient wrapper for the [PS Move API](http://thp.io/2010/psmove/), giving access to the Move Controller's orientation sensors, buttons, actuators (RGB led sphere, vibration motor) and more, as well as camera based 3D tracking. Up to 7 controllers can be connected on a single machine.

The lib will eventually add support for sending and receiving the data from the controllers via OSC. An earlier pde version supporting osc can be found [here](https://github.com/SableRaf/MoveP5).

*Note: MoveLib only works on MacOS at the moment since the PS Move API doesn't provide Java wrappers for Linux and Windows.*

## Download

* [MoveLib.zip v0.1b2](https://github.com/SableRaf/movelib/raw/master/MoveLib/download/MoveLib.zip)

## Installation

Unzip and put the extracted *MoveLib* folder into the libraries folder of your Processing sketch folder. Reference and examples are included in the *MoveLib* folder.

## Pairing

Pairing of the PS Move Controllers is done via the *psmovepair* script you will find in the *utils* folder. Plug your controller to the computer with a mini-USB cable and run the script (Alternatively, you can drag and drop the file into an open Terminal window and press *Enter* in case you want to see what the script does). Repeat for other controllers if necessary. The script will get the sensor calibration information of the controller and save it in a *Users/yourName/.psmoveapi* folder (on MacOS) as well as allow it to be recognized as a bluetooth peripheral. If you later use the controller with your console or another computer, then want to connect it with your computer again, you will need to go through the pairing again.

## Magnetometer calibration

*Note: Magnetometer calibration isn't important, and the library will just calibrate the magnetometer on-the-fly at runtime if it isn't calibrated before. You can ignore error messages saying that a controller has not been calibrated.*

*Note2: You can only calibrate the magnetometer when the controller is connected via Bluetooth, as this is (right now) the only way to read the sensor data.*

Values from the magnetometers in the controller are dependent on the local magnetic field. The magnetometer_calibration script can be used to calibrate the magnetometer output ranges for a given environment. This data will then be used for initializing the magnetometer calibration in future runs of the program until it is reset or re-calibrated. This data is saved in a *Users/yourName/.psmoveapi* folder (on MacOS).

## Sphere tracking

The PSeye camera does not have a MacOS driver. Here is the calibration process for the internal camera on Mac computers. Launch the sketch and put the sphere in front of the camera so that it touches the lense. The LEDs will light in white for a few seconds. Then move the controller further from the lense as it starts to blink in a solid color. Tracking should start after a few seconds. For more information about this workaround, have a look at the [README file of the PS Move API](https://raw.github.com/thp/psmoveapi/master/README.osx), specifically the “Background information about iSight exposure locking” part.


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

* Tested on OSX 10.8.4

Processing Version:

* 2.0.3

## Dependencies

None.

## Support

For problems concerning the core API, please ask to the PSMove [mailing list](https://lists.ims.tuwien.ac.at/mailman/listinfo/psmove).

For bugs in Movelib itself, please file an issue [here](https://github.com/SableRaf/movelib/issues).

## Known issues & limitations

* The lib is 32 bits only (so no Processing 2.1 at the moment)
* The camera tracking sometimes fail to start properly.
* The sphere tracking will not work properly if you try to use more than two controllers (see the *Sphere tracking* section above).
* External cameras are not supported. PS Move API only supports the built-in iSight/Facetime cameras on MacOS, so MoveLib has the same limitation (for updates about that limitation follow [this issue](https://github.com/thp/psmoveapi/issues/68) from the PS Move API repository).

## License

* The library is Open Source Software released under the [GNU General Public License](https://raw.github.com/SableRaf/movelib/master/reference/LICENSE.txt). It's developed by [Raphaël de Courville](https://vimeo.com/sableraf/).
* The PS Move API is released under the [Simplified BSD-style license](https://raw.github.com/thp/psmoveapi/master/COPYING). It's developed by Thomas Perl <[m@thp.io](mailto:m@thp.io)>.

*This README file was last updated on 2013-11-06 by Raphaël de Courville.*