# 2017-Robot-Code
Robot Code for Team 401's robot, PHIL, for the 2017 season FIRST Steamworks. Code in this repository is written in Kotlin and Java, and is a command based system.

Code is divided into several sub-packages, each with a specific purpose, like to hold code for each subsystem, component, etc. Each package and its specific function are explained below.

### How to write or edit code in an IDE:
1. First, fork, clone, or download the repo through your favorite method (command line, git client)
2. Install the Java 8 JDK if it's not installed already.
3. Run `./gradlew (your ide)` in terminal for mac/linux or `gradlew (your ide)` in CMD on windows. (Use `idea` for IntelliJ and `eclipse` for Eclipse)
4. Double click the 2017-Robot-Code.ipr to open in IntelliJ or import the directory as a project in Eclipse.

NOTE: Eclipse users will need to install a plugin so that they can compile .kt files.

### Package Functions
* org.team401.robot

    Contains the robot's main class files, constants, and controls.
    
* org.team401.auto

    Contains the code for autononmus which includes AutoModes, which are a series of actions run for each strategy we run during auto, and actions, which are small steps that each auto mode will run, such as driving, turning, shooting, etc.
    
* org.team401.camera

    Code for USB cameras connected directly to the RoboRIO, and makes it easy to switch between two cameras on the dashboard.
    
* org.team401.components

    Contains classes for components that are used more than once on the robot, or are better represented as an object in code rather than an entirely separate subsystem.
    
* org.team401.loops

    Periodic tasks that are run with the LoopManager class in the lib package, like calibrating the gyro or updating the dashboard display.
    
* org.team401.subsystems

    Each subsystem on the robot is represented into one class per subsystem in this package. Classes here use the singleton concept, where there is only one instance of each "subsystem", or class. Each subsystem uses an enum to track its state, and will update its components or controllers to match its current state. Instead of passing around the instance of a subsystem, you can get the instance of each one in Java by getting the reference to the object with the final field INSTANCE, or in Kotlin by treating the class as a static class.
    
* org.team401.lib

    Contains classes that can and should be reused each year, such as code for common interfaces, utility classes, sensors, crash tracking, and math helpers.
    
### All variables follow the standard java naming convention you would learn in a Computer Science course:
* UpperCamelCase for classes
* lowerCamelCase for identifiers
* CAPS_WITH_UNDERLINES for constants