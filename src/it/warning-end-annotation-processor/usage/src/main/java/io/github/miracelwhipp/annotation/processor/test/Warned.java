package io.github.miracelwhipp.annotation.processor.test;

public class Warned {


    @Warning("you defined a String")
    private String string;

    @Warning("you defined an integer")
    private int integer;

    @Warning("you defined an object")
    private Object object;


    @Warning("you defined a method")
    private void select() {

    }

    @Warning("you defined another method")
    private String addimum(String param) {

        return param + integer;
    }


}
