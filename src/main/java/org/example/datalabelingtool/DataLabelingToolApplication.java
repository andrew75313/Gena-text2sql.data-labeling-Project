package org.example.datalabelingtool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class DataLabelingToolApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataLabelingToolApplication.class, args);
    }

}
