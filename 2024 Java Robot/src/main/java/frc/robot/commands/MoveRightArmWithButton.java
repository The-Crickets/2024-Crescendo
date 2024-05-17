// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.RightArmButtonHandler;
import edu.wpi.first.wpilibj2.command.Command;

/** A command to move the right hook using the {@link frc.robot.subsystems.RightArmButtonHandler RightArmButtonHadler} subsystem */
public class MoveRightArmWithButton extends Command {
  
  private final RightArmButtonHandler handler;
  
  /**
   *  Creates a new MoveRightArmWithButton command.
   * 
   * @param xbox (The drivers xbox controllet)
   * @param subsystem (The {@link frc.robot.subsystems.RightArmButtonHandler RightArmButtonHandler} subsystem)
   */
  public MoveRightArmWithButton(RightArmButtonHandler subsystem) {
    handler = subsystem;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(handler);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    handler.moveArm();
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    //This is required as the arm must stop once the button is let go.
    handler.stopArm();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}