#FleetSpeak

######by DeadliestTrucks
===============

##Purpose
Android application for VoIP communication designed for use in vehicles.
The application allows multiple people to create or join rooms and talk to everyone in that room.
Everything is to be designed with safety in mind following the NHTSA-guidelines to reduce driver distraction.

##Structure
The project consists of:
  A JAVA-based server running in a desktop environment.
  A PostgreSQL database.
  The application running on an android device supporting API 10 or above.
  A management GUI for desktop clients.

**Server:**

The server handles all clients and connections. One of its major responsibilities is the mixing of sound to reduce bandwidth usage for the clients. Can be run on Windows and MacOS.

**Database:**

Handles all information about the users and used to verify which ones are authorized to connect.

**AndroidClient:**

The android client is responsible for recording the audio, send it to the server, receiving audio from the server and playing it. Most of the other features need to go through the server before they take effect such as changing or creating new rooms.

**ManagementClient:**

Work in progress.

##Getting started
**Server:**

1. The server is an eclipse project in the fleetspeak/Server folder.
   (You can also build the project with an ant build to start it withoput eclipse)
2. Make sure all used ports are forwarded in the network with the server (Default is port 8867 for tcp and 8868+ for udp).
3. Start the ServerConsole class. Make sure to add the VM-argument -Djava.library.path=libs/native/[your OS, see below].
    Windows 32bit: win32-x86
    Windows 64bit: win32-x86-64
    MacOS: darwin
4. The server is now running, all events on the server is written to the log file \log\fleetspeak.log and can be easily read with a good log reader (we use Gamut Log Viewer).

**Database:**

1. Create the PostgreSQL database.
2. Run the tables.sql script.
3. Run the data.sql script.

**Client:**

1. This is a Android Studio project.
2. Requires an Android device of your choice supporting API 10 or higher.
3. Input a valid username.
3. Make sure the IP is the same as the servers IP.
4. Press connect.

**ManagementClient:**

1. Run main class.

##Troubleshooting
**Client can't get connection to the server:**

If it's for testing purposes make sure you are on the same network with the clients and the server, for example a mobile hotspot on a phone.
Otherwise make sure the port and IP on the client is the one used by the server, its local IP if on the same network, its external IP if on different networks (google "whatsmyip").

**Client is wonky:**

Exit the application and close it completely before restarting it.

##Unimplemented features
- Push-to-talk button is only implemented in the GUI, it only toggles the image.
- Muting other users is only implemented in the GUI, it only toggles the image.
- Changing the mic volume is only implemented in the GUI, it's only a visual change.

##Known problems
- Reconnecting in the client after connecting once does not work, exit and kill the app to reset it.
- Error message upon killing the app due to a service leak.
- The app cannot be started in "Car mode", it has to be started in "Standstill mode" and then changed (wheel based speed > 0).
- More than two people in a conversation at the same time lowers the sound quality exponentionally (3 hearable, 4 static).
- No packet-loss-handling makes the soundquality drop significantly on bad connections.
- Using the android built-in back button can cause problems. Use the in-app back button for more consistent results.
