package org.springframework.validation;

import java.util.Map;

public interface MyBindingResult {

    Map<String, Object> getModel();
}
