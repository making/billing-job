package lol.maki.billing.config;

import javax.sql.DataSource;

import lol.maki.billing.model.Bill;
import lol.maki.billing.model.Usage;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.RecordFieldSetMapper;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BillingConfig {
	private final Resource usageResource;

	public BillingConfig(Environment environment) {
		this.usageResource = Binder.get(environment)
				.bind("usage.file.name", Bindable.of(Resource.class))
				.orElseGet(() -> new ClassPathResource("usageinfo.csv"));
	}

	@Bean
	public Job billingJob(ItemReader<Usage> itemReader, ItemProcessor<Usage, Bill> itemProcessor, ItemWriter<Bill> itemWriter, JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		final Step step = new StepBuilder("BilliProcessing", jobRepository)
				.<Usage, Bill>chunk(3, transactionManager)
				.reader(itemReader)
				.processor(itemProcessor)
				.writer(itemWriter)
				.build();
		return new JobBuilder("BillingJob", jobRepository)
				.incrementer(new RunIdIncrementer())
				.start(step)
				.build();
	}

	@Bean
	public ItemReader<Usage> usageItemReader() {
		return new FlatFileItemReaderBuilder<Usage>()
				.name("UsageItemReader")
				.resource(this.usageResource)
				.delimited()
				.names("id", "firstName", "lastName", "minutes", "dataUsage")
				.fieldSetMapper(new RecordFieldSetMapper<>(Usage.class))
				.linesToSkip(1)
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
