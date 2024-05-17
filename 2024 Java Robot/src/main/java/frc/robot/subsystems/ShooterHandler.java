// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkBase.IdleMode;
import com.revrobotics.CANSparkLowLevel.MotorType;

import static frc.robot.Constants.*;

/**
 * This class handles the actual shooter (grey wheels on the front).
 * <p>Functions include the {@link #ShooterHandler() constructor}, {@link #shootFast()}, {@link #shootNormal()}, {@link #shootSlow()}, and {@link #shootStop()}.
 */
public class ShooterHandler extends SubsystemBase {

  private CANSparkMax leftShooterMotorController, rightShooterMotorController;
  private String speed, lastSpeed;

  /** Creates a new ShooterHandler */
  public ShooterHandler() {
    leftShooterMotorController = new CANSparkMax(LEFT_LAUNCHER_MOTOR_CONTROLLER_ID, MotorType.kBrushless);
    rightShooterMotorController = new CANSparkMax(RIGHT_LAUNCHER_MOTOR_CONTROLLER_ID, MotorType.kBrushless);

    leftShooterMotorController.setIdleMode(IdleMode.kCoast);
    rightShooterMotorController.setIdleMode(IdleMode.kCoast);
    SmartDashboard.putString("Shooter Speed: ", "Stopped");
    speed = "Stopped";
    lastSpeed = "Stopped";
  }

  /** Makes the wheels spin at {@link frc.robot.Constants#FAST_SHOOT_SPEED fastShootSpeed} */
  public void shootFast() {
    speed= "Fast";
    leftShooterMotorController.set(FAST_SHOOT_SPEED);
    rightShooterMotorController.set(FAST_SHOOT_SPEED);
  }

  /** Makes the wheels spin at {@link frc.robot.Constants#NORMAL_SHOOT_SPEED normalShootSpeed} */
  public void shootNormal() {
    speed = "Normal";
    leftShooterMotorController.set(NORMAL_SHOOT_SPEED);
    rightShooterMotorController.set(NORMAL_SHOOT_SPEED);
  }

  /** Makes the wheels spin at {@link frc.robot.Constants#SLOW_SHOOT_SPEED slowShootSpeed} */
  public void shootSlow() {
    speed = "Slow";
    leftShooterMotorController.set(SLOW_SHOOT_SPEED);
    rightShooterMotorController.set(SLOW_SHOOT_SPEED);
  }

  /** Reverses the shooter motors */
  public void shootBack() {
    speed = "Reverse";
    leftShooterMotorController.set(-SLOW_SHOOT_SPEED);
    rightShooterMotorController.set(-SLOW_SHOOT_SPEED);
  }

  /** Stops the motors for the shooter */
  public void shootStop() {
    SmartDashboard.putString("Shooter Speed: ", "Stopped");
    speed = "Stopped";
    leftShooterMotorController.stopMotor();
    rightShooterMotorController.stopMotor();
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    if (speed != lastSpeed) {
      SmartDashboard.putString("Shooter Speed: ", speed);
      lastSpeed = speed;
    }
  }
}
