// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.ShooterArmHandler;
import frc.robot.subsystems.ShooterHandler;


public class AutoCenter extends Command {
  
 
  private Drivetrain drivetrain;
  private ShooterArmHandler shooterArm;
  private ShooterHandler shooter;
  private int stage;
  private double startTime;
  private boolean setTime;

  /** Creates a new AutoCenter. */
  public AutoCenter(Drivetrain drive, ShooterArmHandler shootarm, ShooterHandler shoot) {

    drivetrain = drive;
    shooterArm = shootarm;
    shooter = shoot;
    stage = 1;
    setTime = true;
   

    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(drivetrain, shooterArm, shooter);
  }
  

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute(){
    SmartDashboard.putNumber("Auto Stage", stage);
    
    if (stage == 1) {
      stageOne();
    } else if (stage == 2) {
      stageTwo();
    } else if (stage == 3) {
      stageThree();
    } else if (stage == 4) {
      drivetrain.stopAll();
      shooter.shootStop();
      shooterArm.stopArm();
    }
  }

  public void stageOne() {
    if (shooterArm.moveShooterArmAuto(-83, -80)) {
      if (setTime) {
          startTime = System.currentTimeMillis();
          setTime = false;
      } else if ((System.currentTimeMillis() - startTime)/1000 <= 1 && !setTime) {
        shooter.shootNormal();
      } else if ((System.currentTimeMillis() - startTime)/1000 < 2) {
        shooter.shootNormal();
        shooterArm.moveShooterArm(0, 0.9, 0);
      } else {
          shooter.shootStop();
          shooterArm.stopArm();
          setTime = true;
          stage = 2;
      }
    }
  }

  public void stageTwo() {
    if (shooterArm.moveShooterArmAuto(-102, -100)) {
      if (setTime) {
        startTime = System.currentTimeMillis();
        setTime = false;
      } else if ((System.currentTimeMillis() - startTime)/1000 <= 3) {
        shooterArm.moveShooterArm(0, 0.9, 0);
        drivetrain.driveTank(-0.5, 0, 0, true);
      } else if ((System.currentTimeMillis() - startTime)/1000 <=6) {
        shooterArm.stopArm();
        drivetrain.driveTank(0.5, 0, 0, true);
        shooter.shootNormal();
      } else {
        drivetrain.stopAll();
        shooterArm.stopArm();
        setTime = true;
        stage = 3;
      }
    }
  }

  public void stageThree () {
    if (setTime) {
      startTime = System.currentTimeMillis();
      setTime = false;
    } else if (shooterArm.moveShooterArmAuto(-83, -80)) {
        if ((System.currentTimeMillis() - startTime)/1000 <= 1.5) {
          shooter.shootNormal();
          shooterArm.moveShooterArm(0, 0.9, 0);
        } else {
            stage = 4;
        }
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
