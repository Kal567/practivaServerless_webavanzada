package edu.pucmm.functions;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import edu.pucmm.encapsulations.Reservation;
import edu.pucmm.services.ReservationDynamoDbServices;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;

/**
 * Funcion para trabajar el CRUD de la entidad Estudiantes.
 * Es importante crear los permisos necesarios para conectar DynamoDB con el servicio de los Serverless.
 * Se cambia el rol del serverless, pueden utilizar desde la plantilla "Permisos de microservicios sencillos" o
 * "Simple microservice permissions"
 */
public class CRUDReservation implements RequestStreamHandler {

    //Instanciando objeto el manejo de la base de datos.
    private ReservationDynamoDbServices funcionesDynamoDbReservation = new ReservationDynamoDbServices();
    private Gson gson = new Gson();

    /**
     * Estaremos analizando el metodo de acceso a lo interno de la función y realizando la conversación.
     * @param input
     * @param output
     * @param context
     * @throws IOException
     */
    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        //Objetos para el control de la salida.
        JSONParser parser = new JSONParser();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String cuerpoRecibido = null;
        JSONObject responseJson = new JSONObject();
        String salida = "";
        Reservation reservation = null;
        //
        try {
            //Parseando el objeto.
            JSONObject evento = (JSONObject) parser.parse(reader);

            //Ver la salida por la consola sobre la trama enviada por el APIGateway
            context.getLogger().log(""+evento.toJSONString());

            //Recuperando el metodo de acceso de la llamada del API.
            if(evento.get("requestContext")==null){
                throw new IllegalArgumentException("No respesta el API de entrada");
            }
            String metodoHttp = evento.get("httpMethod").toString();

            //Realizando la operacion
            switch (metodoHttp){
                case "GET":
                    ReservationDynamoDbServices.ListReservationResponse listReservationResponse = funcionesDynamoDbReservation.listReservations(null, context);
                    salida = gson.toJson(listReservationResponse);
                    break;
                case "POST":
                case "PUT":
                    reservation = getEstudianteBodyJson(evento);
                    funcionesDynamoDbReservation.insertReservation(reservation, context);
                    salida = gson.toJson(reservation);
                    break;
                case "DELETE":
                    reservation = getEstudianteBodyJson(evento);
                    funcionesDynamoDbReservation.deleteReservation(reservation, context);
                    salida = gson.toJson(reservation);
                    break;
            }

            //La información enviada por el metodo Post o Put estará disponible en la propiedad body:
            if(evento.get("body")!=null){
                cuerpoRecibido = evento.get("body").toString();
            }

            //Respuesta en el formato esperado:
            JSONObject responseBody = new JSONObject();
            responseBody.put("data", JsonParser.parseString(salida));
            responseBody.put("entrada", cuerpoRecibido);

            JSONObject headerJson = new JSONObject();
            headerJson.put("mi-header", "Mi propio header");
            headerJson.put("Content-Type", "application/json");
            headerJson.put("Access-Control-Allow-Origin", "*");
            headerJson.put("Access-Control-Allow-Headers", "Content-Type");

            responseJson.put("statusCode", 200);
            responseJson.put("headers", headerJson);
            responseJson.put("body", responseBody.toString());

        }catch (Exception ex){
            responseJson.put("statusCode", 400);
            responseJson.put("exception", ex);
        }

        //Salida
        OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
        writer.write(responseJson.toString());
        writer.close();
    }

    /**
     *
     * @param json
     * @return
     */
    private Reservation getEstudianteBodyJson(JSONObject json) throws IllegalArgumentException{
        if(json.get("body")==null){
            throw new IllegalArgumentException("No envio el cuerpo en la trama.");
        }
        Reservation reservation =gson.fromJson(json.get("body").toString(), Reservation.class);
        return reservation;
    }
}
