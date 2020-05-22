package lol.maki.billing.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lol.maki.billing.model.Bill;
import lol.maki.billing.model.Usage;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BillingConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final Resource usageResource;

    public BillingConfig(JobBuilderFactory jobBuilderFactory,
                         StepBuilderFactory stepBuilderFactory,
                         @Value("${usage.file.name:classpath:usageinfo.json}") Resource usageResource) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.usageResource = usageResource;
    }

    @Bean
    public Job billingJob(ItemReader<Usage> itemReader, ItemProcessor<Usage, Bill> itemProcessor, ItemWriter<Bill> itemWriter) {
        final Step step = this.stepBuilderFactory.get("BilliProcessing")
                .<Usage, Bill>chunk(1)
                .reader(itemReader)
                .processor(itemProcessor)
                .writer(itemWriter)
                .build();
        return this.jobBuilderFactory.get("BillingJob")
                .incrementer(new RunIdIncrementer())
                .start(step)
                .build();
    }

    @Bean
    public ItemReader<Usage> jsonItemReader() {
        final JacksonJsonObjectReader<Usage> jsonObjectReader = new JacksonJsonObjectReader<>(Usage.class);
        jsonObjectReader.setMapper(new ObjectMapper());
        return new JsonItemReaderBuilder<Usage>()
                .jsonObjectReader(jsonObjectReader)
                .resource(this.usageResource)
                .name("UsageJsonItemReader")
                .build();
    }

    @Bean
    public ItemProcessor<Usage, Bill> billItemProcessor() {
        return new ItemProcessor<Usage, Bill>() {
            @Override
            public Bill process(Usage usage) throws Exception {
                return Bill.fromUsage(usage);
            }
        };
    }

    @Bean
    public ItemWriter<Bill> jdbcBillWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Bill>()
                .beanMapped()
                .dataSource(dataSource)
                .sql("INSERT INTO BILL_STATEMENTS (id, first_name, last_name, minutes, data_usage,bill_amount) VALUES (:id, :firstName, :lastName, :minutes, :dataUsage, :billAmount)")
                .build();
    }

}
