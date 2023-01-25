package com.utilsrv.doc;

import com.utilsrv.doc.batch.convert.DocPageBuilder;
import com.utilsrv.doc.batch.convert.PDF2HmlConverter;
import com.utilsrv.doc.batch.convert.PDF2HtmlTaskDef;
import com.utilsrv.doc.batch.convert.WordSegBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.item.support.ListItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableBatchProcessing
public class DFSBatchConfiguration {
    @Autowired
    public JobBuilderFactory jobBuilderFactory;
    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job convertDocumentJob(Step convertDocumentStep, Step buildPages, Step buildSegments) {
        return this.jobBuilderFactory.get("covertDocumentJob")
                .incrementer(new RunIdIncrementer())
                .start(convertDocumentStep)
                .next(buildPages)
                .next(buildSegments)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet buildPagesTasklet(DocPageBuilder docPageBuilder,
                                     @Value("#{jobParameters['"+DocConstants.JOB_PARAM_SHA1+"']}") String sha1) {
        return (contribution, chunkContext) -> {
            docPageBuilder.buildPages(sha1);
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    @StepScope
    public Tasklet wordSegBuilderTasklet(WordSegBuilder wordSegBuilder,
                                  @Value("#{jobParameters['"+DocConstants.JOB_PARAM_SHA1+"']}") String sha1) {
        return (contribution, chunkContext) -> {
            wordSegBuilder.buildSegments(sha1);
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step buildPages(Tasklet buildPagesTasklet) {
        return this.stepBuilderFactory.get("buildIndexStep")
                .tasklet(buildPagesTasklet)
                .build();
    }

    @Bean
    public Step buildSegments(Tasklet wordSegBuilderTasklet) {
        return this.stepBuilderFactory.get("buildIndexStep")
                .tasklet(wordSegBuilderTasklet)
                .build();
    }

    @Bean
    @StepScope
    public ListItemReader<PDF2HtmlTaskDef> convertDocReader(@Value("#{jobParameters['"+DocConstants.JOB_PARAM_SHA1+"']}")
                                                                String taskParam) {
        PDF2HtmlTaskDef task = GlobalVarsCache.getTaskDef(taskParam);
        List<PDF2HtmlTaskDef> taskList;
        if (task != null) {
            taskList = List.of(task);
        }
        else {
            taskList = Collections.emptyList();
        }

        return new ListItemReader<>(taskList);
    }

    @Bean
    public Step convertDocumentStep(ListItemReader<PDF2HtmlTaskDef> convertDocReader, PDF2HmlConverter converter) {
        return this.stepBuilderFactory.get("covertDocumentStep")
                .<PDF2HtmlTaskDef, PDF2HtmlTaskDef> chunk(1)
                .reader(convertDocReader)
                .processor(converter)
                .writer(new ListItemWriter<>())
                .build();
    }

}
