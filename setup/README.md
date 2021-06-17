# Raspio Setup

A working Raspio system requires the Raspio Server program to be installed on a Raspberry Pi. This folder contains the necessary files to install Raspio Server.

## Installation Methods

### Install script

```bash
curl -s https://raw.githubusercontent.com/bickoSiTiEff/Raspio/dev/setup/install.sh | sudo bash /dev/stdin dev
```

The above command will download and execute a script that installs everything necessary to run a Raspio Server. You can replace the two occurrences of `dev` in the above snipped with a different Git branch name.

It will:

* Create the required folder structure
* Install system updates
* Create a virtual audio device (used internally)
* Install and configure MPD
  * Additionally to the radio broadcast, you can listen to the current transmission using the 3.5mm jack on the Raspberry Pi or via the audio streaming server running on port 8000.
* Install additional dependencies (PiFmAdv, Node.js, Yarn)
* Download and compile the Raspio Server
* Install a Systemd Unit that automatically starts the Raspio Server after the Raspberry Pi finished booting
* Install a Avahi service that is used by the Raspio App to detect the Raspio Servers IP address
* Look fancy.

### Prepared SD-Card image (easiest)

In the future there will be a ready-to-use SD-Card image available that you just need to flash to an SD-Card and insert it into a Raspberry Pi.

## Created files

```bash
/raspio/              # The main folder used for persistant storage
/raspio/install.log   # A log file containing the output of the installation script
/raspio/pi_fm_adv     # The actual FM transmitter binary used by the server
/raspio/music/        # This folder contains all music files that are saved locally
/raspio/playlists/    # A folder used internally by MPD to store playlists
/raspio/repo/         # A clone of this repository that contains the server code

/etc/avahi/services/raspio.service  # A DNS-SD service definition for Raspio
/etc/systemd/system/raspio.service  # A Systemd Unit file for Raspio
/etc/mpd.conf                       # Custom MPD configuration
```

## Virtual Audio device

The `snd-aloop` kernel module is used to create a virtual audio device. This is needed to redirect the MPD audio output to the PiFmAdv transmitter.

## Service Discovery

After the smartphone running the Raspio App and the Raspberry Pi running the Raspio Server are in the same network they need a way to find each other. A common way this is solved is using DNS-SD (Domain Name System Service Discovery). Google Cast and Spotify also use this to detect compatible devices.

The preinstalled Avahi server is configured to send a service advertisement that can be picked up by other devices. The service name is configured as `_raspio._tcp`.

