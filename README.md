# MoveLib

A [Processing](http://processing.org/) interface for the [PS Move Controller](http://en.wikipedia.org/wiki/PlayStation_Move) based on the [PS Move API](http://thp.io/2010/psmove/).

###Important note: This library is still under development and very much BETA. Use at your own risks.

## About

This library provides a convenient wrapper for the [PS Move API](http://thp.io/2010/psmove/), giving access to the Move Controller's orientation sensors, buttons, actuators (RGB led sphere, vibration motor) and more, as well as camera based 3D tracking. Up to 7 controllers can be connected on a single machine.

The lib will eventually adds support for sending and receiving the data from the controllers via OSC. An earlier pde version supporting osc can be found [here](https://github.com/SableRaf/MoveP5).

## Download

* [MoveLib.zip v0.1](https://raw.github.com/SableRaf/movelib/master/download/MoveLib.zip)

## Installation

Unzip and put the extracted *MoveLib* folder into the libraries folder of your Processing sketch folder. Reference and examples are included in the *MoveLib* folder.

## Usage

Pairing of the PS Move Controllers with your Mac is done via the [pairing utility](https://raw.github.com/SableRaf/movelib/master/tools/Pairing.zip). 

Refer to the comments in the examples for indications on how to get started.

*Note: The iSight/Facetime camera support is still very much a hack and only supports up to two simultaneous controllers.*

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

* The library is Open Source Software released under the [GNU General Public License](https://raw.github.com/SableRaf/movelib/master/LICENSE.txt). It's developed by [Raphaël de Courville](https://vimeo.com/sableraf/).
* The PS Move API is released under the [Simplified BSD-style license](https://raw.github.com/thp/psmoveapi/master/COPYING). It's developed by Thomas Perl <[m@thp.io](mailto:m@thp.io)>.

*This README file was last updated on 2013-03-17 by Raphaël de Courville.*