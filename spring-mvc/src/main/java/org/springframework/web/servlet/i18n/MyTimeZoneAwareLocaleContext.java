package org.springframework.web.servlet.i18n;

import java.util.TimeZone;

public interface MyTimeZoneAwareLocaleContext {

    TimeZone getTimeZone();
}
