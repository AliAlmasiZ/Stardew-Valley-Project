package com.ap.stardew.controllers.validators;

import com.ap.stardew.records.Result;

public interface Validator <T>{
    Result isValid(T object);
}
