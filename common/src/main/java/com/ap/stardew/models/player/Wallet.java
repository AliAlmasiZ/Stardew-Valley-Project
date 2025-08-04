package com.ap.stardew.models.player;

import java.io.Serializable;

public class Wallet implements Serializable {
    private double balance;


    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void reduceBalance(double balance) {
        this.balance -= balance;
        if (balance < 0) {
            System.out.println("RIDIM: balance less than 0");
        }
    }

    public void addBalance(double balance) {
        this.balance += balance;
    }

    public void changeBalance(double price) {
        this.balance += price;
    }

    public static Wallet combineWallets(Wallet wallet1, Wallet wallet2) {
        Wallet wallet = new Wallet();
        wallet.setBalance(wallet1.getBalance() + wallet2.getBalance());
        return wallet;

    }
}
