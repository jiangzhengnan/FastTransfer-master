package com.guohui.fasttransfer.base;

import com.guohui.fasttransfer.utils.annotation.JsonParam;
import com.guohui.fasttransfer.utils.annotation.Jsonable;

import java.io.File;


/**
 * Created by Dikaros on 2016/5/11.
 */
public class FileMsg implements Jsonable {


    @JsonParam(name = "length")
    long length;
    @JsonParam(name = "name")
    String name;
    @JsonParam(name = "type")
    String type;
    @JsonParam(name = "other")
    String other;

    public FileMsg(){

    }

    public FileMsg(File file){
        this.length = file.length();
        this.name = file.getName();
        this.type = getTailName(file.getName());
    }

    private String getTailName(String name) {
        String type = null;
        if (name.contains(".")) {
            String[] splits = name.split("\\.");
            // System.out.println(splits.length);
            type = splits[splits.length - 1].toLowerCase();
        }
        return type;
    }

    public long getLength() {
        return length;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }
}
