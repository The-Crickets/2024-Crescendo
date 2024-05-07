// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import static frc.robot.Constants.*;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;

/**
 * This class is responsible for the right hook on the robot.
 * It determines when to move the arm up or down based on button inputs and encoder values.
 * <p>Its functions include the {@link #RightArmButtonHandler constructor}, {@link #moveArm moveArm (main)}, and {@link #stopArm}.
 */
public class RightArmButtonHandler extends SubsystemBase {

  private CANSparkMax rightArmMotorController;
  private SendableChooser<String> rightEncoderOptions;
  private String encoderSetting, movement, lastMovement;
  private double position, lastPosition;
  private boolean inverted, atLimit, previousAtLimit;

  /** Creates a new RightArmButtonHandler. */
  public RightArmButtonHandler() {
    inverted = false;
    position = 0;
    lastPosition = 0;

    rightArmMotorController = new CANSparkMax(RIGHT_ARM_MOTOR_CONTROLLER_ID, MotorType.kBrushless);
    rightEncoderOptions = new SendableChooser<>();
    rightEncoderOptions.setDefaultOption("Limiting RArm Movement", "Limit");
    rightEncoderOptions.addOption("Reset Encoder", "Zero");
    rightEncoderOptions.addOption("Emergency Override", "Override");
    SmartDashboard.putData("RHook: ", rightEncoderOptions);
    SmartDashboard.putNumber("Right Arm Position", 0);
    SmartDashboard.putString("Right Arm: ", "Stopped");
    SmartDashboard.putBoolean(" Right Arm at Limit", false);
  }

  /**
   * <p>The primary function of the right arm.
   * Determines whether to move the arm up or down.
   * Also, it handles encoder limits and different modes.
   */
  public void moveArm() {

    //Gets the desired encoder option
    encoderSetting = rightEncoderOptions.getSelected();

    //Resets the encoder value if needed.
    if (encoderSetting == "Zero") {
      rightArmMotorController.getEncoder().setPosition(0);
    }

    //Gets the actual encoder value
    position = rightArmMotorController.getEncoder().getPosition();

    //The primary condition for limiting arm movement
    //This will only run if the mode is set to "limiting movement"
    if (encoderSetting != "Override" && encoderSetting != "Zero") {
      //Moves the arm down under the right conditions
      if (inverted && position < 0) {
        //Moves the arm down
        rightArmMotorController.set(ARM_SPEED);
        //Sends data about the arm to the dashboard
        movement = "Moving Down";
        atLimit = false;
      } else if (!inverted && position > -327) {
        //Moves the arm upwards
        rightArmMotorController.set(-ARM_SPEED);
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
        rightArmMotorController.set(ARM_SPEED);
        movement = "Moving Down";
      } else if (!inverted) {
        rightArmMotorController.set(-ARM_SPEED);
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
  }
  
  /**
   * A quick and easy method to stop the Left Arm.
   */
  public void stopArm() {
    rightArmMotorController.stopMotor();
    movement = "Stopped";
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run

    //Sends the value to the dashboard
    if (position != lastPosition) {
      SmartDashboard.putNumber("Right Arm Position", position);
      lastPosition = position;
    }

    if (atLimit != previousAtLimit) {
      SmartDashboard.putBoolean(" Right Arm at Limit", atLimit);
      previousAtLimit = atLimit;
    }

    if (movement != lastMovement) {
      SmartDashboard.putString("Right Arm: ", movement);
      lastMovement = movement;
    }
  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }
}
