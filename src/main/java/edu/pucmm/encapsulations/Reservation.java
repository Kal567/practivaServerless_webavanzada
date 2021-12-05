package edu.pucmm.encapsulations;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 * Clase que utiliza en enlace para DynamoDB.
 */
@DynamoDBTable(tableName="reservation")
public class Reservation {

    @DynamoDBHashKey(attributeName="matricula")
    private int matricula;
    @DynamoDBAttribute(attributeName = "name")
    private String name;
    @DynamoDBAttribute(attributeName = "email")
    private String email;
    @DynamoDBAttribute(attributeName = "schedule")
    private String schedule;
    @DynamoDBAttribute(attributeName = "laboratory")
    private String laboratory;

    public Reservation(){
        
    }

    /*public Estudiante(int matricula, String nombre) {
        this.matricula = matricula;
        this.nombre = nombre;
    }*/

    public Reservation(int matricula, String name, String email, String schedule, String laboratory) {
        this.matricula = matricula;
        this.name = name;
        this.email = email;
        this.schedule = schedule;
        this.laboratory = laboratory;
    }

    public int getMatricula() {
        return matricula;
    }

    public void setMatricula(int matricula) {
        this.matricula = matricula;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getLaboratory() {
        return laboratory;
    }

    public void setLaboratory(String laboratory) {
        this.laboratory = laboratory;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "matricula=" + matricula +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", schedule='" + schedule + '\'' +
                ", laboratory='" + laboratory + '\'' +
                '}';
    }
}
