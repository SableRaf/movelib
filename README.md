# MoveLib

An interface for the [PS Move Controller](http://en.wikipedia.org/wiki/PlayStation_Move) for [Processing](http://processing.org/) based on the [PS Move API](http://thp.io/2010/psmove/) by Thomas Perl.

*This library is still in development and not ready for use yet. Please wait until this note is removed to download.* In the meantime, you can start using the development pde version available [here](https://github.com/SableRaf/MoveP5).

## About

“The PS Move API is an open source library for Linux, Mac OS X and Windows to access the Sony Move Motion Controller via Bluetooth and USB directly from your PC without the need for a PS3. Tracking in 3D space is possible using a PS Eye (on Linux and Windows), an iSight camera (on Mac OS X) or any other suitable camera source.” (quoted from the [PS Move API](http://thp.io/2010/psmove/) main page)

This library provides a convenient wrapper for the PS Move API, giving access to the Move Controller's orientation sensors, buttons, actuators (RGB led sphere, vibration motor), and camera based 3D tracking.

It also adds support for sending and receiving the data from the controllers via OSC.

## Download

* [MoveLib.zip v0.1](https://raw.github.com/SableRaf/movelib/master/download/MoveLib.zip)

## Installation

Unzip and put the extracted *MoveLib* folder into the libraries folder of your Processing sketch folder. Reference and examples are included in the *MoveLib* folder.

## Usage

Pairing of the PS Move Controllers with your Mac is done via the [pairing utility](https://raw.github.com/SableRaf/movelib/master/tools/Pairing.zip). 

Refer to the comments in the examples for indications on how to get started.

## Snapshot

![Snapshots](https://raw.github.com/SableRaf/movelib/master/reference/capture.png)

## Examples

* [Button Events](https://raw.github.com/SableRaf/movelib/master/examples/button_events/button_events.pde)
* [Camera Tracking](https://raw.github.com/SableRaf/movelib/master/examples/camera_tracking/camera_tracking.pde)
* [OSC streaming](https://raw.github.com/SableRaf/movelib/master/examples/osc_streaming/osc_streaming.pde)

## Tested

System:

* OSX

Processing Version:

* 2.0b7
* 2.0b8

## Dependencies

oscP5 (to transmit the data over a network or locally to another program)

## License

The library is Open Source Software released under the [GNU General Public License](https://raw.github.com/SableRaf/movelib/master/LICENSE.txt). It's developed by [Raphaël de Courville](https://vimeo.com/sableraf/).

The PS Move API is released under the [Simplified BSD-style license](https://raw.github.com/thp/psmoveapi/master/COPYING). It's developed by [Thomas Perl](m@thp.io).
