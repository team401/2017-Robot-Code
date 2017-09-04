package org.team401.robot.auto.modes

import com.ctre.CANTalon
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import org.team401.robot.auto.AutoModeSelector
import java.io.*
import java.util.*



internal class MotionProfiles(profile: String){
//come back to this
private var setValue: CANTalon.SetValueMotionProfile = CANTalon.SetValueMotionProfile.Disable
private val status = CANTalon.MotionProfileStatus(); // tracks the status of our MP Update with every iteration of the loop
private val talon = CANTalon(1) // the talons running the MP
private var state: Int = 0 // state for the state machine
private var loopTimeOut: Int = 0
private val kLoopTimeOut = 10
private val minPoints: Int = 10
private val path = scanCSV(profile)
    
    /*
    Plan:
    Set this us so routine() runs a motion profile

    runs every iteration of a auto periodic loop

    if motionprofiles on?
        No
            state = 0
            looptimeout = -1 //disable
        Yes
            //run a MP
            when(state){
                0 ->
                    startFilling() //buffer the points into the talons
                    setvalue = disabled
                    looptimeout = kloopstimeout //tracks progress
                    state = 1
                1 ->
                    //check if enough points have been buffered'
                    if (statud.btmbuffercount > minpoints){
                        setValue = enabled //starts profile once
                        state = 2;
                    }
                2 ->
                    //check how the profile is going
                    if(status.isunderrun == false){
                        looptimeout = klooptimeout
                    }
                    if(status.activepointValid && status.activepointislast)//if it is on the last point/ done
                        setvalue = hold
                        state = 0//reset state machine
                        looptimeout = -1 //disable
            }
     */

    private fun control(){
        if (talon.controlMode != CANTalon.TalonControlMode.MotionProfile) {
            state = 0
            loopTimeOut = -1
        }else {
            when (state) {
                0 -> {
                    startFilling(path)
                    setValue = CANTalon.SetValueMotionProfile.Disable
                    state = 1
                    loopTimeOut = kLoopTimeOut
                }
                1->{
                    if(status.btmBufferCnt >= minPoints)
                        setValue = CANTalon.SetValueMotionProfile.Enable
                        state = 2
                }
                2->{
                    if(status.hasUnderrun == false)
                        loopTimeOut = kLoopTimeOut
                    if(status.activePointValid && status.activePoint.isLastPoint)
                        setValue = CANTalon.SetValueMotionProfile.Hold
                        state = 0
                        loopTimeOut = -1
                }
            }
        }
    }


}

/**
 * Parses the contents of a .csv as a 2D array of doubles.
 * @param fileName Name, without file extension, of the file to read from.
 * @return Contents of the .csv
 */
private fun scanCSV(fileName: String): Array<DoubleArray> {
    var fileName = fileName
    //Add file extension
    fileName = fileName + ".csv"

    //BufferedReader will read from the file, Scanner will find row count
    val br: BufferedReader
    val scan: Scanner

    //Index of the loops
    var i = 0

    //Places to store each line and the finished product
    var str: Array<String>
    val result: Array<DoubleArray>

    //File I/O sometimes causes errors
    try {
        //Scan the file into Java
        br = BufferedReader(FileReader(fileName))

        //Size the result to the length of the file
        scan = Scanner(File(fileName))
        scan.useDelimiter("\n")
        while (scan.hasNext()) {
            scan.next()
            i++
        }
        result = Array(i) { DoubleArray(3) }

        //Read each line and parse it into 3 doubles
        i = 0
        var line: String = br.readLine()
        while (line != null) {
            str = line.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (j in 0..2)
                result[i][j] = java.lang.Double.parseDouble(str[j])
            i++
        }

        //Close the file reader and return the result
        br.close()
        return result

        //Notify user of exceptions
    } catch (e: FileNotFoundException) {
        notifyError("File $fileName not found!")
    } catch (e: IOException) {
        notifyError("IOException in scanCSV while scanning $fileName!")
    }

    //Return empty array if there was an exception
    return Array(0) { DoubleArray(0) }
}

private fun notifyError(message: String) {
    SmartDashboard.putString("Latest Error", message)
    println(message)
}

//work in progress
private fun startFilling(profile: Array<DoubleArray>, totalRows: Int = profile.size){

    val point: CANTalon.TrajectoryPoint




}
