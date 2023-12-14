package io.github.miracelwhipp.annotation.processor.test;

public class CountMe {


    @ToCount
    private String hihi;

    @ToCount
    private int huhu;

    @ToCount
    private Object gruuuu;


    @ToCount
    private void select() {

    }


    @ToCount
    private String addimum(String param) {

        return param + hihi;
    }



}
