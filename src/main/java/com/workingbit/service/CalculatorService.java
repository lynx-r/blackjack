package com.workingbit.service;

import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Created by Aleksey Popryaduhin on 12:57 21/06/2017.
 */
@Service
public class CalculatorService {

    // TODO прикрути сюда какую-нибудь виндовую либу для парсинга
    public Double calculate(String expression) {
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("JavaScript");
        try {
            return Double.valueOf(String.valueOf(engine.eval(expression)));
        } catch (ScriptException e) {
            e.printStackTrace();
            return null;
        }
    }
}
