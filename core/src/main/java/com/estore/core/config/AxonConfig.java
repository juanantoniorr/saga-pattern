package com.estore.core.config;

import com.thoughtworks.xstream.XStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AxonConfig {
    @Bean
    public XStream xStream(){
        XStream xStream = new XStream();
        xStream.allowTypesByWildcard(new String[] {"com.app.estore.**", "com.estore.core.**", "com.app.userservice.**", "com.estore.paymentservice.**"});
        return xStream;
    }
}
