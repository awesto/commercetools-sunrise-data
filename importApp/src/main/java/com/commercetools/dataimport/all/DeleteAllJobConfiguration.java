package com.commercetools.dataimport.all;

import com.commercetools.dataimport.commercetools.CommercetoolsJobConfiguration;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
@EnableBatchProcessing
@EnableAutoConfiguration
@Lazy
public class DeleteAllJobConfiguration {
    @Autowired
    protected JobBuilderFactory jobBuilderFactory;

    @Autowired
    protected StepBuilderFactory stepBuilderFactory;


    @Bean
    Job deleteAllJob(Job productsDeleteJob, Job productTypesDeleteJob, Job categoriesDeleteJob) {
        return jobBuilderFactory.get("allDeleteJob")
                .start(stepBuilderFactory.get("productsDeleteJob").job(productsDeleteJob).build())
                .next(stepBuilderFactory.get("categoriesDeleteJob").job(categoriesDeleteJob).build())
                .next(stepBuilderFactory.get("productTypesDeleteJob").job(productTypesDeleteJob).build())
                .build();
    }
}
