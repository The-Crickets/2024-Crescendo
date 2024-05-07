// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkBase.IdleMode;
import com.revrobotics.CANSparkLowLevel.MotorType;

import static frc.robot.Constants.*;

/**
 * The class responsible for moving the shooter arm and intake systems.
 * <p>Functions include the {@link #ShooterArmHandler constructor}, {@link #getEncoderData}, {@link #moveShooterArmAuto}, {@link #getToZero}, {@link #moveShooterArm moveShooterArm (main)}, and {@link #stopArm}.
 */
public class ShooterArmHandler extends SubsystemBase {

  private CANSparkMax leftChainMotorController, rightChainMotorController, topShooterMotorController;
  private double leftspeed, rightspeed, position, intake_speed, lastPosition;
  private String mode, intakeSpeed, lastIntakeSpeed;
  private boolean atLimit, previousAtLimit;
  private SendableChooser<String> encoderOptions;

  /** Creates a new ShooterArmHandler */
  public ShooterArmHandler() {
    //Defines motor controllers
    leftChainMotorController = new CANSparkMax(LEFT_CHAIN_MOTOR_CONTROLLER_ID, MotorType.kBrushless);
    rightChainMotorController = new CANSparkMax(RIGHT_CHAIN_MOTOR_CONTROLLER_ID, MotorType.kBrushless);
    topShooterMotorController = new CANSparkMax(TOP_LAUNCHER_MOTOR_CONTROLLER_ID, MotorType.kBrushed);
    
    //Sets the idle mode
    leftChainMotorController.setIdleMode(IdleMode.kBrake);
    rightChainMotorController.setIdleMode(IdleMode.kBrake);
    topShooterMotorController.setIdleMode(IdleMode.kCoast);
    leftChainMotorController.setInverted(false);

    //Creates the encoder options chooser on the dashboard
    encoderOptions = new SendableChooser<>();
    encoderOptions.setDefaultOption("Limiting Movement", "Limit");
    encoderOptions.addOption("Reset Encoder", "Zero");
    encoderOptions.addOption("Emergency Override", "Override");
    encoderOptions.addOption("Go to Zero", "Go");
    SmartDashboard.putData("Arm Encoder: ", encoderOptions);
    SmartDashboard.putNumber("Chain Position: ", 0);
    SmartDashboard.putBoolean(" Shooter at Limit", false);
    SmartDashboard.putString("Intake Status: ", "Stopped");

    intakeSpeed = "Stopped";
    lastIntakeSpeed = "Stopped";
    atLimit = false;
    previousAtLimit = false;
  }

  /**
   * This class just returns the ecoder position of the left motor controller, which is used for the entire shooter arm.
   * @return {double} Encoder position
   */
  public double getEncoderData() {
    return leftChainMotorController.getEncoder().getPosition();
  }

  /**
   * This function is used by the autonomous mode to move the arm between two encoder values.
   * @param lowerAngle (minimum position)
   * @param upperAngle (maximum position)
   * @return {boolean} isArmInPosition
   */
  public boolean moveShooterArmAuto(double lowerAngle, double upperAngle) {
    
    boolean armInPosition;

    if (getEncoderData() > upperAngle + 1) {
      moveShooterArm(-0.6, 0, 0);
      armInPosition = false;
    } else if (getEncoderData() < lowerAngle - 1) {
      moveShooterArm(0.6, 0, 0);
      armInPosition = false;
    } else if (getEncoderData() > lowerAngle - 1 && getEncoderData() < lowerAngle) {
      moveShooterArm(0.2, 0, 0);
      armInPosition = false;
    } else if (getEncoderData() > upperAngle + 1 && getEncoderData() < upperAngle) {
      moveShooterArm(-0.2, 0, 0);
      armInPosition = false;
    } else {
      stopArm();
      armInPosition = true;
    }
    return armInPosition;
  }

  /**
   * This function moves the arm until it gets to its current zero position.
   * It can only be activated through the dashboard.
   */
  public void getToZero() {
    position = getEncoderData();
    if (position > 0.2) {
      leftChainMotorController.set(-0.1);
      rightChainMotorController.set(0.1);
    } else if (position < 0.2 && position > 0) {
      leftChainMotorController.set(-0.03);
      rightChainMotorController.set(0.03);
    } else if (position < -0.2) {
      leftChainMotorController.set(0.1);
      rightChainMotorController.set(-0.1);
    } else if (position > -0.2 && position < 0) {
      leftChainMotorController.set(0.03);
      rightChainMotorController.set(-0.03);
    } else {
      stopArm();
    }
  }

  /**
   * This is the main function of the class.
   * The moveShooterArm function moves the shooter arm and the intake.
   * The left joystick controls the primary arm and the triggers control the intake motor.
   * 
   * @param leftJoyY (Left joystick y-value that moves the main arm)
   * @param rightTrigger (Right trigger press value that makes the intake intake)
   * @param leftTrigger (Left trigger press value that makes the intake expell)
   */
  public void moveShooterArm(double leftJoyY, double rightTrigger, double leftTrigger) {
    
    //Gets the mode from the dashboard
    mode = encoderOptions.getSelected();

    //Sets the encoder to zero if that mode is selected
    if (mode == "Zero") {
      leftChainMotorController.getEncoder().setPosition(0);
    }

    //Calculates speeds and get the encoder position
    intake_speed = -(rightTrigger - leftTrigger) * INTAKE_SPEED;
    position = getEncoderData();
    leftspeed = leftJoyY * SHOOTER_ARM_SPEED;
    rightspeed = -leftspeed;
    
    if (mode != "Go" && mode != "Zero") {
      if (mode == "Limit") {
        //Tests the position and determines whether the motors are at their limit
        if (position < 47 && leftspeed > 0 || position > -100 && leftspeed < 0) {
          atLimit = false;
          leftChainMotorController.set(leftspeed);
          rightChainMotorController.set(rightspeed);
        } else if (position >= 47 || position <= -100) {
          atLimit = true;
          leftChainMotorController.stopMotor();
          rightChainMotorController.stopMotor();
        } 
        //Override mode moves them regardless
      } else if (mode == "Override") {
        leftChainMotorController.set(leftspeed);
        rightChainMotorController.set(rightspeed);
      }
      //Helps to prevent stick drift
      if (leftJoyY > -0.15 && leftJoyY < 0.15) {
        leftChainMotorController.stopMotor();
        rightChainMotorController.stopMotor();
      }
    } else if (mode == "Go") {
      getToZero();
    }
    
    //Moves the intake
    if (intake_speed > 0) {
      intakeSpeed = "Expelling";
    } else if (intake_speed < 0) {
      intakeSpeed = "Intaking";
    } else {
      intakeSpeed = "Stopped";
    }
    topShooterMotorController.set(intake_speed);
  }

  /**
   * A simple function to stop the arm and intake.
   */
  public void stopArm() {
    leftChainMotorController.stopMotor();
    rightChainMotorController.stopMotor();
    topShooterMotorController.stopMotor();
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run

    //Sends Position to dashboard
    if (position != lastPosition) {
      SmartDashboard.putNumber("Chain Position: ", position);
      lastPosition = position;
    }

    //Sends data about the intake to dashboard
    if (intakeSpeed != lastIntakeSpeed) {
      SmartDashboard.putString("Intake Status: ", intakeSpeed);
      lastIntakeSpeed = intakeSpeed;
    }

    if (atLimit != previousAtLimit) {
      SmartDashboard.putBoolean(" Shooter at Limit", atLimit);
      previousAtLimit = atLimit;
    }
  }
}
