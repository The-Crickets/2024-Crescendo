// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

//Wpilib imports
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

//3rd party imports
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;

//Uncomment these lines in case you need to use the gyroscope.
//import com.kauailabs.navx.frc.AHRS;
//import edu.wpi.first.wpilibj.SerialPort;

import static frc.robot.Constants.*;

import java.lang.Math;

/**
 * The Drivetrain is the class responsible for handling the driving motors.
 * It calculates the driving speed, determines drive type, and more.
 * <p>Its functions include {@link #Drivetrain() the constructor}, {@link #invertDrive}, {@link #driveTank the main function}, {@link #autoTurn autoTurn}, and {@link #stopAll stopAll}.
 */
public class Drivetrain extends SubsystemBase {
  
  //Define motor controllers
  private CANSparkMax frontLeftMotorController, backLeftMotorController;
  private CANSparkMax frontRightMotorController, backRightMotorController;
  private DifferentialDrive tankDrive;

  //Define local variables
  private double driveSpeedLeft, driveSpeedRight, turnSpeed;
  private String drive;
  private boolean invert;
  SendableChooser<String> drive_chooser;


  /** Creates a new Drivetrain. */
  public Drivetrain() {

    //Initializes the Driver Chooser
    drive_chooser = new SendableChooser<>();

    //Sets the options for the drive chooser
    //The names are what's listed on the dashboard.
    //The objects are what value is associated with that selection.
    drive_chooser.setDefaultOption("Arcade Drive", "Arcade");
    drive_chooser.addOption("Tank Drive", "Tank");
    drive_chooser.addOption("Curvature w/ TIP", "Curvature1");
    drive_chooser.addOption("Curvature w/out TIP", "Curvature2");
    drive_chooser.addOption("Joke Drive", "Suffer");

    //Sends the choices to dashboard
    SmartDashboard.putData("Drive Modes: ", drive_chooser);

    // Initialize motor controllers
    frontLeftMotorController = new CANSparkMax(FRONT_LEFT_MOTOR_CONTROLLER_ID, MotorType.kBrushed);
    backLeftMotorController = new CANSparkMax(BACK_LEFT_MOTOR_CONTROLLER_ID, MotorType.kBrushed);
    frontRightMotorController = new CANSparkMax(FRONT_RIGHT_MOTOR_CONTROLLER_ID, MotorType.kBrushed);
    backRightMotorController = new CANSparkMax(BACK_RIGHT_MOTOR_CONTROLLER_ID, MotorType.kBrushed);

    // Tells these motors to mimic the front ones
    backLeftMotorController.follow(frontLeftMotorController);
    backRightMotorController.follow(frontRightMotorController);

    // Inverts the values of the right side
    // Since the back motor follows the front one, there's no need to invert it
    frontRightMotorController.setInverted(true);

    // Initialize tank drive
    tankDrive = new DifferentialDrive(frontLeftMotorController, frontRightMotorController);

    //Sets the invert value to false
    invert = false;
    SmartDashboard.putBoolean(" Inverted Drive", invert);
  }

  /**
   * A basic method that returns the selected drive on the dashboard.
   * @return {String} Selected drive mode
   */
  public String getDriveMode() {
    return drive_chooser.getSelected();
  }

  /**
   * The invert drive function just changes the invert value.
   * There's not much else to say.
   */
  public void invertDrive() {
    //Inverts drive value
    invert = !invert;
    SmartDashboard.putBoolean(" Inverted Drive", invert);
  }

  /**
   * 
   * @param leftJoyY is the left joystick y-value
   * @param rightJoyX is the right joystick x-value (for turning)
   * @param rightJoyY is the right joystick y-value (only used in tank drive)
   * @param forceArcade forces the use of arcade drive (only in auto)
   * <p>
   * <p>The driveTank function is the primary function of the drivetrain.
   * It calculates the different motor speeds and determines what drive type to use.
   */
  public void driveTank(double leftJoyY, double rightJoyX, double rightJoyY, boolean forceArcade) {

    //An attempt to stop joystick drift
    if (leftJoyY < 0.1 && leftJoyY > -0.1) {
      leftJoyY = 0;
    }
    if (rightJoyY < 0.1 && rightJoyY > -0.1) {
      rightJoyY = 0;
    }
    if (rightJoyX < 0.1 && rightJoyX > -0.1) {
      rightJoyX = 0;
    }

    //Determines the drive type
    //Force arcade is only used by the autonomous mode
    if (!forceArcade) {
      drive = getDriveMode();
    } else {
      drive = "Arcade";
    }

    //defines the different speeds.
    driveSpeedRight = Math.pow(Math.abs(rightJoyY), (Math.abs(rightJoyY) + 0.5))* DRIVE_MULTIPLIER;
    driveSpeedLeft = Math.pow(Math.abs(leftJoyY), (Math.abs(leftJoyY) + 0.5)) * DRIVE_MULTIPLIER;

    //Determines which turn speed to use based on drive speed
    //This value isn't used in Tank drive
    if (driveSpeedLeft > 0.3 && drive != "Tank") {
      turnSpeed = Math.pow(Math.abs(rightJoyX), (Math.abs(rightJoyX) + 0.5)) * TURN_MULTIPLIER;
    } else if (driveSpeedLeft <= 0.3 && drive != "Tank") {
      turnSpeed = Math.pow(Math.abs(rightJoyX), (Math.abs(rightJoyX) + 0.5)) * TURN_IN_PLACE_MULTIPLIER;
    } else {
      turnSpeed = 0;
    }

    //Inverts the speed values if they were originally negative
    if (leftJoyY < 0) {
      driveSpeedLeft *= -1;
    } if (rightJoyY < 0) {
      driveSpeedRight *= -1;
    } if (rightJoyX < 0 && drive != "Tank") {
      turnSpeed *= -1;
    }

    //Inverts the values if L3 is pressed
    if (invert) {
      driveSpeedLeft *= -1;
      driveSpeedRight *= -1;
    }

    //Sets the maximum output possible
    tankDrive.setMaxOutput(MAX_MOTOR_OUTPUT);

    //Determines what kind of drive mode to use
    if (drive == "Arcade") {
      tankDrive.arcadeDrive(driveSpeedLeft, turnSpeed);
    } else if (drive == "Tank") {
      tankDrive.tankDrive(driveSpeedLeft, driveSpeedRight);
    } else if (drive == "Curvature1") {
      tankDrive.curvatureDrive(driveSpeedLeft, turnSpeed, true);
    } else if (drive == "Curvature2") {
      tankDrive.curvatureDrive(driveSpeedLeft, turnSpeed, false);
    }
  }

  /**
   * <em>why? just why?</em>
   * It's not worth the time to document.
   * @param dpad
   * @param rightJoyX
   */
  public void jokeDrive(int dpad) {

    //Switch and case are like if and elif but better.
    //The switch starts the statement with the parameter
    //Each case is a different condition that the parameters could meet with instructions for each
    //The default case is like an else statement. It handles unlisted cases
    switch (dpad) {
      case 0:
        if (driveSpeedLeft > -MAX_MOTOR_OUTPUT) {
          driveSpeedLeft -= 0.1;
        }
        turnSpeed = 0;
        break;

      case 45:
        if (driveSpeedLeft > -0.75) {
          driveSpeedLeft -= 0.1;
        } else if (driveSpeedLeft < -0.75) {
          driveSpeedLeft += 0.1;
        }
        turnSpeed = 0.5;
        break;
      
      case 90:
        driveSpeedLeft = 0;
        turnSpeed = 0.95;
        break;
      
      case 135:
        if (driveSpeedLeft < 0.75) {
          driveSpeedLeft += 0.1;
        } else if (driveSpeedLeft > 0.75) {
          driveSpeedLeft -= 0.1;
        }
        turnSpeed = -0.5;
        break;
      
      case 180:
        if (driveSpeedLeft < 0.9) {
          driveSpeedLeft += 0.1;
        }
        turnSpeed = 0;
        break;

      case 225:
        if (driveSpeedLeft < 0.75) {
          driveSpeedLeft += 0.1;
        } else if (driveSpeedLeft > 0.75) {
          driveSpeedLeft -= 0.1;
        }
        turnSpeed = 0.5;
        break;
      
      case 270:
        driveSpeedLeft = 0;
        turnSpeed = -0.95;
        break;
      
      case 315:
        if (driveSpeedLeft > -0.75) {
          driveSpeedLeft -= 0.1;
        } else if (driveSpeedLeft < -0.75) {
          driveSpeedLeft += 0.1;
        }
        turnSpeed = -0.5;
        break;

      default:
        if (driveSpeedLeft > 0) {
          driveSpeedLeft -= 0.1;
        } else if (driveSpeedLeft < 0) {
          driveSpeedLeft += 0.1;
        }
        turnSpeed = 0;
        break;
    }
    if (invert){
      tankDrive.arcadeDrive(-driveSpeedLeft, turnSpeed);
    } else {
      tankDrive.arcadeDrive(driveSpeedLeft, turnSpeed);
    }
  }

  public void autoTurn(boolean invertTurn) {
    if (invertTurn) {
      tankDrive.arcadeDrive(0, -0.75);
    } else {
      tankDrive.arcadeDrive(0, 0.75);
    }
  }

  /**
   * A method to stop all motors in the drivetrain.
   */
  public void stopAll() {
    frontLeftMotorController.stopMotor();
    frontRightMotorController.stopMotor();
    backLeftMotorController.stopMotor();
    backRightMotorController.stopMotor();
  }


  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
