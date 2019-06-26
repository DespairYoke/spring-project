package org.springframework.context;

import org.springframework.lang.Nullable;

@FunctionalInterface
public interface MyMessageSourceResolvable {
    @Nullable
    String[] getCodes();

    /**
     * Return the array of arguments to be used to resolve this message.
     * <p>The default implementation simply returns {@code null}.
     * @return an array of objects to be used as parameters to replace
     * placeholders within the message text
     * @see java.text.MessageFormat
     */
    @Nullable
    default Object[] getArguments() {
        return null;
    }

    /**
     * Return the default message to be used to resolve this message.
     * <p>The default implementation simply returns {@code null}.
     * Note that the default message may be identical to the primary
     * message code ({@link #getCodes()}), which effectively enforces
     * {@link org.springframework.context.support.AbstractMessageSource#setUseCodeAsDefaultMessage}
     * for this particular message.
     * @return the default message, or {@code null} if no default
     */
    @Nullable
    default String getDefaultMessage() {
        return null;
    }
}
