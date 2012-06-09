/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package oliver.jvm.analyse;

/**
 * GC类型
 * 
 * @author lichengwu
 * @created 2011-11-18
 * 
 * @version 1.0
 */
public enum GCType {

    FULL_GC_SYSTEM("Full GC (System)"), FULL_GC("Full GC"), MINOR_GC("GC"),FULL_GC_CMS("Full GC CMS");

    private String name;

    /**
     * 
     */
    private GCType(String name) {
        this.name = name;
    }

    public static GCType getByName(String name) {
        for (GCType type : GCType.values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }
        return null;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

}
