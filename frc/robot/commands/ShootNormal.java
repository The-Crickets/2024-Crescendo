// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.ShooterHandler;

/** A basic class for makking the shooter wheels spin at resonable speed. */
public class ShootNormal extends Command {
  
 
  private ShooterHandler subsystem;

  /** Creates a new ShootNormal Command
   * @param handler ({@link frc.robot.subsystems.ShooterHandler ShooterHandler})
  */
  public ShootNormal(ShooterHandler handler) {

    subsystem = handler;

    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(subsystem);
  }
  

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute(){
    //Speed up the wheels

    subsystem.shootNormal();
     
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    //Stops the wheels when the buttons are let go
    subsystem.shootStop();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
