// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.LeftArmButtonHandler;
import frc.robot.subsystems.RightArmButtonHandler;


public class InvertHooks extends Command {
  
 
  private LeftArmButtonHandler leftArmHandler;
  private RightArmButtonHandler rightArmHandler;
  private boolean alreadyInverted;

  /**
   * Creates a new InvertDrive command
   * @param drive (The {@link frc.robot.subsystems.Drivetrain Drivetrain} subsystem)
   */
  public InvertHooks(RightArmButtonHandler right, LeftArmButtonHandler left) {

    leftArmHandler = left;
    rightArmHandler = right;
   

    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(right, left);
  }
  

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    alreadyInverted = false;
  }

  // Called every time the scheduler runs while the command is scheduled.
  /**
   * Calls {@link frc.robot.subsystems.Drivetrain#invertDrive invertDrive} once.
   */
  @Override
  public void execute(){
    //inverts the hooks only once
    if (!alreadyInverted) {
        leftArmHandler.invertHook();
        rightArmHandler.invertHook();
        //setting this to true causes this to never run again until the button is released and pressed again.
        alreadyInverted = true;
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
