#!/bin/bash

GREEN_ANSI=$'\033[0;32m'
BLANK_ANSI=$'\033[0m'

if [ "$EUID" -ne 0 ]; then
	echo "Please run this script as root"
	exit
fi

if [ $# -gt 1 ]; then
	echo "Too many parameters! Either pass no parameters to this script to install from the default branch or specify a branch name as the only parameter."
	exit
fi

if [ -z "$BRANCH" ]; then
	if [ $# -eq 1 ]; then
		BRANCH="$1"
	else
		BRANCH="master"
	fi
fi

function header() {
	cat <<"EOF"


  _____                 _       
 |  __ \               (_)      
 | |__) |__ _ ___ _ __  _  ___  
 |  _  // _` / __| '_ \| |/ _ \ 
 | | \ \ (_| \__ \ |_) | | (_) |
 |_|  \_\__,_|___/ .__/|_|\___/ 
                 | |            
                 |_|            
EOF
	cat <<EOF

--------------------------------
Installing Raspio from branch $GREEN_ANSI$BRANCH$BLANK_ANSI!
${GREEN_ANSI}An install log will be saved to /raspio/install.log!${BLANK_ANSI}
EOF
}

function createDirectories() {
	echo -e "--------------------------------\n${GREEN_ANSI} +\tCreating needed folders... ${BLANK_ANSI}\n"
	echo "Creating /raspio/music"
	mkdir -p /raspio/music
	chmod 777 /raspio/music
	echo "Creating /raspio/playlists"
	mkdir -p /raspio/playlists
	chmod 777 /raspio/playlists
	echo "Creating /raspio/server"
	mkdir -p /raspio/server
	chmod 777 /raspio/server
	echo -e "\n${GREEN_ANSI} +\tCreated needed folders. ${BLANK_ANSI}"
}

function updateSystem() {
	echo -e "--------------------------------\n${GREEN_ANSI} +\tInstalling system updates... ${BLANK_ANSI}\n"
	apt-get update
	apt-get upgrade -y
	echo -e "\n${GREEN_ANSI} +\tInstalled system updates. ${BLANK_ANSI}"
}

function createVirtualAudio() {
	echo -e "--------------------------------\n${GREEN_ANSI} +\tCreating virtual audio device... ${BLANK_ANSI}\n"
	echo "Loading snd-aloop kernel module"
	modprobe snd-aloop
	if ! grep -q snd-aloop /etc/modules; then
		echo "Enabling snd-aloop kernel module"
		echo -e "\nsnd-aloop\n" >> /etc/modules
	fi
	echo -e "\n${GREEN_ANSI} +\tCreated virtual audio device. ${BLANK_ANSI}"
}

function installMPD() {
	echo -e "--------------------------------\n${GREEN_ANSI} +\tInstalling MPD... ${BLANK_ANSI}\n"
	apt-get install mpd -y
	systemctl enable --now mpd.service
	curl -o /etc/mpd.conf "https://raw.githubusercontent.com/bickoSiTiEff/Raspio/$BRANCH/setup/additional-files/etc/mpd.conf"
	echo -e "\n${GREEN_ANSI} +\tInstalled MPD. ${BLANK_ANSI}"
}

function installPiFmAdv() {
	echo -e "--------------------------------\n${GREEN_ANSI} +\tInstalling PiFmAdv... ${BLANK_ANSI}\n"
	apt-get install libsndfile1-dev git -y
	pushd /tmp
	git clone https://github.com/Miegl/PiFmAdv.git
	pushd /tmp/PiFmAdv/src
	make clean
	make
	mv pi_fm_adv /raspio/pi_fm_adv
	chmod 777 /raspio/pi_fm_adv
	rm -rf /tmp/PiFmAdv
	popd
	popd
	echo -e "\n${GREEN_ANSI} +\tInstalled PiFmAdv. ${BLANK_ANSI}"
}

function installNodeJS() {
	echo -e "--------------------------------\n${GREEN_ANSI} +\tInstalling Node.js... (external script)${BLANK_ANSI}\n"
	curl -fsSL https://deb.nodesource.com/setup_lts.x | bash -
	apt-get install nodejs -y
	echo -e "\n${GREEN_ANSI} +\tInstalled Node.js. ${BLANK_ANSI}"
}

function installYarn() {
	echo -e "--------------------------------\n${GREEN_ANSI} +\tInstalling Yarn... ${BLANK_ANSI}\n"
	curl -sL https://dl.yarnpkg.com/debian/pubkey.gpg | gpg --dearmor > /usr/share/keyrings/yarnkey.gpg
	echo "deb [signed-by=/usr/share/keyrings/yarnkey.gpg] https://dl.yarnpkg.com/debian stable main" > /etc/apt/sources.list.d/yarn.list
	apt-get update
	apt-get install yarn -y
	echo -e "\n${GREEN_ANSI} +\tInstalled Yarn. ${BLANK_ANSI}"
}

function installRaspioServer() {
	echo -e "--------------------------------\n${GREEN_ANSI} +\tInstalling Raspio Server... ${BLANK_ANSI}\n"
	pushd /raspio
	rm -rf /raspio/repo
	git clone -b $BRANCH --single-branch --depth 1 https://github.com/bickoSiTiEff/Raspio.git repo
	pushd /raspio/repo/server
	yarn
	yarn build
	popd
	popd
	echo -e "\n${GREEN_ANSI} +\tInstalled Raspio Server. ${BLANK_ANSI}"
}

function enableRaspioAutostart() {
	echo -e "--------------------------------\n${GREEN_ANSI} +\tEnabling Raspio Autostart... ${BLANK_ANSI}\n"
	curl -o /etc/systemd/system/raspio.service "https://raw.githubusercontent.com/bickoSiTiEff/Raspio/$BRANCH/setup/additional-files/etc/systemd/system/raspio.service"
	systemctl daemon-reload
	echo "Starting Raspio..."
	systemctl enable --now raspio.service
	echo -e "\n${GREEN_ANSI} +\tEnabled Raspio Autostart. ${BLANK_ANSI}"
}

function installDiscoverability() {
	echo -e "--------------------------------\n${GREEN_ANSI} +\tInstalling service discoverability... ${BLANK_ANSI}\n"
	curl -o /etc/avahi/services/raspio.service "https://raw.githubusercontent.com/bickoSiTiEff/Raspio/$BRANCH/setup/additional-files/etc/avahi/services/raspio.service"
	systemctl enable --now avahi-daemon.service
	systemctl restart avahi-daemon.service # Could've been running before
	echo -e "\n${GREEN_ANSI} +\tInstalled service discoverability. ${BLANK_ANSI}"
}

function footer() {
	cat <<EOF
--------------------------------

${GREEN_ANSI}Installation has finished!${BLANK_ANSI}
You can now connect to this Raspio instance using the Raspio App.


EOF
}

# Setup directory
mkdir -p /raspio
chmod 777 /raspio

# Log into file
exec > >(tee /raspio/install.log) 2>&1

header
createDirectories
updateSystem
createVirtualAudio
installMPD
installPiFmAdv
installNodeJS
installYarn
installRaspioServer
enableRaspioAutostart
installDiscoverability
footer
