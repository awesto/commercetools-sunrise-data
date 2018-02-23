package com.commercetools.dataimport.orders;

import com.commercetools.sdk.jvm.spring.batch.item.ItemReaderFactory;
import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.types.Type;
import io.sphere.sdk.types.queries.TypeQuery;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.util.Collections.singletonList;

@Configuration
@EnableBatchProcessing
public class OrderTypesDeleteJobConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private BlockingSphereClient sphereClient;

    @Bean
    public Job orderTypesDeleteJob(final Step orderTypesDeleteStep) {
        return jobBuilderFactory.get("orderTypesDeleteJob")
                .start(orderTypesDeleteStep)
                .build();
    }

    @Bean
    @JobScope
    public Step orderTypesDeleteStep(final ItemWriter<Type> typeDeleteWriter) {
        return stepBuilderFactory.get("orderTypesDeleteStep")
                .<Type, Type>chunk(1)
                .reader(reader())
                .writer(typeDeleteWriter)
                .build();
    }

    private ItemReader<Type> reader() {
        return ItemReaderFactory.sortedByIdQueryReader(sphereClient, TypeQuery.of()
                .withPredicates(type -> type.resourceTypeIds().containsAny(singletonList("order"))));
    }
}
