// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

//THE AUTOAMP IS STILL UNSTABLE!!!

package frc.robot.commands;

import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.ShooterArmHandler;
import frc.robot.subsystems.ShooterHandler;


public class AutoAmp extends Command {
  
 
  private Drivetrain drivetrain;
  private ShooterArmHandler shooterArm;
  private ShooterHandler shooter;
  private int stage;
  private double startTime, currentTime;
  private boolean setTime;
  private Alliance team;

  /** Creates a new AutoAmp. */
  public AutoAmp(Drivetrain drive, ShooterArmHandler shootarm, ShooterHandler shoot, Alliance color) {

    drivetrain = drive;
    shooterArm = shootarm;
    shooter = shoot;
    stage = 1;
    setTime = true;
    team = color;
  
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(drivetrain, shooterArm, shooter);
  }
  

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute(){
    //Drive using xbox joystick values
    SmartDashboard.putNumber("Auto Stage", stage);
    if (stage == 1) {
      stageOne();
    } else if (stage == 2) {
      stageTwo();
    } else if (stage == 4) {
      drivetrain.stopAll();
      shooter.shootStop();
      shooterArm.stopArm();
    }
  }

  public void stageOne() {
    currentTime = System.currentTimeMillis();

    if (shooterArm.moveShooterArmAuto(-82, -80)) {
      if (setTime) {
          startTime = System.currentTimeMillis();
          setTime = false;
      } else if ((currentTime - startTime)/1000 <= 2) {
        shooter.shootFast();
      } else if ((currentTime - startTime)/1000 <= 4) {
        shooterArm.moveShooterArm(0, 0.9, 0);
      } else if ((currentTime - startTime)/1000 > 4) {
          shooter.shootStop();
          shooterArm.stopArm();
          setTime = true;
          stage = 2;
      }
    }
  }

  public void stageTwo() {
    currentTime = System.currentTimeMillis();

    if (setTime) {
      startTime = System.currentTimeMillis();
      setTime = false;
    } else if ((currentTime - startTime)/1000 <= 0.45) {
      if (team == Alliance.Red) {
        drivetrain.driveTank(0, -0.75, 0, true);
      } else if (team == Alliance.Blue) {
        drivetrain.driveTank(0, 0.3, 0, true);
      }
    } else if ((currentTime - startTime)/1000 <= 2.5) {
      drivetrain.driveTank(-0.6, 0, 0, true);
    } else {
      drivetrain.stopAll();
      shooterArm.stopArm();
      setTime = true;
      stage = 4;
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    stage = 1;
    drivetrain.stopAll();
    shooter.shootStop();
    shooterArm.stopArm();
    setTime = true;
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
