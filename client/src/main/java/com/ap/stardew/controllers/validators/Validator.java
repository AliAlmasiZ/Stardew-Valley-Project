package com.ap.stardew.controllers.validators;

import com.ap.stardew.models.Result;

public interface Validator <T>{
    Result isValid(T object);
}
