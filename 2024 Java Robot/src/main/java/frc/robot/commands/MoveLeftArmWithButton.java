// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.LeftArmButtonHandler;
import edu.wpi.first.wpilibj2.command.Command;

/** A command to move the left hook using the {@link frc.robot.subsystems.LeftArmButtonHandler LeftArmButtonHadler} subsystem */
public class MoveLeftArmWithButton extends Command {



  private final LeftArmButtonHandler handler;

  /**
   *  Creates a new MoveLeftArmWithButton command.
   * 
   * @param xbox (The drivers xbox controllet)
   * @param subsystem (The {@link frc.robot.subsystems.LeftArmButtonHandler LeftArmButtonHandler} subsystem)
   */
  public MoveLeftArmWithButton(LeftArmButtonHandler subsystem) {
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