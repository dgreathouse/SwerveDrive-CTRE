//Copyright (c) 2020-2023 Essexville Hampton Public Schools (FRC 8517)

package frc.robot.subsystems;

import com.ctre.phoenix6.configs.Slot0Configs;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.lib.EDriveMode;
import frc.robot.lib.RobotDrive;
import frc.robot.lib.SwerveDriveConstantsCreator;
import frc.robot.lib.SwerveDriveTrainConstants;
import frc.robot.lib.SwerveModuleConstants;
import frc.robot.lib.k;

public class DrivetrainSubsystem extends SubsystemBase {
  public RobotDrive m_robotDrive;
  public EDriveMode m_driveMode = EDriveMode.FIELD_CENTRIC;

  /** Creates a new DrivetrainSubsystem. */
  public DrivetrainSubsystem() {
    // TODO: Calibrate the PID values and SlipCurrent for stator
    SwerveDriveTrainConstants m_drivetrainConstants = new SwerveDriveTrainConstants()
          .withPigeon2Id(1)
          .withCANbusName(k.ROBOT.CANFD_NAME)
          .withTurnKp(5)
          .withTurnKi(0.1);
    Slot0Configs m_steerGains = new Slot0Configs();
    Slot0Configs m_driveGains = new Slot0Configs();
    m_steerGains.kP = 30;
    m_steerGains.kI = 0.0;
    m_steerGains.kD = 0.2;
    m_driveGains.kP = 1;
    m_driveGains.kI = 0;
    SwerveDriveConstantsCreator m_constantsCreator = new SwerveDriveConstantsCreator(
        k.DRIVE.GEAR_RATIO, //  ratio for the drive motor
        k.STEER.GEAR_RATIO_TO_CANCODER, // ratio for the steer motor
        k.DRIVE.WHEEL_DIAMETER_m, // 4 inch diameter for the wheels
        17, // Only apply 24 stator amps to prevent slip
        m_steerGains, // Use the specified steer gains
        m_driveGains, // Use the specified drive gains
        true // CANcoder not reversed from the steer motor. For WCP Swerve X this should be
              // true.
    );
    // TODO: Calibrate offsets for CANCoders. Get all CAN IDs correct. 
        /**
     * Note: WPI's coordinate system is X forward, Y to the left so make sure all locations are with
     * respect to this coordinate system
     * This particular drive base is 22" x 22"
     */
    SwerveModuleConstants m_frontRight = m_constantsCreator.createModuleConstants(
        0, 1, 0, -0.538818,k.DRIVEBASE.WHEEL_BASE_X_m / 2.0, -k.DRIVEBASE.WHEEL_BASE_Y_m / 2.0);

    SwerveModuleConstants m_frontLeft = m_constantsCreator.createModuleConstants(
        2, 3, 1, -0.474609, k.DRIVEBASE.WHEEL_BASE_X_m / 2.0, k.DRIVEBASE.WHEEL_BASE_Y_m / 2.0);
    SwerveModuleConstants m_back = m_constantsCreator.createModuleConstants(
        4, 5, 2, -0.928467, -k.DRIVEBASE.WHEEL_BASE_X_m / 2.0, 0.0);

    m_robotDrive = new RobotDrive(m_drivetrainConstants, m_frontLeft, m_frontRight, m_back);


  }
  public void updateDashboard(){
    SmartDashboard.putString(k.DRIVE.T_DRIVER_MODE, m_driveMode.toString());
    m_robotDrive.updateDashboard();
  }
  public void driveStopMotion(){
    m_robotDrive.driveStopMotion();
  }
  public void driveRobotCentric(ChassisSpeeds _speeds){
    m_robotDrive.driveRobotCentric(_speeds);
  }
  public void driveFieldCentric(ChassisSpeeds _speeds){
    m_robotDrive.driveFieldCentric(_speeds);
  }
  public void driveAngleFieldCentric(double _x, double _y, Rotation2d _lastTargetAngle){
    m_robotDrive.driveAngleFieldCentric(_x, _y, _lastTargetAngle);
  }
  public void drivePolarFieldCentric(double _driveAngle, double _speed, double _robotAngle){
    double x = Math.sin(_driveAngle) * _speed;
    double y = Math.cos(_driveAngle) * _speed;
   
    driveAngleFieldCentric(x, y, new Rotation2d(Units.degreesToRadians(_robotAngle)));
  }
  public void changeDriveMode(){
    switch(m_driveMode){
      case FIELD_CENTRIC:
        m_driveMode = EDriveMode.ANGLE_FIELD_CENTRIC;
      break;
      case ANGLE_FIELD_CENTRIC:
        m_driveMode = EDriveMode.ROBOT_CENTRIC;
      break;
      case ROBOT_CENTRIC:
        m_driveMode = EDriveMode.FIELD_CENTRIC;
      break;
      default:
        m_driveMode = EDriveMode.ROBOT_CENTRIC;
      break;
    }
  }
  public EDriveMode getDriveMode(){
    return m_driveMode;
  }
  public double getRobotAngle(){
    return m_robotDrive.getRobotYaw();
  }
  @Override
  public void periodic() {
    // This method will be called once per scheduler run.

  }
}
