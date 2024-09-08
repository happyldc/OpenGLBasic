package com.example.demo.base.egl;

/**
 * TODO 类描述
 *
 * @author HB.LDC
 * @date 2024-09-07
 */
public class Event {
    public static final int RENDER = 1;
    public static final int ADD_SURFACE = 2;
    public static final int REMOVE_SURFACE = 3;
    public static final int EXIT = 4;
    private int type;
    private int what;
    private Object params;

    public Event(int type) {
        this(type, null);
    }

    public Event(int type, Object params) {
        this(type, 0, params);
    }

    public Event(int type, int what, Object params) {
        this.type = type;
        this.what = what;
        this.params = params;
    }

    public int getType() {
        return type;
    }

    public int getWhat() {
        return what;
    }

    public Object getParams() {
        return params;
    }
}
