// Define UI elements
var ui = {
    timer: document.getElementById('timer'),
    robotState: document.getElementById('robot-state').firstChild,
    robot: {
      tractionFront: document.getElementById('traction-front'),
      tractionRear: document.getElementById('traction-rear'),
      intakeArm: document.getElementById('intake-arm')
    },
    gyro: {
        container: document.getElementById('gyro'),
        val: 0,
        offset: 0,
        visualVal: 0,
        arm: document.getElementById('gyro-arm'),
        number: document.getElementById('gyro-number')
    },
    tuning: {
        list: document.getElementById('tuning'),
        button: document.getElementById('tuning-button'),
        name: document.getElementById('name'),
        value: document.getElementById('value'),
        set: document.getElementById('set'),
        get: document.getElementById('get')
    },
    autoStartPos: document.getElementById('auto-start'),
    autoStrat: document.getElementById('auto-strat')
};
let address = document.getElementById('connect-address'),
    connect = document.getElementById('connect');

// Sets function to be called on NetworkTables connect. Commented out because it's usually not necessary.
// NetworkTables.addWsConnectionListener(onNetworkTablesConnection, true);

// Sets function to be called when robot dis/connects
NetworkTables.addRobotConnectionListener(onRobotConnection, false);

// Sets function to be called when any NetworkTables key/value changes
NetworkTables.addGlobalListener(onValueChanged, true);

let escCount = 0;
onkeydown = key => {
    if (key.key === 'Escape') {
        setTimeout(() => { escCount = 0; }, 400);
        escCount++;
        console.log(escCount);
        if (escCount === 2) {
            document.body.classList.toggle('login-close', true);
        }
    }
    else
        console.log(key.key);
};
if (noElectron) {
    document.body.classList.add('login-close');
}

function onRobotConnection(connected) {
    var state = connected ? 'Robot connected!' : 'Robot disconnected.';
    console.log(state);
    ui.robotState.data = state;
    if (!noElectron) {
        if (connected) {
            // On connect hide the connect popup
            document.body.classList.toggle('login-close', true);
        }
        else {
            // On disconnect show the connect popup
            document.body.classList.toggle('login-close', false);
            // Add Enter key handler
            address.onkeydown = ev => {
                if (ev.key === "Enter") {
                    connect.click();
                }
            };
            // Enable the input and the button
            address.disabled = false;
            connect.disabled = false;
            connect.firstChild.data = "Connect";
            // Add the default address and select xxxx
            address.value = "roborio-401-frc.local";
            address.focus();
            address.setSelectionRange(0, address.value.length);
            // On click try to connect and disable the input and the button
            connect.onclick = () => {
                ipc.send('connect', address.value);
                address.disabled = true;
                connect.disabled = true;
                connect.firstChild.data = "Connecting";
            };
        }
    }
}

/**** KEY Listeners ****/

// Gyro rotation
let updateGyro = (key, value) => {
    ui.gyro.val = value;
    ui.gyro.visualVal = Math.floor(ui.gyro.val - ui.gyro.offset);
    if (ui.gyro.visualVal < 0) {
        ui.gyro.visualVal += 360;
    }
    ui.gyro.arm.style.transform = `rotate(${ui.gyro.visualVal}deg)`;
    ui.gyro.number.innerHTML = ui.gyro.visualVal + 'º';
};
NetworkTables.addKeyListener('/SmartDashboard/gyro_angle', updateGyro);

NetworkTables.addKeyListener('/SmartDashboard/time_running', (key, value) => {
    // Sometimes, NetworkTables will pass booleans as strings. This corrects for that.
    if (typeof value === 'string')
        value = value === "true";
    // When this NetworkTables variable is true, the timer will start.
    // You shouldn't need to touch this code, but it's documented anyway in case you do.
    var s = 135;
    if (value) {
        // Make sure timer is reset to black when it starts
        ui.timer.style.color = 'black';
        // Function below adjusts time left every second
        var countdown = setInterval(function () {
            s--; // Subtract one second
            // Minutes (m) is equal to the total seconds divided by sixty with the decimal removed.
            var m = Math.floor(s / 60);
            // Create seconds number that will actually be displayed after minutes are subtracted
            var visualS = (s % 60);
            // Add leading zero if seconds is one digit long, for proper time formatting.
            visualS = visualS < 10 ? '0' + visualS : visualS;
            if (s < 0) {
                // Stop countdown when timer reaches zero
                clearTimeout(countdown);
                return;
            }
            else if (s <= 15) {
                // Flash timer if less than 15 seconds left
                ui.timer.style.color = (s % 2 === 0) ? '#FF3030' : 'transparent';
            }
            else if (s <= 30) {
                // Solid red timer when less than 30 seconds left.
                ui.timer.style.color = '#FF3030';
            }
            ui.timer.firstChild.data = m + ':' + visualS;
        }, 1000);
    }
    else {
        s = 135;
    }
    NetworkTables.putValue(key, false);
});

NetworkTables.addKeyListener('/SmartDashboard/Match Time', (key, value) => {
    // Make sure timer is reset to orange when it starts
    ui.timer.style.color = 'orange';
    // Minutes (m) is equal to the total seconds divided by sixty with the decimal removed.
    var m = Math.floor(s / 60);
    // Create seconds number that will actually be displayed after minutes are subtracted
    var visualS = (s % 60);
    // Add leading zero if seconds is one digit long, for proper time formatting.
    visualS = visualS < 10 ? '0' + visualS : visualS;
    if (value <= 15) {
        // Flash timer if less than 15 seconds left
        ui.timer.style.color = (s % 2 === 0) ? '#FF3030' : 'transparent';
    }
    else if (value <= 30) {
        // Solid red timer when less than 30 seconds left.
        ui.timer.style.color = '#FF3030';
    }
    ui.timer.firstChild.data = m + ':' + visualS;
});

// update robot state
NetworkTables.addKeyListener('/SmartDashboard/strafing_enabled', (key, value) => {
    if (typeof value === 'string')
        value = value === "true";

    if (value) {
        ui.robot.tractionFront.style.cy = 400;
        ui.robot.tractionRear.style.cy = 400;
        //ui.robot.tractionFront.style.color = 'blue';
        //ui.robot.tractionRear.style.color = 'blue';
    } else {
        ui.robot.tractionFront.style.cy = 410;
        ui.robot.tractionRear.style.cy = 410;
        //ui.robot.tractionFront.style.color = 'orange';
        //ui.robot.tractionRear.style.color = 'orange';
    }
});

// Load list of prewritten autonomous modes
NetworkTables.addKeyListener('/SmartDashboard/Starting Position/options', (key, value) => {
    // Clear previous list
    while (ui.autoStartPos.firstChild) {
        ui.autoStartPos.removeChild(ui.autoStartPos.firstChild);
    }
    // Make an option for each autonomous mode and put it in the selector
    for (let i = 0; i < value.length; i++) {
        var option = document.createElement('option');
        option.appendChild(document.createTextNode(value[i]));
        ui.autoStartPos.appendChild(option);
    }
    // Set value to the already-selected mode. If there is none, nothing will happen.
    ui.autoStartPos.value = NetworkTables.getValue('/SmartDashboard/Starting Position/selected');
});
NetworkTables.addKeyListener('/SmartDashboard/Strategy/options', (key, value) => {
    // Clear previous list
    while (ui.autoStrat.firstChild) {
        ui.autoStrat.removeChild(ui.autoStrat.firstChild);
    }
    // Make an option for each autonomous mode and put it in the selector
    for (let i = 0; i < value.length; i++) {
        var option = document.createElement('option');
        option.appendChild(document.createTextNode(value[i]));
        ui.autoStrat.appendChild(option);
    }
    // Set value to the already-selected mode. If there is none, nothing will happen.
    ui.autoStrat.value = NetworkTables.getValue('/SmartDashboard/Strategy/selected');
});

// Load list of prewritten autonomous modes
NetworkTables.addKeyListener('/SmartDashboard/Starting Position/selected', (key, value) => {
    ui.autoStartPos.value = value;
});
NetworkTables.addKeyListener('/SmartDashboard/Strategy/selected', (key, value) => {
    ui.autoStrat.value = value;
});

// Global Listener
function onValueChanged(key, value, isNew) {
    // Sometimes, NetworkTables will pass booleans as strings. This corrects for that.
    if (value == 'true') {
        value = true;
    }
    else if (value == 'false') {
        value = false;
    }
    // The following code manages tuning section of the interface.
    // This section displays a list of all NetworkTables variables (that start with /SmartDashboard/) and allows you to directly manipulate them.
    var propName = key.substring(16, key.length);
    // Check if value is new and doesn't have a spot on the list yet
    if (isNew && !document.getElementsByName(propName)[0]) {
        // Make sure name starts with /SmartDashboard/. Properties that don't are technical and don't need to be shown on the list.
        if (/^\/SmartDashboard\//.test(key)) {
            // Make a new div for this value
            var div = document.createElement('div'); // Make div
            ui.tuning.list.appendChild(div); // Add the div to the page
            var p = document.createElement('p'); // Make a <p> to display the name of the property
            p.appendChild(document.createTextNode(propName)); // Make content of <p> have the name of the NetworkTables value
            div.appendChild(p); // Put <p> in div
            var input = document.createElement('input'); // Create input
            input.name = propName; // Make its name property be the name of the NetworkTables value
            input.value = value; // Set
            // The following statement figures out which data type the variable is.
            // If it's a boolean, it will make the input be a checkbox. If it's a number,
            // it will make it a number chooser with up and down arrows in the box. Otherwise, it will make it a textbox.
            if (typeof value === "boolean") {
                input.type = 'checkbox';
                input.checked = value; // value property doesn't work on checkboxes, we'll need to use the checked property instead
                input.onchange = function () {
                    // For booleans, send bool of whether or not checkbox is checked
                    NetworkTables.putValue(key, this.checked);
                };
            }
            else if (!isNaN(value)) {
                input.type = 'number';
                input.onchange = function () {
                    // For number values, send value of input as an int.
                    NetworkTables.putValue(key, parseInt(this.value));
                };
            }
            else {
                input.type = 'text';
                input.onchange = function () {
                    // For normal text values, just send the value.
                    NetworkTables.putValue(key, this.value);
                };
            }
            // Put the input into the div.
            div.appendChild(input);
        }
    }
    else {
        // Find already-existing input for changing this variable
        var oldInput = document.getElementsByName(propName)[0];
        if (oldInput) {
            if (oldInput.type === 'checkbox') {
                oldInput.checked = value;
            } else {
                oldInput.value = value;
            }
        }
        else {
            console.log('Error: Non-new variable ' + key + ' not present in tuning list!');
        }
    }

}
// Reset gyro value to 0 on click
ui.gyro.container.onclick = function () {
    // Store previous gyro val, will now be subtracted from val for callibration
    ui.gyro.offset = ui.gyro.val;
    // Trigger the gyro to recalculate value.
    updateGyro('/SmartDashboard/gyro_angle', ui.gyro.val);
};
// Open tuning section when button is clicked
ui.tuning.button.onclick = function () {
    if (ui.tuning.list.style.display === 'none') {
        ui.tuning.list.style.display = 'block';
    } else {
        ui.tuning.list.style.display = 'none';
    }
};
// Manages get and set buttons at the top of the tuning pane
ui.tuning.set.onclick = function () {
    // Make sure the inputs have content, if they do update the NT value
    if (ui.tuning.name.value && ui.tuning.value.value) {
        NetworkTables.putValue('/SmartDashboard/' + ui.tuning.name.value, ui.tuning.value.value);
    }
};
ui.tuning.get.onclick = function () {
    ui.tuning.value.value = NetworkTables.getValue(ui.tuning.name.value);
};
// Update NetworkTables when autonomous selector is changed
ui.autoStartPos.onchange = function () {
    NetworkTables.putValue('/SmartDashboard/Starting Position/selected', this.value);
    console.log('help');
};
ui.autoStrat.onchange = function () {
    NetworkTables.putValue('/SmartDashboard/Strategy/selected', this.value);
};
