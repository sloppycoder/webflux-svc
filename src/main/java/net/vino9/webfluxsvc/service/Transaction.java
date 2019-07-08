package net.vino9.webfluxsvc.service;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Transaction {
    private Long id;
    private double amount;
    private String memo;
}
