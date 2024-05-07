// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj.XboxController;
import frc.robot.subsystems.ShooterArmHandler;

/** A command class that handles moving the shooter arm with the {@link frc.robot.subsystems.ShooterArmHandler ShooterArmHandler} subsystem */
public class MoveShooter extends Command {  
 
  private ShooterArmHandler subsystem;
  private XboxController xboxJoysticks;

  /** Creates a new MoveShooter Command. */
  public MoveShooter(ShooterArmHandler handler, XboxController xbox) {

    subsystem = handler;
    xboxJoysticks = xbox;

    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(subsystem);
  }
  

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute(){
    
    //Move the arm and the intake using joystick and trigger values.
    //We invert the y-value of the joystick because it's naturally inverted (up is normally negative).
    subsystem.moveShooterArm(-xboxJoysticks.getLeftY(), xboxJoysticks.getRightTriggerAxis(), xboxJoysticks.getLeftTriggerAxis());
     
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
