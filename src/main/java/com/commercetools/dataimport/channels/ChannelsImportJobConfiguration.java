package com.commercetools.dataimport.channels;

import com.commercetools.dataimport.JsonUtils;
import io.sphere.sdk.channels.Channel;
import io.sphere.sdk.channels.ChannelDraft;
import io.sphere.sdk.channels.commands.ChannelCreateCommand;
import io.sphere.sdk.client.BlockingSphereClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
@EnableBatchProcessing
public class ChannelsImportJobConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelsImportJobConfiguration.class);

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private BlockingSphereClient sphereClient;

    @Bean
    public Job channelsImportJob(final Step channelsImportStep) {
        return jobBuilderFactory.get("channelsImportJob")
                .start(channelsImportStep)
                .build();
    }

    @Bean
    @JobScope
    public Step channelsImportStep(final ItemReader<ChannelDraft> channelsImportReader) {
        return stepBuilderFactory.get("channelsImportStep")
                .<ChannelDraft, ChannelDraft>chunk(1)
                .reader(channelsImportReader)
                .writer(writer())
                .build();
    }

    @Bean
    @StepScope
    public ListItemReader<ChannelDraft> channelsImportReader(@Value("#{jobParameters['resource']}") final Resource resource) throws IOException {
        return JsonUtils.createJsonListReader(resource, ChannelDraft.class);
    }

    private ItemWriter<ChannelDraft> writer() {
        return items -> items.forEach(channelDraft -> {
            final Channel channel = sphereClient.executeBlocking(ChannelCreateCommand.of(channelDraft));
            LOGGER.debug("Created channel \"{}\" in project {}", channel.getKey());
        });
    }
}
