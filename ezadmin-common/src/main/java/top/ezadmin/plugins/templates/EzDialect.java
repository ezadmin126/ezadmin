package top.ezadmin.plugins.templates;

import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;

import java.util.HashSet;
import java.util.Set;

public class EzDialect extends AbstractProcessorDialect {

    public static final String NAME = "Ez Dialect";
    public static final String PREFIX = "th";

    public EzDialect() {
        super(NAME, PREFIX, 1000);
    }

    @Override
    public Set<IProcessor> getProcessors(String dialectPrefix) {
        Set<IProcessor> processors = new HashSet<>();
        processors.add(new EzAttrsProcessor(dialectPrefix));
        return processors;
    }
}
