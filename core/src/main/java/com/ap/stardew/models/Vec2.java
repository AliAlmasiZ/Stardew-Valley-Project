package com.ap.stardew.models;

import com.badlogic.gdx.math.Vector2;

import java.io.Serializable;

public class Vec2 extends Vector2 implements Serializable {
    public Vec2() {
    }

    public Vec2(float x, float y) {
        super(x, y);
    }

    public Vec2(Vector2 v) {
        super(v);
    }

    public int getCol(){
        return (int) x / 16;
    }
    public int getRow(){
        return (int) y / 16;
    }

    @Override
    public Vec2 add(Vector2 v) {
        super.add(v);
        return this;
    }

    @Override
    public Vec2 add(float x, float y) {
        super.add(x, y);
        return this;
    }

    @Override
    public Vec2 sub(Vector2 v) {
        super.sub(v);
        return this;
    }

    @Override
    public Vec2 sub(float x, float y) {
        super.sub(x, y);
        return this;
    }

    @Override
    public Vec2 scl(Vector2 v) {
        super.scl(v);
        return this;
    }

    @Override
    public Vec2 scl(float scalar) {
        super.scl(scalar);
        return this;
    }

    @Override
    public Vec2 scl(float x, float y) {
        super.scl(x, y);
        return this;
    }

    @Override
    public Vec2 nor() {
        super.nor();
        return this;
    }

    @Override
    public Vec2 cpy() {
        return new Vec2(x, y);
    }

    public Vec2 convertToInt(){
        x = getCol();
        y = getRow();
        return this;
    }
}
