package com.company.autoconfigure.urlchangercomponent;

import com.company.urlchangercomponent.UrlConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({UrlConfiguration.class})
public class UrlAutoConfiguration {
}

