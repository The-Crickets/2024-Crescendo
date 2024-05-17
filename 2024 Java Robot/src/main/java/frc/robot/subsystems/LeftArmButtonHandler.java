// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import static frc.robot.Constants.*;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;

/**
 * This class is responsible for the left hook on the robot.
 * It determines when to move the arm up or down based on button inputs and encoder values.
 * <p>Its functions include the {@link #LeftArmButtonHandler constructor}, {@link #moveArm moveArm (main)}, and {@link #stopArm}.
 */
public class LeftArmButtonHandler extends SubsystemBase {

  private CANSparkMax leftArmMotorController;
  private SendableChooser<String> leftEncoderOptions;
  private String encoderSetting, movement, lastMovement;
  private double position, lastPosition;
  private boolean inverted, atLimit, previousAtLimit;

  /** Creates a new LeftArmButtonHandler. */
  public LeftArmButtonHandler() {
    inverted = false;

    position = 0;
    lastPosition = 0;

    //Creates a new motor controller object
    leftArmMotorController = new CANSparkMax(LEFT_ARM_MOTOR_CONTROLLER_ID, MotorType.kBrushless);

    //Inverts the motor
    leftArmMotorController.setInverted(true);

    //Creates and defines options for a new chooser on the dashboard.
    leftEncoderOptions = new SendableChooser<>();
    leftEncoderOptions.setDefaultOption("Limiting LArm Movement", "Limit");
    leftEncoderOptions.addOption("Reset Encoder", "Zero");
    leftEncoderOptions.addOption("Emergency Override", "Override");
    SmartDashboard.putData("LHook: ", leftEncoderOptions);
    SmartDashboard.putNumber("Left Arm Position", 0);
    SmartDashboard.putString("Left Arm: ", "Stopped");
    SmartDashboard.putBoolean(" Left Arm at Limit", false);
    SmartDashboard.putBoolean(" Arms Inverted", inverted);
  }

  /**
   * <p>The primary function of the left arm.
   * Determines whether to move the arm up or down.
   * Also, it handles encoder limits and different modes.
   */
  public void moveArm() {

    //Gets the desired encoder option
    encoderSetting = leftEncoderOptions.getSelected();

    //Resets the encoder value if needed.
    if (encoderSetting == "Zero") {
      leftArmMotorController.getEncoder().setPosition(0);
    }

    //Gets the actual encoder value
    position = leftArmMotorController.getEncoder().getPosition();

    //The primary condition for limiting arm movement
    //This will only run if the mode is set to "limiting movement"
    if (encoderSetting == "Limit") {
      //Moves the arm down under the right conditions
      if (inverted && position < 0) {
        //Moves the arm down
        leftArmMotorController.set(ARM_SPEED);
        //Sends data about the arm to the dashboard
        movement = "Moving Down";
        atLimit = false;
      } else if (!inverted && position > -300) {
        //Moves the arm upwards
        leftArmMotorController.set(-ARM_SPEED);
        //Sends arm data to the dashboard
        movement = "Moving Up";
        atLimit = false;
      } else {
        //Indicates that the arm is at its limit and stops motor
        stopArm();
        atLimit = true;
      }
      //The override just moves the Arm without considering the limits.
    } else if (encoderSetting == "Override") {
      atLimit = false;
      if (inverted) {
        leftArmMotorController.set(ARM_SPEED);
        movement = "Moving Down";
      } else if (!inverted) {
        leftArmMotorController.set(-ARM_SPEED);
        movement = "Moving Up";
      }
    }
  }

  /**
   * The invert Hook function just changes the invert value.
   * There's not much else to say.
   */
  public void invertHook() {
    //Inverts drive value
    inverted = !inverted;
    SmartDashboard.putBoolean(" Arms Inverted", inverted);
  }

  /**
   * A quick and easy method to stop the Left Arm.
   */
  public void stopArm() {
    leftArmMotorController.stopMotor();
    movement = "Stopped";
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run

    //Sends the value to the dashboard
    if (position != lastPosition) {
      SmartDashboard.putNumber("Left Arm Position", position);
      lastPosition = position;
    }

    if (movement != lastMovement) {
      SmartDashboard.putString("Left Arm: ", movement);
      lastMovement = movement;
    }

    if (atLimit != previousAtLimit) {
      SmartDashboard.putBoolean(" Left Arm at Limit", atLimit);
      previousAtLimit = atLimit;
    }
  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }
}
