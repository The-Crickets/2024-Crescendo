// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.ShooterHandler;

/** A basic class for makking the shooter wheels spin backwards slowly. */
public class ShootReverse extends Command {
  
 
  private ShooterHandler subsystem;

  /** Creates a new ShootReverse Command
   * @param handler ({@link frc.robot.subsystems.ShooterHandler ShooterHandler})
  */
  public ShootReverse(ShooterHandler handler) {

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
    //Spins the wheels slowly backward

    subsystem.shootBack();
     
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
