# CopperheadDash
CopperheadDash is a html/css/js dashboard based off of the [FRCDashboard](https://github.com/FRCDashboard/FRCDashboard) project.

You need nodejs 6.10 to use this dashboard!

## Building
1. Get `npm`
2. Run `npm i` to install all of the dependencies
3. Run `npm run dist` to pack the entire application into a single file
4. The Run the setup file located in dist/ on the system where you want to install the dashboard

## Running
Connect to your robot's network if you haven't already.
The preferred method of using the dashboard is to run it using the [Electron](http://electron.atom.io) framework. Your dashboard will be its own application, and will be easy to manipulate.

While in the dashboard directory, run:

    npm start

This will start a Python server and open the dashboard application. Note that you don't have to close and reopen the application every time you make a change, you can just press `Ctrl+R` (`Cmd+R` on Mac) to refresh the application.

## Authors
* [Zach Kozar](https://github.com/Masterzach32)
Creators of FRCDashboard:
* [Erik Boesen](https://github.com/ErikBoesen) is the primary developer of FRC Dashboard.
* [Team 1418](https://github.com/frc1418) used earlier versions of this code in 2015 and 2016.
* [Leon Tan](https://github.com/lleontan) led the original 1418 UI team, coded pynetworktables2js, and developed a browser-based UI which was years later reworked to create FRC Dashboard.
* [Dustin Spicuzza](https://github.com/virtuald) leads the [RobotPy](https://github.com/robotpy) project mentored team 1418 through much of FRC Dashboard's genesis.

## Modifying
FRC Dashboard is designed to be modified for your team's purposes, so you're allowed to do whatever you think is best for you. However, it would be good if you could fork this repository or copy it to another. This will allow you to easily pull updates when they occur, and if you fork it it helps us tell who's using it.

This software is licensed under the GNU GPL v3 license. Basically, modify as much as you like, as long as you give credit where it's due and don't hold us accountable for anything. More information in `LICENSE`.
