package org.springframework.context;

import org.springframework.beans.factory.Aware;

public interface MyApplicationEventPublisherAware  extends Aware {

    void setApplicationEventPublisher(MyApplicationEventPublisher applicationEventPublisher);
}
