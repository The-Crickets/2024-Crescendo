// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.ShooterArmHandler;
import frc.robot.subsystems.ShooterHandler;


public class AutoSideShot extends Command {
  
 
  private ShooterArmHandler shooterArm;
  private ShooterHandler shooter;
  private int stage;
  private double startTime, currentTime;
  private boolean setTime;

  /** Creates a new DriveWithJoysticks. */
  public AutoSideShot(ShooterArmHandler shootarm, ShooterHandler shoot) {

    shooterArm = shootarm;
    shooter = shoot;
    stage = 2;
    setTime = true;

    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(shooterArm, shooter);
  }
  

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute(){
    //Drive using xbox joystick values
    SmartDashboard.putNumber("Auto Stage", stage);
    if (stage == 2) {
      stageOne();
    } else if (stage == 4) {
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
        shooter.shootFast();
        shooterArm.moveShooterArm(0, 0.9, 0);
      } else if ((currentTime - startTime)/1000 > 4) {
          stage = 4;
      }
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    stage = 2;
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
