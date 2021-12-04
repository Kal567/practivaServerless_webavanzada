package edu.pucmm.services;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.lambda.runtime.Context;
import edu.pucmm.encapsulations.Reservation;
import edu.pucmm.util.ServerlessHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReservationDynamoDbServices {


    public ReservationResponse insertReservation(Reservation reservation, Context context){
        AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.defaultClient();

        if(reservation.getMatricula() == 0 || reservation.getName().isEmpty()){
           throw new RuntimeException("Sent data not valid");
        }

        try {
            DynamoDBMapper mapper = new DynamoDBMapper(ddb);

            mapper.save(reservation);
        }catch (Exception e){
           return new ReservationResponse(true, e.getMessage(), null);
        }

        //Retornando
        return new ReservationResponse(false, null, reservation);
    }

    public ListReservationResponse listReservations(FilterListReservation filtro, Context context) {
        AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.defaultClient();

        List<Reservation> reservations = new ArrayList<>();

        ScanRequest scanRequest = new ScanRequest().withTableName(ServerlessHelper.getTableName());
        ScanResult result = null;

        do {// La consulta vía ScanRequest solo retorna 1 MB de datos por iteracion,
            //debemos iterar.

            if (result != null) {
                scanRequest.setExclusiveStartKey(result.getLastEvaluatedKey());
            }
            result = ddb.scan(scanRequest);
            List<Map<String, AttributeValue>> rows = result.getItems();

            // Iterando todos los elementos
            for (Map<String, AttributeValue> mapReservations : rows) {
                System.out.println(""+mapReservations);
                //
                AttributeValue matriculaAttribute = mapReservations.get("matricula");
                AttributeValue nameAttribute = mapReservations.get("name");
                AttributeValue emailAttribute = mapReservations.get("email");
                AttributeValue scheduleAttribute = mapReservations.get("schedule");
                //
                Reservation tmp = new Reservation();
                tmp.setMatricula(Integer.valueOf(matriculaAttribute.getN()));
                if(nameAttribute!=null){
                   tmp.setName(nameAttribute.getS());
                }
                if(emailAttribute!=null){
                    tmp.setEmail(emailAttribute.getS());
                }
                if(scheduleAttribute!=null){
                    tmp.setSchedule(scheduleAttribute.getS());
                }
                //
                reservations.add(tmp);
            }

        } while (result.getLastEvaluatedKey() != null);

        return new ListReservationResponse(false, "", reservations);
    }

    public ReservationResponse deleteReservation(Reservation reservation, Context context){
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
        DynamoDB dynamoDB = new DynamoDB(client);

        Table table = dynamoDB.getTable(ServerlessHelper.getTableName());

        DeleteItemOutcome outcome = table.deleteItem("matricula", reservation.getMatricula());
        return new ReservationResponse(false, null, reservation);
    }

    public static class ListReservationResponse{
        boolean error;
        String messageError;
        List<Reservation> reservations;

        public ListReservationResponse() {
        }

        public ListReservationResponse(boolean error, String messageError, List<Reservation> reservations) {
            this.error = error;
            this.messageError = messageError;
            this.reservations = reservations;
        }

        public boolean isError() {
            return error;
        }

        public void setError(boolean error) {
            this.error = error;
        }

        public String getMessageError() {
            return messageError;
        }

        public void setMessageError(String menssageError) {
            this.messageError = messageError;
        }

        public List<Reservation> getReservations() {
            return reservations;
        }

        public void setReservations(List<Reservation> reservations) {
            this.reservations = reservations;
        }
    }

    public static class ReservationResponse{
        boolean error;
        String messageError;
        Reservation reservation;

        public ReservationResponse(){

        }

        public ReservationResponse(boolean error, String messageError, Reservation reservation) {
            this.error = error;
            this.messageError = messageError;
            this.reservation = reservation;
        }

        public boolean isError() {
            return error;
        }

        public void setError(boolean error) {
            this.error = error;
        }

        public String getMessageError() {
            return messageError;
        }

        public void setMessageError(String mensajeError) {
            this.messageError = messageError;
        }

        public Reservation getReservation() {
            return reservation;
        }

        public void setReservation(Reservation reservation) {
            this.reservation = reservation;
        }
    }

    public static class FilterListReservation{
        String filter;

        public String getFilter() {
            return filter;
        }

        public void setFilter(String filter) {
            this.filter = filter;
        }
    }

}
