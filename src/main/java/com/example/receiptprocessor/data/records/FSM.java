package com.example.receiptprocessor.data.records;

import io.vavr.Lazy;

/**
 * Finite state machine
 */
public record FSM<T>(String id,
                     Lazy<Boolean> state,
                     Lazy<T> value) { }
