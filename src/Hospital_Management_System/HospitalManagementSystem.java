package Hospital_Management_System;

import java.sql.*;
import java.util.Scanner;

public class HospitalManagementSystem {
    private  static  final String url="jdbc:mysql://localhost:3306/hospital";
    private  static  final String username="root";
    private  static  final String password="Faang@2024";

    public static void main(String[] args) {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        Scanner scanner=new Scanner(System.in);
        try{
            Connection connection= DriverManager.getConnection(url,username,password);
            Patient patient=new Patient(connection,scanner);
            Doctor doctor=new Doctor(connection);
            while (true){
                System.out.println("HOSPITAL MANAGEMENT SYSTEM");
                System.out.println("1. Add Patients");
                System.out.println("2. View Patients");
                System.out.println("3. View Doctors");
                System.out.println("4. Book Appointment");
                System.out.println("5. Exit");
                System.out.println("Enter your choice");
                 int choice=scanner.nextInt();
                 switch (choice){
                     case 1:
                         patient.addPatient();
                         System.out.println();
                         break;
                     case 2:
                         patient.viewPatient();
                         System.out.println();
                         break;
                     case 3:
                         doctor.viewDoctor();
                         System.out.println();
                         break;
                     case 4:
                         bookAppointment(patient,doctor,connection,scanner);
                         System.out.println();
                         break;
                     case 5:
                         System.out.println("Thank you for using Hospital management system");
                         return;
                     default:
                         System.out.println("enter valid choice!!!");
                         break;
                 }
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }
    public  static void bookAppointment(Patient patient,Doctor doctor,Connection connection,Scanner scanner){
        System.out.print("Enter patient id :");
        int patientId=scanner.nextInt();
        System.out.print("Enter doctor ID: ");
        int doctorId=scanner.nextInt();
        System.out.print("Enter Appointment Date (YYYY-MM-DD): ");
        String appointmentDate=scanner.next();
        if(patient.getPatientById(patientId)&&doctor.getDocotorById(doctorId)){
            if(checkDoctorAvailability(doctorId,appointmentDate,connection)){
                String appointmentQuery="INSERT INTO appointments(patient_id,doctors_id, appointment_date) VALUES (?,?,?)";
                try{
                    PreparedStatement preparedStatement= connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1,patientId);
                    preparedStatement.setInt(2,doctorId);
                    preparedStatement.setString(3,appointmentDate);
                    int affectedRows=preparedStatement.executeUpdate();
                    if(affectedRows>0){
                        System.out.println("Appointment Booked");
                    }
                    else{
                        System.out.println("Failed to Book Appointment");
                    }
                }
                catch(SQLException e){
                    e.printStackTrace();
                }
            }
            else{
                System.out.println("Doctor not available on this date");
            }
        }
        else{
            System.out.println("Either doctor or patient does not exist");
        }
    }
    public static boolean checkDoctorAvailability(int doctorId,String appoinmentDate,Connection connection){
        String query="SELECT COUNT(*) FROM appointments WHERE doctors_id=? AND appointment_date=?";
        try{
            PreparedStatement preparedStatement=connection.prepareStatement(query);
            preparedStatement.setInt(1,doctorId);
            preparedStatement.setString(2,appoinmentDate);
            ResultSet resultSet=preparedStatement.executeQuery();
            if(resultSet.next()) {
                int count = resultSet.getInt(1);
                if (count == 0) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}
