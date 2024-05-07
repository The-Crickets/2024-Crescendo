// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.DriverStation;
//Here we import all of the extra classes we need from the wpilib library
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

//These imports are from our own files.
//Since we declare all of our commands and subsystems here, just about every file is here.
import frc.robot.commands.AutoAmp;
import frc.robot.commands.AutoBasic;
import frc.robot.commands.AutoCenter;
import frc.robot.commands.AutoCenterShot;
import frc.robot.commands.AutoFar;
import frc.robot.commands.AutoSideShot;
import frc.robot.commands.DriveWithJoysticks;
import frc.robot.commands.InvertDrive;
import frc.robot.commands.InvertHooks;
import frc.robot.commands.MoveLeftArmWithButton;
import frc.robot.commands.MoveRightArmWithButton;
import frc.robot.commands.MoveShooter;
import frc.robot.commands.ShootFast;
import frc.robot.commands.ShootNormal;
import frc.robot.commands.ShootReverse;
import frc.robot.commands.ShootSlow;
import frc.robot.subsystems.LeftArmButtonHandler;
import frc.robot.subsystems.RightArmButtonHandler;
import frc.robot.subsystems.ShooterArmHandler;
import frc.robot.subsystems.ShooterHandler;
import frc.robot.subsystems.Drivetrain;
import static frc.robot.Constants.*;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {

  //Declaring Arm Motor Controllers

  // The robot's subsystems and commands are defined here...
  private final XboxController driver_xbox = new XboxController(DRIVER_XBOX_PORT);
  private final XboxController shooter_xbox = new XboxController(SHOOTER_XBOX_PORT);
  
  private final Drivetrain drivetrain = new Drivetrain();
  private final DriveWithJoysticks driveCommand = new DriveWithJoysticks(drivetrain, driver_xbox);

  private final InvertDrive invertCommand = new InvertDrive(drivetrain);

  //Commands and subsystems for the hooks 
  private final LeftArmButtonHandler leftarmsubsystem = new LeftArmButtonHandler();
  private final MoveLeftArmWithButton leftarmcommand = new MoveLeftArmWithButton(leftarmsubsystem);

  private final RightArmButtonHandler rightarmsubsystem = new RightArmButtonHandler();
  private final MoveRightArmWithButton rightarmcommand = new MoveRightArmWithButton(rightarmsubsystem);

  private final InvertHooks hookInvertCommand = new InvertHooks(rightarmsubsystem, leftarmsubsystem);

  //Commands and subsystems for shooter arm

  private final ShooterArmHandler shootersubsystem = new ShooterArmHandler();
  private final MoveShooter shootercommand = new MoveShooter(shootersubsystem, shooter_xbox);

  //Commands and subsystem for shooting
  private final ShooterHandler shotsubsystem = new ShooterHandler();
  private final ShootReverse reverseShotCommand = new ShootReverse(shotsubsystem);
  private final ShootSlow slowShotCommand = new ShootSlow(shotsubsystem);
  private final ShootNormal normalShotCommand = new ShootNormal(shotsubsystem);
  private final ShootFast fastShotCommand = new ShootFast(shotsubsystem);

  //Auto Commands
  private final AutoBasic basicAutoCommand = new AutoBasic(drivetrain, shootersubsystem);
  private final AutoCenterShot centerShotAutoCommand = new AutoCenterShot(shootersubsystem, shotsubsystem);
  private final AutoSideShot sideShotAutoCommand = new AutoSideShot(shootersubsystem, shotsubsystem);
  private final AutoCenter centerAutoCommand = new AutoCenter(drivetrain, shootersubsystem, shotsubsystem);
  private final AutoAmp ampAutoCommand = new AutoAmp(drivetrain, shootersubsystem, shotsubsystem, DriverStation.getAlliance().get());
  private final AutoFar farAutoCommand = new AutoFar(drivetrain, shootersubsystem, shotsubsystem, DriverStation.getAlliance().get());

  SendableChooser<Command> auto_chooser;

  //These are the "triggers." They are what we use to call commands from button inputs.
  //5 = Left Bumper, 6 = Right Bumper
  //You can determine the number of a button through the light indicators on the frc driverstation.
  private JoystickButton leftBump = new JoystickButton(driver_xbox, 5);
  private JoystickButton rightBump = new JoystickButton(driver_xbox, 6);
  private JoystickButton inverter = new JoystickButton(driver_xbox, 9);

  private JoystickButton backShot = new JoystickButton(shooter_xbox, 2);
  private JoystickButton slowShot = new JoystickButton(shooter_xbox, 5);
  private JoystickButton normShot = new JoystickButton(shooter_xbox, 6);
  private JoystickButton fastShot = new JoystickButton(shooter_xbox, 3);

  private JoystickButton hookInverter = new JoystickButton(driver_xbox, 3);

  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {

    //Initializes the autonomous chooser
    auto_chooser = new SendableChooser<>();

    //Sets the options for the Auto_Chooser
    auto_chooser.setDefaultOption("Basic Auto", basicAutoCommand);
    auto_chooser.addOption("Center Only Shoot Auto", centerShotAutoCommand);
    auto_chooser.addOption("Side Only Shot Command", sideShotAutoCommand);
    auto_chooser.addOption("Center Auto", centerAutoCommand);
    auto_chooser.addOption("Amp Auto", ampAutoCommand);
    auto_chooser.addOption("Far Auto", farAutoCommand);
    auto_chooser.addOption("Nothing", null);

    //Sends choices to dashboard
    SmartDashboard.putData("Auto Choices: ", auto_chooser);
    
    // Configure the button bindings
    configureButtonBindings();
    configureDefaultCommands();
    
  }

  /**
   * Use this method to define your button->command mappings. Buttons can be created by
   * instantiating a {@link GenericHID} or one of its subclasses ({@link
   * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing it to a {@link
   * edu.wpi.first.wpilibj2.command.button.JoystickButton}.
   */
  private void configureButtonBindings() {
    //Here we define when these should run
    //While true indicates that while the button is pressed these will run
    inverter.whileTrue(invertCommand);
    leftBump.whileTrue(leftarmcommand);
    rightBump.whileTrue(rightarmcommand);
    backShot.whileTrue(reverseShotCommand);
    slowShot.whileTrue(slowShotCommand);
    normShot.whileTrue(normalShotCommand);
    fastShot.whileTrue(fastShotCommand);
    hookInverter.whileTrue(hookInvertCommand);
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // The auto mode selected in the dashboard will be returned here.
    return auto_chooser.getSelected();
  }
    //For subsystem default commands (driving, etc.)
    private void configureDefaultCommands(){

      //Drivetrain -> drive with xbox joysticks
      drivetrain.setDefaultCommand(driveCommand);
      shootersubsystem.setDefaultCommand(shootercommand);
    }
}
