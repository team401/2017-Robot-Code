### org.team401.subsystems

Each subsystem on the robot is represented into one class per subsystem in this package. Classes here use the singleton concept, where there is only one instance of each "subsystem", or class. Each subsystem uses an enum to track its state, and will update its components or controllers to match its current state. Instead of passing around the instance of a subsystem, you can get the instance of each one in Java by getting the reference to the object with the final field INSTANCE, or in Kotlin by treating the class as a static class.
