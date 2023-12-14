package io.github.miracelwhipp.compiler.plugin.test;

public class CountMe {


    private final int a = 9;

    public static final String CONSTANT = "this is a string constant";

    public void method() {

        long huhu;
        huhu = 999L;

        long haha;
        haha = callMe(haha = huhu);
    }

    private long callMe(long huhu) {
        return huhu;
    }


}
