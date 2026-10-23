package com.uniovi.sercheduler.localsearch.export;

public class Formatter {


    public static String millisecondsToHourFormat(long milisegundos) {
        long segundosTotales = milisegundos / 1000;

        long horas = segundosTotales / 3600;
        long minutos = (segundosTotales % 3600) / 60;
        long segundos = segundosTotales % 60;

        return String.format("%02d:%02d:%02d", horas, minutos, segundos);
    }
}
