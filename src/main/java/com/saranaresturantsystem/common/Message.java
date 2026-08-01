package com.saranaresturantsystem.common;



public class Message {
    public  static  String getAll (String entity) {
        return   entity + " retrieved successfully";
    }

    public static String getById(String entity, Long id) {
        return entity + " " + id + " retrieved successfully";
    }

    public static String created(String entity) {
        return entity + " created successfully";
    }

    public static String updated(String entity, long id) {
        return entity + " " + id + " updated successfully";
    }

    public static String deleted(String entity, long id) {
        return entity + " " + id + " deleted successfully";
    }

}
