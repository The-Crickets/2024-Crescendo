// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.ShooterArmHandler;

public class AutoBasic extends Command {
  
 
  private Drivetrain drivetrain;
  private ShooterArmHandler shooterArm;
  private int stage;
  private double startTime;
  private boolean setTime;

  /** Creates a new DriveWithJoysticks. */
  public AutoBasic(Drivetrain drive, ShooterArmHandler shootarm) {

    drivetrain = drive;
    shooterArm = shootarm;
    stage = 2;
    setTime = true;
   

    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(drivetrain, shooterArm);
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
      stageTwo();
    } else if (stage == 4) {
      drivetrain.stopAll();
      shooterArm.stopArm();
    }
  }

  public void stageTwo() {
    if (shooterArm.moveShooterArmAuto(-110, -100)) {
      if (setTime) {
        startTime = System.currentTimeMillis();
        setTime = false;
      } else if ((System.currentTimeMillis() - startTime)/1000 <= 3) {
        drivetrain.driveTank(-0.5, 0, 0, true);
      } else {
        drivetrain.stopAll();
        setTime = true;
        stage = 4;
      }
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    stage = 2;
    drivetrain.stopAll();
    shooterArm.stopArm();
    setTime = true;
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
