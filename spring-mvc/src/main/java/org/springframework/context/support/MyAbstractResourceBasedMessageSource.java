package org.springframework.context.support;

import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.util.LinkedHashSet;
import java.util.Set;

public abstract class MyAbstractResourceBasedMessageSource extends MyAbstractMessageSource {

    private final Set<String> basenameSet = new LinkedHashSet<>(4);
    public void setBasename(String basename) {
        setBasenames(basename);
    }

    public void setBasenames(String... basenames) {
        this.basenameSet.clear();
        addBasenames(basenames);
    }
    public void addBasenames(String... basenames) {
        if (!ObjectUtils.isEmpty(basenames)) {
            for (String basename : basenames) {
                Assert.hasText(basename, "Basename must not be empty");
                this.basenameSet.add(basename.trim());
            }
        }
    }
}
