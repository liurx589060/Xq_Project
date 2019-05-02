package com.cd.xq.beans;

/**
 * Created by Administrator on 2019/5/2.
 */

public class BGetProfit {
    private int allCount;
    private int successCount;
    private int is_exchange;
    private long balance;
    private float reportRate;
    private int self_successCount;
    private int self_allCount;
    private int self_reportCount;
    private float self_rate;

    public int getAllCount() {
        return allCount;
    }

    public void setAllCount(int allCount) {
        this.allCount = allCount;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getIs_exchange() {
        return is_exchange;
    }

    public void setIs_exchange(int is_exchange) {
        this.is_exchange = is_exchange;
    }

    public float getReportRate() {
        return reportRate;
    }

    public void setReportRate(float reportRate) {
        this.reportRate = reportRate;
    }

    public int getSelf_successCount() {
        return self_successCount;
    }

    public void setSelf_successCount(int self_successCount) {
        this.self_successCount = self_successCount;
    }

    public int getSelf_allCount() {
        return self_allCount;
    }

    public void setSelf_allCount(int self_allCount) {
        this.self_allCount = self_allCount;
    }

    public int getSelf_reportCount() {
        return self_reportCount;
    }

    public void setSelf_reportCount(int self_reportCount) {
        this.self_reportCount = self_reportCount;
    }

    public float getSelf_rate() {
        return self_rate;
    }

    public void setSelf_rate(float self_rate) {
        this.self_rate = self_rate;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }
}
