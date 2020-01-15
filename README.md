# Propertymanager

This is a distributed system project made for University of Turku's course *Techniques for Distributed Systems and Cloud Services* course.
Assignment was to create a basic house control system consisting of three elements: 
* Lights to be controlled
* Controlserver consisting of Controlserver, which starts RMIServer and Lightswitchserver
* Remoteserver which connects to Controlserver via RMI, and WWWServer which controls a website for remote control

## How to run

Propertymanager is developed in pure Java 8, so other dependencies aren'r needed. To run project, clone this with `git clone https://github.com/arttuhuttunen/propertymanager/`
. I recommend using IDE for compiling and running, as development and running is done using IntelliJ IDEA. 

First class to run is Controlserver, with VM-arguments `-Djava.security.policy=security.policy`, which starts the lightswitchServer
and RMIServer. After these are launched, Lightswitches run Lightswitch with program argument from {1...9}, which determines the id of light.
After this run Remoteserver with same VM-arguments `-Djava.security.policy=security.policy`, and wait for it to complete (done when *RMI Connection successful* appears in console output).
After startup remote browser interface can be accessed in [localhost:8000](http://localhost:8000). 

## Explanation of structure

Control server contains startup methods for lightswitchServer and RMIServer, and maintains the status of temperature (non validated textfield).
lightswithServer communicates with lightswitches through sockets, and RMIServer communicates with RMIClient through Java's RMI.
RemoteServer starts and controls RMIClient and WWWServer. WWWServer contains webserver and dynamic page generator made entirely from scratch,
by loading template file filled with keywords, and it is sended to user replaced with proper values.
