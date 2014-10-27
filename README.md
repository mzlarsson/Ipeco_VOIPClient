#FleetSpeak

##by DeadliestTrucks
===============

##Purpose
Android application for VoIP communication specifically targeting truck drivers.
The application allows multiple people create or join rooms and talk to everyone in that room.
Everything is to be designed with safety in mind following the NHTSA-guidelines to reduce driver distraction.

##Structure
The application consists of two parts, the application running on an android device supporting API 14 or above and a JAVA-based server running in a desktop environment.

**Server:**
The server keeps track of all connected clients, what name they have and in which room they are in. With this it handles which clients should hear each other and mixes the audiostreams to reduce the bandwidth required to the client.
The server can be run with a GUI or through the console and provides some useful interactions such as changing names on clients and move clients between rooms.

**Client:**
The client is responsible for recording the audio to send to the server and receiving audio from the server and playing it. Most of the other features need to go through the server before they take effect such as changing or creating new rooms.

##Getting started
**Server:**
1. Make sure the wanted port is forwarded in the network with the server (Default is port 8867).
2. Start the ServerGUI if you want a GUI, ServerConsole if not.

**Client:**
1. Start the application.
2. Make sure the IP is the same as the servers IP.
3. Make sure the selected port is the same as the servers.
4. Press connect.

##Troubleshooting
**Client can't get connection to the server**
If it's for testing purposes make sure you are on the same network with the clients and the server, for example a mobile hotspot on a phone.
Otherwise make sure the port and IP on the client is the one used by the server, its local IP if on the same network, its external IP if on different networks (google "whatsmyip").

**Client is wonky**
Exit the application and close it completely before restarting it.