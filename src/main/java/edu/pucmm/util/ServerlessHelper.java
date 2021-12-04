package edu.pucmm.util;

public class ServerlessHelper {

    public enum EnvVariable{

        TABLA("TABLE_RESERVATION");

        private String value;

        EnvVariable(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public static String getTableName(){
        return System.getenv(EnvVariable.TABLA.getValue());
    }
}
