package org.springframework.ui.context;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-06-04
 **/
public interface MyThemeSource {

    MyTheme getTheme(String themeName);
}
