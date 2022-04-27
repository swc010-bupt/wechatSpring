package com.example.test.bo;

import lombok.Data;

/**
 *  发起支付时的参数
 * */
@Data
public class PaymentBO {
    //省略其他的业务参数，如商品id、购买数量等

    //商品名称
    private String subject;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Float getTotal() {
        return total;
    }

    public void setTotal(Float total) {
        this.total = total;
    }

    //总金额
    private Float total = 0.00f;
}
