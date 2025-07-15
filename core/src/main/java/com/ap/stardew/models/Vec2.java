package com.ap.stardew.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Vec2 implements Serializable {
    protected double x;
    protected double y;
    protected static float tileSize = 16;

    @JsonCreator
    public Vec2(
            @JsonProperty("x") double x,
            @JsonProperty("y") double y
    ){
        this.x = x;
        this.y = y;
    }
    public Vec2 copy(){
        return new Vec2(x, y);
    }

    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public int getCol(){
        return (int) Math.floor(this.x / tileSize);
    }
    public int getRow(){
        return (int) Math.floor(this.y / tileSize);
    }
    public double getDistance(Vec2 other){
        return Math.hypot(x - other.x, y - other.y);
    }

    public void setX(double x) {
        this.x = x;
    }
    public void set(Vec2 v){
        this.x = v.x;
        this.y = v.y;
    }

    public void setY(double y) {
        this.y = y;
    }
    public Vec2 add(int x, int y){
        this.x += x;
        this.y += y;
        return this;
    }
    public Vec2 add(double x, double y){
        this.x += x;
        this.y += y;
        return this;
    }
    public Vec2 add(Vec2 v){
        this.add(v.getX(), v.getY());
        return this;
    }
    public Vec2 subtract(Vec2 v){
        this.x -= v.x;
        this.y -= v.y;
        return this;
    }
    public Vec2 multiply(double m){
        this.x *= m;
        this.y *= m;
        return this;
    }
    public Vec2 divide(double m){
        this.x /= m;
        this.y /= m;
        return this;
    }
}
